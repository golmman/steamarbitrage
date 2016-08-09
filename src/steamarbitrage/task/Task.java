/**
 * 
 */
package steamarbitrage.task;

/**
 * Tasks are procedures that are performed on a regular basis.
 * Buying an item is not regular hence it cannot be a task.
 *
 */
public abstract class Task implements Runnable {
	
	private final String name;
	
	/**
	 * 
	 */
	public Task(String name) {
		this.name = name;
	}

	
	
	protected void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	
}
