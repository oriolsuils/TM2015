package Encoder;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This is the encoder class
 * @author Oriol i Pol
 */
public class Encoder {
    private ArrayList<BufferedImage> raw_images;
    //private ArrayList<Frame> frames;
    private ArrayList<Frame> compressed_images;
    private ArrayList<Tesela> allTiles;
    private int nTiles;
    private float quality;
    private int gop;
    private int seekRange; 
    private int tileWidth;
    private int tileHeight;

    /**
     * Construct
     * @param raw_images
     * @param nTiles
     * @param quality
     * @param gop
     * @param seekRange 
     */
    public Encoder(ArrayList<BufferedImage> raw_images, int nTiles, float quality, int gop, int seekRange) {
        this.raw_images = raw_images;
        this.nTiles = nTiles;
        this.quality = quality;
        this.gop = gop;
        this.seekRange = seekRange;
        //this.frames = new ArrayList<>();
        this.compressed_images = new ArrayList<>();
        this.allTiles = new ArrayList<>();
        this.tileWidth = this.raw_images.get(0).getWidth()/this.nTiles;
        this.tileHeight = this.raw_images.get(0).getHeight()/this.nTiles;
    }
    
    /**
     * This method is the GOP cicle, and call other methods to
     * take the first image as an I-frame to generate the tiles. The next 
     * images are taken as P-frames.
     */
    public void encode(){
        int gopCount = this.gop;
        int i=0; 
        Frame next = null;
        Frame anterior = null;
        while(i<this.raw_images.size()) {
            anterior = new Frame(this.raw_images.get(i));
            if((i+1) == this.raw_images.size()){
                compressed_images.add(anterior);
            } else {
                next = new Frame(this.raw_images.get(i+1));
                anterior.setTeseles(generateMacroblocks(anterior.getImage()));
                next = findCompatibleBlock(anterior, next.getImage());
                Frame resultant = new Frame(setPFramesColor(anterior.getTeseles(), next.getImage()));
                if(gopCount == this.gop){
                    compressed_images.add(anterior);
                }
                compressed_images.add(resultant);
            }
            gopCount--;
            if(gopCount == 0){
                gopCount = this.gop;
                i++;
            }else{
                for(Tesela t : anterior.getTeseles()) this.allTiles.add(t);
            }
            i++;
        }
        System.out.println("FORAAAA: " + this.allTiles.size());
        System.out.println("IMAGES SIZE: " + this.compressed_images.size());
        this.saveZIP();
    }
    
    /**
     * This method generate the tiles of a given image.
     * @param image 
     * @return teseles
     */
    public ArrayList<Tesela> generateMacroblocks(BufferedImage image){
        ArrayList<Tesela> teseles = new ArrayList<>();
        Tesela t;
        int count = 0;
        for(int y=0; y<image.getHeight(); y+=this.tileHeight){
            for(int x=0; x<image.getWidth(); x+=this.tileWidth){
                t = new Tesela(image.getSubimage(x, y, this.tileWidth, this.tileHeight), count);
                teseles.add(t);
                count++;
            }
        }
        return teseles;
    }

    /**
     * This method search every tile from I-frame in the seekrange P-frame. If
     * it's found and pass the quality, then the method save the coordinates 
     * where the tile was found in P-frame.
     * @param iFrame
     * @param pFrame
     * @return iFrame
     */
    private Frame findCompatibleBlock(Frame iFrame, BufferedImage pFrame) {
        float maxPSNR = Float.MIN_VALUE;
        int xMaxValue = 0, yMaxValue = 0, xMin, xMax, yMin, yMax, idTesela, idX, idY;
        ArrayList<Tesela> teselesResultants = new ArrayList<>();
        for (Tesela t : iFrame.getTeseles()) {
            maxPSNR = Float.MIN_VALUE;
            idTesela = t.getIdOriginal();
            idY = (idTesela%this.nTiles)*this.tileHeight;
            idX = ((int)Math.ceil(idTesela/this.nTiles))*this.tileWidth;
            xMin = ((idX - this.seekRange) < 0) ? 0 : (idX - this.seekRange);
            yMin = ((idY - this.seekRange) < 0) ? 0 : (idY - this.seekRange);
            xMax = (idX + this.seekRange + this.tileHeight) > (this.raw_images.get(0).getHeight()) ? (this.raw_images.get(0).getHeight()) : (idX + this.seekRange + this.tileHeight);
            yMax = (idY + this.seekRange + this.tileWidth) > (this.raw_images.get(0).getWidth()) ? (this.raw_images.get(0).getWidth()) : (idY + this.seekRange + this.tileWidth);
            /*System.out.println("X: " + xMin +" Y:" + yMin);
            System.out.println("X: " + xMax +" Y:" + yMax);
            System.out.println("========================");
            */for(int x=xMin; x<(xMax-this.tileHeight); x++){
                for(int y=yMin; y<(yMax-this.tileWidth); y++){
                    //System.out.println("X: " + x +" Y:" + y);
                    float psnr = calculatePSNR(t, pFrame.getSubimage(y, x, this.tileWidth, this.tileHeight));
                    if(psnr > maxPSNR && psnr >= this.quality){
                        maxPSNR = psnr;
                        xMaxValue = x;
                        yMaxValue = y;
                    }
                }
            }
            if(maxPSNR != Float.MIN_VALUE){
                t.setxCoordDest(xMaxValue);
                t.setyCoordDest(yMaxValue);
            }else{
                t.setxCoordDest(-1);
                t.setyCoordDest(-1);
            }
            //System.out.println("diff:"+maxPSNR+"  "+xMaxValue+"  "+yMaxValue);
            teselesResultants.add(t);
        }
        iFrame.setTeseles(teselesResultants);
        return iFrame;
    }
    
