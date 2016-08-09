/**
 * 
 */
package steamarbitrage.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import steamarbitrage.Evaluation;
import steamarbitrage.Logging;
import steamarbitrage.SteamArbitrage;
import steamarbitrage.gui.MainWindow;
import steamarbitrage.gui.Preferences;
import steamarbitrage.steamio.ListingInputProcessor;
import steamarbitrage.steamio.ListingRecord;
import steamarbitrage.steamio.SteamIO;

/**
 * 
 * Searches the evaluated listings for arbitrage by sending out listing requests.
 * See {@link steamarbitrage.steamio.ListingInputProcessor ListingInputProcessor}
 * 
 */
public class SearchTask extends Task {

	/**
	 * 
	 * Searches the evaluated listings for arbitrage by sending out listing requests.
	 * See {@link steamarbitrage.steamio.ListingInputProcessor#ListingInputProcessor ListingInputProcessor}
	 * 
	 */
	public SearchTask() {
		super("Search");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//Logging.out.println("Searching for arbitrage...");
		
		int count = 1;
		String urlstr = "";
		int debugIndex = 0;
		
		
		// shuffle requests
		ArrayList<String> names = new ArrayList<String>();
		for (String full_name : Evaluation.evaluatedNames) {
			int score = SteamArbitrage.priceData.get(full_name).score;
			for (int k = 0; k < score; ++k) {
				names.add(full_name);
			}
		}
		Collections.shuffle(names);
		
		MainWindow.progressBar.setMaximum(names.size());
		MainWindow.progressBar.setValue(0);
		
		
		if (Preferences.getInstance().buyAt > 0.7f) {
			Logging.err.println("search task stopped: attempted to buy at " + Preferences.getInstance().buyAt);
			return;
		}
		
		// send requests
		SteamIO.setProperties(Preferences.getInstance().searchRequestDelay, 5*60*1000, 6, 4);
		
		for (String name : names) {
		
			try {
				urlstr = URLEncoder.encode(name, "UTF-8");
				urlstr = urlstr.replace("+", "%20");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			//System.out.println(urlstr);
			
			
			float buyAt = name.contains("AWP | Asiimov") ? 0.9f : Preferences.getInstance().buyAt;
			
			
			SteamIO.queueRequest(
					"http://steamcommunity.com/market/listings/730/" + urlstr + "/render/"
					+ "?query="
					+ "&start=0"
					+ "&count=" + count
					+ "&country=DE"
					+ "&language=english"
					+ "&currency=3", 
					null, 
					null, 
					new ListingInputProcessor(
						new ListingRecord(
								name,
								buyAt, 
								debugIndex)));
			
			++debugIndex;
		}
		
		
		do {
			sleep(1000);
		} while (SteamIO.getThreadDeque().size() > 0);
	}
}
