package snomproxy.data.text;

import snomproxy.data.Data;

/**
 * Einfacher Wrapper für einen String der das Data-Interface implementiert
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class StringData extends Data {

	private StringBuilder content;

	public StringData() {
		this("","");
	}

	public StringData(CharSequence content) {
		this(content,"");
	}

	public StringData(CharSequence content, String title) {
		this.set(content);
		this.title = title;
		this.resetLinks();
	}

	public StringData(StringBuilder content, String title) {
		this.set(content);
		this.title = title;
		this.resetLinks();
	}

	public StringData set(CharSequence content) {
		return set(new StringBuilder(content));
	}

	public StringData set(StringBuilder content) {
		this.content=content;
		return this;
	}
	
	public StringData append(CharSequence content){
		this.content.append(content);
		return this;
	}
	
	public boolean isEmpty(){
		return content.toString().isEmpty();
	}

	@Override
	public String toString() {
		return content.toString();
	}
}
