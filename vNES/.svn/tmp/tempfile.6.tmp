package vNES;

import java.util.Hashtable;



public class Mapper003 extends MapperDefault{

  public void translate(Hashtable<String, byte[]> state) {
    short bank = (short) (state.remove("SFMDATA:LATC")[0] & 255);
    System.out.println("Mapper 3 reports bank: "+bank);
    write(0x8000, (short) (bank & 0x03));
  }

	public void init(NES nes){
		
		super.init(nes);
		
	}
	
	public void write(int address, short value){
		
		if(address < 0x8000){
			
			// Let the base mapper take care of it.
			super.write(address,value);
			
		}else{
			
			// This is a VROM bank select command.
			// Swap in the given VROM bank at 0x0000:
			int bank = (value%(nes.getRom().getVromBankCount()/2))*2;
			loadVromBank(bank,0x0000);
			loadVromBank(bank+1,0x1000);
			load8kVromBank(value*2,0x0000);
			
		}
		
	}
	
	
}