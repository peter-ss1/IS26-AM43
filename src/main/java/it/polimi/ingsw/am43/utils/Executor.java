package it.polimi.ingsw.am43.utils;

import it.polimi.ingsw.am43.network.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Executor<T> {
    final BlockingQueue<Task<T>> taskQueue;
    final T target;
    final Thread loop;
    private final AtomicBoolean active;


    public Executor(T target){
        this.target=target;
        this.loop=new Thread(this::runLoop);
        this.taskQueue=new LinkedBlockingQueue<>();
        this.active=new AtomicBoolean(false);

    }

    public void delegate(Task<T> task)throws RuntimeException{
        try {
            this.taskQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void start(){
        if(this.active.compareAndSet(false,true))
            this.loop.start();
    }
    public void stop(){
        if(this.active.compareAndSet(true,false))
            this.loop.interrupt();
    }


    private void runLoop(){
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
