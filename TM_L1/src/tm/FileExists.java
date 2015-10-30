/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import java.io.File;

/**
 *
 * @author mat.aules
 */
public class FileExists implements IParameterValidator{

    @Override
    public void validate(String name, String value) {
        File f = new File(value);
        
        if(!f.exists() || f.isDirectory()) {
            throw new ParameterException("Input zip file is not a valid file.");
        }
    }
    
}
