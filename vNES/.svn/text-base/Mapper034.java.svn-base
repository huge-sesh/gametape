package vNES;



public class Mapper034 extends MapperDefault{
	
	public void init(NES nes){
		
		super.init(nes);
		
	}
	
	public void write(int address, short value){
		
		if(address < 0x8000){
			
			// Let the base mapper take care of it.
			super.write(address,value);
			
		}else{
			
			// Swap in the given PRG-ROM bank at 0x8000:
			load32kRomBank(value,0x8000);
			
		}
		
	}	
	
}
