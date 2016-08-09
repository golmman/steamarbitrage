package steamarbitrage.steamio.handler;

import steamarbitrage.Logging;

@Deprecated
public class TestHandler implements OutputHandler {

	@Override
	public void handle(String out) {
		Logging.out.println(out);
	}
	
}
