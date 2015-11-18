/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Args.ArgParser;
import ZipHandler.ZipHandler;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 *
 * @author Oriol
 */
public class Main {
        
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
                Visor v = new Visor(parser.getFps(), parser.isDebug());
                v.readAndSaveImages(parser.getInputZip(), parser.getOutputZip(), parser.getBinarization(), parser.isNegative(), (int) parser.getAveraging());
                v.play(parser.isBatch());
            } 
        } catch (ParameterException pe){
            System.err.println(pe.getMessage());
            System.err.println("Try --help or -h for help.");
        }
    }
}
