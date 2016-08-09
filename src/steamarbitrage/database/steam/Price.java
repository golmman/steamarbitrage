package steamarbitrage.database.steam;

import java.io.Serializable;
import java.util.Date;

public class Price implements Serializable {
	private static final long serialVersionUID = -1997059586622609700L;
	
	public float low;
	public float median;
	public int volume; 
	public Date date;
	public int score;
	
	public Price() {
		this.low = 0.0f;
		this.median = 0.0f;
		this.volume = 0;
		this.date = new Date(0L);
		this.score = 0;
	}
	
	public Price(float low, float median, int volume, Date date) {
		this.low = low;
		this.median = median;
		this.volume = volume;
		this.date = date;
		this.score = 0;
	}
	
	public Price(float low, float median, int volume, Date date, int score) {
		this.low = low;
		this.median = median;
		this.volume = volume;
		this.date = date;
		this.score = score;
	}
}