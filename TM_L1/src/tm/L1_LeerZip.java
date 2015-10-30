/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;

/**
 *
 * @author mat.aules
 */
public class L1_LeerZip {
    public static void main(String[] args) {
        ZipFile zf = null;
        try{
            File f = new File("./Cubo.zip");
            zf = new ZipFile(f);
            
            System.out.println(zf.size());
        
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                InputStream is = zf.getInputStream(entry);
                BufferedImage image = ImageIO.read(is);
                System.out.println(entry.getName().substring(entry.getName().lastIndexOf(".")+1));
                new Visor(image).setVisible(true);
            }
            zf.close();
        } catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
