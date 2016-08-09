package steamarbitrage.steamio;



/**
 * Creates an InputProcessor that does nothing with the input,
 * i.e. creating an instance of DefaultInputProcessor is equivalent to
 * 
 * <pre>
 * <code>
 * InputProcessor<Object> ip = new InputProcessor<Object>(null) {
 * 	{@literal @}Override
 * 	public void process(String steamIn, Object result) {}
 * }
 * </code>
 * </pre>
 */
public class DefaultInputProcessor extends InputProcessor<Object> {

	
	/**
	 * Creates an InputProcessor that does nothing with the input,
	 * i.e. creating an instance of DefaultInputProcessor is equivalent to
	 * 
	 * <pre>
	 * <code>
	 * InputProcessor<Object> ip = new InputProcessor<Object>(null) {
	 * 	{@literal @}Override
	 * 	public void process(String steamIn, Object result) {}
	 * }
	 * </code>
	 * </pre>
	 */
	public DefaultInputProcessor() {
		super(null);
	}

	@Override
	public void process(String steamIn, Object result) {}

}
