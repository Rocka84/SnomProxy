package snomproxy.providers;

import java.io.File;
import java.util.logging.Level;
import snomproxy.SnomProxy;
import snomproxy.data.Data;
import snomproxy.data.contacts.ContactList;
import snomproxy.data.text.StringData;
import snomproxy.gui.popup.PopUpList;
import snomproxy.server.Server;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class GuiProvider extends Provider{

	private PopUpList popups;

	public GuiProvider() {
		super();
		this.popups = new PopUpList();
	}
	
	@Override
    public String getContentType() {
        String t=super.getContentType();
        return t.isEmpty()?"text/plain":t;
    }
	
	@Override
	public byte[] getContent(String target, String source, String data) {
		if (source.equals("favicon.ico") || source.equals("favicon.png")) {
            try {
                this.setTempContentType("image/png");
                return Server.getFileBytes(new File(SnomProxy.getResource("favicon.png").toURI()));
            } catch (Exception ex) {
                this.setTempContentType("");
                SnomProxy.getLogger().log(Level.SEVERE, null, ex);
                return "favicon not found".getBytes();
            }
        } else if (this.server.hasSource(source)) {
            Data out=this.server.getSource(source).request(data);
			if (out instanceof ContactList){
				popups.showContactList((ContactList) out);
			}else if (out instanceof StringData){
				popups.addPopup(((StringData) out).toString(),true);
			}else{
				popups.addPopup("Unbekannter Datentyp (".concat(out.getClass().getSimpleName()).concat(")."));
			}
        } else {
			popups.addPopup("Unbekannte Quelle \"".concat(source).concat("\" ").concat(String.valueOf(Math.round(Math.random()*1000))));
            return "Unbekannte Quelle \"".concat(source).concat("\"").getBytes();
        }
		return "OK".getBytes();
	}

}
