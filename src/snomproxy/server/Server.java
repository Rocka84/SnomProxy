package snomproxy.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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
import snomproxy.providers.SnomProvider;
import snomproxy.sources.DataSource;

/**
 * Wartet auf eingehende Verbindungen und ruft den gewünschten Inhalt vom
 * Content Provider ab
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class Server extends Observable {

	private ServerSocket serverSocket;
	private int port = 8180;
	private boolean running = false;
	private InetAddress address;
	private static int worker_active_count = 0;
	private int worker_count = 3;
	protected HashMap<String, DataSource> sources;
	protected HashMap<String, Provider> providers;
	private final static Logger logger = Logger.getLogger("snomproxy.server");

	public Server() {
		this(8180, false);
	}
	public Server(int port) {
		this(port, false);
	}

	public Server(int port, boolean autorun) {
		//@TODO Geht das ermitteln der Adresse auch einfacher?
		this.address = null;
		this.sources=new HashMap<String, DataSource>();
		this.providers=new HashMap<String, Provider>();
		try {
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
			logger.log(Level.SEVERE, "Server Exception; " + e.getMessage());
		}

		this.port = port;
		if (autorun) {
			this.startServer();
		}
	}

	public void addSource(String name, DataSource source) {
		this.sources.put(name, source);
	}

	public DataSource getSource(String name) {
		return this.sources.get(name);
	}

	public boolean hasSource(String name) {
		return this.sources.containsKey(name);
	}

	public void addProvider(String name, Provider provider) {
		this.providers.put(name, provider);
		provider.setServer(this);
	}

	public Provider getProvider(String name) {
		return this.providers.get(name);
	}

	public boolean hasProvider(String name) {
		return this.providers.containsKey(name);
	}

	public void startServer() {
		if (!this.isRunning()) {
			try {
				this.running = true;
				logger.log(Level.INFO, "Server running at ".concat(getAddress().getHostAddress()).concat(":").concat(String.valueOf(getPort())));
				serverSocket = new ServerSocket(getPort(), 1, getAddress());

				for (int i = 0; i < this.worker_count; i++) {
					new Thread(new Worker()).start();
				}
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Server Error: ".concat(ex.getMessage()));
			}
		}
	}

	public void stopServer() {
		this.running = false;
		if (serverSocket != null && !serverSocket.isClosed()) {
			logger.log(Level.INFO, "Server shutting down...");
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

	public InetAddress getAddress() {
		return this.address;
	}

	public String getAddressString() {
		return "http://".concat(this.address.getHostAddress()).concat(":").concat(String.valueOf(this.port));
	}

	public boolean isRunning() {
		return running;
	}

	private byte[] getContent(String client, String provider, String source, String data) {
		return this.providers.containsKey(provider) ? this.providers.get(provider).getContent(client, source, data) : "".getBytes();
	}

	public void setWorkerCount(int worker_count) {
		this.worker_count = worker_count;
	}

	private String buildHeader(int status, String content_type, String charset, final int content_length) {
		StringBuilder out = new StringBuilder("HTTP/1.1 ");
		out.append(Integer.toString(status));
		switch (status) {
			case 200:
				out.append(" OK");
				break;
			case 404:
				out.append(" NOT FOUND");
				break;
			default:
				out.append(" UNKNOWN");
				break;
		}
		out.append("\nContent-Length: ");
		out.append(content_length);
		out.append("\nContent-Type: ");
		out.append(content_type);
		out.append("; charset=");
		out.append(charset);
		out.append("\nConnection: close\n\n");
		return out.toString();
	}

	public static Logger getLogger() {
		return logger;
	}

	public static HashMap<String, String> splitData(String data) {
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
		private final Pattern request_pattern = Pattern.compile(".*GET /([^\\?]*)\\??(.*) HTTP.*", Pattern.MULTILINE | Pattern.DOTALL);
		private final Pattern provider_pattern = Pattern.compile("^([^\\/]*)(\\/(.*))?$");

		public Worker() {
			this.worker_nr = ++Server.worker_active_count;
		}

		@Override
		public void run() {
			try {
				logger.log(Level.INFO, "Worker #".concat(String.valueOf(this.worker_nr)).concat(" listening."));

				Matcher request_matcher;
				Matcher provider_matcher;
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
							request_matcher = request_pattern.matcher(new String(bytes));
							String provider;
							String source;
							String data;
							if (request_matcher.matches() && request_matcher.groupCount() >= 1) {
								provider = request_matcher.group(1);
								provider_matcher = provider_pattern.matcher(provider);
								if (provider_matcher.matches() && provider_matcher.groupCount() == 3) {
									provider=provider_matcher.group(1);
									source=provider_matcher.group(3);
								}else{
									source="";
								}
							} else {
								provider = "";
								source = "";
							}
							if (request_matcher.matches() && request_matcher.groupCount() >= 2) {
								data = request_matcher.group(2);
							} else {
								data = "";
							}
							logger.log(Level.INFO, "Worker #".concat(String.valueOf(this.worker_nr)).concat("; Request client: \"").concat(clientSocket.getInetAddress().getHostAddress()).concat("\" provider: \"").concat(provider!=null ? provider : "none").concat("\" source: \"").concat(source!=null ? source : "none").concat("\" data: \"").concat(data!=null ? data : "none").concat("\""));
							if (!providers.containsKey(provider)){
								output = "Unknown Provider".getBytes();
								clientSocket.getOutputStream().write(buildHeader(404,"text/plain","utf-8", output.length).getBytes());
							}else{
								output = getContent(clientSocket.getInetAddress().getHostAddress(), provider, source, data);
								clientSocket.getOutputStream().write(buildHeader(providers.get(provider).getStatus(),providers.get(provider).getContentType(),providers.get(provider).getCharset(), output.length).getBytes());
							}
							clientSocket.getOutputStream().write(output);

							clientSocket.close();
							clientSocket = null;
						}
					}
				}
			} catch (final Exception e) {
//				e.printStackTrace();
				logger.log(Level.SEVERE, e.getMessage());
			}
			logger.log(Level.INFO, "Worker #".concat(String.valueOf(this.worker_nr)).concat(" stopped"));

			if (clientSocket != null && !clientSocket.isClosed()) {
				try {
					clientSocket.close();
					clientSocket = null;
				} catch (IOException e) {
				}
			}
		}
	}

	public static byte[] getFileBytes(File file) throws FileNotFoundException, IOException {
		RandomAccessFile f = new RandomAccessFile(file, "r");
		byte[] b = new byte[(int) f.length()];
		f.read(b);
		return b;
	}
}
