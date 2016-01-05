package Encoder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Oriol
 */
public class Frame {
    private BufferedImage image;
    private ArrayList<Tesela> teseles;
    private ArrayList<BufferedImage> pFrames;

    public Frame(BufferedImage image) {
        this.image = image;
        this.teseles = new ArrayList<>();
        this.pFrames = new ArrayList<>();
    }

    public ArrayList<Tesela> getTeseles() {
        return teseles;
    }

    public void setTeseles(ArrayList<Tesela> teseles) {
        this.teseles = teseles;
    }

    public ArrayList<BufferedImage> getpFrames() {
        return pFrames;
    }

    public void setpFrames(ArrayList<BufferedImage> pFrames) {
        this.pFrames = pFrames;
    }
    
}
