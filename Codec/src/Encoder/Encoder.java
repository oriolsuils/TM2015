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
    private ArrayList<Frame> frames;
//private ArrayList<BufferedImage> cod_images;
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
        this.frames = new ArrayList<>();
    }
    
    public void encode(){
        int gopCount = this.gop;
        Frame f = null;
        for (BufferedImage frame : raw_images) {
            if(gopCount == this.gop){ //Es pren la referencia (I-Frame) i es generen les teseles
                f = new Frame(frame);
                f.setTeseles(generateMacroblocks(frame));
            }else{
                findCompatibleBlock(f, frame);
                /*for(Tesela t : findCompatibleBlock){
                    System.out.println("ID: "+t.getIdOriginal()+" X: " +t.getxCoordDest(t.getIdOriginal())+" Y: " +t.getyCoordDest(t.getIdOriginal()));
                }*/
            }
            gopCount--;
            if(gopCount == 0){
                gopCount = this.gop;
                frames.add(f);
            }
        }
    }
    
    public ArrayList<Tesela> generateMacroblocks(BufferedImage image){
        ArrayList<Tesela> teseles = new ArrayList<>();
        int tileWidth = image.getWidth()/this.nTiles;
        int tileHeight = image.getHeight()/this.nTiles;
        Tesela t;
        int count = 0;
        for(int y=0; y<image.getHeight(); y+=tileHeight){
            for(int x=0; x<image.getWidth(); x+=tileWidth){
                t = new Tesela(image.getSubimage(x, y, tileWidth, tileHeight), count);
                teseles.add(t);
                count++;
            }
        }
        return teseles;
    }

    private void findCompatibleBlock(Frame iFrame, BufferedImage pFrame) {
        float maxPSNR = Float.MIN_VALUE;
        int xMaxValue = 0, yMaxValue = 0, xMin, xMax, yMin, yMax, idTesela, idX, idY;
        int tileWidth = pFrame.getWidth()/this.nTiles;
        int tileHeight = pFrame.getHeight()/this.nTiles;
        ArrayList<Tesela> teselesResultants = new ArrayList<>();
        for (Tesela t : iFrame.getTeseles()) {
            idTesela = t.getIdOriginal();
            System.out.println("TESELA: " + idTesela);
            if(((int)(Math.ceil(idTesela/this.nTiles)) == 0)){
                //System.out.println("["+(idTesela%this.nTiles)+","+((int)Math.ceil(idTesela/this.nTiles))+"]");
                idX = (idTesela%this.nTiles);
                idY = ((int)Math.ceil(idTesela/this.nTiles));
            } else{
                //System.out.println("["+((int)Math.ceil(idTesela/this.nTiles))+","+(idTesela%this.nTiles)+"]");
                idX = ((int)Math.ceil(idTesela/this.nTiles));
                idY = (idTesela%this.nTiles);
            }
            xMin = (((idX - this.seekRange) * tileWidth) < 0) ? 0 : ((idX - (this.seekRange)) * tileWidth);
            yMin = (((idY - this.seekRange) * tileHeight) < 0) ? 0 : (idY - (this.seekRange) * tileHeight);
            xMax = (((idX + (this.seekRange+1)) * tileWidth) > (this.nTiles * tileWidth)) ? (this.nTiles * tileWidth) : ((idX + (this.seekRange+1)) * tileWidth);
            yMax = (((idY + (this.seekRange+1)) * tileHeight) > (this.nTiles * tileHeight)) ? (this.nTiles * tileHeight) : ((idY + (this.seekRange+1)) * tileHeight);
            int count = 0;
            for(int x=xMin; x<(xMax-tileWidth); x++){
                for(int y=yMin; y<(yMax-tileHeight); y++){
                    float psnr = calculatePSNR(t, pFrame.getSubimage(x, y, tileWidth, tileHeight));
                    if(psnr > maxPSNR && psnr >= this.quality){
                        maxPSNR = psnr;
                        xMaxValue = x;
                        yMaxValue = y;
                    }
                    count++;
                }
            }
            System.out.println("COUNT: " + count);
            if(maxPSNR != Float.MIN_VALUE){
                t.addxCoordDest(xMaxValue);
                t.addyCoordDest(yMaxValue);
            }else{
                t.addxCoordDest(-1);
                t.addyCoordDest(-1);
            }
            teselesResultants.add(t);
        }
        iFrame.setTeseles(teselesResultants);
    }
    
    private float calculatePSNR(Tesela tesela, BufferedImage pframe){
        float noise = 0, mse = 0, psnr = 0;
        BufferedImage iFrame = tesela.getTesela();
        for (int i=0; i<iFrame.getHeight(); i++) {
            for (int j=0; j<iFrame.getWidth(); j++) {
                Color iframe_rgb = new Color(iFrame.getRGB(j, i));
                Color pframe_rgb = new Color(pframe.getRGB(j, i));
                noise += Math.pow(pframe_rgb.getRed() - iframe_rgb.getRed(), 2);
                noise += Math.pow(pframe_rgb.getGreen()- iframe_rgb.getGreen(), 2);
                noise += Math.pow(pframe_rgb.getBlue() - iframe_rgb.getBlue(), 2);
            }
          }
        mse = noise/(iFrame.getHeight()*iFrame.getWidth()*3);
        psnr = (float) (10 * Math.log10((255 * 255) / mse));
        return psnr;
    }
}
