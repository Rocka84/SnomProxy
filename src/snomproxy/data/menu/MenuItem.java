package snomproxy.data.menu;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class MenuItem {

	private String label;
	private String action;

	public MenuItem() {
		this("", "");
	}

	public MenuItem(String label, String action) {
		this.label = label;
		this.action = action;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
