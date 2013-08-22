package snomproxy.providers;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import snomproxy.SnomProxy;
import snomproxy.data.Data;
import snomproxy.data.contacts.Contact;
import snomproxy.data.contacts.ContactList;
import snomproxy.data.menu.Menu;
import snomproxy.data.menu.MenuItem;
import snomproxy.data.text.StringData;
import snomproxy.server.Server;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneDirectory;
import snomproxy.xml.snom.SnomIPPhoneMenu;
import snomproxy.xml.snom.SnomIPPhoneText;

/**
 * Ruft die angeforderten Daten von einer DataSource ab und gibt sie als
 * SnomDocument zurück und/oder sendet Befehle ein SnomIPPhone Telefone
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class SnomProvider extends Provider {

    public SnomProvider() {
        super();
    }

	@Override
    public String getContentType() {
        String t=super.getContentType();
        return t.isEmpty()?"text/xml":t;
    }

	@Override
    public byte[] getContent(String target, String source, String data) {
        if (source.equals("SnomIPPhone.xsl")) {
            try {
                return Server.getFileBytes(new File(SnomProxy.getResource("SnomIPPhone.xsl").toURI()));
            } catch (Exception ex) {
                this.setTempContentType("");
                Logger.getLogger(SnomProvider.class.getName()).log(Level.SEVERE, null, ex);
                return "".getBytes();
            }
        } else if (source.equals("favicon.ico") || source.equals("favicon.png")) {
            try {
                this.setTempContentType("image/png");
                return Server.getFileBytes(new File(SnomProxy.getResource("favicon.png").toURI()));
            } catch (Exception ex) {
                this.setTempContentType("");
                Logger.getLogger(SnomProvider.class.getName()).log(Level.SEVERE, null, ex);
                return "".getBytes();
            }
        } else if (this.server.hasSource(source)) {
            return SnomProvider.marshal(dataToSnomDocument(this.server.getSource(source).request(data))).getBytes();
        } else {
            return SnomProvider.marshal(new SnomIPPhoneText("Fehler", "Unbekannte Quelle \"" + source + "\"")).getBytes();
        }

    }
	
	private static SnomDocument dataToSnomDocument(Data data){
		SnomDocument out;
		if (data instanceof StringData){
			out=new SnomIPPhoneText(data.getTitle(),data.toString());
		}else if (data instanceof ContactList){
			out=new SnomIPPhoneDirectory(data.getTitle());
			for (Contact contact : (ContactList) data){
				HashMap<String, String> phones=contact.getPhones();
				for (String key : phones.keySet()){
					((SnomIPPhoneDirectory) out).addDirectoryEntry(contact.getLastname().concat(", ").concat(contact.getLastname()).concat(key.isEmpty()?"":" - ".concat(key)), phones.get(key));
				}
			}
		}else if (data instanceof Menu){
			out=new SnomIPPhoneMenu(data.getTitle());
			for (MenuItem item : (Menu) data){
				((SnomIPPhoneMenu) out).addMenuItem(item.getLabel(), item.getAction());
			}
		}else{
			out=new SnomIPPhoneText(data.getTitle(),"Nicht unterstüztes Daten-Format: ".concat(data.getClass().getCanonicalName()));
		}
		for (HashMap<String, String> link : data.getLinks()){
			out.addSoftKeyItem(link.get("label"), link.get("label"), link.get("action"));
		}
		if (data.getRelocationTarget()!=null && !data.getRelocationTarget().isEmpty()){
			out.setFetch(data.getRelocationTarget(), data.getRelocationDelay());
		}
		return out;
	}
}
