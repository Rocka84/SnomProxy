package snomproxy.sources;

import java.util.HashMap;
import snomproxy.SnomProxy;
import snomproxy.data.Data;
import snomproxy.data.menu.Menu;
import snomproxy.server.Server;

/**
 * On-Phone-Menu
 *
 * Ein Menü der Funktionen des Programms zur Navigation auf dem Telefon
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class MainMenuSource extends MenuSource {

	@Override
	public Data request(String data) {
		HashMap<String, String> args = Server.splitData(data);

		Data out = new Menu();
		if (!args.containsKey("menu") || args.get("menu").isEmpty()) {
			out.setTitle("SnomProxy - Main Menu");
			((Menu) out).addItem("CSV", SnomProxy.getServer().getAddressString().concat("/snom/?menu=csv"));
			((Menu) out).addItem("blau", SnomProxy.getServer().getAddressString().concat("/snom/blau"));
			((Menu) out).addItem("Eingehende Anrufe testen", SnomProxy.getServer().getAddressString().concat("/snom/?menu=call"));
			((Menu) out).addItem("Tellows", SnomProxy.getServer().getAddressString().concat("/snom/?menu=tellows"));
		} else if (args.get("menu").equals("csv")) {
			out.setTitle("SnomProxy - CSV");
			((Menu) out).addItem("Alles", SnomProxy.getServer().getAddressString().concat("/snom/csv"));
			((Menu) out).addItem("Suche", SnomProxy.getServer().getAddressString().concat("/snom/?menu=csv_suche"));
		} else if (args.get("menu").equals("csv_suche")) {
//			out = new SnomIPPhoneInput();
//			((SnomIPPhoneInput) out).setTitle("SnomProxy - CSV");
//			((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/snom/csv"));
//			((SnomIPPhoneInput) out).setInputItem("Suche", "search", "", "a");
		} else if (args.get("menu").equals("call")) {
			out.setTitle("SnomProxy - Anrufe");
			((Menu) out).addItem("Eingehenden Anruf simulieren", SnomProxy.getServer().getAddressString().concat("/snom/?menu=call_incoming"));
			((Menu) out).addItem("Angenommenen Anruf simulieren", SnomProxy.getServer().getAddressString().concat("/snom/?menu=call_answered"));
			((Menu) out).addItem("Auflegen", SnomProxy.getServer().getAddressString().concat("/snom/call?end=1"));
			((Menu) out).addItem("Anruf Info", SnomProxy.getServer().getAddressString().concat("/snom/call?info=1"));
		} else if (args.get("menu").equals("call_incoming")) {
//			out = new SnomIPPhoneInput();
//			((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/snom/call"));
//			((SnomIPPhoneInput) out).setInputItem("Nummer", "incoming", "0123456789", "a");
//			((SnomIPPhoneInput) out).setTitle("SnomProxy - Anrufe");
		} else if (args.get("menu").equals("call_answered")) {
//			out = new SnomIPPhoneInput();
//			((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/snom/call"));
//			((SnomIPPhoneInput) out).setInputItem("Nummer", "answered", "0123456789", "a");
//			((SnomIPPhoneInput) out).setTitle("SnomProxy - Anrufe");
		} else if (args.get("menu").equals("tellows")) {
			out.setTitle("SnomProxy - Tellows");
			((Menu) out).addItem("Tellows Suche", SnomProxy.getServer().getAddressString().concat("/snom/?menu=tellows_search"));
		} else if (args.get("menu").equals("tellows_search")) {
//			out = new SnomIPPhoneInput();
//			((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/snom/tellows"));
//			((SnomIPPhoneInput) out).setInputItem("Nummer", "search", "0123456789", "a");
//			((SnomIPPhoneInput) out).setTitle("SnomProxy - Tellows Suche");
		}

		out.addLink("Index", SnomProxy.getServer().getAddressString().concat("/snom/"));

		return out;
	}
}
