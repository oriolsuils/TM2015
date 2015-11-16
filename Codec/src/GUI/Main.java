/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import ZipHandler.ZipHandler;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.util.Timer;
import java.util.TimerTask;

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
                System.out.println("Debug mode: " + parser.getDebug());
                System.out.println("Working with file: " + parser.getInputZip());
            }
        } catch (ParameterException pe){
            System.err.println(pe.getMessage());
            System.err.println("Try --help or -h for help.");
        }
        ZipHandler zHandler = new ZipHandler();
        zHandler.setInputZipName(parser.getInputZip());
        zHandler.setOutputZipName(parser.getOutputZip());
        zHandler.readZip(); 
        zHandler.writeZip();
        /*Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }, null, period);*/
    }
}
