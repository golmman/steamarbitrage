package steamarbitrage.database.algo;

import java.util.Random;

class Listing {
	private static int size;
	
	public int volume;
	
	public double scoreGain;
	public double score;
	
	public double partition;
	public double partitionEst;
	
	
	public int lastTime;
	
	public Listing(int real) {
		Listing.size += 1;
		
		this.volume = real;
		this.scoreGain = 0;
		this.score = 0;
		this.partitionEst = 0;
		this.partition = 0;
		this.lastTime = 0;
	}
	
	/**
	 * For a volume of real = 1000 we want one new item every Listing.size/5 ticks,
	 * i.e. if real = 1 one item for 1000 * Listing.size/5 = 200 * Listing.size ticks
	 * 
	 * @param currTime
	 * @return
	 */
	public boolean success(int currTime) {
		final double LAMBDA = 1.0 / (200 * Listing.size);
		
		int time = (currTime - lastTime) * this.volume;
		lastTime = currTime;
		
		int newItems = 0;
		
		while ((time -= Simulation.nextExp(LAMBDA)) > 0) {
			++newItems;
		}
		
		return (newItems > 0);
	}
}


public class Simulation {

	public static final int LISITNGS = 10;
	
	public static Listing[] listing = new Listing[LISITNGS];
	
	public static Random rnd = new Random();
	
	public Simulation() {
		
	}

	
	public static void main(String[] args) {
		
		
		final double STARTGAIN = 10.0;
		
		
		long sum = 0;
		
		for (int i = 0; i < listing.length; i++) {
			
			int volume = (int)(50 + Math.abs(5000.0/4.60517 * nextExp(1)));
			sum += volume;
			
			listing[i] = new Listing(volume);
		}
//		listing[0].volume = 10;
//		listing[1].volume = 500;
//		listing[2].volume = 200;
//		listing[3].volume = 10;
//		listing[4].volume = 0;
		
		for (int i = 0; i < listing.length; i++) {
			
			listing[i].partition = (double)listing[i].volume / sum;
			listing[i].partitionEst = (double)1 / listing.length;
			listing[i].scoreGain = STARTGAIN;
			
			System.out.println(listing[i].volume + "     " + listing[i].partition + "       " + listing[i].partitionEst);
		}
		
		
		
		System.out.println("-------------------");
		
		
//		for (int i = 0; i < 100; i++) {
//			for (int j = 0; j < listing.length; j++) {
//				System.out.print((listing[j].success(i) ? "1" : "0") + " ");
//			}
//			System.out.println();
//		}
		
		
		for (int j = 0; j < listing.length; j++) {
			System.out.format("  %08d  ", listing[j].volume); 
		}
		System.out.println();
		
		
		
		int timer = 0;
		
		for (int i = 0; i < 10000; i++) {
			
			double scoreGainSum = 0.0;
			
			for (int j = 0; j < listing.length; j++) {
				
				listing[j].score += listing[j].scoreGain;
				int score = (int)listing[j].score;
				
				
				for (int k = 0; k < score; k++) {
					
					if (listing[j].success(timer)) {
						listing[j].scoreGain += 0.01;
					} else {
						listing[j].scoreGain -= 0.01;
					}
					
					listing[j].score -= 1.0;
					
					++timer;
				}
				
				if (listing[j].scoreGain < 0.1) listing[j].scoreGain = 0.1;
				
				scoreGainSum += listing[j].scoreGain;
				//System.out.format("%10.3f  ", listing[j].scoreGain); 
			}
			
			
			// normalize
			double factor = (STARTGAIN * listing.length) / scoreGainSum;
			for (int j = 0; j < listing.length; j++) {
				listing[j].scoreGain *= factor;
				System.out.format("%10.3f  ", listing[j].scoreGain); 
			}
			
			System.out.println(" sum1: " + scoreGainSum);
			
		}
		
		
		System.out.println();
		
		
		double dsum1 = 0.0;
		double dsum2 = 0.0;
		for (int j = 0; j < listing.length; j++) {
			dsum1 += listing[j].scoreGain;
			dsum2 += listing[j].volume; 
		}
		
		
		
		for (int j = 0; j < listing.length; j++) {
			System.out.format("%10.3f  ", listing[j].scoreGain / dsum1); 
		}
		System.out.print(" estimate");
		System.out.println();
		
		for (int j = 0; j < listing.length; j++) {
			System.out.format("%10.3f  ", (double)listing[j].volume / dsum2); 
		}
		System.out.print(" true");
		System.out.println();
		
	}
	
	
	public static double nextExp(double lambda) {
		return -Math.log1p(-rnd.nextDouble()) / lambda;
	}
}





















