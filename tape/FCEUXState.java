package tape;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

/**
 *
 * @author jeff
 */
enum Chunk_T {

  SFMDATA,
  PPU,
  CONTROL,
  SOUND,
  CPU,
  CPUC,
}

public class FCEUXState {

  public static void load(byte[] compressed_state) throws Exception {
    Hashtable<String, byte[]> memory = readStateChunks(decompress(compressed_state));
    Translate.translate(memory);
  }

  public static byte[] getBackBuffer (byte[] compressed_state) throws Exception {
    Hashtable<String, byte[]> memory = readStateChunks(decompress(compressed_state));
    return memory.remove("BACKBUFFER");
  }

  public static ByteBuffer decompress(byte[] compressed_state) throws Exception {
    ByteBuffer buf = ByteBuffer.wrap(compressed_state);
    buf.order(ByteOrder.LITTLE_ENDIAN);
    if (buf.get() == 'F' && buf.get() == 'C' && buf.get() == 'S' && buf.get() == 'X') {
      System.out.println("genuine fceux save");
      int total_size = buf.getInt();
      int state_version = buf.getInt();
      int compressed_length = buf.getInt();
      System.out.println(total_size + " " + state_version + " " + compressed_length);
      byte[] state = new byte[total_size];
      if (compressed_length != -1) {
        ZStream stream = new ZStream();
        stream.next_in = compressed_state;
        stream.next_in_index = 16;
        //stream.avail_in=compressed_length;
        int err = stream.inflateInit();
        CHECK_ERR(stream, err, "inflateInit");
        stream.next_out = state;
        stream.next_out_index = 0;
        //stream.avail_out=total_size; // force small buffers
        while (stream.total_in < compressed_length &&
                stream.total_out < total_size) {
          stream.avail_in = stream.avail_out = 1;
          err = stream.inflate(JZlib.Z_NO_FLUSH);
          if (err == JZlib.Z_STREAM_END) {
            break;
          }
          CHECK_ERR(stream, err, "inflate");
        }
        //err=stream.inflateSync();
        //CHECK_ERR(stream,err, "inflateSync");
        err = stream.inflateEnd();
        CHECK_ERR(stream, err, "inflateEnd");
        System.out.println("decompressed!");

        ByteBuffer new_buf = ByteBuffer.wrap(state);
        new_buf.order(ByteOrder.LITTLE_ENDIAN);
        return new_buf;
      } else {
        return buf;
      }
    } else throw new RuntimeException("not a an fceux state");
  }

  static void CHECK_ERR(ZStream z, int err, String msg) {
    if (err != JZlib.Z_OK) {
      if (z.msg != null) {
        System.out.print(z.msg + " ");
      }
      System.out.println(msg + " error: " + err);
    }
  }

  private static Hashtable<String, byte[]> readStateChunks(ByteBuffer buf) {
    Hashtable<String, byte[]> memory = new Hashtable<String, byte[]>();
    while (buf.hasRemaining()) {
      int type = buf.get();
      int size = buf.getInt();
      switch (type) {
        case 1:
          readStateChunk(buf, memory, Chunk_T.CPU, size);
          break;
        case 3:
          readStateChunk(buf, memory, Chunk_T.PPU, size);
          break;
        case 4:
          readStateChunk(buf, memory, Chunk_T.CONTROL, size);
          break;
        case 7:
        case 6:
          System.out.println("dropping movie data");
          buf.position(buf.position() + size);
          break;
        case 0x10:
          readStateChunk(buf, memory, Chunk_T.SFMDATA, size);
          break;
        case 5:
          readStateChunk(buf, memory, Chunk_T.SOUND, size);
          break;
        case 2:
          readStateChunk(buf, memory, Chunk_T.CPUC, size);
          break;
        case 8:
          byte[] backBuf = new byte[size];
          System.out.println("backbuffer is "+ size +"bytes");
          buf.get(backBuf);
          memory.put("BACKBUFFER", backBuf);
          break;
        default:
          System.err.println("got bad type: " + type);
          buf.position(buf.position() + size);
          break;
      }
    }
    return memory;
  }

  static void readStateChunk(
    ByteBuffer buf,
    Hashtable<String, byte[]> memory,
    Chunk_T t,
    int size) {

    int end = buf.position() + size;
    while (buf.position() < end) {
      byte[] bdesc = new byte[4];
      buf.get(bdesc);
      String description = new String(t.toString() + ":");
      for (int n = 0; n < 4; n++) {
        if (bdesc[n] == 0) {
          break;
        }
        description += (char) bdesc[n];
      }
      int chunksize = buf.getInt();
      byte[] b = new byte[chunksize];
      buf.get(b);
      memory.put(description, b);
    }
  }
}
