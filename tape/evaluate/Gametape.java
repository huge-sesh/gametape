/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tape.evaluate;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import tape.FCEUXState;
import tape.FileLoader;
import vNES.Globals;
import vNES.ROM;

/**
 *
 * @author jeff
 */
public class Gametape {

  public static Map<String, Integer> gametapeVariableMap = new HashMap<String, Integer>();
  public static HashMap<String, Track> trackMap = new HashMap<String, Track>();

  public static boolean running = true;
  public static String name;
  public static Track startTrack;
  public static Track currentTrack;
  public static Evaluator evaluator;

  public static void load(String filename) throws Exception {
    if (evaluator == null) evaluator = new Evaluator();
    evaluator.start();
    trackMap.clear();
    trackMap.put("@gameover", new GameOver());
    BufferedInputStream tape = FileLoader.getStream(Globals.TAPE_PREFIX, filename);
    new TapeReader(tape);
  }

  public static void loadTrack(String nextTrack) {
    if (nextTrack.equals("@halt")) {
      running = false;
      Globals.nes.stopEmulation();
    } else {
      currentTrack = trackMap.get(nextTrack);
      Object nextRom = Globals.resources.get(currentTrack.rom);
      if (nextRom == null) throw new RuntimeException("null rom "+currentTrack.rom);
      System.out.println("loading rom "+currentTrack.rom);
      Globals.nes.loadRom((ROM)Globals.resources.get(currentTrack.rom));
      System.out.println("rom loaded");
    }
  }

  public static void loadState() {
    if (!running) return;
    System.out.println("gametape loading "+currentTrack.state+" for "+currentTrack.rom);
    Globals.nes.stopEmulation();
    try {
      FCEUXState.load((byte[]) Globals.resources.get(currentTrack.state));
      Globals.nes.startEmulation();
    } catch (Exception e) {
      e.printStackTrace();
      Globals.nes.stopEmulation();
    }
  }

  public static void addTrack(Track track) {
    if (trackMap.size() == 1) {
      startTrack = track;
    }
    trackMap.put(track.name, track);
  }

  public static void print(int indentation) {
    for (int n = 0; n < indentation; n++) {
      System.out.print(" ");
    }
    System.out.println("<gametape name=" + name + ">");
    Iterator<Track> it = trackMap.values().iterator();
    while (it.hasNext()) {
      it.next().print(indentation + 2);
    }
  }
}
