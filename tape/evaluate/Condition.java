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
public class Condition extends Evaluable {
  final char operator;
  final int address;
  final int value;
  public Condition(char op, String addr, String val)
  {
    operator = op;
    address = Integer.parseInt(addr, 16);
    value = Integer.parseInt(val, 16);
  }
  public boolean evaluate()
  {
    boolean yes = false;
    int memValue = Globals.nes.cpu.load(address);
    switch(operator) {
      case '=': yes = (memValue == value); break;
      case '>': yes = (memValue > value); break;
      case '<': yes = (memValue < value); break;
      case '!': yes = (memValue != value); break;
      default: throw new RuntimeException("memory check operator is "+operator);
    }
    //System.out.println("condition eval "+address+operator+value+": "+memValue+", "+yes);
    if (yes) set();
    return yes;
  }
  public void addChild(Evaluable e)
  {
    throw new RuntimeException("Trying to add child to <condition> node");
  }
  public void print(int indentation)
  {
    for (int n = 0; n < indentation; n++) System.out.print(" ");
    System.out.println("<condition operator="+operator+" address="+address+" value="+value+">");
  }
}