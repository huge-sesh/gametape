package vNES;

import java.util.Hashtable;



public class Mapper002 extends MapperDefault{

  public void translate(Hashtable<String, byte[]> state) {

    System.out.println("rom bank count: "+rom.getVromBankCount());
    byte[] characterRam = state.remove("SFMDATA:CHRR");
    for (int n = 0; n < 0x2000; n+=16) {
      for (int i = 0; i < 8; i++) {
        int lo = (short) (characterRam[n + i]&255);
        int hi = (short) (characterRam[n + i + 8]&255);
        for (int j = 0; j < 8; j++) {
          int target = lo & 0x1;
          target += (hi & 0x1) * 2;
          lo = lo>>>1;
          hi = hi>>>1;
          ppu.ptTile[n/16].pix[(i*8)+(7-j)] = target;
        }
      }
    }

    short bank = (short) (state.remove("SFMDATA:LATC")[0] & 255);
    System.out.println("Mapper 2 reports bank: "+bank);
    write(0x8000, bank);
  }

	public void init(NES nes){
		
		super.init(nes);
		
	}
	
	public void write(int address, short value){
		
		if(address < 0x8000){
			
			// Let the base mapper take care of it.
			super.write(address,value);
			
		}else{
			
			// This is a ROM bank select command.
			// Swap in the given ROM bank at 0x8000:
			loadRomBank(value,0x8000);
			
		}
		
	}
	
	public void loadROM(ROM rom){
	
		if(!rom.isValid()){
			//System.out.println("UNROM: Invalid ROM! Unable to load.");
			return;
		}
		
		//System.out.println("UNROM: loading ROM..");
		
		// Load PRG-ROM:
		loadRomBank(0,0x8000);
		loadRomBank(rom.getRomBankCount()-1,0xC000);
		
		// Load CHR-ROM:
		loadCHRROM();
		
		// Do Reset-Interrupt:
		//nes.getCpu().doResetInterrupt();
		nes.getCpu().requestIrq(CPU.IRQ_RESET);
		
	}
	
}