/*
 * Function.java
 *
 * Created on January 11, 2003, 1:36 PM
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

package net.sourceforge.cilib.Functions;

import net.sourceforge.cilib.Problem.*;

/**
 *
 * @author  espeer
 */
public abstract class Function {
    
    public Function(int dimension, Domain domain, double minimum) {
        this.dimension = dimension;
        this.domain = domain;
        this.minimum = minimum;
    }
    
    public int getDimension() {
        return dimension;
    }
    
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    
    public Domain getDomain() {
        return domain;
    }
       
    public void setDomain(Domain domain) {
        this.domain = domain;
    }
    
    public double getMinimum() {
        return minimum;
    }
    
    public abstract double evaluate(double[] x);
    
    protected int dimension;
    protected Domain domain;
    protected double minimum;
}