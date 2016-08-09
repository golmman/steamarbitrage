package steamarbitrage.database.trades;

import java.io.Serializable;


public class Sold implements Serializable {

	private static final long serialVersionUID = -7381845440437667817L;
	
	public Trade buy;
	public Trade sell;
	
	public Sold() {
		// TODO Auto-generated constructor stub
	}

}
