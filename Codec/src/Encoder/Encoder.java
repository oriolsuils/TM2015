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
        ArrayList<Tesela> iFrame = null;
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
    
    public ArrayList<Tesela> generateMacroblocks(BufferedImage image){
        ArrayList<Tesela> teseles = new ArrayList<>();
        int tileWidth = image.getWidth()/this.nTiles;
        int tileHeight = image.getHeight()/this.nTiles;
        Tesela t;
        int count = 0;
        for(int x=0; x<image.getWidth(); x+=tileWidth){
            for(int y=0; y<image.getHeight(); y+=tileHeight){
                t = new Tesela(image.getSubimage(x, y, tileWidth, tileHeight), count);
                teseles.add(t);
                count++;
            }
        }
        return teseles;
    }

    private void findCompatibleBlock(BufferedImage pFrame, ArrayList<Tesela> iFrame) {
        this.seekRange = 3;
        for (Tesela t : iFrame) {
            float maxPSNR = Float.MIN_VALUE;
            int xMaxValue = 0, yMaxValue = 0;
            int tileWidth = pFrame.getWidth()/this.nTiles;
            int tileHeight = pFrame.getHeight()/this.nTiles;
            int xOriginal = (int) Math.ceil(t.getIdOriginal()/this.nTiles);            
            int yOriginal = t.getIdOriginal()%this.nTiles;
            int xMin = xOriginal - (this.seekRange); 
            int xMax = xOriginal + ((this.seekRange+1)); 
            int yMin = yOriginal - (this.seekRange); 
            int yMax = yOriginal + ((this.seekRange+1)); 
            if(xMin < 0) xMin = 0;
            if(yMin < 0) yMin = 0;
            if(xMax > this.nTiles) xMax = this.nTiles;
            if(yMax > this.nTiles) yMax = this.nTiles;
            xMin *= tileWidth;
            xMax *= tileWidth;
            yMin *= tileHeight;
            yMax *= tileHeight;
            for(int x=xMin; x<(xMax-tileWidth); x++){
                for(int y=yMin; y<(yMax-tileHeight); y++){
                    float psnr = calculatePSNR(t, pFrame.getSubimage(x, y, tileWidth, tileHeight));
                    if(psnr > maxPSNR){
                        maxPSNR = psnr;
                        xMaxValue = x;
                        yMaxValue = y;
                    }
                }
            }
            /*if(maxPSNR >= this.quality){*/
                t.addxCoordDest(xMaxValue);
                t.addyCoordDest(yMaxValue);
                System.out.println("ID: " + t.getIdOriginal() + " X: " + t.getxCoordDest(0)+ " Y: " + t.getyCoordDest(0));
            /*}else{
                t.addxCoordDest(-1);
                t.addyCoordDest(-1);
            }*/
            /*
            BufferedImage img=t.getTesela();
            ImageIcon icon=new ImageIcon(img);
            JFrame frame=new JFrame();
            frame.setSize(200,300);
            JLabel lbl=new JLabel();
            lbl.setIcon(icon);
            frame.add(lbl);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            BufferedImage img2=pFrame.getSubimage(t.getxCoordDest(0), t.getyCoordDest(0), tileWidth, tileHeight);
            ImageIcon icon2=new ImageIcon(img2);
            JFrame frame2=new JFrame();
            frame2.setSize(200,300);
            JLabel lbl2=new JLabel();
            lbl2.setIcon(icon2);
            frame2.add(lbl2);
            frame2.setVisible(true);
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
        }
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
