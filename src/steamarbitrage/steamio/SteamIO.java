package steamarbitrage.steamio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import steamarbitrage.Logging;
import steamarbitrage.database.trades.Trade;
import steamarbitrage.database.trades.TradeHistory;

import java.util.Deque;

enum RequestMethod {
	POST, GET, HEAD
}

/*
 * Record for anonymous login InputProcessor
 */
class LoginRecord {
	public String requires_twofactor;
	public String login_complete;
	public String transfer_url;
	public String steamid;
	public String token;
	public String auth;
	public String remember_login;
	public String token_secure;
	
	public LoginRecord() {
		
	}
	
	public void fill(Matcher matcher) {
		requires_twofactor = matcher.group(1);
		login_complete = matcher.group(2);
		transfer_url = matcher.group(3).replace("\\/", "/");
		steamid = matcher.group(4);
		token = matcher.group(5);
		auth = matcher.group(6);
		remember_login = matcher.group(7);
		token_secure = matcher.group(8);
	}
}


/*
 * Record for anonymous RSA InputProcessor
 */
class RSARecord {
	public String publickey_mod;
	public String publickey_exp;
	public String timestamp;
	public String steamid;
	public String token_gid;
	
	public RSARecord() {
		
	}
	
	public void fill(Matcher matcher) {
		publickey_mod = matcher.group(1);
		publickey_exp = matcher.group(2);
		timestamp = matcher.group(3);
		steamid = matcher.group(4);
		token_gid = matcher.group(5);
	}
}



public class SteamIO {
	
	private static SteamThreadQueue threadQueue;
	
	private static boolean response403 = false;
	private static boolean threadQueuePaused = false;
	
	private static int requestDelay = 75;
	private static int reponse403Delay = 5 * 60 * 1000;
	private static int maxRequests = 6;
	private static int minRequests = 4; 
	
	
	private SteamIO() {}
	
	public static void init() {
		threadQueue = SteamThreadQueue.getInstance();
		SteamCookieManager.init();
		SteamSession.init();
	}
	
	
	public static void clearQueue() {
		threadQueue.getDeque().clear();
	}
	
	public static boolean isResponse403() {
		return response403;
	}
	
	public static void setResponse403(boolean b) {
		response403 = b;
	}
	
	
	public static boolean isQueuePaused() {
		return threadQueuePaused;
	}


	public static void setQueuePaused(boolean steamQueuePaused) {
		if (steamQueuePaused == true) {
			Logging.out.println("Steam queue paused");
		} else {
			Logging.out.println("Steam queue resumed");
		}
		SteamIO.threadQueuePaused = steamQueuePaused;
	}


	public static void setProperties(int requestDelay, int reponse403Delay, int maxRequests, int minRequests) {
		SteamIO.requestDelay = requestDelay;
		SteamIO.reponse403Delay = reponse403Delay;
		SteamIO.maxRequests = maxRequests;
		SteamIO.minRequests = minRequests;
		
		Logging.debug.println("Search properties set to:\n"
				+ "  requestDelay: "	 + requestDelay		 + "\n"
				+ "  reponse403Delay: "	 + reponse403Delay	 + "\n"
				+ "  maxRequests: "		 + maxRequests		 + "\n"
				+ "  minRequests: "		 + minRequests
				);
	}
	
	public static int getRequestDelay() {
		return requestDelay;
	}

	public static void setRequestDelay(int requestDelay) {
		SteamIO.requestDelay = requestDelay;
	}

	public static int getReponse403Delay() {
		return reponse403Delay;
	}

	public static void setReponse403Delay(int reponse403Delay) {
		SteamIO.reponse403Delay = reponse403Delay;
	}

	public static int getMaxRequests() {
		return maxRequests;
	}

	public static void setMaxRequests(int maxRequests) {
		SteamIO.maxRequests = maxRequests;
	}

	public static int getMinRequests() {
		return minRequests;
	}

	public static void setMinRequests(int minRequests) {
		SteamIO.minRequests = minRequests;
	}

