package snomproxy.sources;

import snomproxy.contacts.ContactList;

/**
 * Ein lokale CSV-Datenquelle - PoC
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public abstract class ContactsSource implements DataSource {

    protected ContactList contacts;

	public ContactsSource() {
		this.resetContacts();
	}

	public final void resetContacts() {
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
