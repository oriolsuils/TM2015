/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import java.io.File;

/**
 * This class checks if a file exists
 * @author Oriol i Pol
 */
public class FileExists implements IParameterValidator{
    /**
     * This overrided method validates the file
     * @param name
     * @param value 
     */
    @Override
    public void validate(String name, String value) {
        File f = new File(value);
        
        if(!f.exists() || f.isDirectory()) {
            throw new ParameterException("Input zip file is not a valid file.");
        }
    }
    
}
