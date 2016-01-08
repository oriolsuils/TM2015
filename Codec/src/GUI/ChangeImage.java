/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.TimerTask;

/**
 * This class sets the timer
 * @author Oriol i Pol
 */
public class ChangeImage extends TimerTask{
    private int count;
    private int size;
    private FPSCounter fpsCounter;
    private boolean debug;

    /**
     * Construct
     * @param count
     * @param size
     * @param debug 
     */
    public ChangeImage(int count, int size, boolean debug) {
        this.count = count;
        this.size = size;
        this.debug = debug;
        this.fpsCounter = new FPSCounter();
    }

    /**
     * Get Count
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * Get FPS counter
     * @return fpsCounter
     */
    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }

    /**
     * Run
     */
    @Override
    public void run() {
        this.fpsCounter.timeStamp();
        if(this.debug) System.out.println("FPS: " + this.fpsCounter.getFPS() + "\tMilliseconds: " + this.fpsCounter.getms());
        this.fpsCounter.removeFirstTime();
        if(this.count >= this.size-1) this.count = 0;
        else this.count++;
    }
}
