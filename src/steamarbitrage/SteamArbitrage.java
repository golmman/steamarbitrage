package steamarbitrage;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import steamarbitrage.database.ItemNames;
import steamarbitrage.database.steam.PriceData;
import steamarbitrage.database.steamanalyst.SteamAnalyst;
import steamarbitrage.database.trades.TradeHistory;
import steamarbitrage.gui.DebugWindow;
import steamarbitrage.gui.JTextAreaOutputStream;
import steamarbitrage.gui.MainWindow;
import steamarbitrage.gui.Preferences;
import steamarbitrage.steamio.SteamError;
import steamarbitrage.steamio.SteamIO;
import steamarbitrage.task.TaskManager;

public class SteamArbitrage {
	
	
	public static PriceData priceData = new PriceData();
	public static TaskManager taskManager = new TaskManager();

	public static void main(String[] args) throws IOException  {
		JWindow windowLoading = new JWindow();
		JPanel panel = new JPanel();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(new JLabel("Loading..."));
		panel.add(Box.createHorizontalGlue());
		
		panel.setPreferredSize(new Dimension((int)(1.618f * 200), 200));
		
		windowLoading.add(panel);
		windowLoading.pack();
		windowLoading.setLocationRelativeTo(null);
		windowLoading.setVisible(true);
		
		
		
		
		Logging.init(
				new JTextAreaOutputStream(MainWindow.areaOutput), 
				new JTextAreaOutputStream(MainWindow.areaError),
				new JTextAreaOutputStream(DebugWindow.areaDebug));
		
		DebugWindow.init();
		
		ItemNames.loadNames();
		TradeHistory.load();
		priceData.loadPrices();
		SteamAnalyst.load(new StringBuilder());
		
		taskManager.start();
		SteamError.init(10, 2 * 60 * 1000);
		SteamIO.init();
		System.setProperty("sun.net.http.errorstream.enableBuffering", "true");
		
		Logging.out.println("Current profile: " + Preferences.getInstance().activeProfile);
		
		windowLoading.dispose();
		new MainWindow("SteamArbitrage");
		
		
	}
}



