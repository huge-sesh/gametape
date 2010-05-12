/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;


/**
 *
 * @author jeff
 */
public class Timer extends Evaluable{
  long startTime = 0;
  final long time;
  public Timer(String t)
  {
    time = Integer.parseInt(t);
  }
  public void clear()
  {
    startTime = 0;
  }
  public boolean evaluate() {
    if (startTime == 0) startTime = System.currentTimeMillis();
    //System.out.println("start: "+startTime+" end: "+(startTime + time)+" current: "+System.currentTimeMillis());
    if (System.currentTimeMillis() >= (startTime + time)) {
        set();
        System.out.println("timer evaluated true");
        return true;
    }
    return false;
  }
  public void print(int indentation)
  {
    for (int n = 0; n < indentation; n++) System.out.print(" ");
    System.out.println("<timer time="+time+">");
  }
}