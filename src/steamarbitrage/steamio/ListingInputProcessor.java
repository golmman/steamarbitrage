package steamarbitrage.steamio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import steamarbitrage.Logging;
import steamarbitrage.SteamArbitrage;
import steamarbitrage.gui.MainWindow;

/**
 * 
 * Compares stored price to current listings price. 
 * Usually invoked by {@link steamarbitrage.task.SearchTask SearchTask}.
 *
 */
public class ListingInputProcessor extends InputProcessor<ListingRecord> {

	
	private boolean debug = false;
	
	/**
	 * Compares stored price to current listings price. 
	 * Used to test Tor proxies, debug should be true.
	 * @param result
	 * @param debug
	 */
	public ListingInputProcessor(ListingRecord result, boolean debug) {
		super(result);
		this.debug = debug;
	}
	
	
	/**
	 * 
	 * Compares stored price to current listings price. 
	 * Usually invoked by {@link steamarbitrage.task.SearchTask#SearchTask() SearchTask}.
	 * @param result
	 */
	public ListingInputProcessor(ListingRecord result) {
		super(result);
	}

	/* (non-Javadoc)
	 * @see steamarbitrage.steamio.InputProcessor#process(java.lang.String, java.lang.Object)
	 */
	@Override
	public void process(String steamIn, ListingRecord result) {
		
		// Request:
		// 		http://steamcommunity.com/market/listings/730/P250%20%7C%20Boreal%20Forest%20%28Battle-Scarred%29/render/?query=&start=0&count=10&country=DE&language=english&currency=3
		// Response:
		//		?
		
		
		float priceEstimate = 0.0f;
		
		if (debug) {
			priceEstimate = 0.0f;
		} else {
			priceEstimate = Math.min(SteamArbitrage.priceData.get(result.full_name).low, SteamArbitrage.priceData.get(result.full_name).median);
			
			// Update MainWindow
			MainWindow.textCurrent.setText("Search Price: " + result.full_name);
			int pbCur = MainWindow.progressBar.getValue() + 1;
			int pbMax = MainWindow.progressBar.getMaximum();
			int threadCount = SteamIO.getThreadGroup().activeCount();
			MainWindow.progressBar.setValue(pbCur);
			MainWindow.progressBar.setString("" + pbCur + "/" + pbMax + ", Threads: " + threadCount);
		}
		
		
		// Process input
		if (steamIn.contains("Sold!")) {
			return;
		}
		
		float total = 0.0f;
		long listingId = 0L;
		long assetId = 0L;
		int subtotal = 0;
		int fee = 0;
		
		
		int strStart = steamIn.indexOf("listinginfo");
		int strEnd = steamIn.indexOf("market_actions", strStart);
		
		if (strStart == -1 || strEnd == -1) {
			// this happens mostly when InputProcessor.readInput failed because of connection errors.
			// So there is a high chance this error is already reported.
			Logging.err.println("ListingInputProcessor: \"listinginfo\" or \"market_actions\" not found in streamIn");
			return;
		}
		
		String listing = steamIn.substring(strStart, strEnd);	
		
		// !!!!
		// TODO: This pattern seems only to work if count=1 in the json query
		Pattern pattern = Pattern.compile(
				"(.*?)\"listingid\":\"(\\d*)\""
				+ "(.*?)\"converted_price\":(\\d*)"
				+ "(.*?)\"converted_fee\":(\\d*)"
				+ "(.*?)\"id\":\"(\\d*)\""
		);
		Matcher matcher = pattern.matcher(listing);
		
		if (matcher.find()) {
			listingId = Long.parseLong(matcher.group(2));
			
			subtotal = Integer.parseInt(matcher.group(4));
			fee = Integer.parseInt(matcher.group(6));
			
			assetId = Long.parseLong(matcher.group(8));
			
			total = 0.01f * (subtotal + fee);
		} else {
			// mostly listing equals 
			Logging.err.println(this.getClass().getSimpleName() + " matching error: " + listing);
			return;
		}
		
		
		
		// debug?
		if (debug) {
			Logging.out.println(
					"index: " + result.debugIndex 
					+ ", cur: " + total 
					+ ", est: " + priceEstimate 
					+ ", name: " + result.full_name
					+ ", listingid: " + listingId);
			return;
		}
		
		
		
		// try to buy if price is acceptable
		if (total <= result.margin * priceEstimate) {
			
			Logging.out.println(
					"index: " + result.debugIndex 
					+ ", cur: " + total 
					+ ", est: " + priceEstimate 
					+ ", Profit!, " + result.full_name
					+ ", listingid: " + listingId);
			
			
			if (total <= SteamSession.getBalance()) {
				Logging.out.println("Buying listing: " + result.full_name);
				SteamIO.buyListing(new BuyRecord(listingId, assetId, subtotal, fee, result.full_name, priceEstimate));
			} else {
				Logging.out.println("balance = " + SteamSession.getBalance() + " < " + total + " = cost ==> not buying");
			}
		}
		
		
		
		
	}

}
