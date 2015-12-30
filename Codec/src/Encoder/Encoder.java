package Encoder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Oriol
 */
public class Encoder {
    private ArrayList<BufferedImage> raw_images;
    private int nTiles;
    private float quality;
    private int gop;
    private float seekRange; 

    public Encoder(ArrayList<BufferedImage> raw_images, int nTiles, float quality, int gop, float seekRange) {
        this.raw_images = raw_images;
        this.nTiles = nTiles;
        this.quality = quality;
        this.gop = gop;
        this.seekRange = seekRange;
    }
    
    public void encode(){
        ArrayList<BufferedImage> iFrame = null;
        int gopCount = this.gop;
        for(int i=0; i<raw_images.size(); i++){
            BufferedImage pFrame = raw_images.get(i);
            if(gopCount == this.gop){ //Es pren la referencia (I-Frame) i es generen les teseles
                iFrame = generateMacroblocks(pFrame);
            }else{
                findCompatibleBlock(pFrame, iFrame);
            }
            gopCount--;
            if(gopCount == 0) gopCount = this.gop;
        }
    }
    
    private ArrayList<BufferedImage> generateMacroblocks(BufferedImage image){
        ArrayList<BufferedImage> blocks = new ArrayList<>();
        int tileWidth = image.getWidth()/this.nTiles;
        int tileHeight = image.getHeight()/this.nTiles;
        for(int i=0; i<image.getWidth(); i+=tileWidth){
            for(int j=0; j<image.getHeight(); j+=tileHeight){
                blocks.add(image.getSubimage(i, j, tileWidth, tileHeight));
            }
        }
        return blocks;
    }

    private void findCompatibleBlock(BufferedImage pFrame, ArrayList<BufferedImage> iFrame) {
        ArrayList<BufferedImage> pFrameBlocked = generateMacroblocks(pFrame);
        for (BufferedImage iframe : iFrame) {
            for (BufferedImage pframe : pFrameBlocked) {
                compare2Images(iframe, pframe);
            }
        }
    }

    private float compare2Images(BufferedImage iframe, BufferedImage pframe) {
        float sum = 0;
        for(int i=0; i<iframe.getHeight(); i++){
            for(int j=0; j<iframe.getWidth(); j++){
                Color iframe_rgb = new Color(iframe.getRGB(j, i));
                Color pframe_rgb = new Color(pframe.getRGB(j, i));
                sum += Math.pow(iframe_rgb.getRed() - pframe_rgb.getRed(), 2);
                sum += Math.pow(iframe_rgb.getGreen()- pframe_rgb.getGreen(), 2);
                sum += Math.pow(iframe_rgb.getBlue() - pframe_rgb.getBlue(), 2);
            }
        }
        return sum/(iframe.getHeight()*iframe.getWidth()*3);
    }
}
