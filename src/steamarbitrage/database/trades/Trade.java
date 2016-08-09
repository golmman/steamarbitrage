package steamarbitrage.database.trades;

import java.io.Serializable;
import java.util.Date;

public class Trade implements Serializable {

	private static final long serialVersionUID = -930994144370795842L;
	
	public String name;
	public float estimate;
	public float price;
	public Date date;
	public long assetId;
	
	public Trade() {
		
	}

	public Trade(String name, float estimate, float price, Date date, long assetId) {
		this.name = name;
		this.estimate = estimate;
		this.price = price;
		this.date = date;
		this.assetId = assetId;
	}
	
	@Override
	public String toString() {
		return date + " " + estimate + " "+ price + " " + name + " " + assetId;
	}

}
