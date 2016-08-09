package steamarbitrage.database.steamanalyst;

import java.util.regex.Matcher;

public class SteamAnalystRecord {

	public String type;
	public String url;
	public String statbase;
	public String skin;
	public String wear;
	public String wearAbbr;
	
	// default values should give a bad evaluation score
	public float low = 0.0f;
	public float median = 0;
	public float diff = 0;
	public int sold = 0;
	public int volume = 0;
	public float trend = -100000.0f;
	public float volatility = 100000.0f;
	
	
	public SteamAnalystRecord(Matcher matcher) {
		type = matcher.group(1);
		url = matcher.group(2);
		statbase = matcher.group(3);
		skin = matcher.group(4);
		wear = matcher.group(5);
		wearAbbr = matcher.group(6);
		
		try {
			low = Float.parseFloat(matcher.group(7));
		} catch (NullPointerException | NumberFormatException e) {}
		
		try {
			median = Float.parseFloat(matcher.group(8));
		} catch (NullPointerException | NumberFormatException e) {}
		
		try {
			diff = Float.parseFloat(matcher.group(9));
		} catch (NullPointerException | NumberFormatException e) {}
		
		try {
			sold = Integer.parseInt(matcher.group(10));
		} catch (NullPointerException | NumberFormatException e) {}
		
		try {
			volume = Integer.parseInt(matcher.group(11));
		} catch (NullPointerException | NumberFormatException e) {}
		
		try {
			trend = Float.parseFloat(matcher.group(13));
		} catch (NullPointerException | NumberFormatException e) {}
		
		try {
			volatility = Float.parseFloat(matcher.group(14));
		} catch (NullPointerException | NumberFormatException e) {}
	}
	
	
	@Override
	public String toString() {
		String str = type + " "
		+ url + " "
		+ statbase + " "
		+ skin + " "
		+ wear + " "
		+ wearAbbr + " "
		+ low + " "
		+ median + " "
		+ diff + " "
		+ sold + " "
		+ volume + " "
		+ trend + " "
		+ volatility;
		
		return str;
	}
}
