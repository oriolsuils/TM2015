/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 *
 * @author mat.aules
 */
public class L2_Parametros {
    public static void main(String[] args) {
        ArgParser parser = new ArgParser();
        JCommander jComm = null;
        try {
            jComm = new JCommander(parser, args);
            jComm.setProgramName("TM1516.jar");
            if(parser.help) {
                jComm.usage();
            } else {
                System.out.println("Debug mode: " + parser.getDebug());
                System.out.println("Verbose log level: " + parser.getVerbose());
                System.out.println("Working with file: " + parser.getInputZip());
                System.out.println("Input: " + parser.getNumParams());
            }
        } catch (ParameterException pe){
            System.err.println(pe.getMessage());
            System.err.println("Try --help or -h for help.");
        }
    }
}
