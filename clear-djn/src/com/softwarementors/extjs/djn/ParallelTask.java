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

/*
 * Just to give credit where credit is due...
 * 
 * The original implementation for this class was written 
 * by Jacob Hookom.
 * 
 * Take a look at http://weblogs.java.net/blog/jhook/
 * for details and a nice discussion on handling
 * concurrent tasks.
 */

package com.softwarementors.extjs.djn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ParallelTask<V> implements Future<Collection<V>> {

  private class BoundedFuture extends FutureTask<V> {
    BoundedFuture(Callable<V> c) { super(c); }
    @Override
    protected void done() {
      ParallelTask.this.semaphore.release(); // Allow other thread to be executed -if there is one waiting to enter the semaphore protected code
      ParallelTask.this.completedQueue.add(this); // Add this task to the lisf of completed tasks
    }
  }

  //List of submitted tasks
  @NonNull private final List<BoundedFuture> submittedQueue;         
  //List of completed tasks: must be thread safe, for different BoundedFuture tasks will attempt to add themselves here as they finish, concurrently  
  @NonNull private final BlockingQueue<BoundedFuture> completedQueue; 
  @NonNull private final Semaphore semaphore;
  @NonNull private final Executor executor;
  private final int size;
  private boolean cancelled = false;

  public ParallelTask(Executor exec, Collection<Callable<V>> callable, int permits) {
    if (exec == null || callable == null) 
      throw new NullPointerException();
    
    this.executor = exec;
    this.semaphore = new Semaphore(permits);
    this.size = callable.size();
    this.submittedQueue = new ArrayList<BoundedFuture>(this.size);
    this.completedQueue = new LinkedBlockingQueue<BoundedFuture>(this.size);
    for (Callable<V> c : callable) {
      this.submittedQueue.add(new BoundedFuture(c));
    }
  }

  public boolean cancel(boolean mayInterruptIfRunning) {
    if (this.isDone()) 
      return false;
    
    this.cancelled = true;
    for (Future<?> f : this.submittedQueue) {
      f.cancel(mayInterruptIfRunning);
    }
    return this.cancelled;
  }

  public Collection<V> get() throws InterruptedException, ExecutionException {
    // throw new UnsupportedOperationException( "We do not use this in DirectJNgine, and therefore we haven't tested the code (even though it is from a reliable source)");

    Collection<V> result = new ArrayList<V>(this.submittedQueue.size());
    boolean done = false;
    try {
      // Start executing threads: the number of threads running concurrently is limited by the semaphore
      for (BoundedFuture f : this.submittedQueue) {
        if (this.isCancelled()) { 
          break;
        }
        this.semaphore.acquire();
        this.executor.execute(f);
      }
      
      // Get results once all tests have started running: calling take() on the completed queue will block unless all
      // threads have finished running!
      for (int i = 0; i < this.size; i++) {
        if (this.isCancelled())
          break;
        
        result.add(this.completedQueue.take().get());
      }
      done = true;
    } 
    finally {
      if (!done) 
        this.cancel(true);
    }
    return result;
  }

  public Collection<V> get(long timeout, TimeUnit unit)
    throws InterruptedException, ExecutionException, TimeoutException 
  {
    throw new UnsupportedOperationException( "We do not support timeouts in DirectJNgine, and therefore we haven't tested the following code!");
    /*
    // timeout handling isn't perfect, but it's an attempt to 
    // replicate the behavior found in AbstractExecutorService
    long nanos = unit.toNanos(timeout);
    long endTime = System.nanoTime() + nanos;
    
    boolean done = false;
    Collection<V> taskExecutionResults = new ArrayList<V>(this.submittedQueue.size());
    try {
      for (BoundedFuture f : this.submittedQueue) {
        if (System.nanoTime() >= endTime) 
          throw new TimeoutException();
        
        // If we cancelled execution, do not try to keep adding more task to execute
        if (this.isCancelled()) 
          break;

        // We will block here if there are already n tasks running (n=number passed on semaphore creation)
        // When one of those running tasks is finished, a new task will be allowed to start execution 
        this.semaphore.acquire();
        this.executor.execute(f);
      }
      
      for (int i = 0; i < this.size; i++) {
        if (this.isCancelled()) 
          break;
        
        long nowTime = System.nanoTime();
        if (nowTime >= endTime) 
          throw new TimeoutException();
        
        BoundedFuture f = this.completedQueue.poll(endTime - nowTime, TimeUnit.NANOSECONDS);
        if (f == null) 
          throw new TimeoutException();
        
        taskExecutionResults.add(f.get());
      }
      done = true;
    } 
    finally {
      if (!done) {
        this.cancel(true);
      }
    }
    return taskExecutionResults;
    */
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

  public boolean isDone() {
    return this.completedQueue.size() == this.size;
  }
}