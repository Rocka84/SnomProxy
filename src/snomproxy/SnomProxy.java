package snomproxy;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import snomproxy.gui.GUI;
import snomproxy.providers.SnomProvider;
import snomproxy.server.Server;
import snomproxy.sources.ActiveCallSource;
import snomproxy.sources.BlauDataSource;
import snomproxy.sources.CSVDataSource;
import snomproxy.sources.MenuSource;
import snomproxy.sources.TellowsSource;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneDirectory;
import snomproxy.xml.snom.SnomIPPhoneImage;
import snomproxy.xml.snom.SnomIPPhoneInput;
import snomproxy.xml.snom.SnomIPPhoneMenu;
import snomproxy.xml.snom.SnomIPPhoneText;

/**
 * Main Controller
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class SnomProxy {

    private static Server server;
    private static GUI gui;
    private static ResourceBundle language;
    private final static Logger logger = Logger.getLogger("snomproxy");
    
    private static boolean testMode=false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initLogger(Level.SEVERE);

        HashMap<String, Object> argsList=parseArgs(args);
        SnomProxy.language = ResourceBundle.getBundle("snomproxy.resources.lang", java.util.Locale.getDefault());

        try {
            SnomProxy.server = new Server(new SnomProvider());

            SnomProxy.server.getProvider().addSource("", new MenuSource());
            SnomProxy.server.getProvider().addSource("blau", new BlauDataSource());
            SnomProxy.server.getProvider().addSource("csv", new CSVDataSource("blau_data.csv"));
            SnomProxy.server.getProvider().addSource("call", new ActiveCallSource());
            SnomProxy.server.getProvider().addSource("tellows", new TellowsSource());

            logger.log(Level.INFO, "Server created");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }


        if (argsList.containsKey("T")) {
            setTestMode(true);
        }
        
        SnomProxy.server.startServer();
        if (!argsList.containsKey("nogui")) {
            gui=new GUI();
        }
    }

    private static void initLogger(Level loglevel) {
        logger.setLevel(loglevel);
        logger.setUseParentHandlers(false);

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(record.getMillis());

                StringBuilder out = new StringBuilder();
                out.append(String.format("[ %02d:%02d:%02d ] ", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)));
                out.append(record.getLevel().getName()).append(": ").append(record.getMessage());
                if (logger.getLevel().intValue() <= Level.FINE.intValue()) {
                    out.append("  [ ").append(record.getSourceClassName()).append(".").append(record.getSourceMethodName()).append("() ]");
                }
                out.append(System.getProperty("line.separator"));

                return out.toString();
            }
        });
        logger.addHandler(handler);
    }

    public static Server getServer() {
        return server;
    }

    public static GUI getGui() {
        return gui;
    }

    public static URL getResource(String name) {
        return SnomProxy.class.getClassLoader().getResource("snomproxy/resources/".concat(name));
    }

    private static void testElems() {

        SnomDocument elem = new SnomIPPhoneText();

        ((SnomIPPhoneText) elem).setTitle("Hello World!");
        ((SnomIPPhoneText) elem).setText("Dies ist ein Text-Element.");
        elem.setFetch(server.getAddressString().concat("/asdf/"), 3000);

        dumpJAXB(elem);



        elem = new SnomIPPhoneInput();
        ((SnomIPPhoneInput) elem).setTitle("SnomIPPhoneInput");
        ((SnomIPPhoneInput) elem).setURL("http://127.0.0.1/asdf/");
        SnomIPPhoneInput.InputItem input = new SnomIPPhoneInput.InputItem();
        input.setDisplayName("Gib was ein;");
        input.setQueryStringParam("&action=foo&antwort=");
        input.setInputFlags("a");
        ((SnomIPPhoneInput) elem).setInputItem(input);

        dumpJAXB(elem);



        elem = new SnomIPPhoneDirectory();
        ((SnomIPPhoneDirectory) elem).setTitle("SnomIPPhoneDirectory");
        SnomIPPhoneDirectory.DirectoryEntry dir_entries[] = new SnomIPPhoneDirectory.DirectoryEntry[3];
        dir_entries[0] = new SnomIPPhoneDirectory.DirectoryEntry();
        dir_entries[0].setName("Name 1");
        dir_entries[0].setTelephone("0 11 1234");
        ((SnomIPPhoneDirectory) elem).getDirectoryEntry().add(dir_entries[0]);

        dir_entries[1] = new SnomIPPhoneDirectory.DirectoryEntry();
        dir_entries[1].setName("Name 2");
        dir_entries[1].setTelephone("0 22 1234");
        ((SnomIPPhoneDirectory) elem).getDirectoryEntry().add(dir_entries[1]);

        dir_entries[2] = new SnomIPPhoneDirectory.DirectoryEntry();
        dir_entries[2].setName("Name 3");
        dir_entries[2].setTelephone("0 33 1234");
        ((SnomIPPhoneDirectory) elem).getDirectoryEntry().add(dir_entries[2]);

        dumpJAXB(elem);



        elem = new SnomIPPhoneMenu();
        ((SnomIPPhoneMenu) elem).setTitle("SnomIPPhoneMenu");
        SnomIPPhoneMenu.MenuItem entries[] = new SnomIPPhoneMenu.MenuItem[3];
        entries[0] = new SnomIPPhoneMenu.MenuItem();
        entries[0].setName("Eintrag 1");
        entries[0].setURL("http://127.0.0.1/asdf/1");
        ((SnomIPPhoneMenu) elem).getMenuItem().add(entries[0]);

        entries[1] = new SnomIPPhoneMenu.MenuItem();
        entries[1].setName("Eintrag 2");
        entries[1].setURL("http://127.0.0.1/asdf/2");
        ((SnomIPPhoneMenu) elem).getMenuItem().add(entries[1]);

        entries[2] = new SnomIPPhoneMenu.MenuItem();
        entries[2].setName("Eintrag 3");
        entries[2].setURL("http://127.0.0.1/asdf/3");
        ((SnomIPPhoneMenu) elem).getMenuItem().add(entries[2]);


        dumpJAXB(elem);



        elem = new SnomIPPhoneImage();
        SnomIPPhoneImage.Data data = new SnomIPPhoneImage.Data();
        data.setValue(("blablab.base64.zeug.blubber").getBytes());
        ((SnomIPPhoneImage) elem).setData(data);


        dumpJAXB(elem);

    }

    public static void dumpJAXB(SnomDocument document) {
        // create JAXB context and instantiate marshaller
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(document.getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter w = new StringWriter();
            m.marshal(document, w);

            System.out.println(w.toString());
        } catch (JAXBException ex) {
            System.out.println(ex);
        }
    }

    public static String getLanguageString(String key) {
        return SnomProxy.language.getString(key);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static void setTestMode(boolean testMode) {
		logger.setLevel(Level.ALL);
        SnomProxy.testMode = testMode;
    }

    private static HashMap<String, Object> parseArgs(String[] args) {
        HashMap<String, Object> argsList = new HashMap<String, Object>();
        ArrayList<String> defaults=new ArrayList<String>();
        String var;
        for (int i = 0; i < args.length; i++) {
            switch (args[i].charAt(0)) {
                case '-':
                    if (args[i].charAt(1) == '-') {
                        var=args[i].substring(2, args[i].length());
                        if (!var.isEmpty()){
                            argsList.put(var, (i<args.length-1 && args[i+1].charAt(0)!='-' )?args[++i]:null);
                        }
                    } else {
                        var=args[i].substring(1, 2);
                        argsList.put(var, args[i].substring(2, args[i].length()));
                    }
                    break;
                default:
                    defaults.add(args[i]);
                    break;
            }
        }
        if (!defaults.isEmpty()){
            argsList.put("_defaults",defaults);
        }
        return argsList;
    }
}
