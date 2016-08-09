/**
 * 
 */
package steamarbitrage.steamio;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import steamarbitrage.Logging;
import steamarbitrage.database.steam.Price;
import steamarbitrage.gui.MainWindow;

/**
 * Updates the price of a listing.
 * Usually invoked by {@link steamarbitrage.task.UpdateTask UpdateTask}.
 *
 */
public class UpdateInputProcessor extends InputProcessor<UpdateRecord> {

	/**
	 * Updates the price of a listing.
	 * Usually invoked by {@link steamarbitrage.task.UpdateTask#UpdateTask() UpdateTask}.
	 * @param result
	 */
	public UpdateInputProcessor(UpdateRecord result) {
		super(result);
	}

	/* (non-Javadoc)
	 * @see steamarbitrage.steamio.InputProcessor#process(java.lang.String, java.lang.Object)
	 */
	@Override
	public void process(String steamIn, UpdateRecord result) {
		
		// Request:
		// 		http://steamcommunity.com/market/priceoverview/?country=DE&currency=3&appid=730&market_hash_name=CZ75-Auto%20%7C%20Victoria%20(Factory%20New)
		// Response:
		//		{"success":true,"lowest_price":"12,45&#8364; ","volume":"26","median_price":"10,71&#8364; "}
		
		// Update MainWindow
		MainWindow.textCurrent.setText("Update Price: " + result.full_name);
		
		int pbCur = MainWindow.progressBar.getValue() + 1;
		int pbMax = MainWindow.progressBar.getMaximum();
		int threadCount = SteamIO.getThreadGroup().activeCount();
		MainWindow.progressBar.setValue(pbCur);
		MainWindow.progressBar.setString("" + pbCur + "/" + pbMax + ", Threads: " + threadCount);
		
		
		// Process input
		float low = 0.0f;
		float median = 0.0f;
		int volume = 0;
		int euro = 0;
		int cent = 0;
		
		Pattern pattern = Pattern.compile(
				"\\{\"success\":true,"
				+ "\"lowest_price\":\"(\\d*),(\\d*)(.*?)\","
				+ "\"volume\":\"(\\d*),?(\\d*)\","
				+ "\"median_price\":\"(\\d*),(\\d*)(.*?)\"\\}"
		);
		Matcher matcher = pattern.matcher(steamIn);
		
		if (matcher.find()) {
			try {
				euro = Integer.parseInt(matcher.group(1));
				if (matcher.group(2).equals("")) {
					cent = 0;
				} else {
					cent = Integer.parseInt(matcher.group(2));
				}
				low = euro + 0.01f * cent;
				
				euro = Integer.parseInt(matcher.group(6));
				if (matcher.group(7).equals("")) {
					cent = 0;
				} else {
					cent = Integer.parseInt(matcher.group(7));
				}
				median = euro + 0.01f * cent;
				
				if (matcher.group(5).equals("")) {
					volume = Integer.parseInt(matcher.group(4));
				} else {
					volume = Integer.parseInt(matcher.group(4) + matcher.group(5));
				}
			} catch (Exception e) {
				Logging.err.println("matcher " + steamIn);
			}
		} else {
			Logging.err.print(result.full_name + " " + steamIn);
		}
		
		

		result.priceData.put(result.full_name, new Price(low, median, volume, new Date()));
	}

}
