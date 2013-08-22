package snomproxy.data.urls;

import java.util.HashMap;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class Url {

	private String id;
	private String text;
	private String href;

	public Url() {
		this("", "");
	}

	public Url(String id) {
		this();
		this.id = id;
	}

	public Url(String text, String href) {
		this.text = text;
		this.href = href;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public String toString() {
		return "[".concat(text).concat(", ").concat(href).concat("]");
	}
}
