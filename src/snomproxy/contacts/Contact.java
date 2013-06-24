package snomproxy.contacts;

import java.util.HashMap;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class Contact {
    private String id;
    private String firstname;
    private String lastname;
    private HashMap<String, String> phones;
    //private HashMap<String, String> email_adresses;

    public Contact() {
		this("","",new HashMap<String, String>());
    }

    public Contact(String id) {
        this("","",new HashMap<String, String>());
		this.id=id;
    }
    
    public Contact(String lastname, String firstname) {
        this(lastname,firstname,new HashMap<String, String>());
    }
    
	public Contact(String lastname, String firstname, HashMap<String, String> phones) {
		this.lastname = lastname;
		this.firstname = firstname;
		this.phones = phones;
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

	@Override
	public String toString(){
		return "[".concat(lastname).concat(", ").concat(firstname).concat(", ").concat(phones.toString()).concat("]");
	}
    
}
