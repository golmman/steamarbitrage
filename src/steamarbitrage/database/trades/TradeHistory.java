package steamarbitrage.database.trades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import steamarbitrage.Logging;
import steamarbitrage.database.ItemNames;

public class TradeHistory {

	public static LinkedList<Trade> buy = new LinkedList<Trade>();
	public static ArrayList<Sold> sell = new ArrayList<Sold>();
	
	private TradeHistory() {}
	
	
	public static void save() {
		try {
			File file = new File("buy");
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(buy);
			oos.close();
			fos.close();
			
			
			file = new File("sell");
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(sell);
			oos.close();
			fos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked") 
	public static void load() {
		try {
			File file = new File("buy");
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			buy = (LinkedList<Trade>)ois.readObject();
			ois.close();
			fis.close();
			
			
			file = new File("sell");
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			sell = (ArrayList<Sold>)ois.readObject();
			ois.close();
			fis.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Logging.debug.println("" + buy.size() + " bought items loaded:");
		for (Trade trade : buy) {
			Logging.debug.println(trade.toString());
		}
		
		
		//loadFirstTrades();
	}
	
	@SuppressWarnings("unused")
	private static void loadFirstTrades() {
		String str = "";
		
		try {
			File file = new File("firsttrades.txt");
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			while ((str = br.readLine()) != null) {
				
				Pattern pattern = Pattern.compile(".*?(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d).*?"
						+ "(\\d*?),.*?(\\d).(\\d\\d).*?(\\d).(\\d\\d).*?, Profit!, "
						+ "(.*?),.*");
				Matcher matcher = pattern.matcher(str);
				
				if (matcher.find()) {
//					for (int i = 1; i <= matcher.groupCount(); i++) {
//						System.out.print(matcher.group(i) + " ");
//					}
//					System.out.println();
					
					
					@SuppressWarnings("deprecation")
					Date date = new Date(
							2015-1900, 3, Integer.parseInt(matcher.group(1)),
							Integer.parseInt(matcher.group(2)),
							Integer.parseInt(matcher.group(3)),
							Integer.parseInt(matcher.group(4)));
					
					float estimate = Integer.parseInt(matcher.group(6)) + 0.01f * Integer.parseInt(matcher.group(7));
					float price = Integer.parseInt(matcher.group(8)) + 0.01f * Integer.parseInt(matcher.group(9));
					
					Trade trade = new Trade(matcher.group(10), estimate, price, date, 0L);
					
					boolean found = false;
					for (String s : ItemNames.names) {
						if (s.equals(trade.name)) {
							found = true;
							break;
						}
					}
					
					if (!found) System.out.println("----------------------------!!!");
					
					//buy.add(trade);
					
				} else {
					Logging.out.println("matcher error");
				}
				
				
			}
			
			System.out.println("" + buy.size() + " trades added");
			
			br.close();
			isr.close();
			fis.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		str = "Mon Apr 06 20:34:42 index: 608, cur: 	0.98	, est: 		2.02	, Profit!, StatTrak™ SG 553 | Pulse (Field-Tested), listingid: 427055046533610811";
		Pattern pattern = Pattern.compile(".*?(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d).*?"
				+ "(\\d*?),.*?(\\d).(\\d\\d).*?(\\d).(\\d\\d).*?, Profit!, "
				+ "(.*?),.*");
		Matcher matcher = pattern.matcher(str);
		
		if (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				System.out.println(matcher.group(i));
			}
		} else {
			Logging.out.println("matcher error");
		}

	}
	

}






/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */


















