/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;

/**
 *
 * @author jeff
 */
public class GameOver extends Track {
  public GameOver()
  {
    super("@gameover", "Super Mario Bros. (W) [!].nes", "smb gameover.fc0");
    Exit exit = new Exit("@halt");
    exit.addChild(new Timer("5000"));
    addChild(exit);
  }
}
