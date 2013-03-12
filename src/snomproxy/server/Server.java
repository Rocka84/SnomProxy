package snomproxy.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import snomproxy.providers.Provider;

/**
 * Wartet auf eingehende Verbindungen und ruft den gewünschten Inhalt vom
 * Content Provider ab
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class Server extends Observable {

    private ServerSocket serverSocket;
    Provider provider;
    private int port = 8180;
    private boolean running = false;
    private InetAddress address;
    private static int worker_active_count=0;
    private int worker_count=3;

    private final static Logger logger = Logger.getLogger("phoneproxy.server");

//    private static Server INSTANCE;
//    public static Server getInstance(Provider contentProvider) {
//        return Server.getInstance(contentProvider,8180,false);
//    }
//
//    public static Server getInstance(Provider contentProvider, int port) {
//        return Server.getInstance(contentProvider,port,false);
//    }
//
//    public static Server getInstance(Provider contentProvider, int port, boolean autorun) {
//        if (Server.INSTANCE==null){
//            Server.INSTANCE=new Server(contentProvider, port, autorun);
//        }
//        return Server.INSTANCE;
//    }
    
    public Server(Provider contentProvider) {
        this(contentProvider, 8180, false);
    }

    public Server(Provider contentProvider, int port) {
        this(contentProvider, port, false);
    }

    public Server(Provider contentProvider, int port, boolean autorun) {       
        try {
            //@TODO Geht das ermitteln der Adresse auch einfacher?
            this.address = null;
            Enumeration ifaces = NetworkInterface.getNetworkInterfaces();
            for (Object iface : Collections.list(ifaces)) {
                Enumeration virtualIfaces = ((NetworkInterface) iface).getSubInterfaces();
                for (Object viface : Collections.list(virtualIfaces)) {
                    Enumeration vaddrs = ((NetworkInterface) viface).getInetAddresses();
                    for (Object vaddr : Collections.list(vaddrs)) {
                        if (vaddr instanceof Inet4Address && !((Inet4Address) vaddr).getHostAddress().startsWith("127.")) {
                            this.address = (Inet4Address) vaddr;
                            break;
                        }
                    }
                }
                if (this.address == null) {
                    Enumeration raddrs = ((NetworkInterface) iface).getInetAddresses();
                    for (Object raddr : Collections.list(raddrs)) {
                        if (raddr instanceof Inet4Address && !((Inet4Address) raddr).getHostAddress().startsWith("127.")) {
                            this.address = (Inet4Address) raddr;
                            break;
                        }
                    }
                }
            }
            if (this.address == null) {
                this.address = InetAddress.getLocalHost();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Server Exception; " + e.getMessage());
        }

        this.provider = contentProvider;
        this.provider.setServer(this);
        this.port = port;
        if (autorun) {
            this.startServer();
        }
    }

    public void startServer() {
        if (!this.isRunning()) {
            try {
                this.running = true;
                logger.log(Level.INFO,"Server running at ".concat(getAddress().getHostAddress()).concat(":").concat(String.valueOf(getPort())));
                serverSocket = new ServerSocket(getPort(), 1, getAddress());

                for (int i=0;i<this.worker_count;i++){
                    new Thread(new Worker()).start();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE,"Server Error: ".concat(ex.getMessage()));
            }
        }
    }

    public void stopServer() {
        this.running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            logger.log(Level.INFO,"Server shutting down...");
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
            }
        }
    }

    public int getPort() {
        return port;
    }

    public Provider getProvider() {
        return provider;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public String getAddressString() {
        return "http://".concat(this.address.getHostAddress()).concat(":").concat(String.valueOf(this.port));
    }

    public boolean isRunning() {
        return running;
    }

    private byte[] getContent(String target, String action, String data) {
        return this.provider.getContent(target, action, data);
        //System.out.println("Server.getContent( action: "+action+", data: "+data+" )");
//        String out = this.provider.getContent(target, action, data);
//        return this.buildHeader(out.length()) + out;
    }

    public void setWorkerCount(int worker_count) {
        this.worker_count = worker_count;
    }
    
    private String buildHeader(final int stringLength) {
        String out = "HTTP/1.1 " + Integer.toString(this.provider.getStatus()) + " ";
        switch (this.provider.getStatus()) {
            case 200:
                out = out.concat("OK");
                break;
            case 404:
                out = out.concat("NOT FOUND");
                break;
            default:
                out = out.concat("UNKNOWN");
                break;
        }
        return out + "\n"
                + "Content-Length: "
                + stringLength + "\n"
                //+ "Content-Type: text/html\n"
                + "Content-Type: " + this.provider.getContentType() + "; charset=" + this.provider.getCharset() + "\n"
                + "Connection: close\n\n";
    }

    public static Logger getLogger() {
        return logger;
    }
    
    public static HashMap<String, String> splitData(String data){
        String[] data_parts = data.split("&");
        HashMap<String, String> args = new HashMap<String, String>();
        for (int i = 0; i < data_parts.length; i++) {
            String[] arg_parts = data_parts[i].split("=");
            if (arg_parts.length == 2) {
                args.put(arg_parts[0], arg_parts[1]);
            }
            if (arg_parts.length == 1) {
                args.put(arg_parts[0], "");
            }
        }
        return args;
    }

    private class Worker implements Runnable {

        private Socket clientSocket = null;
        private int worker_nr;

        public Worker() {
            this.worker_nr=++Server.worker_active_count;
        }

        @Override
        public void run() {
            try {
                logger.log(Level.INFO,"Worker #".concat(String.valueOf(this.worker_nr)).concat(" listening."));
                final Pattern pattern = Pattern.compile(".*GET /([^\\?]*)\\??(.*) HTTP.*", Pattern.MULTILINE | Pattern.DOTALL);
                //final Pattern pattern_data=Pattern.compile("([^=]+)=([^&]*)&?"); // http://regexr.com?31ffd

                Matcher matcher;
                byte[] bytes;
                InputStream is;
                int bytesToRead;
                byte[] output;
                while (isRunning()) {
                    if (clientSocket == null) {
                        clientSocket = serverSocket.accept();
                    } else {
                        is = clientSocket.getInputStream();
                        bytesToRead = is.available();
                        if (bytesToRead > 0) {
                            bytes = new byte[bytesToRead];
                            is.read(bytes, 0, bytesToRead);
                            matcher = pattern.matcher(new String(bytes));

                            String action;
                            String data;
                            if (matcher.matches() && matcher.groupCount() >= 1) {
                                action = matcher.group(1);
                            } else {
                                action = "";
                            }
                            if (matcher.matches() && matcher.groupCount() >= 2) {
                                data = matcher.group(2);
                            } else {
                                data = "";
                            }
                            logger.log(Level.INFO,"Worker #".concat(String.valueOf(this.worker_nr)).concat("; Request target: \"").concat(clientSocket.getInetAddress().getHostAddress()).concat("\" action: \"").concat(action).concat("\" data: \"").concat(data).concat("\""));
                            output=getContent(clientSocket.getInetAddress().getHostAddress(), action, data);
                            clientSocket.getOutputStream().write(buildHeader(output.length).getBytes());
                            clientSocket.getOutputStream().write(output);

                            clientSocket.close();
                            clientSocket = null;
                        }
                    }
                }
            } catch (final Exception e) {
            }
            logger.log(Level.INFO,"Worker #".concat(String.valueOf(this.worker_nr)).concat(" stopped"));

            if (clientSocket != null && !clientSocket.isClosed()) {
                try {
                    clientSocket.close();
                    clientSocket = null;
                } catch (IOException e) {
                }
            }
        }
    }
}
