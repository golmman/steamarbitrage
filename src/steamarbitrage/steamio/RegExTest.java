package steamarbitrage.steamio;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import steamarbitrage.Logging;
import steamarbitrage.database.ItemNames;
import steamarbitrage.database.steam.Price;


public class RegExTest {
	
	private String listing = "";
	
	
	
	private RegExTest() {}
	
	public static void main(String[] args) {
		ItemNames.loadNames();
		
		test7();
	}
	
	
	
	public static byte[] encodePublicKey(RSAPublicKey key) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		/* encode the "ssh-rsa" string */
		byte[] sshrsa = new byte[] { 0, 0, 0, 7, 's', 's', 'h', '-', 'r', 's', 'a' };
		out.write(sshrsa);
		/* Encode the public exponent */
		BigInteger e = key.getPublicExponent();
		byte[] data = e.toByteArray();
		encodeUInt32(data.length, out);
		out.write(data);
		/* Encode the modulus */
		BigInteger m = key.getModulus();
		data = m.toByteArray();
		encodeUInt32(data.length, out);
		out.write(data);
		return out.toByteArray();
	}

	public static void encodeUInt32(int value, OutputStream out) throws IOException {
		byte[] tmp = new byte[4];
		tmp[0] = (byte) ((value >>> 24) & 0xff);
		tmp[1] = (byte) ((value >>> 16) & 0xff);
		tmp[2] = (byte) ((value >>> 8) & 0xff);
		tmp[3] = (byte) (value & 0xff);
		out.write(tmp);
	}
	
	
	
	
	public static void test1() {
		
		
		
		String json = 
					"{\"success\":true,"
					+ "\"publickey_mod\":\"C665A34F0801E6F044B5A0D2714CD7978DBE750FC5AFBE778A601C7E430E4EED33CA110321C147F8C97D724FE63F0FA576F96148C45FECC5158ABF10B7BAB7A6696ABDCC134CCEE157167DCB221D96C1A91213B9D66306E9D048DFA910EE1FE55AA54FC6D8181B56A22D91486FAD283DF6D5A180DE1A13F9B8D139E7FAD5A11100BD976B8759F3EEAC6EBE51E29E6C98493DFF8B0B6096E139BA428EB2113F3AF6C59DE5C516DCC277AF730A01A4E34A1AA23E88654955A9F4F84CF9EF377839BE7872286115D7C2F613A380CDC32614B8CA68AE1811C7BBAA8EF125E88A172F63D7C860CEDC7D3916A52CB5E30FCD9514F84F6527FC5ECBED212E054A86B3A7\","
					+ "\"publickey_exp\":\"010001\","
					+ "\"timestamp\":\"432371800000\","
					+ "\"steamid\":\"76561198121652995\","
					+ "\"token_gid\":\"2d133ba4705f23e\"}";
		
		//str = "{\"success\":false}";
		
		
		// ([^=]*)(=)([^;]*)(;)(.*)
		Pattern pattern = Pattern.compile("\\{\"success\":(.*?)[,\\}]");
		Matcher matcher = pattern.matcher(json);
		matcher.find();
		
		if (matcher.group(1).equals("true")) {
			
			pattern = Pattern.compile(
					"\\{\"success\":true,"
					+ "\"publickey_mod\":\"(.*?)\","
					+ "\"publickey_exp\":\"(.*?)\","
					+ "\"timestamp\":\"(.*?)\","
					+ "\"steamid\":\"(.*?)\","
					+ "\"token_gid\":\"(.*?)\"\\}"
			);
			matcher = pattern.matcher(json);
			matcher.find();
			
			for (int i = 1; i <= matcher.groupCount(); i++) {
			    System.out.println(matcher.group(i));
			}
		} else {
			System.out.println(matcher.group(1));
		}
		
		
		System.out.println(matcher.group(1).length());
		
		String password = "2.Lehrjahr";
		password = "fsdfdsfejzzujw";
		String publickey_mod = matcher.group(1);
		String publickey_exp = matcher.group(2);
		
		BigInteger mod = new BigInteger(publickey_mod, 16);
		BigInteger exp = new BigInteger(publickey_exp, 16);
		
		RSAPublicKeySpec spec = new RSAPublicKeySpec(mod, exp);
		
		
		try {
		
			//KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
			KeyFactory factory = KeyFactory.getInstance("RSA");
		    PublicKey pub = factory.generatePublic(spec);
		    //Cipher rsa = Cipher.getInstance("RSA", "BC");
		    Cipher rsa = Cipher.getInstance("RSA");
		    rsa.init(Cipher.ENCRYPT_MODE, pub);
		    
		    
	
		    byte[] cipherText = rsa.doFinal(password.getBytes());
//		    byte[] cipherText2 = pub.getEncoded();
//		    byte[] cipherText3 = new X509EncodedKeySpec(cipherText).getEncoded();
//		    byte[] cipherText4 = encodePublicKey((RSAPublicKey) pub);
		    
		    byte[] cipherText5 = Base64.getEncoder().encode(cipherText);
		    
		    System.out.println(cipherText.length);
		    System.out.println(cipherText5.length);
		    
		    for (int i = 0; i < cipherText.length; i++) {
		    	System.out.print((char)cipherText5[i]);
		    	//System.out.println((int)(cipherText[i] & 0xFF));
			}
		    
		    
		    
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		
//		Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
//		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//		byte[] cipherData = cipher.doFinal(input.getBytes());
		
		
		System.exit(0);
	}
	
	
	
	
	
	public static void test2() {
		
		String json = "{\"success\":true,"
				+ "\"requires_twofactor\":false,"
				+ "\"login_complete\":true,"
				+ "\"transfer_url\":\"https:\\/\\/store.steampowered.com\\/login\\/transfer\","
				+ "\"transfer_parameters\":"
					+ "{\"steamid\":\"76561198121652995\","
					+ "\"token\":\"25948E9B6F32DA56243C7E47FF01B821E3EB7E1F\","
					+ "\"auth\":\"6472b6ecfea15ee2b286b840d99e8646\","
					+ "\"remember_login\":false,"
					+ "\"token_secure\":\"231ACD0D842DE8BECF35D21B980AA95ACFE0B68F\"}}";
		
		
		Pattern pattern = Pattern.compile("\\{\"success\":(.*?)[,\\}]");
		Matcher matcher = pattern.matcher(json);
		matcher.find();
		
		if (matcher.group(1).equals("true")) {
			
			pattern = Pattern.compile(
					"\\{\"success\":true,"
					+ "\"requires_twofactor\":(.*?),"
					+ "\"login_complete\":(.*?),"
					+ "\"transfer_url\":\"(.*?)\","
					+ "\"transfer_parameters\":\\{"
						+ "\"steamid\":\"(.*?)\","
						+ "\"token\":\"(.*?)\","
						+ "\"auth\":\"(.*?)\","
						+ "\"remember_login\":(.*?),"
						+ "\"token_secure\":\"(.*?)\""
					+ "\\}\\}"
			);
			matcher = pattern.matcher(json);
			matcher.find();
			
			for (int i = 1; i <= matcher.groupCount(); i++) {
			    System.out.println(matcher.group(i));
			}
		} else {
			System.out.println(matcher.group(1));
		}
		
		
		System.out.println(matcher.group(3).replace("\\/", "/"));
		
		
		
		System.exit(0);
	}
	
	
	
	public static void test3() {
		File file = new File("test_update.txt");
		
		String json = "";
		int c;
		FileInputStream f;
		try {
			f = new FileInputStream(file);
			
			while ((c = f.read()) != -1) {
				json += (char)c;
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		System.out.println(json);
		
		
		
		// {"success":true,"lowest_price":"0,05&#8364; ","volume":"958","median_price":"0,03&#8364; "}
		// {"success":true,"lowest_price":"210,--&#8364; "}
		
		Pattern pattern = Pattern.compile(
				"\\{\"success\":true,"
				+ "\"lowest_price\":\"(\\d*),(\\d*)(.*?)\","
				+ "\"volume\":\"(\\d*),?(\\d*)\","
				+ "\"median_price\":\"(\\d*),(\\d*)(.*?)\"\\}"
		);
		Matcher matcher = pattern.matcher(json);
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
			    System.out.println(matcher.group(i));
			}
			
			int euro = Integer.parseInt(matcher.group(1));
			int cent = Integer.parseInt(matcher.group(2));
			float low = euro + 0.01f * cent;
			
			euro = Integer.parseInt(matcher.group(6));
			cent = Integer.parseInt(matcher.group(7));
			float median = euro + 0.01f * cent;
			
			int volume = 0;
			if (matcher.group(5).equals("")) {
				volume = Integer.parseInt(matcher.group(4));
			} else {
				volume = Integer.parseInt(matcher.group(4) + matcher.group(5));
			}
			
			System.out.println(low + "  " + volume + "  " + median);
		}

		
	}
	
	public static void test4() {
		File file = new File("test_listing.txt");
		
		String json = "";
		StringBuilder sb = new StringBuilder("");
		int c;
		
		
		FileInputStream f;
		try {
			f = new FileInputStream(file);
			
			while ((c = f.read()) != -1) {
				//json += (char)c;
				sb.append((char)c);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		json = sb.toString();
		
		
		
		float price = 0.0f;
		long listingId = 0L;
		long assetId = 0L;
		
		
		int strStart = json.indexOf("listinginfo");
		int strEnd = json.indexOf("market_actions", strStart);
		
		if (strStart == -1 || strEnd == -1) {
			Logging.err.println("ListingInputProcessor: \"listinginfo\" or \"market_actions\" not found in streamIn");
			return;
		}
		
		String listing = json.substring(strStart, strEnd);
		
		System.out.println(listing);
		
		
		Pattern pattern = Pattern.compile(
				"(.*?)\"listingid\":\"(\\d*)\""
				+ "(.*?)\"converted_price\":(\\d*)"
				+ "(.*?)\"converted_fee\":(\\d*)"
				+ "(.*?)\"id\":\"(\\d*)\""
		);
		Matcher matcher = pattern.matcher(listing);
		
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
			    System.out.println(matcher.group(i));
			}
			
			listingId = Long.parseLong(matcher.group(2));
			
			int pri = Integer.parseInt(matcher.group(4));
			int fee = Integer.parseInt(matcher.group(6));
			
			assetId = Long.parseLong(matcher.group(8));
			price = 0.01f * (pri + fee);
			
		}
		
		
		System.out.println(listingId + "   " + price + "    " + assetId);
	}
	
	
	
	public static void test5() {
		SteamCookieManager.init();
		
		//String str = "sessionid=8acd398a172c47079a9ebbc6; steamCC_94_223_173_87=DE; steamLogin=76561198121652995%7C%7C259";
		//String str = "steamCC_94_223_173_87=DE; steamLogin=76561198121652995%7C%7C259";
		//String str = "steamMachineAuth76561198121652995=A193A4717783AB2428CD171A7F60C8440174E7A8; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowe";
		//String str = "sBLAAA_94_223_173_87=DE; steamLogin=76561198121652995%7C%7C259";
		String str = "steamLoginSecure=76561198121652995%7C%7C259; sBLAAA_94_223_173_87=DE;";
		
		String keys = "(";
		for (SteamCookieName name : SteamCookieName.values()) {
			keys += (SteamCookieManager.cookies.get(name).key + "|");
		}
		keys += ".*)";
		//keys = "(steamMachineAuth|sessionid|steamLoginSecure|steamLogin|.*)";
		System.out.println(keys);
		
		
		Pattern pattern = Pattern.compile(keys + "(.*?)=(.*?);.*");
		Matcher matcher = pattern.matcher(str);
		
		if (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				System.out.println(matcher.group(i));
			}
		}
		
		//SteamCookie cookie = new SteamCookie(matcher.group(1), matcher.group(2), matcher.group(3));
		
	}
	
	public static void test6() {
		SteamCookieManager.init();
		
		String steamIn = "<div class=\"accountRow accountBalance\"><div class=\"accountData price\">1,09&#8364; </div><div class=\"accountLabel\">Steam-Guthaben</div>";
		
		int start = steamIn.indexOf("<div class=\"accountData price\">");
		int end = steamIn.indexOf("</div>", start);
		String s = steamIn.substring(start, end);
		
		Pattern pattern = Pattern.compile(".*?(\\d*?),(.*?)&#8364.*");
		Matcher matcher = pattern.matcher(s);
		
		if (matcher.find()) {
			System.out.println(s);
			System.out.println(matcher.group(1) + "v   v" + matcher.group(2));
			
			float balance = Integer.parseInt(matcher.group(1)) + 0.01f * Integer.parseInt(matcher.group(2));
			Logging.debug.println("Login: balance set to " + balance);
		} else {
			Logging.err.println(SteamSession.class.getSimpleName() + " account balance matcher failed");
		}
		
	}
	
	
	
	
	public static void test7() {
		
		String steamIn = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			File file = new File("inventory regextest.txt");
			FileInputStream fis = new FileInputStream(file);
			int c = 0;
			
			while ((c = fis.read()) != -1) {
				sb.append((char)c);
			}
			
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		steamIn = sb.toString();
		
		//SellInputProcessor sip = new SellInputProcessor(null);
		//sip.loadInventory(steamIn);
		
		//
		sb = new StringBuilder();
		try {
			File file = new File("test_listing.txt");
			FileInputStream fis = new FileInputStream(file);
			int c = 0;
			
			while ((c = fis.read()) != -1) {
				sb.append((char)c);
			}
			
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		steamIn = sb.toString();
		
		
		
		long listingId = 0L;
		int highestPrice = 100000;
		
		int strStart = steamIn.indexOf("\"listingid\"");
		int strEnd = steamIn.indexOf("\"asset\"", strStart);
		
		while (strStart != -1 && strEnd != -1) {
			
			String listing = steamIn.substring(strStart, strEnd);
			
			//System.out.println(listing);
			
			
			Pattern pattern = Pattern.compile(
					"\"listingid\":\"(\\d*)\""
					+ "(.*?)\"converted_price\":(\\d*)"
					+ "(.*?)\"converted_fee\":(\\d*)"
			);
			Matcher matcher = pattern.matcher(listing);
			
			if (matcher.find()) {
				
				listingId = Long.parseLong(matcher.group(1));
				
				int pri = Integer.parseInt(matcher.group(3));
				int fee = Integer.parseInt(matcher.group(5));

				
				System.out.println(listingId + "   " + pri + " " + fee);
				
			} else {
				System.out.println("matching error");
			}
			
			
			strStart = steamIn.indexOf("\"listingid\"", strEnd);
			strEnd = steamIn.indexOf("\"asset\"", strStart);
		}
		

		
		
		
	}
	
	
	public static final float conversionRate = 1.0f / 1.11f;
	public static final float inverseConversionRate = 1.0f / conversionRate;
	
	public static int ConvertToTheirCurrency(int amount) {
		float flAmount = (float)amount * conversionRate;
		int nAmount = (int)Math.floor(Float.isNaN(flAmount) ? 0 : flAmount);

		return Math.max(nAmount, 0);
	}

	public static int ConvertToOurCurrency(int amount) {
		float flAmount = inverseConversionRate * amount;

		int nAmount = (int)Math.ceil(Float.isNaN(flAmount) ? 0 : flAmount);
		nAmount = Math.max(nAmount, 0);

		// verify the amount. we may be off by a cent.
		if (ConvertToTheirCurrency(nAmount) != amount) {
			for (int i = nAmount - 2; i <= nAmount + 2; i++) {
				if (ConvertToTheirCurrency(i) == amount) {
					nAmount = i;
					break;
				}
			}
		}

		return nAmount;
	}
	
	
	public static int steamFees(int x) {
		return x + Math.max(2 , (int)(0.05f * x) + (int)(0.1f * x));
	}
	
	public static int steamFeesInverse(int x) {
		//System.out.println("-------------------" + (0.05f * x / 1.15f) + "   " + (0.1f * x / 1.15f));
		return x - Math.max(2, (int)(0.05f * x / 1.15f) + (int)(0.1f * x / 1.15f + 0.05f));
	}
	
	public static void test8() {
//		int i0 = ConvertToOurCurrency(39);
//		int i1 = ConvertToOurCurrency(40);
//		
//		System.out.println(i0 + "  " + ConvertToTheirCurrency(i0));
//		System.out.println(i1 + "  " + ConvertToTheirCurrency(i1));
		
		for (int k = 1; k < 1000; ++k) {
			int sf = steamFees(k);
			int sfInv = steamFeesInverse(sf);
			System.out.println(k + " " + sf + " " + sfInv + (k != sfInv ? "--------" : ""));
		}
		
	}

}





































// http://nulliplex.rssing.com/chan-1811969/all_p473.html












