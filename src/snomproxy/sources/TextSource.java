package snomproxy.sources;

import snomproxy.data.text.StringData;

/**
 * Eine Datenquelle für Texte
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public abstract class TextSource implements DataSource {

	protected StringData text;

	public TextSource() {
		text = new StringData();
	}

	public void resetText() {
		text = new StringData();
	}

	public StringData getText() {
		return text;
	}
}
