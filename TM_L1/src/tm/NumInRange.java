/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import tm.ArgParser.numParamLimits;

/**
 *
 * @author mat.aules
 */
public class NumInRange implements IParameterValidator{

    @Override
    public void validate(String name, String value) {
        int number = Integer.parseInt(value);
        
        if(name.equals("--numericParameter") || name.equals("-np")) {
            if(number <numParamLimits.MIN.getValue() || number > numParamLimits.MAX.getValue()) {
                throw new ParameterException("NumericParameter");
            }
        }
    }
    
}
