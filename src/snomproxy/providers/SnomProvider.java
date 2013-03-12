package snomproxy.providers;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import snomproxy.SnomProxy;
import snomproxy.controlers.snom.SnomControler;
import snomproxy.sources.DataSource;
import snomproxy.xml.snom.SnomIPPhoneText;

/**
 * Ruft die angeforderten Daten von einer DataSource ab und gibt sie als
 * SnomDocument zurück und/oder sendet Befehle ein SnomIPPhone Telefone
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class SnomProvider extends Provider {

    public SnomProvider(DataSource source) {
        super(source);
    }

    public SnomProvider() {
    }

    @Override
    public String getContentType() {
        String t=this.getTempContentType();
        return t.isEmpty()?"text/xml":t;
    }

    @Override
    public byte[] getContent(String target, String source, String data) {
        System.out.println("SnomProvider.getContent( target: " + target + ", action: " + source + ", data: " + data + " )");
        if (source.equals("SnomIPPhone.xsl")) {
            try {
                return Provider.getFileBytes(new File(SnomProxy.getResource("SnomIPPhone.xsl").toURI()));
            } catch (Exception ex) {
                this.setTempContentType("");
                Logger.getLogger(SnomProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "".getBytes();
        } else if (source.equals("favicon.ico") || source.equals("favicon.png")) {
            try {
                this.setTempContentType("image/png");
                return Provider.getFileBytes(new File(SnomProxy.getResource("favicon.png").toURI()));
            } catch (Exception ex) {
                this.setTempContentType("");
                Logger.getLogger(SnomProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "".getBytes();
        } else if (source.equals("incoming_call") && this.getControler(target) != null) {
            this.getControler(target).showUrl(snomproxy.SnomProxy.getServer().getAddressString().concat("/foobar"));
//            return ""
            return Provider.marshal(new SnomIPPhoneText("URL pushed to ".concat(target), snomproxy.SnomProxy.getServer().getAddressString().concat("/active_call_info"))).getBytes();
        } else if (source.equals("active_call_info")) {
            return Provider.marshal(new SnomIPPhoneText("Aktueller Anruf", "Im Moment ist kein Anruf aktiv")).getBytes();
        } else if (this.sources.containsKey(source)) {
            return Provider.marshal(this.sources.get(source).request(data)).getBytes();
        } else {
            return Provider.marshal(new SnomIPPhoneText("Fehler", "Unbekannte Quelle \"" + source + "\"")).getBytes();
        }

    }

    @Override
    public void addControler(String target) {
        this.addControler(target, new SnomControler(target));
    }
}
