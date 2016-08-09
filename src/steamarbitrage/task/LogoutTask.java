package steamarbitrage.task;

import steamarbitrage.steamio.SteamSession;

public class LogoutTask extends Task {

	public LogoutTask() {
		super("Log out");
	}

	@Override
	public void run() {
		SteamSession.doLogout();
	}

}
