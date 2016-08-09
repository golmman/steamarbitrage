package steamarbitrage.steamio;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import steamarbitrage.Logging;
import steamarbitrage.database.ItemNames;

public class SellInputProcessor extends InputProcessor<SellRecord> {

	public SellInputProcessor(SellRecord result) {
		super(result);
		
	}

	
	
	@Override
	public void process(String steamIn, SellRecord result) {
		
		
		System.out.println(steamIn);
	}
	
	
	
	public void loadInventory(String steamIn) {
		
		if (!steamIn.substring(1, 15).equals("\"success\":true")) {
			Logging.err.println("Loading inventory failed");
			return;
		}
		
		ArrayList<InventoryInfo> inventory = new ArrayList<InventoryInfo>();
		
		int bracketOpen = -1;
		int bracketClose = -1;
		
		// skip the first two curly open brackets
		bracketOpen = steamIn.indexOf('{', bracketOpen + 1);
		bracketOpen = steamIn.indexOf('{', bracketOpen + 1);
		
		bracketOpen = steamIn.indexOf('{', bracketOpen + 1);
		bracketClose = steamIn.indexOf('}', bracketClose + 1);	
		
		// 
		while (bracketClose > bracketOpen) {
			String line = steamIn.substring(bracketOpen, bracketClose+1);
			//System.out.println(line);
			
			Pattern pattern = Pattern.compile(
					"\\{\"id\":\"(\\d*?)\","
					+ "\"classid\":\"(\\d*?)\","
					+ "\"instanceid\":\"(\\d*?)\","
					+ "\"amount\":\"(\\d*?)\","
					+ "\"pos\":(\\d*?)\\}"
					);
			Matcher matcher = pattern.matcher(line);
			
			if (matcher.find()) {
				long classId = Long.parseLong(matcher.group(2));
				long instanceId = Long.parseLong(matcher.group(3));
				
				int startIndex = steamIn.indexOf(classId + "_" + instanceId, bracketClose);
				int nameIndex = steamIn.indexOf("market_name", startIndex);
				int colonIndex = steamIn.indexOf(':', nameIndex);
				int commaIndex = steamIn.indexOf(',', colonIndex);
				
				String name = steamIn.substring(colonIndex + 2, commaIndex - 1);
				name = name.replace("\\u2122", "™");
				
				if (!ItemNames.names.contains(name)) {
					System.err.println(name + " not found in ItemNames.");
				}
				
				
				inventory.add(new InventoryInfo(
						Long.parseLong(matcher.group(1)),
						classId, 
						instanceId, 
						Integer.parseInt(matcher.group(4)), 
						Integer.parseInt(matcher.group(5)),
						name));
			} else {
				System.err.println("Matcher fail: " + line);
			}	
			
			bracketOpen = steamIn.indexOf('{', bracketOpen + 1);
			bracketClose = steamIn.indexOf('}', bracketClose + 1);	
		} 
		
		
		for (InventoryInfo ii : inventory) {
			System.out.println(ii.assetId + " " + ii.name);
		}
	}
}
