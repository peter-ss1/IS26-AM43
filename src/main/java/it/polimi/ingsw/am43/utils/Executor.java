package it.polimi.ingsw.am43.utils;

import it.polimi.ingsw.am43.network.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Executor<T> {
    final BlockingQueue<Task<T>> taskQueue;
    final T target;
    final Thread loop;
    private volatile boolean active;

    public Executor(T target){
        this.target=target;
        this.loop=new Thread(this::runLoop);
        this.taskQueue=new LinkedBlockingQueue<>();
    }

    public void delegate(Task<T> task)throws RuntimeException{
        try {
            this.taskQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);//TODO manage this
        }

    }

    public void start(){
        this.active=true;
        this.loop.start();
    }
    public void stop(){
        this.active=false;
        this.loop.interrupt();
    }

    private void runLoop(){
        while (this.active) {
            try {
                Task<T> task = taskQueue.take();
                task.execute(this.target);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

}
