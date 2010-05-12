/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;

/**
 *
 * @author jeff
 */
public class Exit extends Or{
  public final String nextTrack;
  public Exit(String go)
  {
    super();
    nextTrack = go;
  }

  @Override
  public boolean evaluate()
  {
    //System.out.println("exit.evaluate()");
    if (super.evaluate())
    {
      System.out.println("exit true");
      set();
      return true;
    }
    return false;
  }
}
