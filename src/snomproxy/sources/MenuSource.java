package snomproxy.sources;

import java.util.HashMap;
import snomproxy.SnomProxy;
import snomproxy.server.Server;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneInput;
import snomproxy.xml.snom.SnomIPPhoneMenu;

/**
 *
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
            ((SnomIPPhoneMenu) out).addMenuItem("Call", SnomProxy.getServer().getAddressString().concat("/?menu=call"));
        }else if (!args.get("menu").isEmpty()){
            if (args.get("menu").equals("csv")){
                ((SnomIPPhoneMenu) out).setTitle("SnomProxy - CSV");
                ((SnomIPPhoneMenu) out).addMenuItem("Alles", SnomProxy.getServer().getAddressString().concat("/csv"));
                ((SnomIPPhoneMenu) out).addMenuItem("Suche", SnomProxy.getServer().getAddressString().concat("/?menu=csv_suche"));
            }else if (args.get("menu").equals("csv_suche")){
                out= new SnomIPPhoneInput();
                ((SnomIPPhoneInput) out).setTitle("SnomProxy - CSV");
                ((SnomIPPhoneInput) out).setURL(SnomProxy.getServer().getAddressString().concat("/csv"));
                ((SnomIPPhoneInput) out).setInputItem("Suche", "", "", "a");
            }else if (args.get("menu").equals("call")){
                ((SnomIPPhoneMenu) out).setTitle("SnomProxy - Anrufe");
                ((SnomIPPhoneMenu) out).addMenuItem("Eingehenden Anruf simulieren", SnomProxy.getServer().getAddressString().concat("/call?incoming=0123456789"));
                ((SnomIPPhoneMenu) out).addMenuItem("Angenommenen Anruf simulieren", SnomProxy.getServer().getAddressString().concat("/call?answered=0123456789"));
                ((SnomIPPhoneMenu) out).addMenuItem("Auflegen", SnomProxy.getServer().getAddressString().concat("/call?end=1"));
                ((SnomIPPhoneMenu) out).addMenuItem("Anruf Info", SnomProxy.getServer().getAddressString().concat("/call?info=1"));
            }
        }
        
        
        return out;
    }
}
