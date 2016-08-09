package steamarbitrage.steamio.handler;

import steamarbitrage.steamio.handler.DefaultConnectionHandler;

@Deprecated
public class ConnectionHandler {
	private static final DefaultConnectionHandler dch = new DefaultConnectionHandler();
	
	private ConnectionHandler() {}

	public static DefaultConnectionHandler getDefault() {
		return dch;
	}
	
}