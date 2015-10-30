/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author mat.aules
 */
public class L1_ModificarImagen {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedImage image;
        try{
            String path = "./cubo00.png";
            FileInputStream fis = new FileInputStream(path);
            image = ImageIO.read(fis);
            fis.close();
            if(image != null){
                new Visor(modificarImagen2(image)).setVisible(true);
            }
        }catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static BufferedImage modificarImagen1(BufferedImage image){
        Color pixelBlanc = Color.WHITE;
        image.setRGB(20, 10, pixelBlanc.getRGB());
        return image;
    }
    
    private static BufferedImage modificarImagen2(BufferedImage image){
        int pixelInt;
        int r, g, b;
        for(int i=0; i < image.getWidth(); i++){
            for(int j=0; j < image.getHeight(); j++){
                pixelInt = image.getRGB(i, j);
                r = new Color(pixelInt).getRed();
                g = 0;
                b = 0;
                image.setRGB(i, j, new Color(r,g,b).getRGB());
            }
        }
        return image;
    }
    
    private static BufferedImage modificarImagen3(BufferedImage image){
        WritableRaster bitmap = (WritableRaster) image.getData();
        WritableRaster tesela = bitmap.createWritableChild(50, 100, 60, 60, 0, 0, null);
        
        BufferedImage new_image = new BufferedImage(image.getColorModel(),
        tesela, image.isAlphaPremultiplied(), null);
        
        return new_image;
    }
}
