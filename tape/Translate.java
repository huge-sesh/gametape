package tape;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import vNES.CPU;
import vNES.ROM;


/**
 *
 * @author jeff
 */
public class Translate {
  public static void translate(Hashtable<String, byte[]> state)
  {
    vNES.NES nes = vNES.Globals.nes;
    byte[] cpuram = state.remove("CPU:RAM");
    for (int n = 0; n < cpuram.length; n++) {
      nes.cpuMem.mem[n] = (short) (cpuram[n] & 255);
      //if (n % 16 == 0) System.out.print("\n"+Integer.toHexString(n)+": ");
      //System.out.printf("%2s ",Integer.toHexString((short)(nes.cpuMem.mem[n] &255)));
    }
    byte[] ppuram = state.remove("PPU:NTAR");

    for (int n = 0; n < 0x400; n++) {
      //if (n % 32 == 0) System.out.print("\n"+Integer.toHexString(n)+": ");
      //System.out.printf("%2s ",Integer.toHexString((short)(ppuram[n] &255)));
      nes.ppu.writeMem(0x2000 + n, (short) (ppuram[n] & 255));
    }
    int target = 0x2400;
    if (nes.rom.getMirroringType() == ROM.HORIZONTAL_MIRRORING) target = 0x2800;
    for (int n = 0; n < 0x400; n++) {
      //if (n % 32 == 0) System.out.print("\n"+Integer.toHexString(n)+": ");
      //System.out.printf("%2s ",Integer.toHexString((short)(ppuram[n] &255)));
      nes.ppu.writeMem(target+ n, (short) (ppuram[n+0x400] & 255));
    }
    System.out.println("Mirroring is type "+nes.rom.getMirroringType());

    byte[] paletteram = state.remove("PPU:PRAM");
    for (int n = 0; n < paletteram.length; n++) {
      nes.ppuMem.mem[0x3f00 + n] = (short) (paletteram[n] & 255);
    }
    nes.ppu.updatePalettes();

    byte[] sprram = state.remove("PPU:SPRA");
    for (int n = 0; n < sprram.length; n++) {
       nes.ppu.spriteRamWriteUpdate(n,(short) (sprram[n] & 255));
    }

    //nes.papu.reset();

    byte[] ppuregs = state.remove("PPU:PPUR");
    nes.memMapper.write(0x2000, (short)(ppuregs[0] & 255));
    nes.memMapper.write(0x2001, (short)(ppuregs[1] & 255));
    nes.cpuMem.write(0x2002, ppuregs[2]);
    nes.ppu.writeSRAMAddress(ppuregs[3]);

    int ppuAddress = getInt(state.remove("PPU:RADD")); //ppu address?
    nes.ppu.firstWrite = true;
    nes.ppu.writeVRAMAddress(ppuAddress&0xff);
    System.out.println("write vram address: "+Integer.toHexString(ppuAddress&0xff));
    nes.ppu.writeVRAMAddress((ppuAddress>>8)&0xff);
    System.out.println("write vram address: "+Integer.toHexString((ppuAddress>>8)&0xff));

    nes.cpu.REG_ACC_NEW = getInt(state.remove("CPU:A"));
    nes.cpu.REG_PC_NEW = getInt(state.remove("CPU:PC")) - 1;
    int fceuStackPointer = getInt(state.remove("CPU:S"));
    fceuStackPointer = 0x100 | (fceuStackPointer&0xFF);
    nes.cpu.REG_SP = fceuStackPointer;
    nes.cpu.REG_X_NEW = getInt(state.remove("CPU:X"));
    nes.cpu.REG_Y_NEW = getInt(state.remove("CPU:Y"));
    nes.cpu.setStatus(getInt(state.remove("CPU:P")));
    nes.cpu.cyclesToHalt = 0;
    nes.cpu.crash = false;
    nes.cpu.irqRequested = false;
    int irq = getInt(state.remove("CPUC:IQLB"));
    if ((irq & (0x00000040 | 0x00000080)) != 0)
    {
      nes.cpu.requestIrq(CPU.IRQ_NMI);
    } else if ((irq &
            (0x00000001 | 0x00000002 | 0x00000100 | 0x00000200 | 0x00000800)) != 0)
    {
      nes.cpu.requestIrq(CPU.IRQ_NORMAL);
    } //reset irq ignored

    nes.memMapper.translate(state);

    System.out.println("Unused save state parts: ");
    Iterator<String> it = new TreeSet<String>(state.keySet()).iterator();
    while (it.hasNext()) {
        String partName = it.next();
        System.out.println("  "+partName +": "+state.remove(partName).length+"bytes");
    }

    System.out.println("rev. A");
    System.out.println("translation finished!");
  }
  public static int getInt(byte[] b) {
    if (b.length > 4) throw new RuntimeException("Trying to get int from too big" +
            "a byte array");
    if (b.length == 1) return (int) (b[0] & 255);
    byte[] full = new byte[4];
    for (int n = 0; n < b.length; n++) full[n] = b[n];
    for (int n = b.length; n < full.length; n++) full[n] = 0;
    ByteBuffer buf = ByteBuffer.wrap(full);
    buf.order(ByteOrder.LITTLE_ENDIAN);
    return buf.getInt();
  }
}

