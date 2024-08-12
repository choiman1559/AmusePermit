package com.amuse.client.tests;

public class Timer {
    private volatile long startTime;
    private volatile long duration;

    public synchronized void start() {
        this.duration = 0;
        this.startTime = System.nanoTime();
    }

    public synchronized long stop() {
        duration += System.nanoTime()-this.startTime;
        return duration;
    }

    public synchronized void resume() {
        this.startTime = System.nanoTime();
    }

    public synchronized long pause() {
        duration += System.nanoTime()-this.startTime;
        return duration;
    }
}