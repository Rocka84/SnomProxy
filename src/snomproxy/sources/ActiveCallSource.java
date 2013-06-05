package snomproxy.sources;

import java.util.HashMap;
import snomproxy.SnomProxy;
import snomproxy.contacts.Contact;
import snomproxy.contacts.ContactList;
import snomproxy.server.Server;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneText;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class ActiveCallSource implements DataSource {

    private String active_caller = "";
    private ContactList active_caller_data;

    @Override
    public SnomDocument request(String data) {
        HashMap<String, String> args = Server.splitData(data);

//        System.out.println(args);
        CSVDataSource source = (CSVDataSource) SnomProxy.getServer().getProvider().getSource("csv");
        SnomDocument out = null;

        if (args.containsKey("incoming") && !args.get("incoming").isEmpty()) {
            out = new SnomIPPhoneText();
            ((SnomIPPhoneText) out).setTitle("Eingehender Anruf");
            ContactList caller = source.search(args.get("incoming"), ContactList.PHONES);
            if (caller.size() == 1) {
                Contact contact = caller.iterator().next();
                ((SnomIPPhoneText) out).setText(contact.getFirstname().concat(" ").concat(contact.getLastname()).concat(" (").concat(args.get("incoming")).concat(") ruft an."));
            } else {
                String text = "Unbekannter Anrufer (".concat(args.get("incoming")).concat(")");
                if (!caller.isEmpty()) {
                    text = text.concat("<br>").concat(multipleEntries(caller));
                }
                ((SnomIPPhoneText) out).setText(text);
            }
        }
        if (args.containsKey("info") || (args.containsKey("answered") && !args.get("answered").isEmpty())) {
            out = new SnomIPPhoneText();
            if (args.containsKey("answered") && !args.get("answered").isEmpty()) {
                active_caller = args.get("answered");
                active_caller_data = source.search(active_caller, ContactList.PHONES);
                out.setFetch(snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"), 3000);
            }
            ((SnomIPPhoneText) out).setTitle(args.containsKey("answered")?"Anruf angenommen":"Aktuelles Gespräch");
            if (active_caller.isEmpty()) {
                ((SnomIPPhoneText) out).setText("Im Moment ist kein Anruf aktiv.");
            } else if (active_caller_data.size() == 1) {
                Contact contact = active_caller_data.iterator().next();
                ((SnomIPPhoneText) out).setText("Sie sprechen mit<br>".concat(contact.getFirstname()).concat(" ").concat(contact.getLastname()).concat(" (").concat(active_caller).concat(")"));
            } else {
                String text = "Sie sprechen mit<br>".concat(active_caller);
                if (active_caller_data.isEmpty()) {
                    text = text.concat("<br>Keine Kontakte gefunden.");
                } else {
                    text = text.concat("<br>").concat(multipleEntries(active_caller_data));
                }
                ((SnomIPPhoneText) out).setText(text);
            }
            out.addSoftKeyItem("info", "Info", snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"));
        }
        if (args.containsKey("end")) {
            out = new SnomIPPhoneText();
            ((SnomIPPhoneText) out).setTitle("Anruf beendet");
            if (active_caller.isEmpty()) {
                ((SnomIPPhoneText) out).setText("Im Moment ist kein Anruf aktiv.");
            } else if (active_caller_data.size() == 1) {
                Contact contact = active_caller_data.iterator().next();
                ((SnomIPPhoneText) out).setText("Gespräch beendet<br>".concat(contact.getFirstname()).concat(" ").concat(contact.getLastname()).concat(" (").concat(active_caller).concat(")"));
            } else {
                String text = "Gespräch beendet<br>".concat(active_caller);
                if (active_caller_data.isEmpty()) {
                    text = text.concat("<br>Keine Kontakte gefunden.");
                } else {
                    text = text.concat("<br>").concat(multipleEntries(active_caller_data));
                }
                ((SnomIPPhoneText) out).setText(text);
            }
            out.addSoftKeyItem("info", "Info", snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"));
            active_caller = "";
            active_caller_data = new ContactList();
        }
        out.addSoftKeyItem("index", "Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/"));

        return out;
    }

//    private SnomDocument contactCard(String number, String title, boolean answered) {
//        SnomDocument out;
//        out = new SnomIPPhoneText();
//        ((SnomIPPhoneText) out).setTitle(title);
//        
//        if (answered){
//            
//        }
//        if (active_caller_data.size() == 1) {
//            Contact contact = active_caller_data.iterator().next();
//            ((SnomIPPhoneText) out).setText("Anruf von ".concat(contact.getFirstname()).concat(" ").concat(contact.getLastname()).concat(" (").concat(number).concat(")"));
//        } else {
//            String text = "Unbekannter Anrufer (".concat(number).concat(").");
//            if (!active_caller_data.isEmpty()) {
//                text = text.concat("<br>").concat(multipleEntries(active_caller_data));
//            }
//            ((SnomIPPhoneText) out).setText(text);
//        }
//        return out;
//    }

    private String multipleEntries(ContactList contacts) {
        String text = String.valueOf(contacts.size()).concat(" passende Kontakte gefunden.<br>");
        int i = 0;
        for (Contact contact : contacts) {
            text = text.concat("<br>").concat(String.valueOf(++i)).concat(". ").concat(contact.getFirstname()).concat(" ").concat(contact.getLastname());
            if (i >= 3) {
                break;
            }
        }
        if (contacts.size() > 3) {
            text = text.concat("<br>...");
        }
        return text;
    }
}
