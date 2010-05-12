package tape.evaluate;

import java.util.LinkedList;
import java.util.ListIterator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jeff
 */
public abstract class Evaluable {

  public LinkedList<Variable> variables = new LinkedList<Variable>();
  public Evaluable parent;

  public abstract boolean evaluate();

  public abstract void print(int indentation);

  public void set() {
    ListIterator<Variable> it = variables.listIterator();
    while (it.hasNext()) {
      it.next().set();
    }
  }

  public void addVariable(Variable var) {
    variables.add(var);
  }
}
