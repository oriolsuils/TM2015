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
 *
 * @author Oriol
 */
public class Encoder {
    private ArrayList<BufferedImage> raw_images;
    private ArrayList<Frame> frames;
    private int nTiles;
    private float quality;
    private int gop;
    private int seekRange; 
    private int tileWidth;
    private int tileHeight;

    public Encoder(ArrayList<BufferedImage> raw_images, int nTiles, float quality, int gop, int seekRange) {
        this.raw_images = raw_images;
        this.nTiles = nTiles;
        this.quality = quality;
        this.gop = gop;
        this.seekRange = seekRange;
        this.frames = new ArrayList<>();
        this.tileWidth = this.raw_images.get(0).getWidth()/this.nTiles;
        this.tileHeight = this.raw_images.get(0).getHeight()/this.nTiles;
    }
    
    public void encode(){
        int gopCount = this.gop;
        int count = 0;
        Frame f = null;
        for (BufferedImage frame : raw_images) {
            if(gopCount == this.gop){ //Es pren la referencia (I-Frame) i es generen les teseles
                f = new Frame(frame);
                f.setTeseles(generateMacroblocks(frame));
                frames.add(f);
            }else{
                f = findCompatibleBlock(f, frame);
                setPFramesColor(count, ((this.gop-gopCount)-1), f.getTeseles(), frame);
            }
            gopCount--;
            if(gopCount == 0){
                gopCount = this.gop;
                count++;
                this.saveZIP();
            }
        }
    }
    
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

    private Frame findCompatibleBlock(Frame iFrame, BufferedImage pFrame) {
        float maxPSNR = Float.MIN_VALUE;
        int xMaxValue = 0, yMaxValue = 0, xMin, xMax, yMin, yMax, idTesela, idX, idY;
        ArrayList<Tesela> teselesResultants = new ArrayList<>();
        for (Tesela t : iFrame.getTeseles()) {
            //System.out.println("TESELA: " + t.getIdOriginal());
            maxPSNR = Float.MIN_VALUE;
            idTesela = t.getIdOriginal();
            if(((int)(Math.ceil(idTesela/this.nTiles)) == 0)){
                idY = (idTesela%this.nTiles);
                idX = ((int)Math.ceil(idTesela/this.nTiles));
            } else{
                idY = ((int)Math.ceil(idTesela/this.nTiles));
                idX = (idTesela%this.nTiles);
            }
            xMin = (((idX - this.seekRange) * this.tileHeight) < 0) ? 0 : ((idX - this.seekRange) * this.tileHeight);
            yMin = (((idY - this.seekRange) * this.tileWidth) < 0) ? 0 : ((idY - this.seekRange) * this.tileWidth);
            xMax = (((idX + (this.seekRange+1)) * this.tileHeight) > (this.nTiles * this.tileHeight)) ? (this.nTiles * this.tileHeight) : ((idX + (this.seekRange+1)) * this.tileHeight);
            yMax = (((idY + (this.seekRange+1)) * this.tileWidth) > (this.nTiles * this.tileWidth)) ? (this.nTiles * this.tileWidth) : ((idY + (this.seekRange+1)) * this.tileWidth);
            for(int x=xMin; x<(xMax-this.tileHeight); x++){
                for(int y=yMin; y<(yMax-this.tileWidth); y++){
                    float psnr = calculatePSNR(t, pFrame.getSubimage(y, x, this.tileWidth, this.tileHeight));
                    if(psnr > maxPSNR && psnr >= this.quality){
                        maxPSNR = psnr;
                        xMaxValue = x;
                        yMaxValue = y;
                    }
                }
            }
            if(maxPSNR != Float.MIN_VALUE){
                t.addxCoordDest(xMaxValue);
                t.addyCoordDest(yMaxValue);
            }else{
                t.addxCoordDest(-1);
                t.addyCoordDest(-1);
            }
            //System.out.println("diff:"+maxPSNR+"  "+xMaxValue+"  "+yMaxValue);
            teselesResultants.add(t);
        }
        iFrame.setTeseles(teselesResultants);
        return iFrame;
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
    
    private void setPFramesColor(int framesId, int coordId, ArrayList<Tesela> teseles, BufferedImage pFrame){
        for(Tesela t : teseles){
            int x = t.getxCoordDest(coordId);
            int y = t.getyCoordDest(coordId);
            if(x != -1 && y != -1){
                Color c = meanColorImage(t.getTesela());
                for(int xCoord=x; xCoord<(x+this.tileHeight); xCoord++){
                    for(int yCoord=y; yCoord<(y+this.tileWidth); yCoord++){
                        pFrame.setRGB(yCoord, xCoord, c.getRGB());
                    }
                }
            }
        }
        this.frames.get(framesId).addpFrame(pFrame);
        
        ImageIcon icon=new ImageIcon(pFrame);
        JFrame frame2=new JFrame();
        frame2.setLayout(new FlowLayout());
        frame2.setSize(300,400);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame2.add(lbl);
        frame2.setVisible(true);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
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
    
    private void saveCompressedImages(){
        int count=0;
        for(Frame f : this.frames){
            try {
                ImageIO.write(f.getImage(), "jpeg", new File("src/resources/Compressed/iframe"+count+".jpeg"));
                count++;
                for(BufferedImage im : f.getpFrames()){
                    ImageIO.write(im, "jpeg", new File("src/resources/Compressed/pframe"+count+".jpeg"));
                    count++;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
        
    private void saveZIP(){
        new File("src/resources/Compressed").mkdir();
        this.makeCoordsFile();
        this.saveCompressedImages();
        this.zipFolder("src/resources/Compressed","src/resources/Compressed.zip");
        this.deleteDir(new File("src/resources/Compressed"));
    }
    
    private void makeCoordsFile(){
        BufferedWriter bw = null;  
        try {
            String name = "src/resources/Compressed/coords.txt";
            bw = new BufferedWriter(new FileWriter(name));
            for (Frame frame : this.frames) {
                for (int i=0; i<frame.getpFrames().size(); i++) {
                    for(int j=0; j<frame.getTeseles().size(); j++){
                        ArrayList<Tesela> teseles = frame.getTeseles();
                        bw.write(j+" "+teseles.get(j).getxCoordDest(i)+" "+teseles.get(j).getyCoordDest(i)+"\n");  
                    }
                }
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
