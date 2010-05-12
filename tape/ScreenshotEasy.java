/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLine;
import ar.com.hjg.pngj.PngWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import vNES.PaletteTable;

/**
 *
 * @author jeff
 */
public class ScreenshotEasy {
  public static void main(String[] args) throws Exception {
    Thread.currentThread().setName("Screenshot main thread");
    String stateName = args[args.length - 2];
    String screenshotName = args[args.length -1];
    new NullGui();
    byte[] buffer = new byte[4096];
    int bytesRead = 0;

    DataInputStream istream = new DataInputStream(new FileInputStream(stateName));
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    buffer = new byte[4096];
    while (true) {
      bytesRead = istream.read(buffer);
      if (bytesRead < 1) break;
      ostream.write(buffer, 0, bytesRead);
    }
    byte[] stateBytes = ostream.toByteArray();
    byte[] screenshot = FCEUXState.getBackBuffer(stateBytes);

    for (int y = 0; y < 240; y++) {
      for (int x = 0; x < 256; x++) {
        System.out.print(screenshot[(y*256) + x]);
      }
      System.out.println();
    }
    System.out.println("screenshot is "+ screenshot.length+" bytes");
    PaletteTable palette = new PaletteTable();
    palette.loadNTSCPalette();
    ImageInfo info = new ImageInfo(256, 240, 8, false);
    PngWriter writer = new PngWriter(screenshotName, info);
    ImageLine iLine = new ImageLine(writer.imgInfo);
    iLine.setRown(0);
    writer.setOverrideFile(true);
    writer.doInit();
    for (int row = 0; row < 240; row++) {
      for (int col = 0; col < 256; col++) {
        int unpacked = (screenshot[row * 256 + col] & 255);
        int r = (unpacked & 0x30) >> 4;
        int g = (unpacked & 0x07) >> 2;
        int b = (unpacked & 0x03);
        //iLine.setValD(col * 3, (r / 4.0));
        //iLine.setValD(col * 3 + 1, (g / 4.0));
        //iLine.setValD(col * 3 + 2, (b / 4.0));
        int repacked = palette.getEntry(unpacked >> 2);
        iLine.setPixelRGB8(col, repacked);
      }
      writer.writeRow(iLine);
      iLine.incRown();
    }
    writer.end();
    System.out.println("wrote screenshot to "+screenshotName);
    FileLoader.threadPool.shutdown();
    System.exit(0);
  }
}
