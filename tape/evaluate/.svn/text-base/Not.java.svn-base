/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;

/**
 *
 * @author jeff
 */
public class Not extends Evaluable implements Parent{
  Evaluable child;
  @Override
  public boolean evaluate() {
    if (!child.evaluate()) {set(); return true;}
    return false;
  }
  public void addChild(Evaluable e)
  {
    if (child != null) throw new RuntimeException("adding second child to <not>");
    child = e;
    e.parent = this;
  }
  public void print(int indentation)
  {
    for (int n = 0; n < indentation; n++) System.out.print(" ");
    System.out.println("<not>");
    child.print(indentation +2);
  }
}