	public static Deque<Thread> getThreadDeque() {
		return threadQueue.getDeque();
	}


	public static ThreadGroup getThreadGroup() {
		return threadQueue.getThreadGroup();
	}
	
	
	
	
	
	/**
	 * Try to buy a listing, invoked by {@link steamarbitrage.steamio.ListingInputProcessor#process(String, ListingRecord)}.
	 * Part of SteamIO, because? Maybe should get its own class and InputProcessor.
	 * @param buyRecord
	 */
	public static void buyListing(BuyRecord buyRecord) {
		
		
		String sessionid = SteamCookieManager.getCookieString(SteamCookieName.SESSION_ID);
		
		String data = 
				sessionid
	    		+ "&currency=3"	// euros
	    		+ "&subtotal=" + buyRecord.subtotal
	    		+ "&fee=" + buyRecord.fee
	    		+ "&total=" + (buyRecord.subtotal + buyRecord.fee)
	    		+ "&quantity=1";
		
		ArrayList<SteamCookieName> cookies = new ArrayList<SteamCookieName>();
		cookies.add(SteamCookieName.SESSION_ID);
		cookies.add(SteamCookieName.MACHINE_AUTH);
		cookies.add(SteamCookieName.LOGIN);
		cookies.add(SteamCookieName.LOGIN_SECURE);
		
		SteamIO.pushRequest(
				"https://steamcommunity.com/market/buylisting/" + buyRecord.listingId,
				cookies, 
				data, 
				new InputProcessor<BuyRecord>(buyRecord) {

					@Override
					public void process(String steamIn, BuyRecord result) {
						float total = 0.01f * (result.subtotal + result.fee);
						
						if (steamIn.contains("\"success\":1")) {
							SteamSession.setBalance(SteamSession.getBalance() - total);
							TradeHistory.buy.add(new Trade(result.fullname, result.estimate, total, new Date(), buyRecord.assetId));
							TradeHistory.save();
						} else {
							Logging.out.println("Buying failed: " + steamIn);
						}
					}
					
				});
	}
	
	
	
	/**
	 * Creates a Thread which forwards parameters to {@link #processRequest(String, List, String, InputProcessor) processRequest}. 
	 * This thread is then added to the back of SteamThreadQueue's deque.
	 * @param urlstr
	 * @param cookies
	 * @param data
	 * @param input
	 */
	public static <T> void queueRequest(String urlstr, List<SteamCookieName> cookies, String data, InputProcessor<T> input) {
		Thread thread = new Thread(threadQueue.getThreadGroup(), "") {
			@Override
			public void run() {
				processRequest(urlstr, cookies, data, input);
			}
		};
		thread.setDaemon(true);
		
		threadQueue.getDeque().addLast(thread);
	}
	
