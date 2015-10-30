/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import com.beust.jcommander.Parameter;

/**
 *
 * @author mat.aules
 */
public class ArgParser {
    @Parameter(names = {"--help", "-h"},
            description = "Prints this help message", help=true)
    public boolean help;
    
    @Parameter(names = "--debug",
            description = "Enters debug mode")
    private boolean debug = false;
    
    @Parameter(names = "--verbose",
            description = "Level of verbosity")
    private int verbose = 1;
    
    @Parameter(names = {"--inputZip", "-i"},
            required = true,
            description = "Zip file with the input images",
            validateWith = FileExists.class)
    private String inputZip;
    
    @Parameter(names = {"--numericParameter", "-np"},
            required = true,
            description = "A number used as a parameter",
            validateWith = NumInRange.class)
    private int numParam = 0;
    
    public static enum numParamLimits { 
        MAX(1000), MIN(0);
        private int value;
        
        private numParamLimits(int n){
            value = n;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public boolean getDebug() {
        return debug;
    }

    public int getVerbose() {
        return verbose;
    }

    public String getInputZip() {
        return inputZip;
    }
    
    public int getNumParams() {
        return numParam;
    }
}
