package ZipHandler;

import Encoder.Encoder;
import GUI.Filters;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Oriol
 */
public class ZipHandler {
    private ZipOutputStream out;
    private int count;
    private String inputZipName;
    private String outputZipName;
    private boolean debug;

    public ZipHandler(String inputZipName, String outputZipName, boolean debug) {
        this.count = 0;
        this.inputZipName = inputZipName;
        this.outputZipName = outputZipName;
        this.debug = debug;
    }
        
    public ArrayList<BufferedImage> readZip(float binarization, boolean negative, int averaging) {
        if(this.debug) System.out.print("Reading the ZIP...");
        ArrayList<BufferedImage> images = new ArrayList<>();
        ZipFile zf = null;
        try{
            zf = new ZipFile(new File(this.inputZipName));
        } catch(FileNotFoundException fnfe){
            System.err.println("ERROR: Does not exist the input file");
        } catch (IOException ex) {
            System.err.println("ERROR: There has been a problem while connecting to the input file");
        }
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(!validateImageExtension(entry.getName())) {
                System.err.println("ERROR: There are at least one file in the ZIP that is not a JPEG, PNG, GIF or BMP file");
                System.exit(0);
            }
            InputStream is = null;
            try {
                is = zf.getInputStream(entry);
            } catch (IOException ex) {
                System.err.println("ERROR: There has been a problem while initializing the output stream");
            }
            BufferedImage image = null;
            try {
                image = ImageIO.read(is);
            } catch (IOException ex) {
                System.err.println("ERROR: There has been a problem while reading the image");
            }
            if(binarization > 0.0) image = Filters.binarization(image, binarization);
            if(negative) image = Filters.negative(image);
            if(averaging > 0) image = Filters.averaging(image, averaging);
            images.add(image);
        }
        try {
            zf.close();
            if(this.debug) {
                System.out.println(" Reading completed");
                if(binarization > 0.0) System.out.println("Binarization filter with " + binarization + " of threshold applied");
                if(negative) System.out.println("Negative filter applied");
                if(averaging > 0) System.out.println("Averaging filter with " + averaging + "x" + averaging + " matrix applied");
            }
        } catch (IOException ex) {
            System.err.println("ERROR: There has been a problem while closing the ZipFile");
        }
        return images;
    }
    
    private void initZip() {
        try {
            if(out == null) out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.outputZipName, true)));
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: There has been a problem while initializing the output ZIP file");
            this.closeStream();
        }
    }
    
    private void writeZip(ArrayList<BufferedImage> images) {
        if(this.debug) System.out.print("Writing the ZIP...");
        initZip();
        for (BufferedImage image : images) {
            ZipEntry entry = new ZipEntry("image"+count+".jpg");
            try {
                out.putNextEntry(entry);
                ImageIO.write(image, "jpg", out);
            } catch (IOException ex) {
                System.out.println("ERROR: There has been a problem while writing the image to the output file");
                this.closeStream();
            }
            this.count++;
        }
        if(this.debug) System.out.println(" Writing completed");
        this.closeStream();
    } 

    private boolean validateImageExtension(String name) {
        String substring = name.substring(name.lastIndexOf(".")+1,name.length());
        return (substring.equalsIgnoreCase("png") || substring.equalsIgnoreCase("jpeg") || substring.equalsIgnoreCase("jpg") || substring.equalsIgnoreCase("gif") || substring.equalsIgnoreCase("bmp"));
    }
    
    public String getInputZipName() {
        return inputZipName;
    }

    public String getOutputZipName() {
        return outputZipName;
    }

    public void setInputZipName(String inputZipName) {
        this.inputZipName = inputZipName;
    }

    public void setOutputZipName(String outputZipName) {
        this.outputZipName = outputZipName;
    }

    private void closeStream() {
        try {
            out.flush();
            out.close();
        } catch (IOException ex) {
            System.out.println("ERROR: There has been a problem while closing the connection");
        }
    }

    public ArrayList<BufferedImage> readAndSaveImages(String inputZip, String outputZip, float binarization, boolean negative, int averaging) {
        ArrayList<BufferedImage> images = this.readZip(binarization, negative, averaging); 
        this.writeZip(images);
        return images;
    }
    
}
