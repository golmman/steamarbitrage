package steamarbitrage.steamio.handler;

import java.util.Date;

import steamarbitrage.database.steam.Price;
import steamarbitrage.database.steam.PriceData;

@Deprecated
public class UpdatePricesHandler implements OutputHandler {
	
	private PriceData priceData;
	private String full_name;
	
	public UpdatePricesHandler(String full_name, PriceData priceData) {
		this.full_name = full_name;
		this.priceData = priceData;
	}

	@Override
	public void handle(String out) {
		
		// find low price
		int ind_start = out.indexOf("&#36;", 0);
		int ind_end = out.indexOf("USD", 0)-1;
		//System.out.println(ind_start + "   " + ind_end);
		float low = 0;
		if (ind_start != -1 && ind_end != -1) {
			low = Float.parseFloat(out.substring(ind_start+5, ind_end));
		}
		
		// find median price
		ind_start = out.indexOf("&#36;", ind_start+1);
		ind_end = out.indexOf("USD", ind_end+2)-1;
		//System.out.println(ind_start + "   " + ind_end);
		float median = 0;
		if (ind_start != -1 && ind_end != -1) {
			median = Float.parseFloat(out.substring(ind_start+5, ind_end));
		}
		
		// find volume
		ind_start = out.indexOf("volume", 0)+9;
		ind_end = out.indexOf('"', ind_start);
		
		int volume = 0;
		if (ind_start >= 9) {
			if (ind_end - ind_start >= 4) {
				int ind_comma = out.indexOf(',', ind_start);
				int v1 = Integer.parseInt(out.substring(ind_start, ind_comma));
				int v2 = Integer.parseInt(out.substring(ind_comma+1, ind_end));
				volume = 1000 * v1 + v2;
			} else {
				volume = Integer.parseInt(out.substring(ind_start, ind_end));
			}
		}
		
		priceData.put(full_name, new Price(low, median, volume, new Date()));
		
	}
	
	
}
