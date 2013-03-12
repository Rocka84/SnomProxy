package snomproxy.sources;

import java.util.HashMap;
import snomproxy.server.Server;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneText;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class ActiveCallSource implements DataSource {

    private String active_caller="";

    @Override
    public SnomDocument request(String data) {
        HashMap<String, String> args = Server.splitData(data);
        
//        System.out.println(args);
        
        SnomDocument out=null;

        if (args.containsKey("incoming") && !args.get("incoming").isEmpty()) {
            out = new SnomIPPhoneText("Eingehender Anruf", "Anruf von ".concat(args.get("incoming")));
            out.addSoftKeyItem("take_call","Annehmen",snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"));
        }
        if (args.containsKey("answered") && !args.get("answered").isEmpty()) {
            active_caller=args.get("answered");
            out = new SnomIPPhoneText("Anruf angenommen", "Anruf von ".concat(active_caller));
            out.addSoftKeyItem("info","Info",snomproxy.SnomProxy.getServer().getAddressString().concat("/call?info=1"));
        }
        if (args.containsKey("end")) {
            out = new SnomIPPhoneText("Anruf Beendet", active_caller.isEmpty()?"Telefonat beendet.":"Telefonat mit ".concat(active_caller).concat(" beendet."));
            active_caller="";
        }
        
        if (args.containsKey("info")) {
            out = new SnomIPPhoneText("Aktiver Anruf", active_caller.isEmpty()?"Im Moment ist kein Anruf aktiv.":"Max Mustermann (".concat(active_caller).concat(")"));
        }else if (out == null){
            out = new SnomIPPhoneText("Fehler", "ungueltige daten");
        }
        out.addSoftKeyItem("index","Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/"));

        return out;
    }
}
