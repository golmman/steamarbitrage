package steamarbitrage.steamio;

import steamarbitrage.Logging;


/**
 * 
 * Remembers errors that were reported. If too many errors occurred in a set time
 * SteamError pauses SteamThreadQueue for errorDelay milliseconds.
 *
 * If more than or equal to 1 Error per 4 seconds over a time of 4 * threshold occur
 * the SteamThreadQueue is paused.
 */
public class SteamError {
	
	private static int count = 0;
	private static int threshold = 10;
	private static int delay = 10 * 60 * 1000;
	
	private SteamError() {}
	
	/**
	 * Initializes parameters and starts the Thread which deals with reported errors.
	 * @param errorThreshold
	 * @param errorDelay
	 */
	public static void init(int errorThreshold, int errorDelay) {
		SteamError.threshold = errorThreshold;
		SteamError.delay = errorDelay;
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				
				
				while (true) {
					
					// pause if too many errors
					if (SteamError.count > SteamError.threshold) {
						Logging.err.println(SteamError.class.getSimpleName()
								+ " Too many Errors, pause for " + (SteamError.delay / 1000) + " seconds.");
						SteamIO.setQueuePaused(true);
						
						try {
							sleep(SteamError.delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						SteamIO.setQueuePaused(false);
						SteamError.count = 0;
					}
					
					// count is capped from below
					SteamError.count -= 1;
					if (SteamError.count < 0) {
						SteamError.count = 0;
					}
					
					// sleep, repeat
					try {
						sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Report an error. Too many errors reported result in pausing the SteamThreadQueue.
	 * Thus you should only report severe errors such as connection failures. 
	 * 
	 * If more than or equal to 1 Error per 4 seconds over a time of 4 * threshold occur
	 * the SteamThreadQueue is paused.
	 */
	public static void report() {
		SteamError.count += 1;
		Logging.debug.println("Error reported, current count: " + SteamError.count);
	}

	public static int getThreshold() {
		return threshold;
	}

	public static void setThreshold(int threshold) {
		SteamError.threshold = threshold;
	}

	public static int getDelay() {
		return delay;
	}

	public static void setDelay(int delay) {
		SteamError.delay = delay;
	}

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		SteamError.count = count;
	}

	
}