    /**
     * This method compare two tiles. The bigger is the psnr, the similar are 
     * the tiles.
     * @param tesela
     * @param pframe
     * @return psnr
     */
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
    
    /**
     * This method call the meanColorImage for every I-frame tile found in P-frame.
     * @param framesId
     * @param coordId
     * @param teseles
     * @param pFrame 
     */
    private BufferedImage setPFramesColor(ArrayList<Tesela> teseles, BufferedImage pFrame){
        BufferedImage result = pFrame;
        for(Tesela t : teseles){
            int x = t.getxCoordDest();
            int y = t.getyCoordDest();
            if(x != -1 && y != -1){
                Color c = meanColorImage(t.getTesela());
                for(int xCoord=x; xCoord<(x+this.tileHeight); xCoord++){
                    for(int yCoord=y; yCoord<(y+this.tileWidth); yCoord++){
                        result.setRGB(yCoord, xCoord, c.getRGB());
                    }
                }
            }
        }
        return result;
        //this.frames.get(framesId).addpFrame(pFrame);
 /*       
        ImageIcon icon=new ImageIcon(pFrame);
        JFrame frame2=new JFrame();
        frame2.setLayout(new FlowLayout());
        frame2.setSize(300,400);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame2.add(lbl);
        frame2.setVisible(true);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
    }
    
    /**
     * This method calculates the mean colour from a given tile
     * @param im
     * @return Color
     */
    private Color meanColorImage(BufferedImage im){
        Color color;
        int sumR=0, sumG=0, sumB=0, pixelCount=0;
        for(int x=0; x<this.tileWidth; x++){
            for(int y=0; y<this.tileHeight; y++){
                color = new Color(im.getRGB(x, y));
                pixelCount++;
                sumR += color.getRed();
                sumG += color.getGreen();
                sumB += color.getBlue();
            }
        }
        return new Color((sumR/pixelCount),(sumG/pixelCount),(sumB/pixelCount));
    }
    
    /**
     * This method save every compressed image
     */
    private void saveCompressedImages(){
        int count=0;
        for(Frame f : this.compressed_images){
            try {
                ImageIO.write(f.getImage(), "jpeg", new File("src/resources/Compressed/frame"+String.format("%03d",count)+".jpeg"));
                count++;
                for(BufferedImage im : f.getpFrames()){
                    ImageIO.write(im, "jpeg", new File("src/resources/Compressed/frame"+String.format("%03d",count)+".jpeg"));
                    count++;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * This method creates a zip folder with the compressed images and a
     * text file with the coordinates.
     */
    private void saveZIP(){
        new File("src/resources/Compressed").mkdirs();
        this.makeCoordsFile();
        this.saveCompressedImages();
        this.zipFolder("src/resources/Compressed","src/resources/Compressed.zip");
        this.deleteDir(new File("src/resources/Compressed"));
    }
    
    /**
     * This method creates a text file with the tiles coordinates in P-frame
     */
    private void makeCoordsFile(){
        BufferedWriter bw = null;  
        try {
            String name = "src/resources/Compressed/coords.txt";
            bw = new BufferedWriter(new FileWriter(name));
            for (Tesela t : this.allTiles) {
                bw.write(t.getIdOriginal()+" "+t.getxCoordDest()+" "+t.getyCoordDest()+"\n");  
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * This method creates the zip folder
     * @param srcFolder
     * @param destZipFile 
     */
    private void zipFolder(String srcFolder, String destZipFile){
        try {
            ZipOutputStream zip = null;
            FileOutputStream fileWriter = null;
            
            fileWriter = new FileOutputStream(destZipFile);
            zip = new ZipOutputStream(fileWriter);
            
            addFolderToZip("", srcFolder, zip);
            zip.flush();
            zip.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Encoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Encoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * This is a support method
     * @param path
     * @param srcFile
     * @param zip 
     */
    private void addFileToZip(String path, String srcFile, ZipOutputStream zip){
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
          addFolderToZip(path, srcFile, zip);
        } else {
            FileInputStream in = null;
            try {
                byte[] buf = new byte[1024];
                int len;
                in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                } } catch (FileNotFoundException ex) {
                Logger.getLogger(Encoder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Encoder.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Encoder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
  }
   
    /**
     * This is a support method
     * @param path
     * @param srcFolder
     * @param zip 
     */
    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip){
        File folder = new File(srcFolder);
        for (String fileName : folder.list()) {
          if (path.equals("")) {
            addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
          } else {
            addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
          }
        }
    }
   
    /**
     * This method deletes the directory
     * @param dir
     * @return 
     */
    private boolean deleteDir(File dir) {
      if (dir.isDirectory()) {
         String[] children = dir.list();
         for (int i = 0; i < children.length; i++) {
            boolean success = deleteDir
            (new File(dir, children[i]));
            if (!success) {
               return false;
            }
         }
      }
      return dir.delete();
  }
}
