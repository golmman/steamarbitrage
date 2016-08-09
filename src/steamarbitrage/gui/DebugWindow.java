package steamarbitrage.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;

import steamarbitrage.Logging;
import steamarbitrage.database.trades.Trade;
import steamarbitrage.database.trades.TradeHistory;
import steamarbitrage.steamio.InputProcessor;
import steamarbitrage.steamio.SellInputProcessor;
import steamarbitrage.steamio.SteamCookie;
import steamarbitrage.steamio.SteamCookieManager;
import steamarbitrage.steamio.SteamCookieName;
import steamarbitrage.steamio.SteamIO;
import steamarbitrage.steamio.SteamSession;


public class DebugWindow extends JDialog implements ActionListener {

	private static final long serialVersionUID = 3718751386514142692L;
	
	public static JTextArea areaDebug = new JTextArea();
	
	private JPanel panel = new JPanel(new GridLayout());
	
	private JButton butLogin = new JButton("Log in");
	private JButton butLogout = new JButton("Log out");
	private JButton butTest1 = new JButton("1");
	private JButton butTest2 = new JButton("TradeHistory");
	private JButton butTest3 = new JButton("Sell");
	private JButton butTest4 = new JButton("Inventory");
	private JButton butTest5 = new JButton("5");

	public DebugWindow(JFrame owner) {
		super(owner, "Debug", false);
		this.setLayout(new BorderLayout());
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		if (owner != null) {
			this.setLocation(owner.getX() + 100, owner.getY() + 100);
		} else {
			this.setLocation(100, 100);
		}
		this.setSize(900, 500);
		
		
		JScrollPane sp = new JScrollPane(areaDebug, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		this.add(sp, BorderLayout.CENTER);
		
		butTest1.setEnabled(true);
		butTest2.setEnabled(true);
		butTest3.setEnabled(true);
		butTest4.setEnabled(true);
		butTest5.setEnabled(false);
		
		butLogin.addActionListener(this);
		butLogout.addActionListener(this);
		butTest1.addActionListener(this);
		butTest5.addActionListener(this);
		butTest2.addActionListener(this);
		butTest4.addActionListener(this);
		butTest3.addActionListener(this);
		
		panel.add(butLogin);
		panel.add(butLogout);
		panel.add(butTest1);
		panel.add(butTest2);
		panel.add(butTest3);
		panel.add(butTest4);
		panel.add(butTest5);
		panel.setPreferredSize(new Dimension(100, 30));
		this.add(panel, BorderLayout.SOUTH);
		
		this.setVisible(true);
		this.setAlwaysOnTop(true);
	}
	
	public static void init() {
		areaDebug.setFont(new Font("Courier New", Font.PLAIN, 12));
		areaDebug.setEditable(false);
		((AbstractDocument)DebugWindow.areaDebug.getDocument()).setDocumentFilter(new OutputDocumentFiler(1000));
	}

	public static void main(String[] args) {
		Logging.init(
				System.out, 
				new JTextAreaOutputStream(MainWindow.areaError),
				new JTextAreaOutputStream(DebugWindow.areaDebug));
		
		init();
		
		for (int i = 0; i < 20; i++) {
			Logging.out.println(i);
		}
		
		
		new DebugWindow(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		
		if (e.getSource().equals(butTest1)) {
			
			Logging.debug.println("test " + System.currentTimeMillis());
			
		} else if (e.getSource().equals(butTest2)) {
			
			
			Logging.debug.println("" + TradeHistory.buy.size() + " bought items loaded:");
			for (Trade trade : TradeHistory.buy) {
				Logging.debug.println(trade.toString());
			}
			
			
			
			/*
			 * http://steamcommunity.com/dev/registerkey
			 * Ihr Steam Web-API-Schlüssel
				Schlüssel: 00494FFF60C547E03817EDE99F329D0F
				Domainname: SteamStinktNachKot.de
			 * 
			 * 
			 */
			
		    
		    
			
			
		} else if (e.getSource().equals(butTest3)) {
			System.out.println("Selling Dual Berettas | Cobalt Quartz (Factory New), assetId: 2299899987");
			
			String sessionid = SteamCookieManager.getCookieString(SteamCookieName.SESSION_ID);
			
			String data = 
					sessionid
					+ "&appid=730"
					+ "&contextid=2"
					+ "&assetid=2299899987"
					+ "&amount=1"
					+ "&price=10000";
//		    		+ "&currency=3"	// euros
//		    		+ "&subtotal=" + buyRecord.subtotal
//		    		+ "&fee=" + buyRecord.fee
//		    		+ "&total=" + (buyRecord.subtotal + buyRecord.fee)
//		    		+ "&quantity=1";
			
			ArrayList<SteamCookieName> cookies = new ArrayList<SteamCookieName>();
			cookies.add(SteamCookieName.SESSION_ID);
			cookies.add(SteamCookieName.MACHINE_AUTH);
			cookies.add(SteamCookieName.LOGIN);
			cookies.add(SteamCookieName.LOGIN_SECURE);
			SteamIO.processRequest(
					"https://steamcommunity.com/market/sellitem/", 
					cookies, data, 
					new SellInputProcessor(null),
					true);
			
			
		} else if (e.getSource().equals(butTest4)) {
			SteamCookie cookie = SteamCookieManager.getCookie(SteamCookieName.MACHINE_AUTH);
			
			SteamIO.processRequest(
					"http://steamcommunity.com/profiles/" + cookie.keySuffix + "/inventory/json/730/2/", 
					null, null, 
					new InputProcessor<Object>(null) {

						@Override
						public void process(String steamIn, Object result) {
							
							System.out.println(steamIn);
							
						}
						
					},
					false);

		} else if (e.getSource().equals(butTest5)) {
			
		} else if (e.getSource().equals(butLogin)) {
			
			// Log in
			Logging.out.println("Logging in...");
			butLogin.setEnabled(false);
			butLogout.setEnabled(false);
			
			Thread thread = new Thread() {
				@Override
				public void run() {
					SteamSession.doLogin();
					Logging.out.println(SteamSession.isLoggedIn() 
							? "Logged in as " + Preferences.getInstance().activeProfile
							: "Logging in failed");
					
					butLogin.setEnabled(true);
					butLogout.setEnabled(true);
				}
			};
			thread.setDaemon(true);
			thread.start();

		
		} else if (e.getSource().equals(butLogout)) {
			
			// Log out
			Logging.out.println("Logging out...");
			butLogin.setEnabled(false);
			butLogout.setEnabled(false);
			
			Thread thread = new Thread() {
				@Override
				public void run() {
					SteamSession.doLogout();
					
					Logging.out.println("Logged out");
					butLogin.setEnabled(true);
					butLogout.setEnabled(true);
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

}
