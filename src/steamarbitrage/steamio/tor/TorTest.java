package steamarbitrage.steamio.tor;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;

import steamarbitrage.database.ItemNames;
import steamarbitrage.steamio.tor.control.TorControlConnection;


public class TorTest {

	public static Process process = null;
	
	public static int controlPort = 8118;
	public static int socketPort = 9050;
	
	public static LinkedList<Gate> gates = new LinkedList<Gate>(); 
	
	
	public TorTest() {
		
	}
	
	
	public static void openFrame() {
		JFrame frame = new JFrame("TorTest");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 200, 200);
		frame.setLayout(new FlowLayout());
		
		JButton button0 = new JButton("get IP");
		JButton button1 = new JButton("exit Tor");
		
		button0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				System.out.println(getPublicIP(-1).getHostAddress());
//				System.out.println(getPublicIP(socketPort).getHostAddress());
//				System.out.println(getPublicIP(socketPort+1).getHostAddress());
				
				for (Gate g : gates) {
					System.out.println(g.requestPublicIP().getHostAddress());
				}
				
			}
		});
		
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				for (Gate g : gates) {
					g.close();
				}
				
				
				
//				try {
//					Socket socket = new Socket("127.0.0.1", controlPort);
//					TorControlConnection torCon = TorControlConnection.getConnection(socket);
//					torCon.launchThread(true);
//					torCon.authenticate(new byte[0]);
//					
//					torCon.setDebugging(System.out);
//					
//					torCon.shutdownTor("SHUTDOWN");
//					
//					socket.close();
//					
//					
//					
//					
//					socket = new Socket("127.0.0.1", controlPort+1);
//					torCon = TorControlConnection.getConnection(socket);
//					torCon.launchThread(true);
//					torCon.authenticate(new byte[0]);
//					
//					torCon.setDebugging(System.out);
//					
//					torCon.shutdownTor("SHUTDOWN");
//					
//					socket.close();
//					
//				} catch (UnknownHostException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				
			}
		});
		
		frame.add(button0);
		frame.add(button1);
		
		frame.setVisible(true);
		
	}
	
	
	public static void main(String[] args) {
		
		ItemNames.loadNames();
		
		
		TestSteamGateListener tsgl = new TestSteamGateListener();
		
		for (int i = 0; i < 2; i++) {
			Gate g = new Gate();
			g.addGateListener(tsgl);
			gates.add(g);
		}
		
		openFrame();
		
		
		
	}
	
	
	private static Inet4Address getPublicIP(int torSocketPort) {
		
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		
		URL url = null;
		HttpURLConnection con = null;
		try {
			
			//url = new URL("https://check.torproject.org/");
			url = new URL("http://myexternalip.com/raw");
			
			if (torSocketPort != -1) {
				Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", torSocketPort));
				con = (HttpURLConnection)url.openConnection(proxy);
			} else {
				con = (HttpURLConnection)url.openConnection();
			}
			
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




}

