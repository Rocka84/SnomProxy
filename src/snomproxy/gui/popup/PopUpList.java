package snomproxy.gui.popup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import snomproxy.data.contacts.Contact;
import snomproxy.data.contacts.ContactList;
import snomproxy.data.urls.Url;
import snomproxy.data.urls.UrlList;

/**
 *
 * @author Philip Wassermann <pflipp at gmail.com>, Fabian Dillmeier <fabian at
 * dillmeier.de>
 */
public class PopUpList implements WindowListener, MouseListener {

	private ArrayList<PopUp> popups;
	static Dimension max_bounds;
	private static int gap = 10;

	static {
		max_bounds = Toolkit.getDefaultToolkit().getScreenSize();
		//System.out.println(max_bounds);

		/*
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (GraphicsDevice gd : gs) {
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (GraphicsConfiguration element : gc) {
				Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(element);
				if (screenInsets.bottom!=0 || screenInsets.top!=0 || screenInsets.left!=0 || screenInsets.right!=0){
					System.out.println(screenInsets);
				}
				virtualBounds = virtualBounds.union(element.getBounds());
				//System.out.println(virtualBounds);
			}
		}
		System.out.println(virtualBounds);
		*/
		
		/*
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment();

		Rectangle bounds = gc.getBounds();

		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

		Rectangle effectiveScreenArea = new Rectangle();

		effectiveScreenArea.x = bounds.x + screenInsets.left;
		effectiveScreenArea.y = bounds.y + screenInsets.top;
		effectiveScreenArea.height = bounds.height - screenInsets.top - screenInsets.bottom;
		effectiveScreenArea.width = bounds.width - screenInsets.left - screenInsets.right;
		*/
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		this.popups.remove((PopUp) e.getWindow());
		rePosition();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		((PopUp) e.getWindow()).close();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("asdad");
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void showContactList(ContactList cl) {
		if (this.popups == null) {
			this.popups = new ArrayList<PopUp>();
		}
		for (Contact c : cl) {
			addPopup(new ContactPopUp(c));
		}
	}

	public void showUrlList(UrlList urls) {
		if (this.popups == null) {
			this.popups = new ArrayList<PopUp>();
		}
		for (Url c : urls) {
			JPanel content = new JPanel();
			content.setBackground(new Color(0, 0, 0, 0));
			content.add(new JLabel(c.getText()));
			content.add(new JLabel(c.getHref()));

			addPopup(content);
		}
	}

	public void addPopup(JPanel content) {
		addPopup(new PopUp(content));
	}

	public void addPopup(String content) {
		addPopup(new PopUp(content));
	}

	public void addPopup(String content, boolean html) {
		addPopup(new PopUp(content, html));
	}

	public void addPopup(PopUp popup) {
		if (this.popups == null) {
			this.popups = new ArrayList<PopUp>();
		}
		this.popups.add(popup);
		popup.addWindowListener(this);
		popup.addMouseListener(this);
		rePosition();
		popup.setVisible(true);
	}
	
	public void closeAll(){
		for (PopUp popup : popups) {
			popup.close();
		}
	}

	private void rePosition() {
		int i = 0;
		Rectangle bounds;
		for (PopUp popup : popups) {
			i++;
			bounds = popup.getBounds();
			popup.setBounds(max_bounds.width - (bounds.width + gap), max_bounds.height - (bounds.height + gap) * i, bounds.width, bounds.height);
		}
	}
}