/*
 * Simulation.java
 *
 * Created on February 5, 2003, 10:13 AM
 *
 * 
 * Copyright (C) 2003 - Edwin S. Peer 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 *   
 */

package net.sourceforge.cilib.Simulator;

import net.sourceforge.cilib.Algorithm.*;
import net.sourceforge.cilib.Problem.*;

import java.io.*;
import java.lang.*;
import java.lang.reflect.*;
import java.util.*;
import java.text.*;

/**
 *
 * @author  espeer
 */
public class Simulation extends Thread implements Serializable, IterationEventListener, CompleteEventListener {
    
    /** Creates a new instance of Simulation */
    public Simulation(AlgorithmFactory algorithmFactory, 
                      ProblemFactory problemFactory,
                      MeasurementSuite measurementSuite) {
        
        measurementSuite.initialise();
        this.measurementSuite = measurementSuite;
        progressListeners = new Vector();
        progress = new HashMap();

        algorithms = new Algorithm[measurementSuite.getSamples()];
        for (int i = 0; i < measurementSuite.getSamples(); ++i) {
            algorithms[i] = algorithmFactory.newAlgorithm();
            algorithms[i].addIterationEventListener(this);
            algorithms[i].addCompleteEventListener(this);
            Problem[] problems = new Problem[1];
            problems[0] = problemFactory.newProblem();
            try {
                String type = problems[0].getClass().getInterfaces()[0].getName();
                Class parameters[] = new Class[1];
                parameters[0] = Class.forName(type);
                String setMethodName = "set" + type.substring(type.lastIndexOf(".") + 1);
                Method setProblemMethod = algorithms[i].getClass().getMethod(setMethodName, parameters);
                setProblemMethod.invoke(algorithms[i], problems);
            }
            catch (Exception ex) {
                throw new InitialisationException(algorithms[i].getClass().getName() + " does not support problems of type " + problems[0].getClass().getName());
            }
            algorithms[i].initialise();
            progress.put(algorithms[i], new Double(0));
        }
    }
    
    public void run() { 
        for (int i = 0; i < measurementSuite.getSamples(); ++i) {
            algorithms[i].start();
        }
        for (int i = 0; i < measurementSuite.getSamples(); ++i) {
            try {
                algorithms[i].join();
            }
            catch (InterruptedException ex) { }
        }
        measurementSuite.getOutputBuffer().close();
        measurementSuite = null;
        algorithms = null;
        progress = null;
        progressListeners = null;
    }
    
    public void terminate() {
        for (int i = 0; i < measurementSuite.getSamples(); ++i) {
            algorithms[i].terminate();
        }
    }
     
    public void handleCompleteEvent(AlgorithmEvent e) {
        measurementSuite.measure(e.getSource());
        progress.put(e.getSource(), new Double(e.getSource().getPercentageComplete()));
        notifyProgress();
    }
    
    public void handleIterationEvent(AlgorithmEvent e) {
        if (e.getSource().getIterations() % measurementSuite.getResolution() == 0) {
            measurementSuite.measure(e.getSource());
            progress.put(e.getSource(), new Double(e.getSource().getPercentageComplete()));
            notifyProgress();
        }
    }
    
    private int getThreadId(Algorithm algorithm) {
        for (int i = 0; i < measurementSuite.getSamples(); ++i) {
            if (algorithm == algorithms[i]) {
                return i;
            }
        }
        return -1;
    }
    
    
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }
    
    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }
    
    private void notifyProgress() {
        double minimum = 100;
        for (Iterator i = progress.values().iterator(); i.hasNext(); ) {
            double percentage = ((Double) i.next()).doubleValue();
            if (percentage < minimum) {
                minimum = percentage;
            }
        }   
        
        for (Iterator i = progressListeners.iterator(); i.hasNext(); ) {
            ProgressListener listener = (ProgressListener) i.next();
            listener.handleProgressEvent(new ProgressEvent(minimum));
        }
    }
    
    private MeasurementSuite measurementSuite;
    private Algorithm[] algorithms;
    private int simulation;
    private Vector progressListeners;
    private HashMap progress;
}