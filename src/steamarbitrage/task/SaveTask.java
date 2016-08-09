package steamarbitrage.task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import steamarbitrage.SteamArbitrage;
import steamarbitrage.gui.MainWindow;

/**
 * 
 * Save
 *
 */
public class SaveTask extends Task {

	public SaveTask() {
		super("Save");
	}

	@Override
	public void run() {
		
		// save prices
		SteamArbitrage.priceData.savePrices();
		
		// save out
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("out.txt", true)))) {
			out.println();
			out.println("-------- Log entry date: " + new Date() + "--------");
			
//			for (int k = 0; k < MainWindow.areaOutput.getLineCount(); ++k) {
//				int start =  MainWindow.areaOutput.getLineStartOffset(k);
//				int end =  MainWindow.areaOutput.getLineEndOffset(k)-1;	// exclude \n
//				String text =  MainWindow.areaOutput.getText(start, end - start);
//				out.println(text);
//			}
			out.write(MainWindow.areaOutput.getText());
			
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// save err
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("err.txt", true)))) {
			
			out.println();
			out.println("-------- Log entry date: " + new Date() + "--------");
			
//			for (int k = 0; k < MainWindow.areaError.getLineCount(); ++k) {
//				int start =  MainWindow.areaError.getLineStartOffset(k);
//				int end =  MainWindow.areaError.getLineEndOffset(k)-1;	// exclude \n
//				String text =  MainWindow.areaError.getText(start, end - start);
//				out.println(text);
//			}
			out.write(MainWindow.areaError.getText());
		    
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		
		// clear err and out
		MainWindow.areaOutput.setText("");
		MainWindow.areaError.setText("");
		
		sleep(1000);
	}

}
