package steamarbitrage.steamio.tor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import steamarbitrage.database.ItemNames;
import steamarbitrage.steamio.ListingInputProcessor;
import steamarbitrage.steamio.ListingRecord;
import steamarbitrage.steamio.SteamIO;


public class TestSteamGateListener implements GateListener {
	
	private Iterator<String> nameIter = ItemNames.names.iterator();
	private int debugIndex = 0;
	
	
	public TestSteamGateListener() {
		
	}

	@Override
	public void gatePrepared(Gate g) {
		
		Thread t = new Thread() {
			@Override
			public void run() {
				System.out.println(g.getProxy().address() + " " + g.requestPublicIP());
			}
		};
		t.setDaemon(true);
		t.start();
		
		
		
		
		
		
		
		
		
		String name = nameIter.next();
		String[] urlstr = {""};
		
		try {
			urlstr[0] = URLEncoder.encode(name, "UTF-8");
			urlstr[0] = urlstr[0].replace("+", "%20");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				SteamIO.processRequest(
						"http://steamcommunity.com/market/listings/730/" + urlstr[0] + "/render/"
						+ "?query="
						+ "&start=0"
						+ "&count=" + 1
						+ "&country=DE"
						+ "&language=english"
						+ "&currency=3", 
						null, 
						null, 
						new ListingInputProcessor(new ListingRecord(name,	1.0f, debugIndex), true),
						g.getProxy());
			}
		};
		
		thread.setDaemon(true);
		//thread.start();
		
		++debugIndex;
		
	}

}
