package steamarbitrage.steamio;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import steamarbitrage.Logging;
import steamarbitrage.gui.Account;
import steamarbitrage.gui.Preferences;


/**
 * Stores and manages all available steam cookies.
 *
 */
@Deprecated
public class SteamCookieManager_old {
	private static HashMap<String, String> cookies = null;
	private static JTextArea areaCookies = null;
	
	// store the key names for important cookies
	private static String steamCC;
	private static String steamMachineAuth;
	private static String sessionid;
	private static String steamLogin;
	private static String steamLoginSecure;
	private static String webTradeEligibility;
	
	
	private SteamCookieManager_old() {}
	
	
	public static void init() {
		Preferences preferences = Preferences.getInstance();
		Account account = preferences.accounts.get(preferences.activeProfile);
		
		cookies = new HashMap<String, String>() {
			private static final long serialVersionUID = -5108053129929285236L;
			@Override
			public String put(String key, String value) {
				
				if (key.contains("steamCC")) {
					steamCC = new String(key);
				} else if (key.contains("steamMachineAuth")) {
					steamMachineAuth  = new String(key);
				} else if (key.contains("sessionid")) {
					sessionid  = new String(key);
				} else if (key.contains("steamLoginSecure")) {
					steamLoginSecure  = new String(key);
				} else if (key.contains("steamLogin")) {
					steamLogin  = new String(key);
				} else if (key.contains("webTradeEligibility")) {
					webTradeEligibility  = new String(key);
				}
				
				return super.put(key, value);
			}
		};
		
		// steamMachineAuth seems to be never set by steam.
		// Probably it is set only the first time you log in and expected to be saved after that.
		
		// johannes pc
		//addCookie("steamMachineAuth76561198121652995", "B80AB107222FA152BB411F0BCF325453D9190FAF");
		
		// swen pc
		//addCookie("steamMachineAuth76561198121652995", "0C76243DE5223C68BC3190660D19F6DAC62B3421");
		
		addCookie(account.machineAuthKey, account.machineAuthValue);
		
		// steamLoginSecure and steamLogin are always the same but sometimes steam does not set them,
		// a probable cause is that you are already logged in via your browser.
		//addCookie("steamLoginSecure", "76561198121652995%7C%7C231ACD0D842DE8BECF35D21B980AA95ACFE0B68F");
		//addCookie("steamLogin", "76561198121652995%7C%7C25948E9B6F32DA56243C7E47FF01B821E3EB7E1F");
	}
	
	public static void debug() {
		System.out.println(steamCC);
		System.out.println(steamMachineAuth);
		
		for (String key : cookies.keySet()) {
			System.out.println(key + ": " + cookies.get(key));
		}
	}
	
	
	/**
	 * Returns the cookie in the format name=value.
	 * @param cookieName
	 * @return
	 */
	public static String getCookie(SteamCookieName cookieName) {
		if (cookies.get(getCookieName(cookieName)) == null) {
			Logging.err.println("getCookie: no such cookie " + cookieName);
		}
		return getCookieName(cookieName) + "=" + cookies.get(getCookieName(cookieName));
	}
	
	public static void printToTextArea() {
		String cookiestr = ""; 
		for (String key : cookies.keySet()) {
			cookiestr += (key + "=" + cookies.get(key) + "\n");
		}
		areaCookies.setText(cookiestr);
	}
	
	@Deprecated
	public static void setRequestAllCookies(HttpURLConnection con) {
		String cookiestr = ""; 
		for (String key : cookies.keySet()) {
			cookiestr += (key + "=" + cookies.get(key) + "; ");
		}
		
		
		if (cookiestr.contains("=")) {
			cookiestr = cookiestr.substring(0, cookiestr.length()-2);
			con.setRequestProperty("Cookie", cookiestr);
			System.out.println("SET COOKIES: " + cookiestr);
		}
	}
	
	
	
	private static String getCookieName(SteamCookieName name) {
//		switch (name) {
//		case CC: 					return steamCC;
//		case MACHINE_AUTH: 			return steamMachineAuth;
//		case SESSION_ID: 			return sessionid;
//		case LOGIN: 				return steamLogin;
//		case LOGIN_SECURE: 			return steamLoginSecure;
//		case WEB_TRADE_ELIGIBILITY: return webTradeEligibility;
//		default: return null;
//		}
		return null;
	}
	
	
	/**
	 * Adds specified cookies to the connections request properties if they are already known
	 * @param con
	 * @param cookieNames
	 */
	public static void setRequestCookies(HttpURLConnection con, List<SteamCookieName> cookieNames) {
		if (cookieNames == null) return;
		
		String cookiestr = "";
		String value = "";
		String key = "";
		for (SteamCookieName name : cookieNames) {
			key = getCookieName(name);
			value = cookies.get(key);
			
			if (value == null) {
				Logging.err.println("setRequestCookies: cookie name \"" + key + "\" (" + name + ") non existant.");
				continue;
			}
			
			cookiestr += (key + "=" + value + "; ");
		}
		
		if (!cookiestr.equals("")) {
			cookiestr = cookiestr.substring(0, cookiestr.length() - 2);
			con.setRequestProperty("Cookie", cookiestr);
		}
	}
	
	
	
	
	/**
	 * Reads and stores all cookies from the response headers.
	 * @param con
	 */
	public static void readHeaderCookies(HttpURLConnection con) {
		Map<String, List<String>> headerMap = con.getHeaderFields();
		List<String> headerList = headerMap.get("Set-Cookie");
		
		if (headerList == null) return;
		
		Pattern pattern = Pattern.compile("([^=]*)(=)([^;]*)(;)(.*)");
		Matcher matcher = null;
		
		for (String str : headerList) {
			matcher = pattern.matcher(str);
			if (matcher.find()) {
				cookies.put(matcher.group(1), matcher.group(3));
			} else {
				Logging.err.println("readHeaderCookies: NO MATCH");
			}
		}
	}
	
	
	/**
	 * Adds a cookie to the cookie hash map.
	 * @param key
	 * @param value
	 */
	public static void addCookie(String key, String value) {
		cookies.put(key, value);
	}

	public static JTextArea getAreaCookies() {
		return areaCookies;
	}

	public static void setAreaCookies(JTextArea areaCookies) {
		SteamCookieManager_old.areaCookies = areaCookies;
	}
	
	
}

