package snomproxy.data.urls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import snomproxy.data.Data;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class UrlList extends Data implements Iterable<Url> {

	public static final int ALL = 0;
	public static final int TEXT = 1;
	public static final int URL = 2;
	//
	private HashMap<String, Url> urls;
	protected int total_count = 0;

	public UrlList() {
		this(new HashMap<String, Url>());
	}

	public UrlList(HashMap<String, Url> urls) {
		this(urls,"");
	}

	public UrlList(String title) {
		this(new HashMap<String, Url>(),title);
	}

	public UrlList(HashMap<String, Url> urls, String title) {
		this.title = title;
		this.urls = urls;
		this.links = new ArrayList<HashMap<String, String>>();
	}

	public void setUrls(HashMap<String, Url> urls) {
		this.urls = urls;
	}

	public void addUrl(Url url) {
		this.urls.put(url.getId(), url);
	}

	public HashMap<String, Url> getUrls() {
		return urls;
	}

	public Url getUrl(String id) {
		return urls.get(id);
	}

	public int size() {
		return urls.size();
	}

	public boolean isEmpty() {
		return urls.isEmpty();
	}

	public int getTotalCount() {
		return total_count < this.size() ? this.size() : total_count;
	}

	public void setTotalCount(int total_count) {
		this.total_count = total_count;
	}

	public UrlList search(String term) {
		return search(term, UrlList.ALL, false);
	}

	public UrlList search(String term, int fields) {
		return search(term, fields, false);
	}

	public UrlList search(String term, int fields, boolean exact) {
		UrlList out = new UrlList();

		for (Url url : this) {
			if (
					((fields == UrlList.ALL || (fields & UrlList.TEXT) > 0)
						&& ((!exact && url.getText().contains(term)) || url.getText().equals(term)))
					
					|| ((fields == UrlList.ALL || (fields & UrlList.URL) > 0)
						&& ((!exact && url.getHref().contains(term)) || url.getHref().equals(term)))
					) {
				out.addUrl(url);
			}
		}
		return out;
	}

	@Override
	public Iterator<Url> iterator() {
		return urls.values().iterator();
	}
}
