/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author Oriol
 */
public class FPSCounter extends Thread{
    private ArrayList<Long> times;
    
    public FPSCounter() {
        this.times = new ArrayList();
        this.times.add(System.nanoTime());
    }
    
    public void timeStamp() {
        this.times.add(System.nanoTime());
    }

    public void removeFirstTime() {
        this.times.remove(0);
    }

    public double getms() {
        Long result = this.times.get(1) - this.times.get(0);
        double milliseconds = result / 1000000.0;
        return milliseconds;
    }
    
    public String getFPS() {
        DecimalFormat df = new DecimalFormat("#.00"); 
        Long result = this.times.get(1) - this.times.get(0);
        double fps = 1/(result / 1000000000.0);
        return df.format(fps);
    }
}