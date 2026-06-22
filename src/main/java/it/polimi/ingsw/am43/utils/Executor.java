package it.polimi.ingsw.am43.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A thread-safe, single-threaded task executor that sequentially processes tasks
 * asynchronously against a specific target object.
 *
 * @param <T> The type of the target object on which tasks operate.
 */
public class Executor<T> {
    final BlockingQueue<Task<T>> taskQueue;
    final T target;
    final Thread loop;
    private final AtomicBoolean active;

    /**
     * Constructs an Executor bound to a specific operational target.
     *
     * @param target The object on which tasks will be executed.
     */
    public Executor(T target) {
        this.target = target;
        this.loop = new Thread(this::runLoop);
        this.taskQueue = new LinkedBlockingQueue<>();
        this.active = new AtomicBoolean(false);

    }

    /**
     * Enqueues a task to be processed asynchronously by the background worker thread.
     *
     * @param task The task configuration logic to schedule.
     * @throws RuntimeException If the thread is interrupted while waiting to queue the task.
     */
    public void delegate(Task<T> task) throws RuntimeException {
        try {
            this.taskQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Starts the background task processing thread if it is currently inactive.
     */
    public void start() {
        if (this.active.compareAndSet(false, true))
            this.loop.start();
    }

    /**
     * Starts the background task processing thread if it is currently inactive.
     */
    public void stop() {
        if (this.active.compareAndSet(true, false))
            this.loop.interrupt();
    }

    /**
     * The core background worker loop that sequentially retrieves tasks from
     * the queue and executes them on the assigned target.
     */
    private void runLoop() {
        while (this.active.get()) {
            try {
                Task<T> task = taskQueue.take();
                task.execute(this.target);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }
}
