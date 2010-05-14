/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape.evaluate;

import java.util.LinkedList;
import java.util.ListIterator;
import tape.FileLoader;
import vNES.Globals;

/**
 *
 * @author jeff
 */
public class Track extends Or {
  public final String name;
  public final String rom;
  public final String state;
  LinkedList<Timer> timers = new LinkedList<Timer>();
  public Track(String n, String r, String s)
  {
    name = n;
    rom = r;
    state = s;
    FileLoader.loadFile(Globals.STATE_PREFIX, state);
    FileLoader.loadFile(Globals.ROM_PREFIX, rom);
    //FileLoader.getShorts(Globals.settings.get("romdir"), rom);
    Exit timeout = new Exit("@gameover");
    timeout.addChild(new Timer("60000"));
    addChild(timeout);
  }
  @Override
  public boolean evaluate()
  {
    //System.out.println("track evaluating");
    ListIterator<Evaluable> it = children.listIterator();
    //System.out.println("track has "+children.size()+" exits");
    while (it.hasNext())
    {
      Exit exit = (Exit) it.next();
      //System.out.println("got exit");
      if (exit.evaluate())
      {
        clearTimers();
        set();
        System.out.println("loading "+exit.nextTrack);
        Gametape.loadTrack(exit.nextTrack);
        //Globals.nes.loadRom((ROM)Globals.resources.get(Gametape.currentTrack.rom));
        Gametape.loadState();
        return true;
      }
    }
    return false;
  }
  @Override
  public void addChild(Evaluable e)
  {
    if (e instanceof Exit) super.addChild(e);
    else if (e instanceof Timer) 
    {
      timers.add((Timer)e);
      super.addChild(e);
    }
    else throw new RuntimeException("can only add <exit> and <set> to <track>");
  }
  @Override
  public void print(int indentation)
  {
    for (int n = 0; n < indentation; n++) System.out.print(" ");
    System.out.println("<track name="+name+" rom="+rom+" state="+state+">");
    ListIterator<Evaluable> it = children.listIterator();
    while (it.hasNext()) it.next().print(indentation + 2);
  }
  public void clearTimers()
  {
    ListIterator<Timer> it = timers.listIterator();
    while (it.hasNext()) it.next().clear();
  }
}
