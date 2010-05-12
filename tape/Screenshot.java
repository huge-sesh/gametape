/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLine;
import ar.com.hjg.pngj.PngWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import vNES.Globals;
import vNES.NES;
import vNES.ROM;

/**
 *
 * @author jeff
 */
public class Screenshot {
  public static void main(String[] args) throws Exception {
    Thread.currentThread().setName("Screenshot main thread");
    Globals.screenshotMode = true;
    String romName = args[args.length - 3];
    String stateName = args[args.length - 2];
    String screenshotName = args[args.length -1];
    new NullGui();
    NES nes = Globals.nes;
    ROM rom = new ROM(nes);
    byte[] buffer = new byte[4096];
    int bytesRead = 0;
    DataInputStream istream = new DataInputStream(new FileInputStream(romName));
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    while (true) {
      bytesRead = istream.read(buffer);
      if (bytesRead < 1) break;
      ostream.write(buffer, 0, bytesRead);
    }
    buffer = ostream.toByteArray();
    istream.close();
    short[] romShorts = new short[buffer.length];
    for (int n = 0; n < buffer.length; n++) {
      romShorts[n] = (short) (buffer[n] & 255);
    }

    istream = new DataInputStream(new FileInputStream(stateName));
    ostream.reset();
    buffer = new byte[4096];
    while (true) {
      bytesRead = istream.read(buffer);
      if (bytesRead < 1) break;
      ostream.write(buffer, 0, bytesRead);
    }
    byte[] stateBytes = ostream.toByteArray();

    rom.load(romName, romShorts);
    nes.loadRom(rom);
    nes.getCpu().beginExecution();
    nes.startEmulation();
    nes.stopEmulation();
    nes.ppu.vblankCountdown = 2;
    FCEUXState.load(stateBytes);
    nes.startEmulation();
    nes.cpu.myThread.join(10000);
    int[] screenshot = nes.ppu.renderFullFrame();
    ImageInfo info = new ImageInfo(256, 240, 8, false);
    PngWriter writer = new PngWriter(screenshotName, info);
    ImageLine iLine = new ImageLine(writer.imgInfo);
    iLine.setRown(0);
    writer.setOverrideFile(true);
    writer.doInit();
    for (int row = 0; row < 240; row++) {
      for (int col = 0; col < 256; col++) {
        iLine.setPixelRGB8(col, screenshot[row * 256 + col]);
      }
      writer.writeRow(iLine);
      iLine.incRown();
    }
    writer.end();
    System.out.println("wrote screenshot to "+screenshotName);
    FileLoader.threadPool.shutdown();
    nes.papu.stop();
    System.exit(0);
  }
}
