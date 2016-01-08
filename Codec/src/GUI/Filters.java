/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * This class sets the filters
 * @author Oriol i Pol
 */
public class Filters {
    /**
     * This method binarizate an image
     * @param image
     * @param threshold
     * @return imageFiltered
     */
    public static BufferedImage binarization(BufferedImage image, float threshold){
        int r, g, b;
        BufferedImage imageFiltered = new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null);
        for(int i=0; i < image.getWidth(); i++){
            for(int j=0; j < image.getHeight(); j++){
                Color color = new Color(image.getRGB(i, j));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                if((float) r > threshold ||(float) g > threshold ||(float) b > threshold){
                    imageFiltered.setRGB(i, j, new Color(255, 255, 255).getRGB());
                } else {
                    imageFiltered.setRGB(i, j, new Color(0, 0, 0).getRGB());
                }
            }
        }
        return imageFiltered;
    }
    /**
     * This methods turns an image to negative
     * @param image
     * @return imageFiltered
     */
    public static BufferedImage negative(BufferedImage image){
        int r, g, b;
        BufferedImage imageFiltered = new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null);
        for(int i=0; i < image.getWidth(); i++){
            for(int j=0; j < image.getHeight(); j++){
                Color color = new Color(image.getRGB(i, j));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                imageFiltered.setRGB(i, j, new Color(255-r, 255-g, 255-b).getRGB());
            }
        }
        return imageFiltered;
    }
    /**
     * This method turns an image to an averaged image
     * @param image
     * @param size
     * @return imageFiltered
     */
    public static BufferedImage averaging(BufferedImage image, int size){
        int numFilter = size*size;
        float[] filter = new float[numFilter];
        for(int i=0; i<numFilter; i++){
            filter[i] = (float)1/(float)numFilter;
        }
        BufferedImageOp op = new ConvolveOp(new Kernel(size, size, filter));
	return op.filter(image, null);
    }
}
