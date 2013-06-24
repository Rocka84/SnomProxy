package snomproxy.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import snomproxy.server.Server;
import snomproxy.xml.snom.SnomDocument;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import snomproxy.SnomProxy;
import snomproxy.contacts.Contact;
import snomproxy.contacts.ContactList;
import snomproxy.xml.snom.SnomIPPhoneDirectory;
import snomproxy.xml.snom.SnomIPPhoneInput;
import snomproxy.xml.snom.SnomIPPhoneMenu;
import snomproxy.xml.snom.SnomIPPhoneText;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class KlicktelSource implements DataSource {

	private String api_key = "";
	private String number_searched;
	private String number_normalized;
	private String location;
	private int score = 5;
	private int max_count = 100;
	private int auto_complete = 0;

	private static JSONObject requestJSON(String url) throws MalformedURLException, IOException {
		System.out.println("requestJSON ".concat(url));
		BufferedReader rd = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
		String line;
		StringBuilder response = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		JSONObject out = new JSONObject(response.toString());
		if (response.toString().isEmpty() || !out.has("response")) {
			throw new JSONException("no response");
		}
		return out.getJSONObject("response");
	}

	private static ContactList jsonToContacts(JSONObject JSON) {
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

	public ContactList searchWhitepages(String where, String name) throws MalformedURLException, IOException {
		return jsonToContacts(requestJSON("http://openapi.klicktel.de/searchapi/whitepages?key=".concat(api_key).concat("&count=").concat(String.valueOf(max_count)).concat("&where=").concat(where).concat("&name=").concat(name)));
	}

	public ContactList searchInvers(String term) throws MalformedURLException, IOException {
		number_searched = term.replaceAll("^\\+", "00").replaceAll("[^\\d]", "");
		return jsonToContacts(requestJSON("http://openapi.klicktel.de/searchapi/invers?key=".concat(api_key).concat("&count=").concat(String.valueOf(max_count)).concat("&number=").concat(number_searched)));
	}

	@Override
	public SnomDocument request(String data) {
		HashMap<String, String> dataMap = Server.splitData(data);

		//System.out.println("csv request; action: "+action+" data: "+data);
		SnomDocument out;
		if (api_key.isEmpty()) {
			return new SnomIPPhoneText("klicktel Fehler", "Bitte geben Sie Ihren klickTel-API key an.");
		}
		try {
			ContactList contacts;
			if (dataMap.containsKey("search")) {
				if (dataMap.get("search").isEmpty()) {
					out = new SnomIPPhoneInput();
					((SnomIPPhoneInput) out).setTitle("klickTel Suche");
					((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/klicktel"));
					((SnomIPPhoneInput) out).setInputItem("Ort, Name", "search", "", "n");
				} else {
					String[] parts = dataMap.get("search").replaceAll("%2C", ",").replaceAll("\\+", " ").split(",");
					String name = (parts.length > 1 ? parts[1] : parts[0]).trim();
					String where = parts.length > 1 ? parts[0].trim() : "";
					contacts = searchWhitepages(where, name);
					if (contacts.size() > 0) {
						out = contactsToDirectory(contacts);
						((SnomIPPhoneDirectory) out).setTitle("Suche \"".concat(name).concat("\" in ").concat(where).concat(" (").concat(String.valueOf(contacts.getTotalCount()).concat(")")));
					} else {
						out = new SnomIPPhoneText("Suche \"".concat(dataMap.get("search")).concat("\""), "Keine Ergebnisse");
					}
				}
			} else if (dataMap.containsKey("invers")) {
				if (dataMap.get("invers").isEmpty()) {
					out = new SnomIPPhoneInput();
					((SnomIPPhoneInput) out).setTitle("klickTel Invers-Suche");
					((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/klicktel"));
					((SnomIPPhoneInput) out).setInputItem("Nummer", "invers", "", "n");
				} else {
					contacts = searchInvers(dataMap.get("invers"));
					if (contacts.size() > 0) {
						out = contactsToDirectory(contacts);
						((SnomIPPhoneDirectory) out).setTitle("Suche \"".concat(dataMap.get("invers")).concat("\" (").concat(String.valueOf(contacts.getTotalCount()).concat(")")));
					} else {
						out = new SnomIPPhoneText("Suche \"".concat(dataMap.get("invers")).concat("\""), "Keine Ergebnisse");
					}
				}
			} else {
				out = new SnomIPPhoneMenu("klickTel");
				((SnomIPPhoneMenu) out).addMenuItem("Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?search"));
				((SnomIPPhoneMenu) out).addMenuItem("Invers-Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?invers"));
			}
		} catch (Exception ex) {
			out = new SnomIPPhoneText("klickTel Fehler", "Fehler bei der Suche!".concat(ex == null || ex.getMessage() == null ? "" : "<br>".concat(ex.getMessage())));
		}
		out.addSoftKeyItem("index", "Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/"));
		out.addSoftKeyItem("klicktel_menu", "klickTel", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel"));
		out.addSoftKeyItem("klicktel_suche", "Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?search"));
		out.addSoftKeyItem("klicktel_invers", "Invers", snomproxy.SnomProxy.getServer().getAddressString().concat("/klicktel?invers"));
		return out;
	}

	private SnomIPPhoneDirectory contactsToDirectory(ContactList contacts) throws MalformedURLException, IOException {
		return contactsToDirectory(contacts, new SnomIPPhoneDirectory());
	}

	private SnomIPPhoneDirectory contactsToDirectory(ContactList contacts, SnomIPPhoneDirectory directory) throws MalformedURLException, IOException {
		if (contacts.size() > 0) {
			if (contacts.size() < auto_complete) {
				for (Contact contact : contacts) {
					System.out.print("completing ");
					if (completeNumber(contact)) {
						System.out.println("successfull");
					} else {
						System.out.println("failed");
					}

				}
			} else {
			}
			for (Contact contact : contacts) {
				//System.out.println("contactsToDirectory ".concat(contact.toString()));
				String name = contact.getLastname().concat(contact.getFirstname().isEmpty() ? "" : ", ".concat(contact.getLastname()));
				HashMap<String, String> phones = contact.getPhones();
				for (String pkey : phones.keySet()) {
					directory.addDirectoryEntry(pkey.isEmpty() ? name : name.concat(" (").concat(pkey).concat(")"), phones.get(pkey));
				}
			}
		}
		return directory;
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

	public int getScore() {
		return score;
	}

	public int getMaxCount() {
		return max_count;
	}
}
