package steamarbitrage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


public class Logging {
	
	public static PrintStream out = System.out;
	public static PrintStream err = System.err;
	public static PrintStream debug = System.out;
	
	private Logging() {}
	
	public static void init(OutputStream out, OutputStream err, OutputStream debug) {
		
		Logging.debug = new PrintStream(debug);
		
		// out and err prints are supposed to be printed in debug as well
		Logging.out = new PrintStream(out) {
			@Override
			public void write(int b) {
				super.write(b);
				Logging.debug.write(b);
			}
			
			@Override
			public void write(byte[] b) throws IOException {
				super.write(b);
				Logging.debug.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) {
				super.write(b, off, len);
				Logging.debug.write(b, off, len);
			}

			@Override
			public void flush() {
				super.flush();
				Logging.debug.flush();
			}
		};
		
		Logging.err = new PrintStream(err) {
			@Override
			public void write(int b) {
				super.write(b);
				Logging.debug.write(b);
			}
			
			@Override
			public void write(byte[] b) throws IOException {
				super.write(b);
				Logging.debug.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) {
				super.write(b, off, len);
				Logging.debug.write(b, off, len);
			}

			@Override
			public void flush() {
				super.flush();
				Logging.debug.flush();
			}
		};
		
		System.setErr(Logging.err);
	}
}
