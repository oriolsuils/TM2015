package Encoder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class represents the I-image, its tiles, and contains the P-images.
 * @author Oriol i Pol
 */
public class Frame {
    private BufferedImage image;
    private ArrayList<Tesela> teseles;
    private ArrayList<BufferedImage> pFrames;

    /**
     * Construct
     * @param image 
     */
    public Frame(BufferedImage image) {
        this.image = image;
        this.teseles = new ArrayList<>();
        this.pFrames = new ArrayList<>();
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
    
}
