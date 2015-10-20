package snomproxy.providers;

import java.io.File;
import java.util.HashMap;
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
public class GuiProvider extends Provider {

	private PopUpList popups;

	public GuiProvider() {
		super();
		this.popups = new PopUpList();
	}

	@Override
	public String getContentType() {
		String t = super.getContentType();
		return t.isEmpty() ? "text/plain" : t;
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
		}
		
		Data out;
		HashMap<String, String> dataMap = Server.splitData(data);
		if (dataMap.containsKey("close") && !dataMap.get("close").isEmpty()) {
			popups.closeAll();
		}
		if (dataMap.containsKey("noop") && !dataMap.get("noop").isEmpty()) {
			out = new StringData("noop");
		}else if (this.server.hasSource(source)) {
			out = this.server.getSource(source).request(data);
			if (out instanceof ContactList) {
				popups.showContactList((ContactList) out);
			} else if (out instanceof StringData) {
				popups.addPopup((((StringData) out).getTitle().isEmpty()?"":((StringData) out).getTitle().concat("<br>\n")).concat(((StringData) out).toString()), true);
			} else {
				popups.addPopup("Unbekannter Datentyp (".concat(out.getClass().getSimpleName()).concat(")."));
			}
			if (!dataMap.containsKey("output_type") || !dataMap.get("output_type").equals("snom")){
				out = new StringData("Ok");
			}
		} else {
			out = new StringData("Unbekannte Quelle \"".concat(source).concat("\""));
			popups.addPopup(out.toString());
		}
		
		
		if (dataMap.containsKey("output_type") && dataMap.get("output_type").equals("snom")){
			this.setTempContentType("text/xml");
			return SnomProvider.marshal(SnomProvider.dataToSnomDocument(out)).getBytes();
		}else{
			return out.toString().getBytes();
		}
	}

	public PopUpList getPopups() {
		return popups;
	}
	
}
