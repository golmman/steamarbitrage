package steamarbitrage.steamio;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import steamarbitrage.Logging;



public class SteamCookieManager {

	public static HashMap<SteamCookieName, SteamCookie> cookies = new HashMap<SteamCookieName, SteamCookie>(32);
	private static HashMap<String, SteamCookieName> cookiesReverseMap = new HashMap<String, SteamCookieName>(32);
	private static String keysPattern = "";
	
	private SteamCookieManager() {}
	
	
	public static void init() {
		// add base cookies
		
		// important cookies
		cookies.put(SteamCookieName.LOGIN, new SteamCookie("steamLogin", "", ""));
		cookies.put(SteamCookieName.LOGIN_SECURE, new SteamCookie("steamLoginSecure", "", ""));
		cookies.put(SteamCookieName.MACHINE_AUTH, new SteamCookie("steamMachineAuth", "", ""));
		cookies.put(SteamCookieName.SESSION_ID, new SteamCookie("sessionid", "", ""));
		
		// uninteresting cookies
//		cookies.put(SteamCookieName.CC, new SteamCookie("steamCC", "", ""));
//		cookies.put(SteamCookieName.WEB_TRADE_ELIGIBILITY, new SteamCookie("webTradeEligibility", "", ""));
//		cookies.put(SteamCookieName.BROWSER_ID, new SteamCookie("browserid", "", ""));
//		cookies.put(SteamCookieName.COUNTRY, new SteamCookie("steamCountry", "", ""));
//		cookies.put(SteamCookieName.LKG_BILLING_COUNTRY, new SteamCookie("LKGBillingCountry", "", ""));
//		cookies.put(SteamCookieName.REMEMBER_LOGIN, new SteamCookie("steamRememberLogin", "", ""));
		
		
		// fill reverse map and prepare the keys pattern
		keysPattern = "(";
		for (SteamCookieName name : cookies.keySet()) {
			cookiesReverseMap.put(cookies.get(name).key, name);
			keysPattern += (cookies.get(name).key + "|");
		}
		keysPattern += ".*)";
		
		// TODO: Calculation above sets steamLogin before steamLoginSecure resulting in errors
		// see RegExTest.test5()
		keysPattern = "(steamMachineAuth|sessionid|steamLoginSecure|steamLogin|.*)";
	}
	
	
	public static void setMachineAuth(String keyAndKeySuffix, String value) {
		Pattern pattern = Pattern.compile("steamMachineAuth(.*)");
		Matcher matcher = pattern.matcher(keyAndKeySuffix);
		
		if (matcher.find()) {
			cookies.put(SteamCookieName.MACHINE_AUTH, new SteamCookie("steamMachineAuth", matcher.group(1), value));
			//Logging.out.println("setMachineAuth: " + "steamMachineAuth" + " " + matcher.group(1) + " " + value);
		} else {
			//Logging.err.println("setMachineAuth: matcher.find failed - " + keyAndKeySuffix + " " + value);
		}
	}
	
	
	/**
	 * Returns the cookie in the format key + keySuffix + "=" + value.
	 * @param cookieName
	 * @return
	 */
	public static String getCookieString(SteamCookieName cookieName) {
		SteamCookie cookie = cookies.get(cookieName);
		
		if (cookie == null) {
			Logging.err.println("getCookie: no such cookie " + cookieName);
			return null;
		}
		
		return cookie.key + cookie.keySuffix + "=" + cookie.value;
	}
	
	/**
	 * Returns a cookie.
	 * @param cookieName
	 * @return
	 */
	public static SteamCookie getCookie(SteamCookieName cookieName) {	
		return cookies.get(cookieName);
	}
	
	
	
	
	/**
	 * Adds specified cookies to the connections request properties if they are already known
	 * @param con
	 * @param cookieNames
	 */
	public static void setRequestCookies(HttpURLConnection con, List<SteamCookieName> cookieNames) {
		if (cookieNames == null) return;
		
		String cookiestr = "";
		
		for (SteamCookieName name : cookieNames) {
			cookiestr += (getCookieString(name) + "; ");
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
		
		
		String key;
		
		Pattern pattern = Pattern.compile(keysPattern + "(.*?)=(.*?);.*");
		Matcher matcher;
		
		for (String str : headerList) {
			matcher = pattern.matcher(str);
			if (matcher.find()) {
				
				key = matcher.group(1);
				
				// avoid errors by not letting the hashmap contain a null key
				if (cookiesReverseMap.get(key) != null) {
					cookies.put(cookiesReverseMap.get(key),
							new SteamCookie(matcher.group(1), matcher.group(2), matcher.group(3)));
				}
				
			} else {
				//System.out.println("readHeaderCookies: NO MATCH");
				Logging.err.println("readHeaderCookies: NO MATCH");
			}
		}
		
	}
}
