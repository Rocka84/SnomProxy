package snomproxy.sources;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import snomproxy.SnomProxy;
import snomproxy.data.Data;
import snomproxy.server.Server;

/**
 * Ruft Daten vom blau direkt WebService ab
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class BlauDataSource extends TextSource {

	private String url = "https://www.maklerinfo.biz/neu/mods/static/voip/1.1/";
	private String userid = "dillmeier";
	private String secret = "f454907a1c44b60e54fa3cdd382af9a7e30a9501";

	@Override
	public Data request(String data) {
		HashMap<String, String> dataMap = Server.splitData(data);
		text.setTitle("blau direkt");
		text.set("Unbekannter Fehler!");
		text.resetLinks();
		text.addLink("Index", SnomProxy.getServer().getAddressString().concat("/"));
		if (!dataMap.containsKey("search") || dataMap.get("search").isEmpty()) {
			text.set("Fehler: Keine Nummer angegeben!");
		} else {
			try {
				JSONObject json = JSONSource.requestJSON(url.concat("?userid=").concat(userid).concat("&secret=").concat(secret).concat("&search=").concat(dataMap.get("search")));
				if (!json.has("state") || !json.getString("state").equals("OK") || !json.has("items")) {
					text.set("Fehler: Service Fehler");
					//@todo: Fehler-Meldung auswerten
				} else if (json.getJSONArray("items").length() == 0) {
					text.set("Kein Ergebnis");
				} else {
					JSONArray entries = json.getJSONArray("items");
					text.set("");
					text.setTitle("blau direkt - ".concat(String.valueOf(entries.length())).concat(entries.length() == 1 ? " Kontakt" : " Kontakte"));
					for (int entry = 0; entry < entries.length(); entry++) {
						JSONArray zeilen = entries.getJSONObject(entry).getJSONArray("zeilen");
						text.append(String.valueOf(entry + 1)).append(". ");
						for (int zeile = 0; zeile < zeilen.length(); zeile++) {
							text.append(zeilen.getString(zeile)).append(" ");
						}
						text.append("<br>");
					}
				}
			} catch (IOException ex) {
				text.set("Fehler: I/O-Fehler");
			}
		}

		return text;
	}
}
