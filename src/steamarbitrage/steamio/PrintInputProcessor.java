package steamarbitrage.steamio;

import steamarbitrage.Logging;

/**
 * 
 * Prints the steam input to Logging.out
 *
 */
public class PrintInputProcessor extends InputProcessor<Object> {

	/**
	 * 
	 * Prints the steam input to Logging.out
	 *
	 */
	public PrintInputProcessor() {
		super(null);
	}

	@Override
	public void process(String steamIn, Object result) {
		Logging.out.println(steamIn);		
	}

}
