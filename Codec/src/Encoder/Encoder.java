package Encoder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
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
                int[][] matrix = transform2Matrix(iFrame);
                calculateRangePositions(matrix, 11);
                //findCompatibleBlock(frame, iFrame);
            }
            gopCount--;
            if(gopCount == 0) gopCount = this.gop;
        }
    }
    
    public ArrayList<BufferedImage> generateMacroblocks(BufferedImage image){
        ArrayList<BufferedImage> blocks = new ArrayList<>();
        int tileWidth = image.getWidth()/this.nTiles;
        int tileHeight = image.getHeight()/this.nTiles;
        for(int i=0; i<image.getHeight(); i+=tileHeight){
            for(int j=0; j<image.getWidth(); j+=tileWidth){
                blocks.add(image.getSubimage(j, i, tileWidth, tileHeight));
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
            for(int row=this.seekRange; row>-this.seekRange; row--){
                for(int col=this.seekRange; col>-this.seekRange; col--){
                    int rowIdx = (i-row);
                    int colIdx = (j-col);
                    if(rowIdx<0){
                        if(colIdx<0) if(!positions.contains(matrix[0][0])) positions.add(matrix[0][0]);
                        else if(colIdx>(size*size)) if(!positions.contains(matrix[0][size-1])) positions.add(matrix[0][size-1]);
                        else if(!positions.contains(matrix[0][colIdx])) positions.add(matrix[0][colIdx]);
                    }else if(colIdx<0){
                        if(rowIdx<0) if(!positions.contains(matrix[0][0])) positions.add(matrix[0][0]);
                        else if(rowIdx>(size*size)) if(!positions.contains(matrix[size-1][0])) positions.add(matrix[size-1][0]);
                        else if(!positions.contains(matrix[rowIdx][0])) positions.add(matrix[rowIdx][0]); 
                    }else if(rowIdx>(size*size)){
                        if(colIdx<0) if(!positions.contains(matrix[size-1][0])) positions.add(matrix[size-1][0]);
                        else if(colIdx>(size*size)) if(!positions.contains(matrix[size-1][size-1])) positions.add(matrix[size-1][size-1]);
                        else if(!positions.contains(matrix[size-1][colIdx])) positions.add(matrix[size-1][colIdx]);
                    }else if(colIdx>(size*size)){
                        if(rowIdx<0) if(!positions.contains(matrix[0][size-1])) positions.add(matrix[0][size-1]);
                        else if(rowIdx>(size*size)) if(!positions.contains(matrix[size-1][size-1])) positions.add(matrix[size-1][size-1]);
                        else if(!positions.contains(matrix[rowIdx][size-1])) positions.add(matrix[rowIdx][size-1]); 
                    } else if(!positions.contains(matrix[rowIdx][colIdx])) positions.add(matrix[rowIdx][colIdx]);
                }
            }
        }
        System.out.println("P: "+positions);
        return positions;
    }
    
    private int[][] transform2Matrix(ArrayList<BufferedImage> blockedImage){
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
