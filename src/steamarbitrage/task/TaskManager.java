/**
 * 
 */
package steamarbitrage.task;

import java.util.LinkedList;
import java.util.Queue;

import steamarbitrage.Logging;

/**
 * 
 *
 */
public class TaskManager extends Thread {

	private boolean stopped = true;
	
	private ThreadGroup threadGroup = new ThreadGroup("TaskThreads");
	
	private Queue<Task> taskQueue = null;
	private Task currentTask = null;
	
	private long lastUpdateTime = 0;
	
	private static final long UPDATE_AFTER = 2 * 60 * 60 * 1000; 
	
	
	public TaskManager() {
		taskQueue = new LinkedList<Task>();
		setDaemon(true);
		
		taskQueue.add(new IdleTask());
		taskQueue.add(new UpdateTask());
		taskQueue.add(new EvaluationTask());
		taskQueue.add(new LoginTask());
		taskQueue.add(new SearchTask());
		taskQueue.add(new LogoutTask());
		
		lastUpdateTime = System.currentTimeMillis();
	}
	
	public boolean isStopped() {
		return stopped;
	}


	public void setStopped(boolean stopped) {
		this.stopped = stopped;
		if (stopped == true) {
			Logging.out.println("All Tasks terminated");
			taskQueue.clear();
			lastUpdateTime = 0L;
		}
	}
	
	
	@Override
	public void run() {
		
		while (true) {
			
			if (threadGroup.activeCount() == 0 && stopped == false) {
				currentTask = taskQueue.poll();
				
				if (currentTask != null) {
					Logging.out.println("Taskmanager: starting task " + currentTask.getName());
					
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					new Thread(threadGroup, currentTask).start();
				} else {
					
					if (System.currentTimeMillis() - lastUpdateTime > UPDATE_AFTER) {
						taskQueue.add(new IdleTask());
						taskQueue.add(new SaveTask());
						taskQueue.add(new UpdateTask());
						taskQueue.add(new EvaluationTask());
						taskQueue.add(new LoginTask());
						taskQueue.add(new SearchTask());
						taskQueue.add(new LogoutTask());
						lastUpdateTime = System.currentTimeMillis();
					} else {
						taskQueue.add(new IdleTask());
						taskQueue.add(new LoginTask());
						taskQueue.add(new SearchTask());
						taskQueue.add(new LogoutTask());
					}
				}
			}
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
