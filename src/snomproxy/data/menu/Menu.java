package snomproxy.data.menu;

import java.util.ArrayList;
import java.util.Iterator;
import snomproxy.data.Data;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class Menu extends Data implements Iterable<MenuItem> {

	private ArrayList<MenuItem> entries;

	public Menu() {
		this(new ArrayList<MenuItem>(),"");
	}

	public Menu(String title) {
		this(new ArrayList<MenuItem>(),title);
	}

	public Menu(ArrayList<MenuItem> entries) {
		this(entries,"");
	}

	public Menu(ArrayList<MenuItem> entries, String title) {
		this.entries = entries;
		this.title = title;
		this.resetLinks();
	}

	public void resetItems(){
		this.entries=new ArrayList<MenuItem>();
	}

	public MenuItem addItem(String label, String action) {
		return addItem(new MenuItem(label, action));
	}

	public MenuItem addItem(MenuItem item) {
		entries.add(item);
		return item;
	}

	@Override
	public Iterator<MenuItem> iterator() {
		return entries.iterator();
	}
}
