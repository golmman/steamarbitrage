package steamarbitrage.steamio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;

import steamarbitrage.Logging;


public abstract class InputProcessor<T> {
	
	private T result;
	
	
	/**
	 * @param result
	 */
	public InputProcessor(T result) {
		this.result = result;
	}
	
	/**
	 * Reads all the input from a connection and calls process with parameter result.
	 * @param con
	 */
	public void readInput(HttpURLConnection con) {
		
		StringBuilder stringBuilder = new StringBuilder("");
		
		String line;
		String steamIn = "";
		String contentEncoding = null;
		
		// Recommended procedure to have persistent connection, see
		// http://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html
		try {
			
			// try to read from input stream
			InputStream is = con.getInputStream();
			InputStreamReader isr;
			GZIPInputStream gzipIs = null;
			
			// unzip if necessary
			contentEncoding = con.getHeaderField("Content-Encoding");
			if (contentEncoding != null) {
				if (contentEncoding.contains("gzip")) {
					gzipIs = new GZIPInputStream(is);
					isr = new InputStreamReader(gzipIs);
				} else {
					isr = new InputStreamReader(is);
				}
			} else {
				isr = new InputStreamReader(is);
			}
			
			BufferedReader br = new BufferedReader(isr);
			
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line + "\n");
				//steamIn += (line + "\n");
			}
			
			br.close();
			isr.close();
			if (gzipIs != null) gzipIs.close();
			is.close();
			
		} catch (SocketTimeoutException e) {
			Logging.err.println(InputProcessor.class.getSimpleName() + " SocketTimeoutException");
			
		} catch (IOException e) {
			
			// Failure: try to read from error stream.
			SteamError.report();
			
			int resp;
			
			try {
				resp = con.getResponseCode();
				
				InputStream is = con.getErrorStream();
				InputStreamReader isr;
				GZIPInputStream gzipIs = null;
				
				// unzip if necessary
				contentEncoding = con.getHeaderField("Content-Encoding");
				if (contentEncoding != null) {
					if (contentEncoding.contains("gzip")) {
						gzipIs = new GZIPInputStream(is);
						isr = new InputStreamReader(gzipIs);
					} else {
						isr = new InputStreamReader(is);
					}
				} else {
					isr = new InputStreamReader(is);
				}
				
				BufferedReader br = new BufferedReader(isr);
				
				
				Logging.err.println("Connection Error, Response Code: " + resp);
				//SteamIO.printRequestHeaders(con, Logging.err);
				SteamIO.printResponseHeaders(con, Logging.err);
				while ((line = br.readLine()) != null) {
					Logging.err.println(line);
				}
				
				
				br.close();
				isr.close();
				if (gzipIs != null) gzipIs.close();
				is.close();
				
			} catch (IOException ex) {
				e.printStackTrace();
			}
		}
		
		steamIn = stringBuilder.toString();
		
		process(steamIn, result);
	}
	
	
	/**
	 * Is called after a SteamInput object is created and all the data from the InputStream is read.
	 * If not overwritten it does nothing.
	 * @param steamIn
	 * 		The data the InputStream fetched and is to be processed.
	 * @param result
	 * 		"Return value" - can be modified at will.
	 */
	public abstract void process(String steamIn, T result);
}
