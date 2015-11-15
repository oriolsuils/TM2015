package ZipHandler;

import GUI.Visor;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Oriol
 */
public class ZipHandler {
    private ZipOutputStream out;
    private ArrayList<BufferedImage> images;
    private int count;
    private String inputZipName;
    private String outputZipName;

    public ZipHandler() {
        this.images = new ArrayList<>();
        this.count = 0;
        this.inputZipName = "";
        this.outputZipName = "";
    }
        
    public void readZip() {
        ZipFile zf = null;
        try{
            zf = new ZipFile(new File(this.inputZipName));
        } catch(FileNotFoundException fnfe){
            System.out.println("ERROR: No existeix el fitxer d'entrada");
        } catch (IOException ex) {
            System.out.println("ERROR: Hi ha hagut algun problema en la connexio al llegir el fitxer");
        }
        System.out.println(zf.size());
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream is = null;
            try {
                is = zf.getInputStream(entry);
            } catch (IOException ex) {
                System.out.println("ERROR: Hi ha hagut algun problema en la connexio al inicialitzar el canal d'entrada");
            }
            BufferedImage image = null;
            try {
                image = ImageIO.read(is);
            } catch (IOException ex) {
                System.out.println("ERROR: Hi ha hagut algun problema en la connexio al llegir la imatge");
            }
            images.add(image);
        }
        try {
            zf.close();
        } catch (IOException ex) {
            System.out.println("ERROR: Hi ha hagut algun problema al tancar el ZipFile");
        }
        
    }
    
    private void initZip() {
        try {
            if(out == null) out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.outputZipName, true)));
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: Hi ha hagut algun problema al inicialitzar el fitxer ZIP de sortida");
        }
    }
    
    public void writeZip() {
        initZip();
        for (BufferedImage image : images) {
            ZipEntry entry = new ZipEntry("cub"+count+".jpg");
            try {
                out.putNextEntry(entry);
                ImageIO.write(image, "jpg", out);
            } catch (IOException ex) {
                System.out.println("ERROR: Hi ha hagut un problema de connexio al escriure la imatge");
                this.closeStream();
            }
            this.count++;
        }
        this.closeStream();
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
            out = null;
        } catch (IOException ex) {
            System.out.println("ERROR: Hi ha hagut un problema al tancar la connexio");
        }
    }
    
}
