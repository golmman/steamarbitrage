package steamarbitrage.task;

import steamarbitrage.Logging;
import steamarbitrage.steamio.SteamSession;

public class LoginTask extends Task {

	public LoginTask() {
		super("Log in");
	}

	@Override
	public void run() {
		SteamSession.doLogin();
		if (SteamSession.isLoggedIn() == false) {
			Logging.err.println("Login Task failed");
		}
	}

}
