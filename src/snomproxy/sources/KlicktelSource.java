package snomproxy.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import snomproxy.server.Server;
import snomproxy.data.Data;
import snomproxy.data.contacts.Contact;
import snomproxy.data.contacts.ContactList;
import snomproxy.data.menu.Menu;
import snomproxy.data.text.StringData;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class KlicktelSource extends ContactSource{

	private String api_key = "0a64ae57395d5d55a44417b1f8256dbe";
	private String number_searched;
	private String number_normalized;
	private String location;
	private int max_count = 100;
	private int autocomplete = 0;

	public ContactList searchWhitepages(String where, String name) throws MalformedURLException, IOException {
		return searchWhitepages(where, name, autocomplete);
	}

	public ContactList searchWhitepages(String where, String name, int autocomplete) throws MalformedURLException, IOException {
		ContactList contacts = requestJSONContacts("http://openapi.klicktel.de/searchapi/whitepages?key=".concat(api_key).concat("&count=").concat(String.valueOf(max_count)).concat("&where=").concat(where).concat("&name=").concat(name));
		if (autocomplete == -1 || contacts.size() <= autocomplete) {
			for (Contact contact : contacts) {
				if (!completeNumber(contact)) {
					contact.setFirstname(contact.getFirstname().concat(" (X)"));
				}

			}
		}
		return contacts;
	}

	public ContactList searchInvers(String term) throws MalformedURLException, IOException {
		return searchInvers(term, autocomplete);
	}

	public ContactList searchInvers(String term, int autocomplete) throws MalformedURLException, IOException {
		number_searched = term.replaceAll("^\\+", "00").replaceAll("[^\\d]", "");
		ContactList contacts = requestJSONContacts("http://openapi.klicktel.de/searchapi/invers?key=".concat(api_key).concat("&count=").concat(String.valueOf(max_count)).concat("&number=").concat(number_searched));
		if (contacts.size() <= autocomplete) {
			for (Contact contact : contacts) {
				System.out.print("completing ");
				if (completeNumber(contact)) {
					System.out.println("successfull");
				} else {
					System.out.println("failed");
				}

			}
		}
		return contacts;
	}

	@Override
	public Data request(String data) {
		HashMap<String, String> dataMap = Server.splitData(data);
		Data out=contacts;
		if (api_key.isEmpty()) {
			return new StringData("Bitte geben Sie Ihren klickTel-API key an.","klicktel Fehler");
		}
		try {
			if (dataMap.containsKey("search") && !dataMap.get("search").isEmpty()) {
//				if (dataMap.get("search").isEmpty()) {
//					out = new SnomIPPhoneInput();
//					((SnomIPPhoneInput) out).setTitle("klickTel Suche");
//					((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/klicktel"));
//					((SnomIPPhoneInput) out).setInputItem("Ort, Name", "search", "", "n");
//				} else {
					String[] parts = dataMap.get("search").replaceAll("%2C", ",").replaceAll("\\+", " ").split(",");
					String name = (parts.length > 1 ? parts[1] : parts[0]).trim();
					String where = parts.length > 1 ? parts[0].trim() : "";
					out = searchWhitepages(where, name, dataMap.containsKey("autocomplete") ? Integer.valueOf(dataMap.get("autocomplete")) : autocomplete);
					if (((ContactList) out).size() > 0) {
						out.setTitle("Suche \"".concat(name).concat("\" in ").concat(where).concat(" (").concat(String.valueOf(((ContactList) out).getTotalCount()).concat(")")));
					} else {
						out = new StringData("Suche \"".concat(dataMap.get("search")).concat("\""), "Keine Ergebnisse");
					}
//				}
			} else if (dataMap.containsKey("invers") && !dataMap.get("invers").isEmpty()) {
//				if (dataMap.get("invers").isEmpty()) {
//					out = new SnomIPPhoneInput();
//					((SnomIPPhoneInput) out).setTitle("klickTel Invers-Suche");
//					((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/klicktel"));
//					((SnomIPPhoneInput) out).setInputItem("Nummer", "invers", "", "n");
//				} else {				contacts = searchInvers(dataMap.get("invers"), dataMap.containsKey("autocomplete") ? Integer.valueOf(dataMap.get("autocomplete")) : autocomplete);
					if (((ContactList) out).size() > 0) {
						out.setTitle("Suche \"".concat(dataMap.get("invers")).concat("\" (").concat(String.valueOf(((ContactList) out).getTotalCount()).concat(")")));
					} else {
						out = new StringData("Suche \"".concat(dataMap.get("invers")).concat("\""), "Keine Ergebnisse");
					}
//				}
			} else {
				out = new Menu("klickTel");
				((Menu) out).addItem("Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?search"));
				((Menu) out).addItem("Invers-Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?invers"));
			}
		} catch (Exception ex) {
			out = new StringData("klickTel Fehler", "Fehler bei der Suche!");
			if (ex != null && ex.getMessage() == null){
				((StringData) out).append("<br>").append(ex.getMessage());
			}
		}
		out.addLink("Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/"));
		out.addLink("klickTel", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel"));
		out.addLink("Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?search"));
		out.addLink("Invers", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?invers"));
		return out;
	}

	public boolean completeNumber(Contact contact) throws MalformedURLException, IOException {
		return completeNumber(contact, "");
	}

	public boolean completeNumber(Contact contact, String phone_key) throws MalformedURLException, IOException {
		String number = contact.getPhone(phone_key);
		if (!number.endsWith("x")) {
			return true;
		} else if (!number.isEmpty()) {
			ContactList contacts;
			number = number.replace("x+$", "");
			for (int i = 0; i < 10; i++) {
				contacts = searchInvers(number.concat(String.valueOf(i)));
				for (Contact _contact : contacts) {
					if (_contact.getId().equals(contact.getId())) {
						contact.setPhone(phone_key, contact.getPhone(phone_key).replaceFirst("x", String.valueOf(i)));
						return completeNumber(contact, phone_key);
					}
				}
			}
		}
		return false;
	}

	public String getNumberSearched() {
		return number_searched;
	}

	public String getNumberNormalized() {
		return number_normalized;
	}

	public String getLocation() {
		return location;
	}

	public int getMaxCount() {
		return max_count;
	}

	public static ContactList requestJSONContacts(String url) throws MalformedURLException, IOException {
		JSONObject json=JSONSource.requestJSON(url);
		if (!json.has("response")){
			throw new IOException("No response!");
		}
		return jsonToContacts(json.getJSONObject("response"));
	}

	public static ContactList jsonToContacts(JSONObject JSON) {
		ContactList contacts = new ContactList();
		if (JSON.has("results") && JSON.getJSONArray("results").length() > 0) {
			contacts.setTotalCount(JSON.getJSONArray("results").getJSONObject(0).getInt("total"));
			JSONArray entries = JSON.getJSONArray("results").getJSONObject(0).getJSONArray("entries");
			for (int entry = 0; entry < entries.length(); entry++) {
				if (entries.getJSONObject(entry).has("phonenumbers")) {
					Contact contact = new Contact(entries.getJSONObject(entry).getString("id"));
					contact.setLastname(entries.getJSONObject(entry).getString("displayname"));
					JSONArray phones = entries.getJSONObject(entry).getJSONArray("phonenumbers");
					for (int phone = 0; phone < phones.length(); phone++) {
						if (!phones.getJSONObject(phone).getString("type").equals("fax")) {
							contact.setPhone(phones.getJSONObject(phone).getInt("main") == 1 || phones.length() == 1 ? "" : String.valueOf(phone + 1), phones.getJSONObject(phone).getString("displayphone").replaceAll("^\\+", "00").replaceAll("[^\\dx]", ""));
						}
					}
					contacts.addContact(contact);
				}
			}
		}
		return contacts;
	}
}
