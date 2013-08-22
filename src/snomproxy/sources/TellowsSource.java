package snomproxy.sources;

import java.io.IOException;
import java.util.HashMap;
import snomproxy.server.Server;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import snomproxy.data.Data;

/**
 * Tellows-Scoring-Source
 * 
 * Ruft ein Scoring und weitere Daten zu einer Telefonnummer von tellows.de ab.
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class TellowsSource extends TextSource {

	private String api_partner = "test";
	private String api_key = "test123";
	private String number_searched;
	private String number_normalized;
	private String location;
	private int score = 5;
	private int search_count = 0;
	private int comment_count = 5;
	private ArrayList<HashMap<String, String>> types;

	public TellowsSource() {
		types = new ArrayList<HashMap<String, String>>();
	}

	private static String getValue(Node node) {
		try { // yes, I was lazy here
			return ((Node) node.getChildNodes().item(0)).getNodeValue();
		} catch (Exception ex) {
			return "";
		}
	}

	public boolean search(String term) throws IOException, SAXException, ParserConfigurationException {
		if (!term.isEmpty()) {

			number_searched = term.replaceAll("^\\+", "00").replaceAll("[^\\d]", "");
			URLConnection conn = new URL("http://www.tellows.de/basic/num/".concat(number_searched).concat("?xml=1&partner=").concat(api_partner).concat("&apikey=").concat(api_key)).openConnection();

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList nodes = doc.getElementsByTagName("tellows");

			if (nodes.getLength() == 1) {
				Element root_element = (Element) nodes.item(0);
				nodes = root_element.getElementsByTagName("normalizedNumber");
				if (nodes.getLength() == 1) {
					number_normalized = getValue(nodes.item(0));
				} else {
					nodes = root_element.getElementsByTagName("number");
					if (nodes.getLength() == 1) {
						number_normalized = getValue(nodes.item(0));
					} else {
						number_normalized = number_searched;
					}
				}

				nodes = root_element.getElementsByTagName("score");
				score = nodes.getLength() == 1 ? Integer.parseInt(getValue(nodes.item(0))) : 0;

				nodes = root_element.getElementsByTagName("location");
				location = (nodes.getLength() == 1 && !getValue(nodes.item(0)).isEmpty()) ? getValue(nodes.item(0)) : "";

				nodes = root_element.getElementsByTagName("searches");
				search_count = nodes.getLength() == 1 ? Integer.parseInt(getValue(nodes.item(0))) : 0;

				nodes = root_element.getElementsByTagName("comments");
				comment_count = nodes.getLength() == 1 ? Integer.parseInt(getValue(nodes.item(0))) : 0;

				nodes = root_element.getElementsByTagName("callerTypes");
				types = new ArrayList<HashMap<String, String>>();
				if (nodes.getLength() > 0) {
					nodes = root_element.getElementsByTagName("caller");
					if (nodes.getLength() > 0) {
						HashMap<String, String> type;
						NodeList type_nodes;
						for (int i = 0; i < nodes.getLength() && i < 4; i++) {
							type = new HashMap<String, String>();
							type_nodes = ((Element) nodes.item(i)).getElementsByTagName("name");
							if (type_nodes.getLength() == 1) {
								type.put("name", getValue(type_nodes.item(0)));
							}

							type_nodes = ((Element) nodes.item(i)).getElementsByTagName("count");
							if (type_nodes.getLength() == 1) {
								type.put("count", getValue(type_nodes.item(0)));
							}
							types.add(type);
						}
					}
				}

				return true;
			}
		}

		number_normalized = number_searched;
		location = "";
		score = 5;
		search_count = 0;
		comment_count = 5;
		types = new ArrayList<HashMap<String, String>>();

		return false;
	}

	@Override
	public Data request(String data) {
		HashMap<String, String> dataMap = Server.splitData(data);
		resetText();
		text.addLink( "Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/"));
		text.addLink("Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/?menu=tellows_search"));
		try {
			if (!dataMap.containsKey("search") || dataMap.get("search").isEmpty()) {
				text.setTitle("Tellows-Suche FEHLER");
				text.set("Keine Nummer angegeben!");
			} else if (!search(dataMap.get("search"))) {
				text.setTitle("Tellows-Suche FEHLER");
				text.set("Keine Ergebnisse!");
			} else {
				text.setTitle("Tellows-Suche \"".concat(String.valueOf(number_normalized)).concat("\""));

				text = text.append("Score: ").append(String.valueOf(score));
				if (score >= 7) {
					text.append(" (--)");
				} else if (score > 5) {
					text.append(" (-)");
				} else if (score <= 3) {
					text.append(" (++)");
				} else if (score < 5) {
					text.append(" (+)");
//				}else{
//					text.append(" (/)");
				}
				text.append("<br>");

				if (!location.isEmpty()) {
					text.append(location).append("<br>");
				}

				text.append(search_count == 0 ? "Keine " : String.valueOf(search_count)).append(" Suchanfragen, ")
						.append(comment_count == 0 ? "Keine " : String.valueOf(comment_count)).append(" Kommentare").append("<br>");

				if (types.size() > 0) {
					text.append("Einstufungen:<br>");
					for (HashMap<String, String> type : types) {
						if (!type.get("name").isEmpty()) {
							text.append(type.get("name"));
							if (!type.get("count").isEmpty()) {
								text.append(" (").append(type.get("count")).append(" mal)");
							}
							text.append("<br>");
						}
					}
				}
			}
		} catch (Exception ex) {
			text.setTitle("Tellows-Suche FEHLER");
			text.set("Fehler bei der Abfrager der Daten!");
			Logger.getLogger("snomproxy.sources.TellowsSource").log(Level.SEVERE, ex.getMessage());
//			ex.printStackTrace();
		}

		return text;
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

	public int getSearchCount() {
		return search_count;
	}

	public int getCommentCount() {
		return comment_count;
	}

	public ArrayList<HashMap<String, String>> getTypes() {
		return types;
	}
}
