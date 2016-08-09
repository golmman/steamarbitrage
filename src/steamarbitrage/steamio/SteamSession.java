package steamarbitrage.steamio;

import java.awt.Color;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import steamarbitrage.Logging;
import steamarbitrage.gui.Account;
import steamarbitrage.gui.MainWindow;
import steamarbitrage.gui.Preferences;

public class SteamSession {

	
	public static final int STATUS_UNKNOWN = 0;
	public static final int STATUS_ONLINE  = 1;
	public static final int STATUS_OFFLINE = 2;
	
//	private static String username = "svenschlotze";
//	private static String password = "2.Lehrjahr";
//	private static String email = "atzepeng.potsdam@freenet.de";
	
	private static Preferences preferences = Preferences.getInstance();
	
	private static float balance = 0.0f;
	
	private static int onlineStatus = STATUS_OFFLINE;
	private static long statusUpdate = 0;
	
	private SteamSession() {}
	
	
	public static void init() {
		statusUpdate = System.currentTimeMillis();
		
		// set online status to unknown if there was no update after some time
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (true) {
					
					if (System.currentTimeMillis() - statusUpdate > 120 * 60 * 1000) {
						onlineStatus = STATUS_UNKNOWN;
					}
					
					
					if (onlineStatus == STATUS_ONLINE) {
						MainWindow.labelOnlineStatus.setText("ONLINE");
						MainWindow.labelOnlineStatus.setForeground(new Color(0, 128, 0));
					} else if (onlineStatus == STATUS_OFFLINE) {
						MainWindow.labelOnlineStatus.setText("OFFLINE");
						MainWindow.labelOnlineStatus.setForeground(new Color(128, 0, 0));
					} else if (onlineStatus == STATUS_UNKNOWN) {
						MainWindow.labelOnlineStatus.setText("ONLINE");
						MainWindow.labelOnlineStatus.setForeground(new Color(128, 128, 0));
					}
					
					
					try {
						sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	
	
	
//	public static void setParameters(String username, String password, String email) {
//		SteamSession.username = username;
//		SteamSession.password = password;
//		SteamSession.email = email;
//	}
	
	
	
	
	/**
	 * 
	 * Login to Steam with username and password.
	 * After successfully logging in we have the Cookies
	 * <pre>
	 * 		sessionid
	 * 		steamMachineAuth
	 * 		steamLogin
	 * 		steamLoginSecure
	 * </pre>
	 * and we can buy stuff. Sets onlineStatus to STATUS_UNKNOWN.
	 * 
	 * @param username
	 * @param password
	 */
	public static void doLogin() {
		// profile to use
		Account account = preferences.accounts.get(preferences.activeProfile);
		
		
		// the data to be sent
		String data;

		// the cookies to be used
		ArrayList<SteamCookieName> cookies;

		// the RSA data received from steam
		RSARecord steamFieldRSA = new RSARecord();

		// the RSA data received from steam
		LoginRecord steamFieldLogin = new LoginRecord();

		// load a nonsense steam site to get a session id
		SteamIO.processRequest(
				"http://steamcommunity.com/market/listings/730/P250%20%7C%20Boreal%20Forest%20%28Field-Tested%29",
				null, 
				null, 
//				new InputProcessor<Object>(null) {
//					@Override
//					public void process(String steamIn, Object result) {
//
//					}
//				}
				new DefaultInputProcessor()
				);

		SteamIO.sleep(2000);

		// request an RSA key
		data = "username=" + account.username
				+ "&donotcache=1427672037461";

		cookies = new ArrayList<SteamCookieName>();
		cookies.add(SteamCookieName.MACHINE_AUTH);
		cookies.add(SteamCookieName.SESSION_ID);

		SteamIO.processRequest(
				"https://steamcommunity.com/login/getrsakey/", 
				cookies,
				data, 
				new InputProcessor<RSARecord>(steamFieldRSA) {
					@Override
					public void process(String steamIn, RSARecord result) {
						//System.out.println(steamIn);

						// fill the RSA field using steams input
						Pattern pattern = Pattern
								.compile("\\{\"success\":(.*?)[,\\}]");
						Matcher matcher = pattern.matcher(steamIn);
						matcher.find();

						if (matcher.group(1).equals("true")) {

							pattern = Pattern.compile("\\{\"success\":true,"
									+ "\"publickey_mod\":\"(.*?)\","
									+ "\"publickey_exp\":\"(.*?)\","
									+ "\"timestamp\":\"(.*?)\","
									+ "\"steamid\":\"(.*?)\","
									+ "\"token_gid\":\"(.*?)\"\\}");
							matcher = pattern.matcher(steamIn);
							matcher.find();

						} else {
							Logging.err.println(matcher.group(1));
						}

						result.fill(matcher);
					}
				});

		SteamIO.sleep(2000);

		// encrypt the password
		BigInteger mod = new BigInteger(steamFieldRSA.publickey_mod, 16);
		BigInteger exp = new BigInteger(steamFieldRSA.publickey_exp, 16);

		RSAPublicKeySpec spec = new RSAPublicKeySpec(mod, exp);

		String cipherPW = null;

		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PublicKey pub = factory.generatePublic(spec);
			Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.ENCRYPT_MODE, pub);

			byte[] cipherText = rsa.doFinal(account.password.getBytes());

			cipherPW = Base64.getEncoder().encodeToString(cipherText);
			cipherPW = cipherPW.replace("/", "%2F");
			cipherPW = cipherPW.replace("+", "%2B");
			cipherPW = cipherPW.replace("=", "%3D");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// request login with the encrypted password
		data = "password=" + cipherPW 
				+ "&username=" + account.username
				+ "&twofactorcode=" 
				+ "&emailauth=" 
				+ "&loginfriendlyname="
				+ "&captchagid=-1" 
				+ "&captcha_text=" 
				+ "&emailsteamid="
				+ "&rsatimestamp=" + steamFieldRSA.timestamp
				+ "&remember_login=false" 
				+ "&donotcache=1427612348145";

		cookies = new ArrayList<SteamCookieName>();
		cookies.add(SteamCookieName.MACHINE_AUTH);
		cookies.add(SteamCookieName.SESSION_ID);

		SteamIO.processRequest(
				"https://steamcommunity.com/login/dologin/", 
				cookies,
				data, 
				new InputProcessor<LoginRecord>(steamFieldLogin) {
					@Override
					public void process(String steamIn, LoginRecord result) {
						//System.out.println(steamIn);

						// fill the Login field using steams input
						Pattern pattern = Pattern
								.compile("\\{\"success\":(.*?)[,\\}]");
						Matcher matcher = pattern.matcher(steamIn);
						matcher.find();

						if (matcher.group(1).equals("true")) {

							pattern = Pattern.compile("\\{\"success\":true,"
									+ "\"requires_twofactor\":(.*?),"
									+ "\"login_complete\":(.*?),"
									+ "\"transfer_url\":\"(.*?)\","
									+ "\"transfer_parameters\":\\{"
									+ "\"steamid\":\"(.*?)\","
									+ "\"token\":\"(.*?)\","
									+ "\"auth\":\"(.*?)\","
									+ "\"remember_login\":(.*?),"
									+ "\"token_secure\":\"(.*?)\"" + "\\}\\}");
							matcher = pattern.matcher(steamIn);
							matcher.find();

						} else {
							Logging.err.println(matcher.group(1));
						}

						result.fill(matcher);
					}
				});

		SteamIO.sleep(2000);

		// follow the transfer url after login
		data = "steamid=" + steamFieldLogin.steamid 
				+ "&token="	+ steamFieldLogin.token 
				+ "&auth=" + steamFieldLogin.auth
				+ "&remember_login=false" 
				+ "&token_secure=" + steamFieldLogin.token_secure;

		cookies = new ArrayList<SteamCookieName>();
		cookies.add(SteamCookieName.MACHINE_AUTH);
		cookies.add(SteamCookieName.SESSION_ID);
		cookies.add(SteamCookieName.LOGIN);
		cookies.add(SteamCookieName.LOGIN_SECURE);

		SteamIO.processRequest(steamFieldLogin.transfer_url, cookies, data,
				new InputProcessor<Object>(null) {
					@Override
					public void process(String steamIn, Object result) {
						//System.out.println(steamIn);
						// no processing needed
					}
				});

		SteamIO.sleep(2000);
		
		statusUpdate = System.currentTimeMillis();
		onlineStatus = STATUS_UNKNOWN;
	}
	
	
	/**
	 * Logs out and sets onlineStatus to STATUS_OFFLINE.
	 */
	public static void doLogout() {
		// the data to be sent
		String data = SteamCookieManager.getCookieString(SteamCookieName.SESSION_ID);

		// the cookies to be used
		ArrayList<SteamCookieName> cookies = new ArrayList<SteamCookieName>();
		cookies.add(SteamCookieName.MACHINE_AUTH);
		cookies.add(SteamCookieName.SESSION_ID);
		cookies.add(SteamCookieName.LOGIN);
		cookies.add(SteamCookieName.LOGIN_SECURE);
				
		SteamIO.processRequest(
				"https://steamcommunity.com/login/logout/",
				cookies, 
				data, 
				new InputProcessor<Object>(null) {
					@Override
					public void process(String steamIn, Object result) {
						//System.out.println(steamIn);
					}
				});

		SteamIO.sleep(2000);
		
		statusUpdate = System.currentTimeMillis();
		onlineStatus = STATUS_OFFLINE;
	}

	
	/**
	 * Tests if logged in by checking whether the email is visible in account settings.
	 * Sets onlineStatus depending on return value.
	 * @return
	 */
	public static boolean isLoggedIn() {
		// account to use
		Account account  = preferences.accounts.get(preferences.activeProfile);

		// test if logged in
		ArrayList<SteamCookieName> cookies = new ArrayList<SteamCookieName>();
		cookies.add(SteamCookieName.MACHINE_AUTH);
		cookies.add(SteamCookieName.SESSION_ID);
		cookies.add(SteamCookieName.LOGIN);
		cookies.add(SteamCookieName.LOGIN_SECURE);

		boolean[] loggedIn = { false };

		SteamIO.processRequest(
				"https://store.steampowered.com/account/", 
				cookies,
				null, 
				new InputProcessor<boolean[]>(loggedIn) {
					@Override
					public void process(String steamIn, boolean[] result) {
						// logged in?
						result[0] = steamIn.contains(account.email);
						
						// check account balance
						if (result[0]) {
							int start = steamIn.indexOf("<div class=\"accountData price\">");
							int end = steamIn.indexOf("</div>", start);
							String s = steamIn.substring(start, end);
							
							Pattern pattern = Pattern.compile(".*?(\\d*?),(.*?)&#8364.*");
							Matcher matcher = pattern.matcher(s);
							
							if (matcher.find()) {
								String g1 = matcher.group(1);
								String g2 = matcher.group(2);
								float bal;
								try {
									bal = (float)Integer.parseInt(g1) + 0.01f * Integer.parseInt(g2);
								} catch (NumberFormatException e) {
									bal = (float)Integer.parseInt(g1);
								}
								SteamSession.setBalance(bal);
								
							} else {
								Logging.err.println(SteamSession.class.getSimpleName() + " account balance matcher failed");
							}
						}
					}
				});
		
		statusUpdate = System.currentTimeMillis();
		onlineStatus = loggedIn[0] ? STATUS_ONLINE : STATUS_OFFLINE;
		
		return loggedIn[0];
	}





	public static int getOnlineStatus() {
		return onlineStatus;
	}



	public static void setOnlineStatus(int onlineStatus) {
		SteamSession.onlineStatus = onlineStatus;
	}


	public static float getBalance() {
		return balance;
	}


	public static void setBalance(float balance) {
		SteamSession.balance = balance;
		Logging.debug.println("Login: balance set to " + balance);
		MainWindow.labelBalance.setText("Balance: " + balance + "€");
	}

}
