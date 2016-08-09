package steamarbitrage.steamio;

public class BuyRecord {
	public long listingId;
	public long assetId;
	public int subtotal;
	public int fee;
	public String fullname; 
	public float estimate;
	
	public BuyRecord() {}

	public BuyRecord(long listingId, long assetId, int subtotal, int fee, String fullname, float estimate) {
		this.listingId = listingId;
		this.assetId = assetId;
		this.subtotal = subtotal;
		this.fee = fee;
		this.fullname = fullname;
		this.estimate = estimate;
	}

}
