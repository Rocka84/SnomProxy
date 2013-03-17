package snomproxy.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import snomproxy.SnomProxy;
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
    protected int status = 200;
    protected HashMap<String, DataSource> sources;
    protected HashMap<String, Controler> controlers;
    private String temp_content_type = "";

    public Provider() {
        this.sources = new HashMap<String, DataSource>();
        this.controlers = new HashMap<String, Controler>();
    }

    public Provider(DataSource source) {
        this();
        this.sources.put("", source);
    }

    public void addSource(String name, DataSource source) {
        this.sources.put(name, source);
    }

    public DataSource getSource(String name) {
        return this.sources.get(name);
    }

    public abstract byte[] getContent(String target, String source, String data);

    public String getTemp_content_type() {
        return temp_content_type;
    }

    protected void setTempContentType(String temp_content_type) {
        this.temp_content_type = temp_content_type;
    }

    protected String getTempContentType() {
        String t = temp_content_type;
        temp_content_type = "";
        return t;
    }

    public String getContentType() {
        String t=this.getTempContentType();
        return t.isEmpty()?"text/plain":t;
    }

    public String getCharset() {
        return "utf-8";
    }

    public int getStatus() {
        return status;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Controler getControler(String target) {
        if (!this.controlers.containsKey(target)) {
            this.addControler(target);
        }
        return this.controlers.get(target);
    }

    public void addControler(String target, Controler controler) {
        this.controlers.put(target, controler);
    }

    public abstract void addControler(String target);

    public static String marshal(Object document) {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(document.getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            if (SnomProxy.isTestMode()){
                m.setProperty("com.sun.xml.internal.bind.xmlHeaders", "<?xml-stylesheet type=\"text/xsl\" href=\"/SnomIPPhone.xsl\" ?>\n");
            }
            StringWriter w = new StringWriter();
            m.marshal(document, w);
            
            if (SnomProxy.isTestMode()){
                return w.toString();
            }else{
                //Snoms xml implementation allows <br> tags
                return w.toString().replace("&lt;br&gt;","<br>");
            }
        } catch (JAXBException ex) {
            System.out.println(ex);
        }
        return "";
    }

    public static byte[] getFileBytes(File file) throws FileNotFoundException, IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        byte[] b = new byte[(int) f.length()];
        f.read(b);
        return b;
    }
}
