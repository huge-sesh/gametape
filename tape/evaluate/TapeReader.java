package tape.evaluate;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import tape.FileLoader;
import vNES.Globals;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jeff
 */
public class TapeReader extends DefaultHandler {
  String gametape;

  enum Tags {
    gametape,
    track,
    exit,
    and,
    or,
    not,
    condition,
    get,
    set,
    timer,
  }
  final XMLReader xmlreader;
  private Parent currentParent;
  public TapeReader(InputStream tape) throws
          SAXException,
          ParserConfigurationException,
          FileNotFoundException,
          IOException
  {
    super();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(false);
    xmlreader = factory.newSAXParser().getXMLReader();
    xmlreader.setContentHandler(this);
    xmlreader.setErrorHandler(this);
    xmlreader.parse(new InputSource(tape));

  }
  @Override
  public void startDocument()
  {
    System.out.println("start parsing xml");
  }
  @Override
  public void endDocument()
  {
    System.out.println("done parsing xml:");
    Gametape.print(0);
    try{
      FileLoader.threadPool.shutdown();
      FileLoader.threadPool.awaitTermination(60, TimeUnit.SECONDS);
    } catch (InterruptedException ie) {
      System.err.println("interruptedexception while waiting for fileloader");
    }
  }
  @Override
  public void startElement(String uri, String name, String qName, Attributes atts)
  {
    Evaluable eval = null;
    switch(Tags.valueOf(qName))
    {
      case gametape:
        Gametape.name = atts.getValue("name");
        break;
      case track:
         Track track = new Track(
                atts.getValue("name"),
                atts.getValue("rom"),
                atts.getValue("state"));
         Gametape.addTrack(track);
         eval = (Evaluable) track;
         break;
      case exit:
        eval = new Exit(atts.getValue("goto"));
        break;
      case and:
        eval = new And();
        break;
      case or:
        eval = new Or();
        break;
      case not:
        eval = new Not();
        break;
      case condition:
        eval = new Condition(
                atts.getValue("operator").charAt(0),
                atts.getValue("address"),
                atts.getValue("value"));
        break;
      case get:
        eval = new Get(
                atts.getValue("variable"),
                atts.getValue("value"));
        break;
      case set:
        if (currentParent != null)
        {
          ((Evaluable)currentParent).addVariable(new Variable(
                  atts.getValue("variable"),
                  atts.getValue("value")));
        } else throw new RuntimeException("adding <set> with no parent");
        break;
      case timer:
        eval = new Timer(atts.getValue("time"));
        break;
      default:
        System.err.println("unrecognized tag "+name);
    }
    if (eval != null) 
    {
      if (currentParent != null)  {
        System.out.println("adding "+eval.getClass().getSimpleName()+" to "+currentParent.getClass().getSimpleName());
        currentParent.addChild(eval);
      }
      if (eval instanceof Parent)
      {
        System.out.println("setting parent to "+eval.getClass().getSimpleName());
        currentParent = (Parent) eval;
      }
    }
  }
  @Override
  public void endElement (String uri, String name, String qName)
  {
    switch(Tags.valueOf(qName))
    {
      case track:
      case exit:
      case and:
      case or:
      case not:
        currentParent = (Parent)((Evaluable)currentParent).parent;
    }
  }
}
