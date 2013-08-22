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
import snomproxy.controlers.snom.SnomControler;
import snomproxy.server.Server;

/**
 * Ruft die angeforderten Daten von einer DataSource ab und gibt sie als
 * SnomDocument zurück und/oder sendet Befehle ein SnomIPPhone Telefone
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public abstract class Provider {

    protected Server server;
    protected int status = 200;
    private String temp_content_type = "";
    protected HashMap<String, Controler> controlers;

    public Provider() {
		this.controlers = new HashMap<String, Controler>();
    }

    public String getContentType() {
        return this.getTempContentType();
    }

    public abstract byte[] getContent(String target, String source, String data);

    public void addControler(String target) {
        this.addControler(target, new SnomControler(target));
    }

    public void addControler(String target, Controler controler) {
        this.controlers.put(target, controler);
    }


    protected void setTempContentType(String temp_content_type) {
        this.temp_content_type = temp_content_type;
    }

    protected String getTempContentType() {
        String t = temp_content_type;
        temp_content_type = "";
        return t;
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

    public static String marshal(Object document) {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(document.getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            if (SnomProxy.isTestMode()){
                m.setProperty("com.sun.xml.internal.bind.xmlHeaders", "<?xml-stylesheet type=\"text/xsl\" href=\"/snom/SnomIPPhone.xsl\" ?>\n");
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

}
