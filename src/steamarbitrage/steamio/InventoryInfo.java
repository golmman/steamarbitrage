package steamarbitrage.steamio;

public class InventoryInfo {
	public long assetId;
	public long classId;
	public long instanceId;
	public long amount;
	public int pos;
	
	public String name;

	public InventoryInfo(long assetId, long classId, long instanceId, long amount, int pos, String name) {
		this.assetId = assetId;
		this.classId = classId;
		this.instanceId = instanceId;
		this.amount = amount;
		this.pos = pos;
		this.name = name;
	}
}
