/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author jeff
 */
public class Or extends Evaluable implements Parent{
  final LinkedList<Evaluable> children = new LinkedList<Evaluable>();
  public boolean evaluate()
  {
    ListIterator<Evaluable> it = children.listIterator();
    while (it.hasNext())
    {
      if (it.next().evaluate()) {set(); return true;}
    }
    return false;
  }
  public void addChild(Evaluable e)
  {
    children.add(e);
    e.parent = this;
  }
  public void print(int indentation)
  {
    for (int n = 0; n < indentation; n++) System.out.print(" ");
    System.out.println("<or>");
    ListIterator<Evaluable> it = children.listIterator();
    while (it.hasNext()) it.next().print(indentation + 2);
  }
}