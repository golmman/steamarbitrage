package steamarbitrage.steamio.handler;

import java.util.LinkedList;

import steamarbitrage.Logging;
import steamarbitrage.database.steam.PriceData;


@Deprecated
public class CurrentListingHandler implements OutputHandler {
	
	private PriceData priceData;
	private String full_name;
	private float margin;
	private int debugIndex;
	
	public CurrentListingHandler(PriceData priceData, String full_name, float margin, int debugIndex) {
		this.priceData = priceData;
		this.full_name = full_name;
		this.margin = margin;
		this.debugIndex = debugIndex;
	}

	
	
	@Override
	public void handle(String out) {
		
		LinkedList<Float> values = new LinkedList<Float>();
		float valueEstimate = Math.min(priceData.get(full_name).low, priceData.get(full_name).median);
		
		int mark = 0;
		int sign = 0;
		int usd  = 0;
		
		final int ERROR = 0;
		final int SOLD = 1;
		final int NOPRICE = 2;
		int break_reason = ERROR;
		
		
		while (true) {
			
			mark = out.indexOf("market_listing_price_with_fee", usd);
			if (mark == -1) {
				break_reason = NOPRICE;
				break;
			} else if (out.contains("Sold!")) {
				break_reason = SOLD;
				break;
			}
			
			sign = out.indexOf("&#36;", mark);
			usd  = out.indexOf("USD", sign);
			
			try {
				values.add(Float.parseFloat(out.substring(sign + 5, usd - 1)));
			} catch (StringIndexOutOfBoundsException e) {
				Logging.err.println("--------------------------------------------------------");
				Logging.err.println(out);
				Logging.err.println("--------------------------------------------------------");
			}
		}
		
		String str = "index: " + debugIndex + ", ";
		
		switch (break_reason) {
		case ERROR:
			str += "ERROR, ";
			break;
		case SOLD:
			str += "SOLD, ";
			break;
		case NOPRICE:
			if (values.isEmpty()) {
				str += "NOPRICE, ";
			}
			break;
		}
		
		if (!values.isEmpty()) {
			str += "cur: " + values.getFirst() + ", est: " + valueEstimate + ", ";
			
			if (values.getFirst() <= margin * valueEstimate) {
				str += "Profit!, " + full_name;
				Logging.out.println(str);
			}
		}
		
		
		//str += full_name;
		//Logging.err.println(str);
		
	}
	
}
