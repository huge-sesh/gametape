/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape;

import java.awt.Point;
import vNES.BufferView;
import vNES.Globals;
import vNES.HiResTimer;
import vNES.InputHandler;
import vNES.KbInputHandler;
import vNES.NES;
import vNES.UI;

/**
 *
 * @author jeff
 */
public class NullGui implements UI {
  HiResTimer hiResTimer;
  KbInputHandler inputHandler;

  NullGui() {
    hiResTimer = new HiResTimer();
    Globals.nes = new NES(this);
    inputHandler = new KbInputHandler(Globals.nes, 0);
  }

  public NES getNES() {
    return Globals.nes;
  }

  public InputHandler getJoy1() {
    return inputHandler;
  }

  public InputHandler getJoy2() {
    return inputHandler;
  }

  public BufferView getScreenView() {
    return null;
  }

  public BufferView getPatternView() {
    return null;
  }

  public BufferView getSprPalView() {
    return null;
  }

  public BufferView getNameTableView() {
    return null;
  }

  public BufferView getImgPalView() {
    return null;
  }

  public HiResTimer getTimer() {
    return hiResTimer;
  }

  public void imageReady(boolean skipFrame) {
  }

  public void init(boolean showGui) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String getWindowCaption() {
    return new String();
  }

  public void setWindowCaption(String s) {
  }

  public void setTitle(String s) {
  }

  public Point getLocation() {
    return new Point(0,0);
  }

  public int getWidth() {
    return 256;
  }

  public int getHeight() {
    return 240;
  }

  public int getRomFileSize() {
    return 1;
  }

  public void destroy() {
  }

  public void println(String s) {
  }

  public void showLoadProgress(int percentComplete) {
  }

  public void showErrorMsg(String msg) {
  }
}
