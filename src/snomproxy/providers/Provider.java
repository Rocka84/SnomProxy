package snomproxy.providers;

import java.io.StringWriter;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import snomproxy.controlers.Controler;
import snomproxy.server.Server;
import snomproxy.sources.DataSource;

/**
 * Ruft die angeforderten Daten von einer DataSource's ab und/oder sendet
 * Befehle an das Gerät
 * 
 * @author dilli
 */
public abstract class Provider {

    protected Server server;
    protected int status=200;
    protected HashMap<String,DataSource> sources;
    protected HashMap<String, Controler> controlers;

    public Provider() {
        this.sources=new HashMap<String, DataSource>();
        this.controlers=new HashMap<String, Controler>();
    }
    
    public Provider(DataSource source) {
        this();
        this.sources.put("", source);
    }
    
    public void addSource(String name,DataSource source){
        this.sources.put(name, source);
    }
    
    public abstract String getContent(String target, String source, String data);
    public abstract String getContentType();
    public String getCharset(){
        return "utf-8";
    }

    public int getStatus() {
        return status;
    }

    public void setServer(Server server) {
        this.server = server;
    }
    
    public Controler getControler(String target) {
        if (!this.controlers.containsKey(target)){
            this.addControler(target);
        }
        return this.controlers.get(target);
    }

    public void addControler(String target,Controler controler) {
        this.controlers.put(target, controler);
    }
    
    public abstract void addControler(String target);
    
    public static String marshal(Object document) {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(document.getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty("com.sun.xml.internal.bind.xmlHeaders","<?xml-stylesheet type=\"text/xsl\" href=\"SnomIPPhone.xsl\" ?>\n");
            StringWriter w = new StringWriter();
            m.marshal(document, w);

            return w.toString();
        } catch (JAXBException ex) {
            System.out.println(ex);
        }
        return "";
    }
}
