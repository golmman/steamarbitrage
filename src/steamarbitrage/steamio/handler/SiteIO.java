package steamarbitrage.steamio.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import steamarbitrage.Logging;

import javax.swing.JLabel;






@Deprecated
class GetHTMLThread implements Runnable {
	
	private OutputHandler outhan = null;
	private String urlstr;
	private boolean steamPost = false;
	
	public GetHTMLThread(String urlstr, OutputHandler outhan) {
		this.urlstr = urlstr;
		this.outhan = outhan;
		this.steamPost = false;
	}
	
	public GetHTMLThread(String urlstr, OutputHandler outhan, boolean steamPost) {
		this.urlstr = urlstr;
		this.outhan = outhan;
		this.steamPost = steamPost;
	}
	
	@Override
	public void run() {
		String out = "";
		
		URL url = null;
		try {
			url = new URL(urlstr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		
		HttpURLConnection con = null;
		
		try {
			con = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		String charset = "UTF-8";
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(20000);
		
		con.setRequestProperty("Accept", "*/*");
		con.setRequestProperty("Accept-Charset", charset);
		//con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2200.0 Iron/41.0.2200.0 Safari/537.36");
		
		
//		int k = 0;
//		String headerField = con.getHeaderField(0);
//		do {
//			System.out.println(headerField + " $$$$$$ " + con.getHeaderFieldKey(k));
//			
//			++k;
//			headerField = con.getHeaderField(k);
//		} while (headerField != null);
		
		try {
			con.connect();
		} catch (SocketTimeoutException e) {
			// timeout, send again
			SiteIO.getHTML(urlstr, outhan);
			Logging.err.println("SocketTimeoutException: ConnectTimeout at " + urlstr);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		BufferedReader r = null;
		
		try {
			int responseCode = con.getResponseCode();
			
			if (responseCode == 200) {
				r = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			} else {
				
				r = new BufferedReader(new InputStreamReader(con.getErrorStream(), charset));
				
				if (responseCode / 100 == 4) {
					Logging.err.println("SiteIO.getHTML responseCode = " + responseCode);
				}
				
				if (responseCode == 403) {
					SiteIO.setSteam403(true);
				}
			}
		} catch (SocketTimeoutException e) {
			// timeout, send again
			SiteIO.getHTML(urlstr, outhan);
			Logging.err.println("SocketTimeoutException: ReadTimeout at " + urlstr);
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		StringBuilder buf = new StringBuilder();
		while (true) {
			int ch = 0;
			try {
				ch = r.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (ch < 0)	break;
			buf.append((char) ch);
		}
		out = buf.toString();
		
		
		
		
		
		outhan.handle(out);
	}
	
}

@Deprecated
class SteamThreadQueue_old extends Thread {
	private Deque<Thread> threadDeque;
	private ThreadGroup steamThreads;
	
	public SteamThreadQueue_old(Deque<Thread> threadQueue, ThreadGroup steamThreads) {
		this.threadDeque = threadQueue;
		this.steamThreads = steamThreads;
		
		setDaemon(true);
	}
	
	
	public int size() {
		return threadDeque.size();
	}
	
	@Override
	public void run() {
		int replaceLine = 0;
		
		
		while (true) {
			
			
			if (SiteIO.isSteamQueuePaused()) {
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			
			if (SiteIO.isSteam403()) {
				try {
					sleep(SiteIO.getSteam403Delay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SiteIO.setSteam403(false);
			}
			
			
			
			if (threadDeque.size() > 0) {
				threadDeque.pollFirst().start();
				
//				Logging.out.printlnInPlace(
//						"active threads: " + steamThreads.activeCount() + 
//						", remaining queries: " + threadDeque.size(),
//						replaceLine);
//				
//			} else {
//				replaceLine = Logging.getLastLine();
			}
			
			
			
			if (steamThreads.activeCount() >= SiteIO.getSteamMaxRequests()) {
				//Logging.out.println(">= 6 steam threads");
				
				while (steamThreads.activeCount() > SiteIO.getSteamMinRequests()) {
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
//			if (threadQueue.isEmpty() == false) {
//				threadQueue.poll().start();
//			}
			
			try {
				sleep(SiteIO.getSteamDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}


@Deprecated
public class SiteIO {
	
//	public static final int STEAM_DELAY = 1000;
//	public static final int STEAM_403DELAY = 5 * 60 * 1000;
//	public static final int STEAM_MAX_REQUESTS = 6;
//	public static final int STEAM_MIN_REQUESTS = 4;
	
	public static JLabel currentLabel = null;
	
	private static boolean steam403 = false;
	private static boolean steamQueuePaused = false;
	
	private static int steamDelay = 75;
	private static int steam403Delay = 5 * 60 * 1000;
	private static int steamMaxRequests = 6;
	private static int steamMinRequests = 4;
	
	private static Deque<Thread> threadDeque = new LinkedList<Thread>();
	private static ThreadGroup steamThreads = new ThreadGroup("SteamThreads");
	
	private SiteIO() {}
	
	
	public static void init() {
		new SteamThreadQueue_old(threadDeque, steamThreads).start();
	}
	
	
	public static void getHTML(String urlstr, OutputHandler dout) {
		
		if (urlstr.contains("steam")) {
			Thread t = new Thread(steamThreads, new GetHTMLThread(urlstr, dout));
			t.setDaemon(true);
			threadDeque.addLast(t);
		} else {
			Thread t = new Thread(new GetHTMLThread(urlstr, dout));
			t.setDaemon(true);
			t.start();
		}
		
	}
	
	
	public static void clearSteamQueue() {
		threadDeque.clear();
	}
	
	public static boolean isSteam403() {
		return steam403;
	}
	
	public static void setSteam403(boolean b) {
		steam403 = b;
	}
	
	
	public static boolean isSteamQueuePaused() {
		return steamQueuePaused;
	}


	public static void setSteamQueuePaused(boolean steamQueuePaused) {
		if (steamQueuePaused == true) {
			Logging.out.println("Steam queue paused");
		} else {
			Logging.out.println("Steam queue resumed");
		}
		SiteIO.steamQueuePaused = steamQueuePaused;
	}


	public static void setSteamProperties(int steamDelay, int steam403Delay, int steamMaxRequests, int steamMinRequests) {
		SiteIO.steamDelay = steamDelay;
		SiteIO.steam403Delay = steam403Delay;
		SiteIO.steamMaxRequests = steamMaxRequests;
		SiteIO.steamMinRequests = steamMinRequests;
	}
	
	
	public static int getSteamDelay() {
		return steamDelay;
	}


	public static void setSteamDelay(int steamDelay) {
		SiteIO.steamDelay = steamDelay;
	}


	public static int getSteam403Delay() {
		return steam403Delay;
	}


	public static void setSteam403Delay(int steam403Delay) {
		SiteIO.steam403Delay = steam403Delay;
	}


	public static int getSteamMaxRequests() {
		return steamMaxRequests;
	}


	public static void setSteamMaxRequests(int steamMaxRequests) {
		SiteIO.steamMaxRequests = steamMaxRequests;
	}


	public static int getSteamMinRequests() {
		return steamMinRequests;
	}


	public static void setSteamMinRequests(int steamMinRequests) {
		SiteIO.steamMinRequests = steamMinRequests;
	}
	
	
	public static Queue<Thread> getThreadQueue() {
		return threadDeque;
	}


	public static ThreadGroup getSteamThreads() {
		return steamThreads;
	}


}
