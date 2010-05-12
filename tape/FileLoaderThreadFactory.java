/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tape;

import java.util.concurrent.ThreadFactory;

/**
 *
 * @author jeff
 */
public class FileLoaderThreadFactory implements ThreadFactory {

  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setName("File Loader thread");
    return t;
  }

}
