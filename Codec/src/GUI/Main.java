/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Args.ArgParser;
import Encoder.Decoder;
import Encoder.Encoder;
import Encoder.Frame;
import ZipHandler.ZipHandler;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class is the Main class
 * @author Oriol i Pol
 */
public class Main {
    /**
     * Main
     * @param args 
     */    
    public static void main(String[] args){
        ArgParser parser = new ArgParser();
        JCommander jComm = null;
        try {
            jComm = new JCommander(parser, args);
            jComm.setProgramName("TM1516.jar");
            if(parser.help) {
                jComm.usage();
            } else {
                if(parser.isDebug()) System.out.println("Debug mode: " + parser.isDebug());
                if(parser.isDebug()) System.out.println("Working with file: " + parser.getInputZip());
                ZipHandler zipHandler = new ZipHandler(parser.getInputZip(), parser.getOutputZip(), parser.isDebug());
                ArrayList<Frame> raw_images = zipHandler.readAndSaveImages(parser.getInputZip(), parser.getOutputZip(), parser.getBinarization(), parser.isNegative(), (int) parser.getAveraging());
                Visor v = new Visor(raw_images, parser.getFps(), parser.isDebug(), parser.isBatch());
                //new Thread(v).start();
                raw_images = zipHandler.readZip(parser.getBinarization(), parser.isNegative(), (int) parser.getAveraging());
                if(parser.isEncode() && parser.isDecode()){
                    Encoder encoder = new Encoder(raw_images, parser.getnTiles(), parser.getQuality(), parser.getGop(), parser.getSeekRange());
                    encoder.encode();
                    Decoder decoder = new Decoder(parser.getGop(), parser.getnTiles());
                    decoder.decode();
                }
                if(parser.isEncode()){
                    Encoder encoder = new Encoder(raw_images, parser.getnTiles(), parser.getQuality(), parser.getGop(), parser.getSeekRange());
                    encoder.encode();
                }
                if(parser.isDecode()){
                    Decoder decoder = new Decoder(parser.getGop(), parser.getnTiles());
                    decoder.decode();
                }
            } 
        } catch (ParameterException pe){
            System.err.println(pe.getMessage());
            System.err.println("Try --help or -h for help.");
        }
    }
}
