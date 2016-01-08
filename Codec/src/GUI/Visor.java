package GUI;

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

/**
 * This is the visor class
 * @author Oriol i Pol
 */
public class Visor extends JFrame implements Runnable{

    private JPanel contentPane;
    private JLabel lblImageHolder;
    
    private ArrayList<BufferedImage> images;
    private int fps;
    private Timer timer;
    private int count;
    private boolean debug;
    private boolean batch;
    /**
     * Construct
     * @param images
     * @param fps
     * @param debug
     * @param batch 
     */
    public Visor(ArrayList<BufferedImage> images, int fps, boolean debug, boolean batch) {
        this.fps = fps;
        this.images = images;
        this.timer = new Timer();
        this.count = 0;
        this.debug = debug;
        this.batch = batch;
    }
    /**
     * This method initializate the components
     */
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
    /**
     * This is the Play method
     * @param batch 
     */
    public void play(boolean batch) {
        if(debug) System.out.println("Begins the display of images");
        if(!batch) initComponents();
        ChangeImage ci = new ChangeImage(this.count, this.images.size(), this.debug);
        this.timer.scheduleAtFixedRate(ci, 0, (long) 1000./(long) fps);
        while(true){
            if(!batch) {
                this.contentPane.setSize(this.images.get(0).getWidth()+20, this.images.get(0).getHeight()+45);
                this.lblImageHolder.setIcon(new ImageIcon((Image) this.images.get(ci.getCount())));
            }
        }
    }
    /**
     * Run
     */
    @Override
    public void run() {
        this.play(this.batch);
    }
}
