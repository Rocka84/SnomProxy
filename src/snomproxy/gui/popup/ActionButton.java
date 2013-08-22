package snomproxy.gui.popup;

import java.awt.Dimension;
import java.awt.Font;

/**
 *
 * @author Philip Wassermann <pflipp at gmail.com>
 */
public class ActionButton extends Button {

	public ActionButton() {
		this("");
	}

	public ActionButton(String text) {
		super(text);
		target_size=new Dimension(120, 25);
		this.setFont(new Font("Arial", 1, 12));
	}

}
