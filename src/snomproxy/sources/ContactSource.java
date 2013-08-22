package snomproxy.sources;

import java.util.HashMap;
import snomproxy.data.contacts.Contact;
import snomproxy.data.contacts.ContactList;

/**
 * Eine Datenquelle für Kontakte
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public abstract class ContactSource implements DataSource {

    protected ContactList contacts;

	public ContactSource() {
		contacts=new ContactList();
	}
	
	public Contact addContact(String lastname, String firstname, String phone){
		HashMap<String, String> phones=new HashMap<String, String>();
		phones.put("", phone);
		return addContact(lastname,firstname,phones);
	}

	public Contact addContact(String lastname, String firstname, HashMap<String, String> phones){
		return addContact(new Contact(lastname, firstname, phones));
	}

	public Contact addContact(Contact contact){
		if (contacts==null){
			contacts=new ContactList();
		}
		contacts.addContact(contact);
		return contact;
	}
	
	public void resetContacts() {
		contacts=new ContactList();
	}

    public ContactList search(String term){
        return search(term,ContactList.ALL,false);
    }
    
    public ContactList search(String term,int cols){
        return search(term,cols,false);
    }

    public ContactList search(String term,int cols,boolean exact){
        return contacts.search(term,cols,exact);
    }
    
}
