package snomproxy.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author dilli
 */
public abstract class Data {

	protected String title;
	protected ArrayList<HashMap<String, String>> links;
	protected String relocation_target;
	protected int relocation_delay;

	public Data() {
		this.links = new ArrayList<HashMap<String, String>>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<HashMap<String, String>> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<HashMap<String, String>> links) {
		this.links = links;
	}

	public void resetLinks() {
		this.links = new ArrayList<HashMap<String, String>>();
	}

	public HashMap<String, String> addLink(String label, String action) {
		HashMap<String, String> link = new HashMap<String, String>();
		link.put("label", label);
		link.put("action", action);
		return addLink(link);
	}

	public HashMap<String, String> addLink(HashMap<String, String> link) {
		links.add(link);
		return link;
	}

	public void setRelocation(String relocation_target, int relocation_time) {
		this.relocation_target = relocation_target;
		this.relocation_delay = relocation_time;
	}

	public String getRelocationTarget() {
		return relocation_target;
	}

	public void setRelocationTarget(String relocation_target) {
		this.relocation_target = relocation_target;
	}

	public int getRelocationDelay() {
		return relocation_delay;
	}

	public void setRelocationDelay(int relocation_time) {
		this.relocation_delay = relocation_time;
	}
}