	/**
	 * Creates a Thread which forwards parameters to {@link #processRequest(String, List, String, InputProcessor) processRequest}. 
	 * This thread is then added to the front of SteamThreadQueue's deque.
	 * @param urlstr
	 * @param cookies
	 * @param data
	 * @param input
	 */
	public static <T> void pushRequest(String urlstr, List<SteamCookieName> cookies, String data, InputProcessor<T> input) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				processRequest(urlstr, cookies, data, input);
			}
		};
		thread.setDaemon(true);
		
		threadQueue.getDeque().addFirst(thread);
	}
	
	

	public static void printResponseHeaders(HttpURLConnection con, PrintStream ps) {
		ps.println("RESPONSE HEADERS");
		Map<String, List<String>> headerMap = con.getHeaderFields();
		for (String key : headerMap.keySet()) {

			ps.print(key + ": ");

			List<String> headerList = headerMap.get(key);
			for (String s : headerList) {
				ps.print(s + "||");
			}

			ps.println();
		}
	}

	public static void printRequestHeaders(HttpURLConnection con, PrintStream ps) {
		ps.println("REQUEST HEADERS");
		Map<String, List<String>> headerMap = con.getRequestProperties();
		for (String key : headerMap.keySet()) {

			ps.print(key + ": ");

			List<String> headerList = headerMap.get(key);
			for (String s : headerList) {
				ps.print(s + "||");
			}

			ps.println();
		}
	}

	
	
	
	/**
	 * Sets all standard steam request properties (but no cookies) and generates
	 * an HttpURLConnection object.
	 * 
	 * @param urlstr
	 * @param method
	 * @return
	 */
	@SuppressWarnings("unused")
	private static HttpURLConnection prepareRequest(String urlstr, RequestMethod method) {
		return prepareRequest(urlstr, method, null, null);
	}

	/**
	 * Sets all standard steam request properties (but no cookies) and generates
	 * an HttpURLConnection object.
	 * 
	 * @param urlstr
	 * @param method
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unused")
	private static HttpURLConnection prepareRequest(String urlstr, RequestMethod method, String data) {
		return prepareRequest(urlstr, method, data, null);
	}
	
	/**
	 * Sets all standard steam request properties (but no cookies) and generates
	 * an HttpURLConnection object.
	 * 
	 * @param urlstr
	 * @param method
	 * @param proxy
	 * @return
	 */
	@SuppressWarnings("unused")
	private static HttpURLConnection prepareRequest(String urlstr, RequestMethod method, Proxy proxy) {
		return prepareRequest(urlstr, method, null, proxy);
	}

	/**
	 * Sets all standard steam request properties (but no cookies) and generates
	 * an HttpURLConnection object.
	 * 
	 * @param urlstr
	 * @param method
	 * @param data
	 * @param proxy
	 * @return
	 */
	private static HttpURLConnection prepareRequest(String urlstr, RequestMethod method, String data, Proxy proxy) {
		if (method == RequestMethod.POST && data == null) {
			Logging.err.println("POST request with empty data string.");
			return null;
		}

		String requestMethod = null;
		switch (method) {
		case POST: requestMethod = "POST"; break;
		case GET:  requestMethod = "GET";  break;
		case HEAD: requestMethod = "HEAD"; break;
		default: break;
		}

		URL url = null;
		HttpURLConnection con = null;
		try {
			url = new URL(urlstr);
			
			if (proxy == null) {
				con = (HttpURLConnection)url.openConnection();
			} else {
				con = (HttpURLConnection)url.openConnection(proxy);
			}
			
			con.setRequestMethod(requestMethod);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		con.setConnectTimeout(10000);
		con.setReadTimeout(20000);

		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2200.0 Iron/41.0.2200.0 Safari/537.36");

		// buy or sell request!
		if (urlstr.contains("buylisting") || urlstr.contains("sellitem")) {
			con.setRequestProperty ("Origin", "http://steamcommunity.com");
		    con.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		    con.setRequestProperty ("Referer", "http://steamcommunity.com/market/listings/730/P250%20%7C%20Boreal%20Forest%20%28Field-Tested%29");
		}
		
		
		if (method == RequestMethod.POST) {
			con.setRequestProperty("Content-Length", String.valueOf(data.length()));
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
		} else {
			con.setDoInput(true);
			con.setDoOutput(false);
			con.setUseCaches(false);
		}

		return con;
	}

	/**
	 * Process a steam request, for instance to get information or buy items
	 * 
	 * @param urlstr the url string
	 * @param cookies list of cookies to use
	 * @param data data to be POSTed, if null it will be a GET request
	 * @param input SteamInput object which processes the incoming data
	 */
	public static <T> void processRequest(String urlstr, List<SteamCookieName> cookies, String data, InputProcessor<T> input) {
		processRequest(urlstr, cookies, data, input, null, false);
	}
	
	/**
	 * Process a steam request, for instance to get information or buy items
	 * 
	 * @param urlstr the url string
	 * @param cookies list of cookies to use
	 * @param data data to be POSTed, if null it will be a GET request
	 * @param input SteamInput object which processes the incoming data
	 * @param proxy Proxy to use (Tor)
	 */
	public static <T> void processRequest(String urlstr, List<SteamCookieName> cookies, String data, InputProcessor<T> input, Proxy proxy) {
		processRequest(urlstr, cookies, data, input, proxy, false);
	}
	
	
	/**
	 * Process a steam request, for instance to get information or buy items
	 * 
	 * @param urlstr the url string
	 * @param cookies list of cookies to use
	 * @param data data to be POSTed, if null it will be a GET request
	 * @param input SteamInput object which processes the incoming data
	 * @param debug if true prints debug info
	 */
	public static <T> void processRequest(String urlstr, List<SteamCookieName> cookies, String data, InputProcessor<T> input, boolean debug) {
		processRequest(urlstr, cookies, data, input, null, debug);
	}
	
	/**
	 * Process a steam request, for instance to get information or buy items
	 * 
	 * @param urlstr the url string
	 * @param cookies list of cookies to use
	 * @param data data to be POSTed, if null it will be a GET request
	 * @param input SteamInput object which processes the incoming data
	 * @param proxy Proxy to use (Tor)
	 * @param debug if true prints debug info
	 */
	public static <T> void processRequest(String urlstr, List<SteamCookieName> cookies, String data, InputProcessor<T> input, Proxy proxy, boolean debug) {
		
		RequestMethod requestMethod = (data == null) ? RequestMethod.GET : RequestMethod.POST;

		HttpURLConnection con = prepareRequest(urlstr, requestMethod, data, proxy);

		SteamCookieManager.setRequestCookies(con, cookies);

		//debug = true;
		
		// TODO: debug
		if (debug) {
			Logging.debug.println("Request: " + urlstr);
			Logging.debug.println("data: " + data);
			Logging.debug.println("request method: " + requestMethod);
			Logging.debug.println();
			printRequestHeaders(con, Logging.debug);
		}

		new OutputProcessor(con, data);

		SteamCookieManager.readHeaderCookies(con);

		// TODO: debug
		if (debug) {
			Logging.debug.println();
			printResponseHeaders(con, Logging.debug);
			Logging.debug.println();
			Logging.debug.println("Processed Input:");
		}
		
		input.readInput(con);

		if (debug) {
			Logging.debug.println("Request End +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			Logging.debug.println();
		}

	}



	// TODO: START
	public static void main(String[] args) {

		SteamIO.init();
		
		JFrame frame = new JFrame("SteamIO Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(300, 300, 400, 200);
		frame.setLayout(null);
		
		
		JButton buttonLogin = new JButton("Login");
		JButton buttonLoggedIn = new JButton("logged in?");
		JButton buttonLogout = new JButton("Logout");
		JButton buttonReset = new JButton("Reset");
		JLabel labelTimer = new JLabel("");
		
		buttonLogin.setBounds(50, 100, 100, 25);
		buttonLoggedIn.setBounds(160, 100, 100, 25);
		buttonLogout.setBounds(270, 100, 100, 25);
		buttonReset.setBounds(240, 20, 100, 25);
		labelTimer.setBounds(30, 20, 200, 40);
		
		
		long[] startTime = {0};
		
		Thread timerThread = new Thread() {
			
			@Override
			public void run() {
				startTime[0] = System.currentTimeMillis();
				
				while (true) {
					
					Duration d = Duration.ofMillis(System.currentTimeMillis() - startTime[0]);
					
					labelTimer.setText(d.toString().substring(2));
					
					try {
						sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		
		buttonLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startTime[0] = System.currentTimeMillis();
				
				SteamSession.doLogin();
			}
		});
		
		buttonLoggedIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Logged in: " + SteamSession.isLoggedIn());
			}
		});
		
		buttonLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SteamSession.doLogout();
			}
		});
		
		buttonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startTime[0] = System.currentTimeMillis();
			}
		});
		
		
		
		frame.add(buttonLogin);
		frame.add(buttonLoggedIn);
		frame.add(buttonLogout);
		frame.add(buttonReset);
		frame.add(labelTimer);
		
		frame.setVisible(true);
		
		
		timerThread.setDaemon(true);
		timerThread.start();
		
		
		//doLogin(username, password);
		
		//System.out.println("Logged in: " + isLoggedIn());
	}

	
	
	
	
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
