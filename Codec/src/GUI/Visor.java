package GUI;

import ZipHandler.ZipHandler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Visor extends JFrame {

    private ArrayList<BufferedImage> images;
    private int fps;
    private Timer timer;
    private int count;
    private JPanel contentPane;
    private JLabel lblImageHolder;
    private boolean debug;

    public Visor(ArrayList<BufferedImage> images, int fps){
        this.images = images;
        this.fps = fps;
        this.timer = new Timer();
        this.count = 0;
    }

    public Visor(ArrayList<BufferedImage> images){
        this.images = images;
        this.timer = new Timer();
        this.fps = 40;
        this.count = 0;
    }

    public Visor(int fps, boolean debug) {
        this.fps = fps;
        this.images = new ArrayList<>();
        this.timer = new Timer();
        this.count = 0;
        this.debug = debug;
    }
    
    private void initComponents() {
        Dimension imDimension = new Dimension(this.images.get(0).getWidth()+20, this.images.get(0).getHeight()+45);
        this.setSize(imDimension);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.contentPane = new JPanel();
        this.contentPane.setSize(imDimension);
        this.lblImageHolder = new JLabel();
        this.lblImageHolder.setSize(imDimension);
        this.contentPane.add(lblImageHolder, BorderLayout.CENTER);
        this.setContentPane(this.contentPane);
        this.setVisible(true);

    }

    public void play(boolean batch) {
        if(debug) System.out.println("Begins the display of images");
        if(!batch) initComponents();
        //FPSCounter fpsCounter = new FPSCounter();
        ChangeImage ci = new ChangeImage(this.count, this.images.size(), this.debug);
        this.timer.scheduleAtFixedRate(ci, 0, (long) 1000./(long) fps);
        while(true){
            if(!batch) {
                this.contentPane.setSize(this.images.get(0).getWidth()+20, this.images.get(0).getHeight()+45);
                this.lblImageHolder.setIcon(new ImageIcon((Image) this.images.get(ci.getCount())));
            }
        }
    }
    
    public void readAndSaveImages(String inputZip, String outputZip, float binarization, boolean negative, int averaging) {
        ZipHandler zHandler = new ZipHandler(inputZip, outputZip, debug);
        this.images = zHandler.readZip(binarization, negative, averaging); 
        zHandler.writeZip(this.images);
    }
}
