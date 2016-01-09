package Encoder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class represents the I-image, its tiles, and contains the P-images.
 * @author Oriol i Pol
 */
public class Frame implements Comparable<Frame>{
    private BufferedImage image;
    private int id;
    private ArrayList<Tesela> teseles;
    private ArrayList<BufferedImage> pFrames;

    /**
     * Construct
     * @param image 
     */
    public Frame(BufferedImage image, int id) {
        this.image = image;
        this.id = id;
        this.teseles = new ArrayList<>();
        this.pFrames = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Get tiles
     * @return teseles
     */
    public ArrayList<Tesela> getTeseles() {
        return teseles;
    }

    /**
     * Set tiles
     * @param teseles 
     */
    public void setTeseles(ArrayList<Tesela> teseles) {
        this.teseles = teseles;
    }

    /**
     * Get P-frames
     * @return pFrames
     */
    public ArrayList<BufferedImage> getpFrames() {
        return pFrames;
    }

    /**
     * Set P-frames
     * @param pFrames 
     */
    public void setpFrames(ArrayList<BufferedImage> pFrames) {
        this.pFrames = pFrames;
    }
    
    /**
     * Add P-frame
     * @param image 
     */
    public void addpFrame(BufferedImage image){
        this.pFrames.add(image);
    }

    /**
     * Get image
     * @return image
     */
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int compareTo(Frame o) {
        //return Comparators.ID.compare(this, o);
        return 1;
    }
/*
    public static class Comparators {
        public static Comparator<Frame> ID = new Comparator<Frame>() {
            @Override
            public int compare(Frame o1, Frame o2) {
                return o1.getId().compareTo(o2.getId());
            }
        };
    }*/
    
}
