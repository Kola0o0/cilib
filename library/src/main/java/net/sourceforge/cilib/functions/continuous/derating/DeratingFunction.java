/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.derating;

import net.sourceforge.cilib.functions.ContinuousFunction;

/**
 *
 */
public interface DeratingFunction extends ContinuousFunction {
    double getRadius();
}
