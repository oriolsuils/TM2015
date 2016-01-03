package Encoder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
        int[][] matrix = generateMatrix();
        for (BufferedImage frame : raw_images) {
            if(gopCount == this.gop){ //Es pren la referencia (I-Frame) i es generen les teseles
                iFrame = generateMacroblocks(frame);
            }else{
                ArrayList<Integer> positions = calculateRangePositions(matrix, 11);
                findCompatibleBlock(frame, iFrame, matrix);
                break;
            }
            gopCount--;
            if(gopCount == 0) gopCount = this.gop;
        }
    }
    
    public ArrayList<BufferedImage> generateMacroblocks(BufferedImage image){
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

    private void findCompatibleBlock(BufferedImage pFrame, ArrayList<BufferedImage> iFrame, int[][] matrix) {
        for(int i=0; i<iFrame.size(); i++){
            ArrayList<Integer> positions = calculateRangePositions(matrix, i);
            float maxPSNR = Float.MIN_VALUE;
            int id = -1;
            for(int pos : positions){
                int tileWidth = pFrame.getWidth()/this.nTiles;
                int tileHeight = pFrame.getHeight()/this.nTiles;
                int x = (int) Math.ceil(pos/matrix.length);
                int y = pos%matrix.length; 
                //System.out.println("POS: " + pos + " X: " + x + " Y: " + y);
                float psnr = calculatePSNR(iFrame.get(i), pFrame.getSubimage(x, y, tileWidth, tileHeight));
                if(psnr > maxPSNR){
                    maxPSNR = psnr;
                    id = pos;
                }
            }
            System.out.println("TESELA: " + i + " VALUE: " + maxPSNR + " ID: " + id);
            if(maxPSNR >= this.quality){
                //Eliminar tesela
            }
        }
    }
    
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
    
    private ArrayList<Integer> calculateRangePositions(int[][] matrix, int id){
        ArrayList<Integer> positions = new ArrayList<>();
        int size = matrix[0].length;
        int i = (int) Math.ceil(id/size);
        int j = id%size;
        int value = matrix[i][j];
        if(value == id){
            int minX = i-this.seekRange;
            int maxX = i+this.seekRange;
            int minY = j-this.seekRange;
            int maxY = j+this.seekRange;
            for(int row=minX; row<=maxX; row++){
                if(row >= 0 && row < size){
                    for(int col=minY; col<=maxY; col++){
                        if(col >= 0 && col < size){
                            if(!positions.contains(matrix[row][col])) positions.add(matrix[row][col]);
                        }
                    }
                }
            }
        }
        Collections.sort(positions);
        return positions;
    }
    
    private int[][] generateMatrix(){
        int[][] matrix = new int[this.nTiles][this.nTiles];
        int count=0;
        for (int row=0; row < matrix.length; row++){
            for (int col=0; col < matrix[row].length; col++){
                matrix[row][col] = count;
                count++;
            }
        }
        return matrix;
    }
}
