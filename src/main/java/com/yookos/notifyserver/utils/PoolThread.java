package com.yookos.notifyserver.utils;

import java.util.concurrent.BlockingQueue;

/**
 * Created by jome on 2014/06/20.
 */
public class PoolThread extends Thread {
    private BlockingQueue queue = null;
    private boolean isStopped = false;

    public PoolThread(BlockingQueue queue){
        this.queue = queue;
    }




    public synchronized boolean isStopped(){
        return this.isStopped;
    }
}
