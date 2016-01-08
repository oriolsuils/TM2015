/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class is the FPS counter
 * @author Oriol i Pol
 */
public class FPSCounter{
    private ArrayList<Long> times;
    /**
     * Construct
     */
    public FPSCounter() {
        this.times = new ArrayList();
        this.times.add(System.nanoTime());
    }
    /**
     * Add a time stamp
     */
    public void timeStamp() {
        this.times.add(System.nanoTime());
    }
    /**
     * Remove the first time
     */
    public void removeFirstTime() {
        this.times.remove(0);
    }
    /**
     * Get milliseconds
     * @return milliseconds 
     */
    public double getms() {
        Long result = this.times.get(1) - this.times.get(0);
        double milliseconds = result / 1000000.0;
        return milliseconds;
    }
    /**
     * Get Frames per second
     * @return fps
     */
    public String getFPS() {
        DecimalFormat df = new DecimalFormat("#.00"); 
        Long result = this.times.get(1) - this.times.get(0);
        double fps = 1/(result / 1000000000.0);
        return df.format(fps);
    }
}