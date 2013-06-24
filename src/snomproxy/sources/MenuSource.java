package snomproxy.sources;

import java.util.HashMap;
import snomproxy.SnomProxy;
import snomproxy.server.Server;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneInput;
import snomproxy.xml.snom.SnomIPPhoneMenu;

/**
 * On-Phone-Menu
 * 
 * Ein Menü der Funktionen des Programms zur Navigation auf dem Telefon
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class MenuSource implements DataSource {

    @Override
    public SnomDocument request(String data) {
        HashMap<String, String> args = Server.splitData(data);
        
        SnomDocument out=null;

        
        out=new SnomIPPhoneMenu();
        out.addSoftKeyItem("index","Index", SnomProxy.getServer().getAddressString().concat("/"));
        if (!args.containsKey("menu") || args.get("menu").isEmpty()){
            ((SnomIPPhoneMenu) out).setTitle("SnomProxy - Main Menu");
            ((SnomIPPhoneMenu) out).addMenuItem("CSV", SnomProxy.getServer().getAddressString().concat("/?menu=csv"));
            ((SnomIPPhoneMenu) out).addMenuItem("blau", SnomProxy.getServer().getAddressString().concat("/blau"));
            ((SnomIPPhoneMenu) out).addMenuItem("Eingehende Anrufe testen", SnomProxy.getServer().getAddressString().concat("/?menu=call"));
            ((SnomIPPhoneMenu) out).addMenuItem("Tellows", SnomProxy.getServer().getAddressString().concat("/?menu=tellows"));
        }else if (!args.get("menu").isEmpty()){
            if (args.get("menu").equals("csv")){
                ((SnomIPPhoneMenu) out).setTitle("SnomProxy - CSV");
                ((SnomIPPhoneMenu) out).addMenuItem("Alles", SnomProxy.getServer().getAddressString().concat("/csv"));
                ((SnomIPPhoneMenu) out).addMenuItem("Suche", SnomProxy.getServer().getAddressString().concat("/?menu=csv_suche"));
            }else if (args.get("menu").equals("csv_suche")){
                out= new SnomIPPhoneInput();
                ((SnomIPPhoneInput) out).setTitle("SnomProxy - CSV");
                ((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/csv"));
                ((SnomIPPhoneInput) out).setInputItem("Suche", "search", "", "a");
            }else if (args.get("menu").equals("call")){
                ((SnomIPPhoneMenu) out).setTitle("SnomProxy - Anrufe");
                ((SnomIPPhoneMenu) out).addMenuItem("Eingehenden Anruf simulieren", SnomProxy.getServer().getAddressString().concat("/?menu=call_incoming"));
                ((SnomIPPhoneMenu) out).addMenuItem("Angenommenen Anruf simulieren", SnomProxy.getServer().getAddressString().concat("/?menu=call_answered"));
                ((SnomIPPhoneMenu) out).addMenuItem("Auflegen", SnomProxy.getServer().getAddressString().concat("/call?end=1"));
                ((SnomIPPhoneMenu) out).addMenuItem("Anruf Info", SnomProxy.getServer().getAddressString().concat("/call?info=1"));
            }else if (args.get("menu").equals("call_incoming")){
                out=new SnomIPPhoneInput();
                ((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/call"));
                ((SnomIPPhoneInput) out).setInputItem("Nummer", "incoming", "0123456789", "a");
                ((SnomIPPhoneInput) out).setTitle("SnomProxy - Anrufe");
            }else if (args.get("menu").equals("call_answered")){
                out=new SnomIPPhoneInput();
                ((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/call"));
                ((SnomIPPhoneInput) out).setInputItem("Nummer", "answered", "0123456789", "a");
                ((SnomIPPhoneInput) out).setTitle("SnomProxy - Anrufe");
            }else if (args.get("menu").equals("tellows")){
                ((SnomIPPhoneMenu) out).setTitle("SnomProxy - Tellows");
                ((SnomIPPhoneMenu) out).addMenuItem("Tellows Suche", SnomProxy.getServer().getAddressString().concat("/?menu=tellows_search"));
            }else if (args.get("menu").equals("tellows_search")){
                out=new SnomIPPhoneInput();
                ((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/tellows"));
                ((SnomIPPhoneInput) out).setInputItem("Nummer", "search", "0123456789", "a");
                ((SnomIPPhoneInput) out).setTitle("SnomProxy - Tellows Suche");
            }
        }
        
        return out;
    }
}
