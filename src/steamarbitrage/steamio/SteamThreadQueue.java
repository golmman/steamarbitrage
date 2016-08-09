package steamarbitrage.steamio;

import java.util.Deque;
import java.util.LinkedList;


/**
 * A thread that manages the steamio threads.
 * We only want exactly one instance of it.
 */
public class SteamThreadQueue {
	
	private static final SteamThreadQueue INSTANCE = new SteamThreadQueue();
	
	private Deque<Thread> deque;
	private ThreadGroup threadGroup;
	private Thread thread;
	
	private SteamThreadQueue() {
		
		deque = new LinkedList<Thread>();
		
		threadGroup = new ThreadGroup("SteamThreads");
		
		
		SteamThreadQueue stq = this;
		
		thread = new Thread() {
			@Override
			public void run() {
				stq.run();
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public static SteamThreadQueue getInstance() {
        return INSTANCE;
    }
	
	
	
	
	private void run() {
		//int replaceLine = 0;
		
		while (true) {
			
			
			if (SteamIO.isQueuePaused()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			
			if (SteamIO.isResponse403()) {
				try {
					Thread.sleep(SteamIO.getReponse403Delay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SteamIO.setResponse403(false);
			}
			
			
			
			if (deque.size() > 0) {
				deque.pollFirst().start();
				
//				Logging.out.printlnInPlace(
//						"active threads: " + threadGroup.activeCount() + 
//						", remaining queries: " + deque.size(),
//						replaceLine);
//				
//			} else {
//				replaceLine = Logging.getLastLine();
			}
			
			
			
			if (threadGroup.activeCount() >= SteamIO.getMaxRequests()) {
				//Logging.out.println(">= 6 steam threads");
				
				while (threadGroup.activeCount() > SteamIO.getMinRequests()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
//			if (threadQueue.isEmpty() == false) {
//				threadQueue.poll().start();
//			}
			
			try {
				Thread.sleep(SteamIO.getRequestDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}




	public Deque<Thread> getDeque() {
		return deque;
	}

	
}
