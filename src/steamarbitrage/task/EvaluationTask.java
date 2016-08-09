package steamarbitrage.task;

import steamarbitrage.Evaluation;
import steamarbitrage.Logging;
import steamarbitrage.gui.Preferences;

public class EvaluationTask extends Task {

	public EvaluationTask() {
		super("Evaluate Prices");
	}

	@Override
	public void run() {
		Preferences prefs = Preferences.getInstance();
		Evaluation.setParameters(prefs.minEvalEstimate, 1000.0f, prefs.minEvalVolume, 100000);
		
		int sum = Evaluation.evaluate();

		Logging.out.println(Evaluation.evaluatedNames.size() + " items evaluated, " + sum + " score sum");
		
		sleep(1000);
	}

}
