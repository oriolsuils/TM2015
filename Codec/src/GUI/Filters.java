/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author osuilspe7.alumnes
 */
public class Filters {
    public void binarization(BufferedImage image, float threshold){
        int r, g, b;
        for(int i=0; i < image.getWidth(); i++){
            for(int j=0; j < image.getHeight(); j++){
                Color color = new Color(image.getRGB(i, j));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                if(r > threshold || g > threshold || b > threshold){
                    image.setRGB(i, j, new Color(255, 255, 255).getRGB());
                } else {
                    image.setRGB(i, j, new Color(0, 0, 0).getRGB());
                }
            }
        }
    }
    
    public void negative(BufferedImage image){
        int r, g, b;
        for(int i=0; i < image.getWidth(); i++){
            for(int j=0; j < image.getHeight(); j++){
                Color color = new Color(image.getRGB(i, j));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                image.setRGB(i, j, new Color(255-r, 255-g, 255-b).getRGB());
                
            }
        }
    }
    
    public void averaging(BufferedImage image, int size){
        
    }
}
