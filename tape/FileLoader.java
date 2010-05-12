package tape;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import vNES.Globals;
import vNES.ROM;

public class FileLoader {

  public static ExecutorService threadPool = Executors.newFixedThreadPool(Globals.downloadThreads, new FileLoaderThreadFactory());

  public static void loadFile(final String host, final String filename) {
    synchronized (Globals.resources) {
      if (Globals.resources.containsKey(filename)) {
        return;
      }
      Globals.resources.put(filename, null);
    }
    /*
    threadPool.execute(new Runnable() {
      public void run() { getBytes(host, filename); }
    });
    */
    getBytes(host, filename);
  }

  public static BufferedInputStream getStream(String host, String filename) {
    System.out.println("getting "+host+" : "+filename);
    try {
      BufferedInputStream in;
      String remote = new String(host + filename).replace(" ", "%20");
      System.out.println(remote);
      in = new BufferedInputStream(new URL(remote).openStream(), 4096);
      return in;
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
      return null;
    } catch (MalformedURLException mue) {
      mue.printStackTrace();
      return null;
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return null;
    }
  }

  public static void getBytes(String host, String filename) {
    try {
      BufferedInputStream in = getStream(host, filename);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[512];
      while (true) {
        int read = in.read(buffer);
        if (read == -1) {
          break;
        }
        out.write(buffer, 0, read);
      }
      byte[] data = out.toByteArray();

      if (filename.endsWith(".nes"))
      {
        short[] shorts = new short[data.length];
        for (int n = 0; n < data.length; n++) {
          shorts[n] = (short) (data[n] & 255);
        }
        ROM rom = new ROM(Globals.nes);
        rom.load(filename, shorts);
        Globals.resources.put(filename, rom);
      } else {
        Globals.resources.put(filename, (Object) data);
      }
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    System.out.println("finished loading "+filename);
  }

  /*
  public static Object getShorts(String romDirectory, String fileName) {
    try {
      File file = new File(romDirectory + File.separator + fileName);
      DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
      short[] shorts = new short[(int) file.length()];
      System.out.println("opening rom "+fileName);
      for (int n = 0; n < file.length(); n++) {
        shorts[n] = (short) (dis.readUnsignedByte());
      }
      System.out.println("done loading "+fileName);
      ROM rom = new ROM(Globals.nes);
      System.out.println("created rom");
      rom.load(fileName, shorts);
      System.out.println("loaded rom");
      Globals.resources.put(fileName, rom);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  */
}