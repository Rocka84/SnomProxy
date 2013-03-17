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
    }
    
    public Contact(String id) {
        this.id = id;
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

    
}
