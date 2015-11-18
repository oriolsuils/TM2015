/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.TimerTask;

/**
 *
 * @author Oriol
 */
public class ChangeImage extends TimerTask{
    private int count;
    private int size;
    private FPSCounter fpsCounter;
    private boolean debug;

    public ChangeImage(int count, int size, boolean debug) {
        this.count = count;
        this.size = size;
        this.debug = debug;
        this.fpsCounter = new FPSCounter();
    }

    public int getCount() {
        return count;
    }

    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }

    @Override
    public void run() {
        this.fpsCounter.timeStamp();
        if(this.debug) System.out.println("FPS: " + this.fpsCounter.getFPS() + "\tMilliseconds: " + this.fpsCounter.getms());
        this.fpsCounter.removeFirstTime();
        if(this.count >= this.size-1) this.count = 0;
        else this.count++;
    }
}
