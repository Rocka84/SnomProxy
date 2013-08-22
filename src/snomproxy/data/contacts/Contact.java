package snomproxy.data.contacts;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class Contact {

	private String id;
	private String firstname;
	private String lastname;
	private HashMap<String, String> phones;
	private HashMap<String, Object> meta_data;
	private Image image;
	//private HashMap<String, String> email_adresses;

	public Contact() {
		this("", "", new HashMap<String, String>());
	}

	public Contact(String id) {
		this("", "", new HashMap<String, String>());
		this.id = id;
	}

	public Contact(String lastname, String firstname) {
		this(lastname, firstname, new HashMap<String, String>());
	}

	public Contact(String lastname, String firstname, HashMap<String, String> phones) {
		this.lastname = lastname;
		this.firstname = firstname;
		this.phones = phones;
		this.meta_data=new HashMap<String, Object>();
	}

	public String getId() {
		return id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public HashMap<String, String> getPhones() {
		return phones;
	}

	public void setPhones(HashMap<String, String> phones) {
		this.phones = phones;
	}

	public String getPhone(String key) {
		return phones.get(key);
	}

	public void setPhone(String key, String phone) {
		this.phones.put(key, phone);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setImageBase64(String base64_data) {
		try {
			this.image = ImageIO.read(new ByteArrayInputStream((new BASE64Decoder()).decodeBuffer(base64_data)));
		} catch (IOException ex) {
			this.image=null;
			Logger.getLogger(Contact.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public HashMap<String, Object> getMetaData() {
		return meta_data;
	}

	public Object getMetaData(String key) {
		return meta_data.get(key);
	}

	public void setMetaData(HashMap<String, Object> meta_data) {
		this.meta_data = meta_data;
	}
	
	public void setMetaData(String key, Object value) {
		this.meta_data.put(key, value);
	}
	
	

	@Override
	public String toString() {
		return "[".concat(lastname).concat(", ").concat(firstname).concat(", ").concat(phones.toString()).concat("]");
	}
}
