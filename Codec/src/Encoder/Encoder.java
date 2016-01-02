package Encoder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Oriol
 */
public class Encoder {
    private ArrayList<BufferedImage> raw_images;
    private ArrayList<BufferedImage> cod_images;
    private int nTiles;
    private float quality;
    private int gop;
    private int seekRange; 

    public Encoder(ArrayList<BufferedImage> raw_images, int nTiles, float quality, int gop, int seekRange) {
        this.raw_images = raw_images;
        this.nTiles = nTiles;
        this.quality = quality;
        this.gop = gop;
        this.seekRange = seekRange;
    }
    
    public void encode(){
        ArrayList<BufferedImage> iFrame = null;
        int gopCount = this.gop;
        for (BufferedImage frame : raw_images) {
            if(gopCount == this.gop){ //Es pren la referencia (I-Frame) i es generen les teseles
                iFrame = generateMacroblocks(frame);
            }else{
                findCompatibleBlock(frame, iFrame);
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
        ArrayList<Float> values = new ArrayList<>();
        for(BufferedImage iframe : iFrame){
            int tileWidth = pFrame.getWidth()/this.nTiles;
            int tileHeight = pFrame.getHeight()/this.nTiles;
            for(int i=0; i<pFrame.getWidth(); i+=tileWidth){
                for(int j=0; j<pFrame.getHeight(); j+=tileHeight){
                    values.add(calculatePSNR(iframe, pFrame.getSubimage(i, j, tileWidth, tileHeight)));
                }
            }
        }
        System.out.println("MAX PSNR: " + Collections.max(values));
    }
/*
    private float compare2Images(BufferedImage iframe, BufferedImage pframe) {
        float sum = 0;
        for(int i=0; i<iframe.getHeight(); i++){
            for(int j=0; j<iframe.getWidth(); j++){
                Color iframe_rgb = new Color(iframe.getRGB(j, i));
                Color pframe_rgb = new Color(pframe.getRGB(j, i));
                sum += Math.pow(pframe_rgb.getRed() - iframe_rgb.getRed(), 2);
                sum += Math.pow(pframe_rgb.getGreen()- iframe_rgb.getGreen(), 2);
                sum += Math.pow(pframe_rgb.getBlue() - iframe_rgb.getBlue(), 2);
            }
        }
        return sum/(iframe.getHeight()*iframe.getWidth()*3);
    }*/
    
    private float calculatePSNR(BufferedImage iframe, BufferedImage pframe){
        float noise = 0, mse = 0, psnr = 0;
        for (int i=0; i<iframe.getHeight(); i++) {
            for (int j=0; j<iframe.getWidth(); j++) {
                Color iframe_rgb = new Color(iframe.getRGB(j, i));
                Color pframe_rgb = new Color(pframe.getRGB(j, i));
                noise += Math.pow(pframe_rgb.getRed() - iframe_rgb.getRed(), 2);
                noise += Math.pow(pframe_rgb.getGreen()- iframe_rgb.getGreen(), 2);
                noise += Math.pow(pframe_rgb.getBlue() - iframe_rgb.getBlue(), 2);
            }
          }
        mse = noise/(iframe.getHeight()*iframe.getWidth()*3);
        psnr = (float) (10 * Math.log10((255 * 255) / mse));
        return psnr;
    }
    
    private ArrayList<Integer> calculateRangePositions(ArrayList<BufferedImage> blockedImage, int id){
        ArrayList<Integer> positions = new ArrayList<>();
        for(int i=-this.seekRange; i<=this.seekRange; i++){
            if((id+i)<0) positions.add(0);
            else if((id+i)>blockedImage.size()) positions.add(blockedImage.size());
            else positions.add(id+i);
        }
        return positions;
    }
}
