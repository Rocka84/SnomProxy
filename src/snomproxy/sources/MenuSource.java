package snomproxy.sources;

import snomproxy.data.menu.Menu;
import snomproxy.data.menu.MenuItem;

/**
 * Eine Datenquelle für Texte
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public abstract class MenuSource implements DataSource {

	protected Menu menu;

	public MenuSource() {
		this(new Menu());
	}

	public MenuSource(Menu menu) {
		this.menu = menu;
	}

	public Menu getMenu() {
		return menu;
	}

}
