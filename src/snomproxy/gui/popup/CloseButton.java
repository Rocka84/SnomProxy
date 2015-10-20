package snomproxy.gui.popup;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author Philip Wassermann <pflipp at gmail.com>
 */
public class CloseButton extends Button {

	public CloseButton() {
		this("");
	}

	public CloseButton(String text) {
		super(text);
//		borderWidth = (byte) 2;
//		borderRadius = (byte) 5;
		target_size = new Dimension(25, 25);
	}

	@Override
	public void paint(Graphics g) {
		this.setBounds(new Rectangle(super.getParent().getWidth() - target_size.width - 5, 5, target_size.width, target_size.height));
		super.paint(g);
	}
}