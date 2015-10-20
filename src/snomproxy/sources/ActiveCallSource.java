package snomproxy.sources;

import java.util.HashMap;
import snomproxy.SnomProxy;
import snomproxy.data.Data;
import snomproxy.data.contacts.Contact;
import snomproxy.data.contacts.ContactList;
import snomproxy.data.text.StringData;
import snomproxy.server.Server;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class ActiveCallSource extends TextSource {

	private String active_caller = "";
	private ContactList active_caller_data;
	private final boolean tellows_available;

	public ActiveCallSource() {
		tellows_available = SnomProxy.getServer().hasSource("tellows");
	}

	@Override
	public Data request(String data) {
		HashMap<String, String> args = Server.splitData(data);

//        System.out.println(args);
//		CSVDataSource source = (CSVDataSource) SnomProxy.getServer().getSource("csv");
		BlauContactSource source = (BlauContactSource) SnomProxy.getServer().getSource("blau");
		text = new StringData();

		if (args.containsKey("incoming") && !args.get("incoming").isEmpty()) {
			text.setTitle("Eingehender Anruf");
			source.request("search=".concat(args.get("incoming")));
			ContactList caller = source.search(args.get("incoming"), ContactList.PHONES);
			if (caller.size() == 1) {
				Contact contact = caller.iterator().next();
				text.set(contact.getFirstname()).append(" ").append(contact.getLastname()).append(" (").append(args.get("incoming")).append(") ruft an.");
			} else {
				text.set("Unbekannter Anrufer (").append(args.get("incoming")).append(")");
				if (!caller.isEmpty()) {
					text.append("<br>").append(multipleEntries(caller));
				}
			}
		}
		if (args.containsKey("info") || (args.containsKey("answered") && !args.get("answered").isEmpty())) {
			if (args.containsKey("answered") && !args.get("answered").isEmpty()) {
				source.request("search=".concat(args.get("answered")));
				active_caller = args.get("answered");
				active_caller_data = source.search(active_caller, ContactList.PHONES);
				text.setRelocation(snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"), 3000);
			}
			text.setTitle(args.containsKey("answered") ? "Anruf angenommen" : "Aktuelles Gespräch");
			if (active_caller.isEmpty()) {
				text.set("Im Moment ist kein Anruf aktiv.");
			} else if (active_caller_data.size() == 1) {
				Contact contact = active_caller_data.iterator().next();
				text.set("Sie sprechen mit<br>").append(contact.getFirstname()).append(" ").append(contact.getLastname()).append(" (").append(active_caller).append(")");
			} else {
				text.set("Sie sprechen mit<br>".concat(active_caller));
				if (active_caller_data.isEmpty()) {
					text.append("<br>Keine Kontakte gefunden.");
				} else {
					text.append("<br>").append(multipleEntries(active_caller_data));
				}
			}
			text.addLink("Info", snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"));
		}
		if (args.containsKey("end")) {
			text.setTitle("Anruf beendet");
			if (active_caller.isEmpty()) {
				text.set("Im Moment ist kein Anruf aktiv.");
			} else if (active_caller_data.size() == 1) {
				Contact contact = active_caller_data.iterator().next();
				text.set("Gespräch beendet<br>").append(contact.getFirstname()).append(" ").append(contact.getLastname()).append(" (").append(active_caller).append(")");
			} else {
				text.set("Gespräch beendet<br>".concat(active_caller));
				if (active_caller_data.isEmpty()) {
					text.append("<br>Keine Kontakte gefunden.");
				} else {
					text.append("<br>").append(multipleEntries(active_caller_data));
				}
			}
			text.addLink("Info", snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"));
			active_caller = "";
			active_caller_data = new ContactList();
		}
		if (text.isEmpty()) {
			text.setTitle("Anruf Fehler");
			text.set("Ungültige Daten");
		}
		text.addLink("Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/"));
		if (tellows_available && (!active_caller.isEmpty() || args.containsKey("incoming"))) {
			text.addLink("Tellows", SnomProxy.getServer().getAddressString().concat("/tellows?search=").concat(args.containsKey("incoming") ? args.get("incoming") : active_caller));
		}

		return text;
	}

	private String multipleEntries(ContactList contacts) {
		StringBuilder txt = new StringBuilder(String.valueOf(contacts.size()).concat(" passende Kontakte gefunden.<br>"));
		int i = 0;
		for (Contact contact : contacts) {
			txt.append("<br>".concat(String.valueOf(++i)).concat(". ").concat(contact.getFirstname()).concat(" ").concat(contact.getLastname()));
			if (i >= 3) {
				break;
			}
		}
		if (contacts.size() > 3) {
			txt.append("<br>...");
		}
		return txt.toString();
	}
}
