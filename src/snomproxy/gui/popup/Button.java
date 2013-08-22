package snomproxy.gui.popup;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JButton;

/**
 *
 * @author Philip Wassermann <pflipp at gmail.com>
 */
public class Button extends JButton {

	protected byte borderWidth = 2;
	protected byte borderRadius = 4;
	protected Color borderColor = Color.BLACK;
	protected Dimension target_size;

	public Button() {
		this("");
	}

	public Button(String text) {
		super(text);
		super.setBorderPainted(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		target_size = new Dimension(50, 25);
		this.setFont(new Font("Arial", 1, 16));
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(this.borderColor);
		g2.fillRoundRect(0, 0, target_size.width, target_size.height, this.borderRadius, this.borderRadius);
		g2.setColor(this.getBackground());
		g2.fillRoundRect(this.borderWidth, this.borderWidth, target_size.width - this.borderWidth * 2, target_size.height - this.borderWidth * 2, this.borderRadius, this.borderRadius);
		g2.setColor(this.getForeground());
		g2.setFont(this.getFont());
		Rectangle2D r2 = g2.getFont().getStringBounds(this.getText(), g2.getFontRenderContext());

		if (r2.getWidth() < target_size.width) {
			g2.drawString(this.getText(), (float) ((((target_size.width - 2) / 2) - r2.getWidth() / 2) + 1), (float) (((target_size.height - 2) / 2 + r2.getHeight() / 2)) - 1);
		} else {
			g2.clipRect(0, 0, target_size.width - 3, target_size.height);
			g2.drawString(this.getText(), 0, (float) (((target_size.height - 2) / 2 + r2.getHeight() / 2)) - 1);
		}
	}

	public byte getBorderWidth() {
		return this.borderWidth;
	}

	public void setBorderWidth(byte borderWidth) {
		this.borderWidth = borderWidth;
	}

	public byte getBorderRadius() {
		return this.borderRadius;
	}

	public void setBorderRadius(byte borderRadius) {
		this.borderRadius = borderRadius;
	}

	public Color getBorderColor() {
		return this.borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
}
