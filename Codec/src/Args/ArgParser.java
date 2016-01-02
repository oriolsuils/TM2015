/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Args;

import com.beust.jcommander.Parameter;

/**
 *
 * @author mat.aules
 */
public class ArgParser {
    @Parameter(names = {"--help", "-h"},
            description = "Prints this help message", 
            help=true)
    public boolean help;
    
    @Parameter(names = "--debug",
            description = "Enters debug mode")
    private boolean debug = false;
    
    @Parameter(names = {"--input", "-i"},
            required = true,
            description = "Zip file with the input images",
            validateWith = FileExists.class)
    private String inputZip;
    
    @Parameter(names = {"--output", "-o"},
            description = "Name of the file with the output images")
    private String outputZip = "filtered_images.zip";
    
    @Parameter(names = {"--encode", "-e"},
            description = " ")
    private boolean encode = false;
    
    @Parameter(names = {"--decode", "-d"},
            description = " ")
    private boolean decode = false;
    
    @Parameter(names = "--fps",
            description = "The number of frame per second")
    private int fps = 40;
    
    @Parameter(names = "--binarization",
            description = "The binarization filter with the threshold")
    private float binarization = (float) -1.0;
    
    @Parameter(names = "--negative",
            description = "The negative filter")
    private boolean negative = false;
    
    @Parameter(names = "--averaging",
            description = "The averaging filter")
    private int averaging = -1;
    
    @Parameter(names = "--nTiles",
            description = "The number of tiles to divide the image")
    private int nTiles = 10;
    
    @Parameter(names = "--seekRange",
            description = "The averaging filter")
    private int seekRange = 5;
    
    @Parameter(names = "--GOP",
            description = "The averaging filter")
    private int gop = 10;
    
    @Parameter(names = "--quality",
            description = "The averaging filter")
    private float quality = 30;
    
    @Parameter(names = {"--batch", "-b"},
            description = " ")
    private boolean batch = false;

    public String getInputZip() {
        return inputZip;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getOutputZip() {
        return outputZip;
    }

    public boolean isEncode() {
        return encode;
    }

    public boolean isDecode() {
        return decode;
    }

    public int getFps() {
        return fps;
    }

    public float getBinarization() {
        return binarization;
    }

    public boolean isNegative() {
        return negative;
    }

    public float getAveraging() {
        return averaging;
    }

    public int getnTiles() {
        return nTiles;
    }

    public int getSeekRange() {
        return seekRange;
    }

    public int getGop() {
        return gop;
    }

    public float getQuality() {
        return quality;
    }

    public boolean isBatch() {
        return batch;
    }
    
}
