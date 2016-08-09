package steamarbitrage.task;

public class IdleTask extends Task {

	public IdleTask() {
		super("Idle");
	}

	@Override
	public void run() {
		sleep(1000);
	}

}
