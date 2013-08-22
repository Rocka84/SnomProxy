package snomproxy.sources;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import snomproxy.SnomProxy;
import snomproxy.data.Data;
import snomproxy.data.contacts.Contact;
import snomproxy.data.text.StringData;
import snomproxy.server.Server;

/**
 * Ruft Daten vom blau direkt WebService ab
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class BlauContactSource extends ContactSource {

	private String url = "https://www.maklerinfo.biz/neu/mods/static/voip/1.1/";
	private String userid = "dillmeier";
	private String secret = "f454907a1c44b60e54fa3cdd382af9a7e30a9501";

	@Override
	public Data request(String data) {
		HashMap<String, String> dataMap = Server.splitData(data);
		if (!dataMap.containsKey("search") || dataMap.get("search").isEmpty()) {
			return new StringData("Fehler: Keine Nummer angegeben!");
		} else {
			try {
				JSONObject json = JSONSource.requestJSON(url.concat("?userid=").concat(userid).concat("&secret=").concat(secret).concat("&search=").concat(dataMap.get("search")));
				if (!json.has("state") || !json.getString("state").equals("OK") || !json.has("items")) {
					return new StringData("Fehler: Service Fehler");
					//@todo: Fehler-Meldung auswerten
				} else if (json.getJSONArray("items").length() > 0) {
					contacts.resetContacts();
					contacts.setTitle("blau direkt");
					contacts.resetLinks();
					contacts.addLink("Index", SnomProxy.getServer().getAddressString().concat("/"));
					JSONArray entries = json.getJSONArray("items");
					contacts.setTitle("blau direkt - ".concat(String.valueOf(entries.length())).concat(entries.length() == 1 ? " Kontakt" : " Kontakte"));
					for (int entry = 0; entry < entries.length(); entry++) {
						JSONArray zeilen = entries.getJSONObject(entry).getJSONArray("zeilen");
						StringBuilder name = new StringBuilder(zeilen.getString(0));
						for (int zeile = 1; zeile < zeilen.length(); zeile++) {
							name.append(" ").append(zeilen.getString(zeile));
						}
						Contact contact = new Contact(md5(entries.getJSONObject(entry).toString()));
						contact.setLastname(name.toString());
						contact.setImageBase64(entries.getJSONObject(entry).getString("icon"));
						contact.setMetaData("button_call", entries.getJSONObject(entry).getJSONObject("button_call"));
						contact.setMetaData("button_hangup", entries.getJSONObject(entry).getJSONObject("button_hangup"));
						contacts.addContact(contact);
					}
				} else {
					return new StringData("Keine Daten gefunden.");
				}
			} catch (Exception ex) {
			}
		}

		return contacts;
	}

	private static String md5(String in) throws NoSuchAlgorithmException {
		byte[] result = MessageDigest.getInstance("MD5").digest(in.getBytes());
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < result.length; i++) {
			if (result[i] <= 15 && result[i] >= 0) {
				out.append("0");
			}
			out.append(Integer.toHexString(0xFF & result[i]));
		}
		return out.toString();
	}
}
