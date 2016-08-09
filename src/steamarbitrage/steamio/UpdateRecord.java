package steamarbitrage.steamio;

import steamarbitrage.database.steam.PriceData;

public class UpdateRecord {
	
	public String full_name;
	public PriceData priceData;
	
	public UpdateRecord(PriceData priceData, String full_name) {
		this.priceData = priceData;
		this.full_name = full_name;
	}
}
