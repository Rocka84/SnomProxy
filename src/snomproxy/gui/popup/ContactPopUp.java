package snomproxy.gui.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.json.JSONObject;
import sas.swing.MultiLineLabel;
import snomproxy.data.contacts.Contact;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class ContactPopUp extends PopUp {

	private Contact contact;

	public ContactPopUp() {
		super();
	}

	public ContactPopUp(Contact contact) {
		super();
		setContact(contact);
		open();
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;

		JPanel panel = new JPanel(new BorderLayout(3, 3));
		panel.setBackground(new Color(0, 0, 0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 31));
//		panel.setBorder(BorderFactory.createMatteBorder(6, 6, 6, 31,Color.RED));
		JPanel panel_wrapper = new JPanel(new BorderLayout(3, 3));
		panel_wrapper.setBackground(new Color(0, 0, 0, 0));

		JPanel panel_center = new JPanel(new BorderLayout());
		panel_center.setBackground(new Color(0, 0, 0, 0));
		//panel_center.setBorder(BorderFactory.createLineBorder(Color.red));

		if (contact.getImage() != null) {
			panel.add(new JLabel(new ImageIcon(contact.getImage())), BorderLayout.WEST);
		}
		StringBuilder text = new StringBuilder();
		if (contact.getFirstname() != null && !contact.getFirstname().isEmpty()) {
			text.append(contact.getFirstname()).append(" ");
		}
		text.append(contact.getLastname());
		
		MultiLineLabel lbl_ln = new MultiLineLabel(text.toString());
		lbl_ln.setVerticalTextAlignment(JLabel.TOP);
		panel_center.add(lbl_ln, BorderLayout.CENTER);
		panel_wrapper.add(panel_center, BorderLayout.CENTER);

		JPanel panel_bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
//		panel_bottom.setBorder(BorderFactory.createLineBorder(Color.red));
//		panel_bottom.setLayout(new BoxLayout(panel_bottom, BoxLayout.Y_AXIS));
		panel_bottom.setBackground(new Color(0, 0, 0, 0));
		for (String key : contact.getMetaData().keySet()) {
			if (key.startsWith("button_")) {
				final JSONObject json = (JSONObject) contact.getMetaData(key);
				ActionButton btn = new ActionButton(json.getString("title"));
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						browseURL(json.getString("action"));
					}
				});
				panel_bottom.add(btn);
			}
		}
		panel_wrapper.add(panel_bottom, BorderLayout.SOUTH);

		panel.add(panel_wrapper, BorderLayout.CENTER);
		setContent(panel);
	}

	static boolean browseURL(String url) {
		if (java.awt.Desktop.isDesktopSupported()) {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
			if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
				try {
					java.net.URI uri = new java.net.URI(url);
					desktop.browse(uri);
					return true;
				} catch (Exception e) {
				}
			}
		}
		return false;
	}
}
