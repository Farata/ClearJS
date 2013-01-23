/*
 * Copyright © 2008, 2012 Pedro Agulló Soliveres.
 * 
 * This file is part of DirectJNgine.
 *
 * DirectJNgine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * Commercial use is permitted to the extent that the code/component(s)
 * do NOT become part of another Open Source or Commercially developed
 * licensed development library or toolkit without explicit permission.
 *
 * DirectJNgine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DirectJNgine.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This software uses the ExtJs library (http://extjs.com), which is 
 * distributed under the GPL v3 license (see http://extjs.com/license).
 */

package com.softwarementors.extjs.djn;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Timer {
  @NonNull public static final Logger logger = Logger.getLogger( Timer.class );
  @NonNull private static final DecimalFormat numberFormatter = new DecimalFormat("#.##");

  private boolean running;  
  private long startNanos;
  private long elapsedNanoseconds;
  
  public Timer() {
    restart();
  }
  
  public void restart() {
    this.running = true;
    this.elapsedNanoseconds = 0;
    this.startNanos = System.nanoTime() ; 
  }
  
  public void stop() {
    // assert isRunning();
    
    this.running = false;
    this.elapsedNanoseconds = System.nanoTime() - this.startNanos;
  }
  
  public boolean isRunning() {
    return this.running;
  }
  
  public long getElapsedMicroseconds() {
    assert !isRunning();
    
    long result = this.elapsedNanoseconds / 1000;
    return result;
  }
  
  public long getElapsedMilliseconds() {
    assert !isRunning();

    return getElapsedMicroseconds() / 1000;
  }
  
  public void stopAnLogDebugTimeInMilliseconds( String message ) {
    assert !StringUtils.isEmpty(message);
    
    stop();
    logDebugTimeInMilliseconds(message);
  }
  
  public void logDebugTimeInMilliseconds( String message ) {
    assert !StringUtils.isEmpty(message);
    assert !isRunning();
  
    if( logger.isDebugEnabled() ) {
      logger.debug( message + ": " + numberFormatter.format(getElapsedMicroseconds() / 1000.0)+ " ms." );
    }
  }
}
