package Encoder;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Oriol i Pol
 */
public class Decoder {
    private ArrayList<Integer> ids;
    private ArrayList<Integer> xCoords;
    private ArrayList<Integer> yCoords;
    private ArrayList<BufferedImage> imatges;
    private int gop;
    private int tileWidth;
    private int tileHeight;
    private int nTiles;

    public Decoder(int gop, int nTiles) {
        this.gop = gop;
        this.nTiles = nTiles;
        this.ids = new ArrayList<>();
        this.xCoords = new ArrayList<>();
        this.yCoords = new ArrayList<>();
        this.imatges = new ArrayList<>();
        
    }        
    
    public void decode(){
        this.readZip();
        this.iterateImages();
    }
    
    private void readZip(){
        try {
            File f = new File("src/resources/Compressed.zip");
            ZipFile z = new ZipFile(f);
            Enumeration <?extends ZipEntry> entries = z.entries();
            
            while(entries.hasMoreElements()){
                ZipEntry entry=entries.nextElement();
                String name = entry.getName();
                if(name.equalsIgnoreCase("Compressed/coords.txt")){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(z.getInputStream(entry)));
                    String line;
                    while ((line = reader.readLine())!=null){
                        String[] parts = line.split(" ");
                        ids.add(Integer.parseInt(parts[0]));
                        xCoords.add(Integer.parseInt(parts[1]));
                        yCoords.add(Integer.parseInt(parts[2]));
                    }
                    reader.close();
                } else {
                    BufferedImage im= ImageIO.read(z.getInputStream(entry));
                    this.imatges.add(im);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void iterateImages(){
        int gopCount=this.gop;
        this.tileWidth = this.imatges.get(0).getWidth()/nTiles;
        this.tileHeight = this.imatges.get(0).getHeight()/nTiles;
        BufferedImage iframe = null;
        for (BufferedImage frame : this.imatges) {
            if(gopCount == this.gop){
                iframe = frame;
            }else{
                this.buildPFrames(iframe, frame);
            }
            gopCount--;
            if(gopCount == -1){
                gopCount = this.gop;
            }
        }
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

    private void buildPFrames(BufferedImage iframe, BufferedImage pframe) {
        ArrayList<Tesela> tiles = generateMacroblocks(iframe);
        for(int i=0; i<ids.size(); i++){
            Tesela t = tiles.get(ids.get(i));
            Integer x = xCoords.get(i);
            Integer y = yCoords.get(i);
            if(x != -1 && y != -1){
                for(int xCoord=0; xCoord<(this.tileHeight); x++){
                    for(int yCoord=0; yCoord<(this.tileWidth); y++){
                        int rgb = t.getTesela().getRGB(yCoord, xCoord);
                        pframe.setRGB(yCoord+y, xCoord+x, rgb);
                    }
                }
            }
        }
        /*ImageIcon icon=new ImageIcon(pframe);
        JFrame frame2=new JFrame();
        frame2.setLayout(new FlowLayout());
        frame2.setSize(300,400);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame2.add(lbl);
        frame2.setVisible(true);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
    }
}
