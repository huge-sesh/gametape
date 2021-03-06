package vNES;

import java.applet.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import tape.evaluate.Gametape;

public class vNES extends Applet implements Runnable {

  boolean scale;
  boolean scanlines;
  boolean sound;
  boolean fps;
  boolean stereo;
  boolean nicesound;
  boolean timeemulation;
  boolean showsoundbuffer;
  int samplerate;
  int romSize;
  int progress;
  AppletUI gui;
  NES nes;
  public String gametape;
  ScreenView panelScreen;
  Font progressFont;
  Color bgColor = Color.black.darker().darker();
  boolean started = false;
  private boolean gotResources;

  @Override
  public void init() {

    readParams();


    System.gc();

    gui = new AppletUI(this);
    gui.init(false);

    Globals.appletMode = true;
    Globals.memoryFlushValue = 0x00; // make SMB1 hacked version work.
    try {
      Gametape.load(gametape);
      gotResources = true;
    } catch (Exception ex) {
      Logger.getLogger(vNES.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void addScreenView() {

    panelScreen = (ScreenView) gui.getScreenView();
    panelScreen.setFPSEnabled(fps);
    //this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setLayout(null);

    if (scale) {

      if (scanlines) {
        panelScreen.setScaleMode(BufferView.SCALE_SCANLINE);
      } else {
        panelScreen.setScaleMode(BufferView.SCALE_NORMAL);
      }

      this.setSize(512, 480);
      this.setBounds(0, 0, 512, 480);
      panelScreen.setBounds(0, 0, 512, 480);

    } else {

      panelScreen.setBounds(0, 0, 256, 240);

    }
    this.setIgnoreRepaint(true);
    this.add(panelScreen);

    if (Globals.PPUDebug) {
        this.setSize(512, 1024);
        this.setBounds(0, 0, 512, 1024);

        gui.getPatternView().setBounds(256,0,256,128);
        gui.getNameTableView().setBounds(0,240,512,512);
        gui.getImgPalView().setBounds(256, 128, 256, 16);
        gui.getSprPalView().setBounds(256, 128+16, 256, 16);

        this.add(gui.getPatternView());
        this.add(gui.getNameTableView());
        this.add(gui.getImgPalView());
        this.add(gui.getSprPalView());
    }

  }

  @Override
  public void start() {

    Thread t = new Thread(this, "Applet thread");
    t.start();

  }
  /*
  public void getSettings()
  {
    Globals.settings = new HashMap<String, String>();
    JFileChooser fc = new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.showDialog(this, "set rom directory");
    Globals.settings.put("romdir", fc.getSelectedFile().getAbsolutePath());
  }

  public boolean getResources(boolean init)
  {
    try {
      try {
        Globals.settings = (HashMap<String, String>) new ObjectInputStream(
                new FileInputStream(
                Globals.settingsFile)).readObject();
      } catch (FileNotFoundException e) {
        if (init) return false;
        else {
          getSettings();
        }
      }
      new ObjectOutputStream(new FileOutputStream(Globals.settingsFile)).writeObject(Globals.settings);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  */

  public void run() {


    // Set font to be used for progress display of loading:
    progressFont = new Font("Tahoma", Font.TRUETYPE_FONT | Font.BOLD, 12);

    // Can start painting:
    started = true;

    // Load ROM file:
    System.out.println("vNES 2.10 \u00A9 2006-2009 Jamie Sanders");
    System.out.println("For games and updates, see www.virtualnes.com");
    System.out.println("Gametape beta 1 2010 huge sesh");
    System.out.println("Use of this program subject to GNU GPL, Version 3.");

    addScreenView();
    if (!gotResources) try {
      Gametape.load(gametape);
    } catch (Exception ex) {
      Logger.getLogger(vNES.class.getName()).log(Level.SEVERE, null, ex);
    }

    nes = gui.getNES();
    nes.enableSound(sound);
    nes.reset();
    //nes.loadRom(rom);
    if (Globals.resources.get(Gametape.startTrack.rom) == null)System.err.println("null first track!");
    Gametape.loadTrack(Gametape.startTrack.name);

    if (nes.rom.isValid()) {

      // Add the screen buffer:

      // Set some properties:
      Globals.timeEmulation = timeemulation;
      nes.ppu.showSoundBuffer = showsoundbuffer;

      // Start emulation:
      //System.out.println("vNES is now starting the processor.");
      nes.getCpu().beginExecution();
      nes.isRunning = true;
      Gametape.running = true;
      Globals.nes.startEmulation();
      Gametape.loadState();


    } else {

      // ROM file was invalid.
      System.out.println("vNES was unable to find (" + Gametape.currentTrack.rom + ").");

    }

  }

  @Override
  public void stop() {
    nes.stopEmulation();
    //System.out.println("vNES has stopped the processor.");
    nes.getPapu().stop();
    this.destroy();

  }

  @Override
  public void destroy() {

    if (nes != null && nes.getCpu().isRunning()) {
      stop();
    }
    //System.out.println("* Destroying applet.. *");

    if (nes != null) {
      nes.destroy();
    }
    if (gui != null) {
      gui.destroy();
    }

    gui = null;
    nes = null;
    panelScreen = null;

    System.runFinalization();
    System.gc();

  }

  public void showLoadProgress(int percentComplete) {

    progress = percentComplete;
    paint(getGraphics());

  }

  // Show the progress graphically.
  @Override
  public void paint(Graphics g) {


    String pad;
    String disp;
    int scrw, scrh;
    int txtw, txth;

    if (!started) {
      return;
    }

    // Get screen size:
    if (scale) {
      scrw = 512;
      scrh = 480;
    } else {
      scrw = 256;
      scrh = 240;
    }


    // Fill background:
    g.setColor(bgColor);
    g.fillRect(0, 0, scrw, scrh);


    // Prepare text:
    if (progress < 10) {
      pad = "  ";
    } else if (progress < 100) {
      pad = " ";
    } else {
      pad = "";
    }
    disp = "vNES is Loading Game... " + pad + progress + "%";

    // Measure text:
    g.setFont(progressFont);
    txtw = g.getFontMetrics(progressFont).stringWidth(disp);
    txth = g.getFontMetrics(progressFont).getHeight();


    // Display text:
    g.setFont(progressFont);
    g.setColor(Color.white);
    g.drawString(disp, scrw / 2 - txtw / 2, scrh / 2 - txth / 2);
    g.drawString(disp, scrw / 2 - txtw / 2, scrh / 2 - txth / 2);
    g.drawString("vNES \u00A9 2006-2009 Jamie Sanders", 12, 448);
    g.drawString("For games and updates, visit www.virtualnes.com", 12, 464);
  }

  @Override
  public void update(Graphics g) {
    // do nothing.
  }

  public void readParams() {

    String tmp;

    tmp = getParameter("gametape");
    if (tmp == null || tmp.equals("")) {
      gametape = "default.gametape";
    } else {
      gametape = tmp;
    }

    tmp = getParameter("scale");
    if (tmp == null || tmp.equals("")) {
      scale = false;
    } else {
      scale = tmp.equals("on");
    }

    tmp = getParameter("sound");
    if (tmp == null || tmp.equals("")) {
      sound = true;
    } else {
      sound = tmp.equals("on");
    }

    tmp = getParameter("stereo");
    if (tmp == null || tmp.equals("")) {
      stereo = true; // on by default
    } else {
      stereo = tmp.equals("on");
    }

    tmp = getParameter("scanlines");
    if (tmp == null || tmp.equals("")) {
      scanlines = false;
    } else {
      scanlines = tmp.equals("on");
    }

    tmp = getParameter("fps");
    if (tmp == null || tmp.equals("")) {
      fps = false;
    } else {
      fps = tmp.equals("on");
    }

    tmp = getParameter("nicesound");
    if (tmp == null || tmp.equals("")) {
      nicesound = true;
    } else {
      nicesound = tmp.equals("on");
    }

    tmp = getParameter("timeemulation");
    if (tmp == null || tmp.equals("")) {
      timeemulation = true;
    } else {
      timeemulation = tmp.equals("on");
    }

    tmp = getParameter("showsoundbuffer");
    if (tmp == null || tmp.equals("")) {
      showsoundbuffer = false;
    } else {
      showsoundbuffer = tmp.equals("on");
    }

    tmp = getParameter("romsize");
    if (tmp == null || tmp.equals("")) {
      romSize = -1;
    } else {
      try {
        romSize = Integer.parseInt(tmp);
      } catch (Exception e) {
        romSize = -1;
      }
    }

    tmp = getParameter("ppudebug");
    if (tmp == null || tmp.equals("")) {
      Globals.PPUDebug = false;
    } else {
      Globals.PPUDebug = tmp.equals("on");
    }
  }
}