package snomproxy.contacts;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class ContactList implements Iterable<Contact> {

    public static final int ALL = 0;
    public static final int LASTNAME = 1;
    public static final int FIRSTNAME = 2;
    public static final int PHONES = 4;
    private HashMap<String, Contact> contacts;

    public ContactList() {
        this(new HashMap<String, Contact>());
    }

    public ContactList(HashMap<String, Contact> contacts) {
        this.setContacts(contacts);
    }

    public void setContacts(HashMap<String, Contact> contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contact contact) {
        this.contacts.put(contact.getId(), contact);
    }

    public HashMap<String, Contact> getContacts() {
        return contacts;
    }

    public Contact getContact(String id) {
        return contacts.get(id);
    }

    public int size() {
        return contacts.size();
    }

    public boolean isEmpty() {
        return contacts.isEmpty();
    }

    public ContactList search(String term) {
        return search(term, ContactList.ALL,false);
    }

    public ContactList search(String term, int fields) {
        return search(term, fields,false);
    }

    public ContactList search(String term, int fields, boolean exact) {
        ContactList out = new ContactList();

        for (Contact contact : this) {
            if (
                    (
                        (fields==ContactList.ALL || (fields & ContactList.LASTNAME)>0)
                        && ((!exact && contact.getLastname().contains(term)) || contact.getLastname().equals(term))
                    )
                    || (
                        (fields==ContactList.ALL || (fields & ContactList.FIRSTNAME)>0)
                        && ((!exact && contact.getFirstname().contains(term)) || contact.getFirstname().equals(term))
                    )
                    || (
                        (fields==ContactList.ALL || (fields & ContactList.PHONES)>0)
                        && (exact && contact.getPhones().containsValue(term))
                    )
                ){
                
                out.addContact(contact);
            }
            if(!exact && (fields==ContactList.ALL || (fields & ContactList.PHONES)>0)){
                HashMap<String, String> phones= contact.getPhones();
                for (String pkey : phones.keySet()) {
                    if (phones.get(pkey).contains(term)){
                        out.addContact(contact);
                        break;
                    }
                }
            }
        }
        return out;
    }
    
    @Override
    public Iterator<Contact> iterator() {
        return contacts.values().iterator();
    }

}
