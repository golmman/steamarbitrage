package steamarbitrage.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import steamarbitrage.SteamArbitrage;
import steamarbitrage.database.ItemNames;
import steamarbitrage.database.steamanalyst.SteamAnalyst;
import steamarbitrage.gui.MainWindow;
import steamarbitrage.steamio.SteamIO;
import steamarbitrage.steamio.UpdateInputProcessor;
import steamarbitrage.steamio.UpdateRecord;

/**
 * 
 * Updates the prices for all listed names by queuing update requests.
 * See {@link steamarbitrage.steamio.UpdateInputProcessor UpdateInputProcessor}
 *
 */
public class UpdateTask extends Task {
	
	/**
	 * 
	 * Updates the prices for all listed names by queuing update requests.
	 * See {@link steamarbitrage.steamio.UpdateInputProcessor#UpdateInputProcessor(UpdateRecord) UpdateInputProcessor}
	 *
	 */
	public UpdateTask() {
		super("Update Prices");
	}

	@Override
	public void run() {
		//Logging.out.println("Updating Prices...");
		
		SteamAnalyst.update();
		
		MainWindow.progressBar.setMaximum(ItemNames.names.size());
		MainWindow.progressBar.setValue(0);

		SteamIO.setProperties(200, 5*60*1000, 6, 4);
		
		for (String full_name : ItemNames.names) {
			
			String urlstr = null;
			try {
				urlstr = URLEncoder.encode(full_name, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			
			SteamIO.queueRequest(
					"http://steamcommunity.com/market/priceoverview/?country=DE&currency=3&appid=730&market_hash_name=" + urlstr, 
					null, 
					null, 
					new UpdateInputProcessor(
							new UpdateRecord(SteamArbitrage.priceData, full_name)));
			
		}
		
		do {
			sleep(1000);
		} while (SteamIO.getThreadDeque().size() > 0);
	}

}
