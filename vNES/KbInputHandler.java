package vNES;

import tape.FCEUXState;
import java.awt.event.*;

public class KbInputHandler implements KeyListener, InputHandler {

    boolean[] allKeysState;
    int[] keyMapping;
    int id;
    NES nes;
    ByteBuffer currentSave;
    boolean favorLeft = true;
    boolean favorUp = true;

    public KbInputHandler(NES nes, int id) {
        this.nes = nes;
        this.id = id;
        allKeysState = new boolean[255];
        keyMapping = new int[InputHandler.NUM_KEYS];
    }

    public short getKeyState(int padKey) {
        switch (padKey) {
            case InputHandler.KEY_LEFT:
                if (allKeysState[keyMapping[KEY_RIGHT]] && !favorLeft) {
                    return 0x40;
                } else {
                    break;
                }
            case InputHandler.KEY_RIGHT:
                if (allKeysState[keyMapping[KEY_LEFT]] && favorLeft) {
                    return 0x40;
                } else {
                    break;
                }
            case InputHandler.KEY_DOWN:
                if (allKeysState[keyMapping[KEY_UP]] && favorUp) {
                    return 0x40;
                } else {
                    break;
                }
            case InputHandler.KEY_UP:
                if (allKeysState[keyMapping[KEY_DOWN]] && !favorUp) {
                    return 0x40;
                } else {
                    break;
                }
        }
        return (short) (allKeysState[keyMapping[padKey]] ? 0x41 : 0x40);
    }

    public void mapKey(int padKey, int kbKeycode) {
        keyMapping[padKey] = kbKeycode;
    }

    public void keyPressed(KeyEvent ke) {

        int kc = ke.getKeyCode();
        if (kc >= allKeysState.length) {
            return;
        }
        allKeysState[kc] = true;
        if (kc == keyMapping[KEY_LEFT]) {
            favorLeft = true;
        } else if (kc == keyMapping[KEY_RIGHT]) {
            favorLeft = false;
        } else if (kc == keyMapping[KEY_UP]) {
            favorUp = true;
        } else if (kc == keyMapping[KEY_DOWN]) {
            favorUp = false;
        }
    }

    public void keyReleased(KeyEvent ke) {

        int kc = ke.getKeyCode();
        if (kc >= allKeysState.length) {
            return;
        }

        allKeysState[kc] = false;

        if (id == 0) {
            switch (kc) {
                case KeyEvent.VK_ESCAPE: {
                }
                case KeyEvent.VK_BACK_SPACE: {

                    // Reset game:
                    /*
                    if (nes.isRunning()) {
                        nes.stopEmulation();
                        nes.reset();
                        nes.reloadRom();
                        nes.startEmulation();
                    } 
                    */
                    System.out.println("Memory dump:");
                    for (int n = 0; n < 0x200; n++) {
                        if (n % 16 == 0) {
                            System.out.print("\n" + Integer.toHexString(n) + ": ");
                        }
                        System.out.printf("%2s ", Integer.toHexString((short) (nes.cpuMem.mem[n] & 255)));
                    }
                    break;
                }
                case KeyEvent.VK_F10: {
                    // Just using this to display the battery RAM contents to user.
                    if (nes.rom != null) {
                        nes.rom.closeRom();
                    }
                    break;
                }
            }
        }
    }

    public void keyTyped(KeyEvent ke) {
        // Ignore.
    }

    public void reset() {
        allKeysState = new boolean[255];
    }

    public void update() {
        // doesn't do anything.
    }

    public void destroy() {
        nes = null;
    }
}
