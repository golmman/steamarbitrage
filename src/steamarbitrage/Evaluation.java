package steamarbitrage;

import java.util.ArrayList;

import steamarbitrage.database.ItemNames;
import steamarbitrage.database.steam.Price;
import steamarbitrage.database.steamanalyst.SteamAnalyst;
import steamarbitrage.database.steamanalyst.SteamAnalystRecord;



public class Evaluation {
	
	private static final int NO_SCORE = 0;
	private static final int MIN_SCORE = 1;
	private static final int MAX_SCORE = 50;
	private static final int MIN_VOLUME = 1; 		// cap for evaluation
	@SuppressWarnings("unused")
	private static final int MAX_VOLUME = 100000; 	// cap for evaluation

	private static float minValue = 0.0f;
	private static float maxValue = 0.0f;
	private static int minVolume = 0;
	private static int maxVolume = 0;
	
	public static ArrayList<String> evaluatedNames = new ArrayList<String>(); 
	
	private Evaluation() {}
	
	
	
	// TODO: improve!
	public static void evaluate(Price p) {
		evaluate(p, Evaluation.minValue, Evaluation.maxValue, Evaluation.minVolume, Evaluation.maxVolume);
	}
	
	public static void evaluate(Price p, float minValue, float maxValue, int minVolume, int maxVolume) {
		p.score = NO_SCORE;
		
		if (Math.max(p.low, p.median) >= minValue && 
			Math.min(p.low, p.median) <= maxValue &&
			p.volume >= minVolume &&
			p.volume <= maxVolume) {
			
			//int maxVol = Math.min(maxVolume, MAX_VOLUME);
			int minVol = Math.max(minVolume, MIN_VOLUME);
			
			p.score = Math.min(p.volume / minVol, MAX_SCORE);
			
			//if (p.score > 1) p.score /= 2;
		}
	}
	
	
	/**
	 * Save to call alternative to evaluate, even during a search.
	 * @param minValue
	 * @param maxValue
	 * @param minVolume
	 * @param maxVolume
	 * @return the score sum
	 */
	public static int getScoreSum(float minValue, float maxValue, int minVolume, int maxVolume) {
		// TODO: same method as evaluation
		int sum = 0;
		
		Price p = null;
		SteamAnalystRecord sar = null;
		
		for (String name : ItemNames.names) {
			p = SteamArbitrage.priceData.get(name);
			sar = SteamAnalyst.data.get(name);
			
			if (sar != null && sar.sold > 0) {
				p.volume = sar.sold;
			} else {
				Logging.err.println(Evaluation.class.getSimpleName() + " sar == null OR sold == 0");
			}
			
			evaluate(p, minValue, maxValue, minVolume, maxVolume);	
			
//			Fade
//			Case Hardened
//			Doppler
//			Crimson Web
//			Asimov Battle-Scarred
//			galil chatterbox battlescarred
			if (name.contains("Fade") 
					|| name.contains("Case Hardened")
					|| name.contains("Doppler")
					|| name.contains("Crimson Web")) {
				p.score = NO_SCORE;
			} else if (name.contains("AWP | Asiimov")) {
				//if (name.contains("Battle-Scarred")) {
					p.score *= 3;
				//}
			}
					
			
			
			
			if (p.score >= MIN_SCORE) {
				sum += p.score;
			}
		}
		
		return sum;
	}
	
	
	/**
	 * Updates the score field of priceData and fills the evaluatedNames array
	 * which is later used in the search.
	 * This method must not be called during a search! 
	 * @return the score sum
	 */
	public static int evaluate() {
		evaluatedNames = new ArrayList<String>();
		
		Price p = null;
		SteamAnalystRecord sar = null;
		
		int sum = 0;
		
		for (String name : ItemNames.names) {
			p = SteamArbitrage.priceData.get(name);
			sar = SteamAnalyst.data.get(name);
			
			if (sar != null && sar.sold > 0) {
				p.volume = sar.sold;
			} else {
				Logging.err.println(Evaluation.class.getSimpleName() + " sar == null OR sold == 0");
			}
			
			evaluate(p);
			
			
			
			
			
//			Fade
//			Case Hardened
//			Doppler
//			Crimson Web
//			Asimov Battle-Scarred
//			galil chatterbox battlescarred
			if (name.contains("Fade") 
					|| name.contains("Case Hardened")
					|| name.contains("Doppler")
					|| name.contains("Crimson Web")) {
				p.score = NO_SCORE;
			} else if (name.contains("AWP | Asiimov")) {
				//if (name.contains("Battle-Scarred")) {
					p.score *= 3;
				//}
			}
					
			
			
			
			if (p.score >= MIN_SCORE) {
				evaluatedNames.add(name);
				sum += p.score;
			}
		}
		
		return sum;
	}
	
	
	/**
	 *
	 * Updates the score field of priceData and fills the evaluatedNames array
	 * which is later used in the search.
	 * This method must not be called during a search! 
	 *
	 * @param minValue
	 * @param maxValue
	 * @param minVolume
	 * @param maxVolume
	 * @return the score sum
	 */
	public static int evaluate(float minValue, float maxValue, int minVolume, int maxVolume) {
		setParameters(minValue, maxValue, minVolume, maxVolume);
		return evaluate();
	}
	
	
	public static void setParameters(float minValue, float maxValue, int minVolume, int maxVolume) {
		Evaluation.minValue = minValue;
		Evaluation.maxValue = maxValue;
		Evaluation.minVolume = minVolume;
		Evaluation.maxVolume = maxVolume;
	}

	public static float getMinValue() {
		return minValue;
	}

	public static void setMinValue(float minValue) {
		Evaluation.minValue = minValue;
	}

	public static float getMaxValue() {
		return maxValue;
	}

	public static void setMaxValue(float maxValue) {
		Evaluation.maxValue = maxValue;
	}

	public static int getMinVolume() {
		return minVolume;
	}

	public static void setMinVolume(int minVolume) {
		Evaluation.minVolume = minVolume;
	}

	public static int getMaxVolume() {
		return maxVolume;
	}

	public static void setMaxVolume(int maxVolume) {
		Evaluation.maxVolume = maxVolume;
	}
	
	
	
	
}
