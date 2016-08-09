package steamarbitrage.steamio;

public class ListingRecord {

	public String full_name;
	public float margin;
	public int debugIndex;
	
	public ListingRecord(String full_name, float margin, int debugIndex) {
		this.full_name = full_name;
		this.margin = margin;
		this.debugIndex = debugIndex;
	}
}
