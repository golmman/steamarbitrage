package steamarbitrage.steamio.tor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import steamarbitrage.steamio.tor.control.TorControlConnection;

public class Gate {
	
	public static String TOR_DIR = "C:/Program Files (x86)/tor/Tor/";
	
	public static final int MAX_GATES = 100;
	public static final int FIRST_CONTROL_PORT = 8118;
	public static final int FIRST_SOCKET_PORT = 9050;
	
	
	private static Stack<Integer> gateIds = null;
	
	private CopyOnWriteArrayList<GateListener> gateListener = new CopyOnWriteArrayList<GateListener>();
	
	private int id;
	private int controlPort;
	private int socketPort;
	private Proxy proxy;
	private Thread thread;
	
	private Socket socket = null;
	private TorControlConnection torCon = null;
	
	private boolean open = true;
	
	
	
	public Gate() {
		// init id stack
		if (gateIds == null) {
			gateIds = new Stack<Integer>();
			for (int i = 0; i < MAX_GATES; i++) {
				gateIds.push(i);
			}	
		}
		
		// init vars
		this.id = gateIds.pop();		// throws exception if empty
		this.controlPort = FIRST_CONTROL_PORT + id;
		this.socketPort = FIRST_SOCKET_PORT + id;
		this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", socketPort));
		
		
		// start tor process
		ProcessBuilder pb = new ProcessBuilder(TOR_DIR + "tor.exe"
				, "--RunAsDaemon"			, "1"
				, "--CookieAuthentication"	, "0"
				, "--HashedControlPassword"	, "\"\""
				, "--ControlPort"			, "" + controlPort
				, "--PidFile"				, "tor" + id + ".pid"
				, "--SocksPort"				, "" + socketPort
				, "--DataDirectory"			, "data/tor" + id
				);
		
		pb.directory(new File(TOR_DIR));
		pb.inheritIO();
		
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		// open control connection
		try {
			socket = new Socket("127.0.0.1", controlPort);
			torCon = TorControlConnection.getConnection(socket);
			torCon.launchThread(true);
			torCon.authenticate(new byte[0]);
			torCon.setDebugging(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		
		// start thread
		Gate thisGate = this;
		thread = new Thread() {
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis(); 
				
				// give it some time to initialize
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				while (open) {
					
					for (GateListener gl : gateListener) {
						gl.gatePrepared(thisGate);
					}
					
					if (System.currentTimeMillis() - timer > 60 * 1000) {
						try {
							torCon.signal("NEWNYM");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					// wait before firing next event
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				
			}
		};
		
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public void close() {
		// end thread
		open = false;
		
		// release id
		gateIds.push(id);
		
		// tell tor to shut down
		try {
			torCon.shutdownTor("SHUTDOWN");
			socket.close();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Inet4Address requestPublicIP() {
		
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		
		URL url = null;
		HttpURLConnection con = null;
		try {
			
			//url = new URL("https://check.torproject.org/");
			url = new URL("http://myexternalip.com/raw");
			
			con = (HttpURLConnection)url.openConnection(proxy);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			
			// try to read from input stream
			InputStream is = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);;
			
			BufferedReader br = new BufferedReader(isr);
			
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			
			br.close();
			isr.close();
			is.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Inet4Address address = null;
		
		try {
			address = (Inet4Address)InetAddress.getByName(stringBuilder.toString().trim());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return address;
	}
	
	public Proxy getProxy() {
		return proxy;
	}
	
	
	public void addGateListener(GateListener gl) {
		this.gateListener.add(gl);
	}
	
}
