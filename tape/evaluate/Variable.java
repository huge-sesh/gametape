/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;

import vNES.Globals;

/**
 *
 * @author jeff
 */
public class Variable {
  final int value;
  final String variable;
  public Variable(String var, String val)
  {
    variable = var;
    value = Integer.parseInt(val);
  }
  public void set()
  {
    Gametape.gametapeVariableMap.put(variable, value);
  }
}
