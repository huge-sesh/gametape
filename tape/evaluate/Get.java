/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;


/**
 *
 * @author jeff
 */
public class Get extends Evaluable {

  final int value;
  final String variable;
  public Get(String var, String val)
  {
    value = Integer.parseInt(val);
    variable = var;
  }
  public boolean evaluate()
  {
    if (Gametape.gametapeVariableMap.get(variable) == value) {set(); return true;}
    return false;
  }
  public void print(int indentation)
  {
    for (int n = 0; n < indentation; n++) System.out.print(" ");
    System.out.println("<get variable="+variable+" value="+value+">");
  }
}
