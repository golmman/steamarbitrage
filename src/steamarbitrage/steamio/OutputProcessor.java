package steamarbitrage.steamio;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class OutputProcessor {
	public OutputProcessor(HttpURLConnection con, String data) {
		if (data == null) return;
		
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
}
