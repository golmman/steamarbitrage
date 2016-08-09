package steamarbitrage.database.steamanalyst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import steamarbitrage.Logging;
import steamarbitrage.database.ItemNames;
import steamarbitrage.steamio.InputProcessor;

public class SteamAnalyst {

	
	//private static ArrayList<SteamAnalystRecord> data = new ArrayList<SteamAnalystRecord>(4300);
	public static HashMap<String, SteamAnalystRecord> data = new HashMap<String, SteamAnalystRecord>(4300);
	
	
	private SteamAnalyst() {}
	
	
	public static void main(String[] args) {
		ItemNames.loadNames();
		//Logging.init(new JTextArea(), new JTextArea());
		
//		StringBuilder sb = null;//readAndSave();
//		load(sb);
		
		update();
		
//		for (String key : ItemNames.names) {
//			SteamAnalystRecord sar = data.get(key);
//
//			Logging.out.println(sar.toString());
//
//		}
	}
	
	
	public static void update() {
		StringBuilder sb = readAndSave();
		load(sb);
		
		
	}
	
	
	
	public static StringBuilder readAndSave() {
		URL url = null;
		HttpURLConnection con = null;
		StringBuilder initListStringBuilder = new StringBuilder();
		String initListString = null;
		String sAjaxSource = null;

		try {
			url = new URL("http://csgo.steamanalyst.com/list");
			con = (HttpURLConnection)url.openConnection();
			
			con.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
			con.setRequestProperty("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2200.0 Iron/41.0.2200.0 Safari/537.36");
			con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			
			GZIPInputStream gis = new GZIPInputStream(con.getInputStream());
			InputStreamReader isr = new InputStreamReader(gis);
			
			int c = 0;
			while ((c = isr.read()) != -1) {
				initListStringBuilder.append((char)c);
			}
			initListString = initListStringBuilder.toString();
			
			gis.close();
			isr.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		/*
		 * 
		 * Find sAjaxSource
		 * 
		 */
		int initListStart = initListString.indexOf("sAjaxSource");
		int initListEnd = initListString.indexOf("bStateSave", initListStart);
		initListString = initListString.substring(initListStart, initListEnd);
		
		Pattern pattern = Pattern.compile("sAjaxSource\":\"(.*?)\"");
		Matcher matcher = pattern.matcher(initListString);
		
		if (matcher.find()) {
			sAjaxSource = matcher.group(1);
			//System.out.println(sAjaxSource);
		} else {
			System.err.println(SteamAnalyst.class.getSimpleName() + " initList Matching error: " + initListString);
		}
		
		
		/*
		 * 
		 * Read and write cookies
		 * 
		 */
		CookieManager cookieManager = new CookieManager();
		
		Map<String, List<String>> headerFields = con.getHeaderFields();
		List<String> cookiesHeader = headerFields.get("Set-Cookie");

		if (cookiesHeader != null) {
			for (String cookie : cookiesHeader) {
				cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
			}
		}
		HttpCookie listCookie = new HttpCookie("SpryMedia_DataTables_list_list", "%7B%22iCreate%22%3A1428594174537%2C%22iStart%22%3A0%2C%22iEnd%22%3A0%2C%22iLength%22%3A50%2C%22aaSorting%22%3A%5B%5B2%2C%22asc%22%2C0%5D%2C%5B3%2C%22asc%22%2C0%5D%5D%2C%22oSearch%22%3A%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%22aoSearchCols%22%3A%5B%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%2C%7B%22bCaseInsensitive%22%3Atrue%2C%22sSearch%22%3A%22%22%2C%22bRegex%22%3Afalse%2C%22bSmart%22%3Atrue%7D%5D%2C%22abVisCols%22%3A%5Btrue%2Cfalse%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%5D%7D");
		cookieManager.getCookieStore().add(null, listCookie);
		

		CookieStore cookieStore = cookieManager.getCookieStore();
		List<HttpCookie> cookieList = cookieStore.getCookies();
		
		String cookieStr = "";
		
		for (HttpCookie cookie : cookieList) {
			cookieStr += (cookie.getName() + "=" + cookie.getValue() + "; ");
			//System.out.println(cookie.getName() + "=" + cookie.getValue());
		}
		
		
		/*
		 * 
		 * get the list
		 * 
		 */
		try {
			//url = new URL("http://csgo.steamanalyst.com/jsonList.php?p=7&_=1428785047101");
			url = new URL("http://csgo.steamanalyst.com/" + sAjaxSource + "&_=1430568181926");
			con = (HttpURLConnection)url.openConnection();
			
			con.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
			con.setRequestProperty("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("Host", "csgo.steamanalyst.com");
			con.setRequestProperty("Referer", "http://csgo.steamanalyst.com/list");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2200.0 Iron/41.0.2200.0 Safari/537.36");
			con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			
			con.setRequestProperty("Cookie", cookieStr);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		StringBuilder res = new StringBuilder();
		
		new InputProcessor<StringBuilder>(res) {
			@Override
			public void process(String steamIn, StringBuilder result) {
				//System.out.println(steamIn.length());
				
				if (steamIn.length() > 100000) {
					try {
						File file = new File("steamanalyst");
						FileOutputStream fos = new FileOutputStream(file);
						OutputStreamWriter osw = new OutputStreamWriter(fos);
						BufferedWriter bw = new BufferedWriter(osw);
						
						bw.write(steamIn);
						
						bw.close();
						osw.close();
						fos.close();
						
					} catch (IOException e) {
						result.setLength(0);
						e.printStackTrace();
					}
					
					result.append(steamIn);
				} else {
					result.setLength(0);
					Logging.err.println(this.getClass().getSimpleName() + " Read from connection failed.");				
				}
			}
		}.readInput(con);
		
		return res;
	}
	
	
	public static void load(StringBuilder rawSb) {
		String raw;
		
		if (rawSb.length() == 0) {
			Logging.err.println(SteamAnalyst.class.getSimpleName() + " Loading from file.");
			
			StringBuilder sb = new StringBuilder();
			String str;
			
			try {
				File file = new File("steamanalyst");
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);
				
				while ((str = br.readLine()) != null) {
					sb.append(str);
				}
				
				br.close();
				isr.close();
				fis.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			raw = sb.toString();
		} else {
			raw = rawSb.toString();
		}
		
		
		
		
		int braceOpen;
		int braceClose;
		braceOpen = raw.indexOf("[[", 0);
		
		
		while ((braceOpen = raw.indexOf("[", braceOpen+1)) != -1) {
			braceClose = raw.indexOf("]", braceOpen);
			
			// SteamAnalystNO MATCH: ["","0.15","0.19<\/a>","-0.04","0",0,"<span class=\"downtrend\">-31.34<\/span>","0.00064"
			String sub = raw.substring(braceOpen, braceClose);
			
			
			Pattern pattern = Pattern.compile(
					"\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)<\\\\/a>\","
					+ "\"(.*?)\",\"(.*?)\",(.*?),(\"<.*?>(.*?)<.*?>\"|\"N\\\\/A\"),\"(.*?)\".*");
			Matcher matcher = pattern.matcher(sub);
			
			
			if (matcher.find()) {
				
				SteamAnalystRecord sar = new SteamAnalystRecord(matcher);
				sar.statbase = sar.statbase.replace("\\u2122", "™");
				String fullname;
				
				if (sar.type.equals("Container")) {
					fullname = sar.statbase;
				} else {
					fullname = sar.statbase + " | " + sar.skin + " (" + sar.wear + ")";
				}
				data.put(fullname, sar);
			} else {
				Logging.err.println(SteamAnalyst.class.getSimpleName() + " NO MATCH: " + sub);
			}
			
		}
		
		Logging.out.println(SteamAnalyst.class.getSimpleName() + " " + data.size() + " records loaded");
		
//		for (String key : data.keySet()) {
//			SteamAnalystRecord sar = data.get(key);
//			
//			if (key.contains("Case")) {
//				System.out.println(key + "      >>" + sar.type);
//			}
//		}
//		
//		System.out.println("---------------------");
//		
//		for (String key : ItemNames.names) {
//			SteamAnalystRecord sar = data.get(key);
//			
//			if (sar == null) {
//				//System.out.print("------------------- NOT FOUND ");
//				System.out.println(key);
//			}
//			
//		}
	}
}




















