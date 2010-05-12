/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;


/**
 *
 * @author jeff
 */
public class Evaluator extends Thread{
  public void run()
  {
    Thread.currentThread().setName("Evaluator thread");
    while (true)
    {
      synchronized(this) {
        try {
          this.wait();
        } catch (InterruptedException ex) {
          System.out.println("interrupted exception evaluator thread");
        }
        if (Gametape.currentTrack.evaluate()) System.out.println("true");
      }
    }
  }

}
