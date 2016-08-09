package steamarbitrage.database.steam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import steamarbitrage.Logging;
import steamarbitrage.database.ItemNames;



public class PriceData extends HashMap<String, Price> {
	
	
	private static final long serialVersionUID = -3067135018166541003L;

	public PriceData() {
		super(5400);
		
		String full_name = "";
		for (int index_stat = 0; index_stat < ItemNames.STATS; ++index_stat) {
			for (int index_wear = 0; index_wear < ItemNames.WEARS; ++index_wear) {
				for (int index_base = 0; index_base < ItemNames.BASES; ++index_base) {
					
					int index_skin = 0;
					
					do {
						
						full_name = ItemNames.getFullName(index_stat, index_base, index_skin, index_wear);
						
						this.put(full_name, new Price());
						
						//System.out.print(full_name + " ### ");
						//fetchPrice(full_name, index_stat, index_base, index_skin, index_wear);
						
						if (index_base >= ItemNames.CS_GO_WEAPON_CASE) {
							break;
						}
						
						++index_skin;
					} while (!ItemNames.SKIN_NAMES[index_base][index_skin].equals(""));
					
				}
			}
		}
		
	}
	
	
	
	
	public void savePrices() {
		
		HashMap<String, Price> hm = new HashMap<String, Price>(5400);

		for (String key : ItemNames.names) {
			hm.put(key, this.get(key));
		}

		
		try {
			File file = new File("pricedata");
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(hm);
			s.close();
			f.close();
			Logging.out.println("Prices saved to file " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public void loadPrices() {
		File file = new File("pricedata");
		
		
		FileInputStream f;
		try {
			f = new FileInputStream(file);

			ObjectInputStream s = new ObjectInputStream(f);
			
			@SuppressWarnings("unchecked")
			HashMap<String, Price> hm = (HashMap<String, Price>)s.readObject();
			s.close();
			
			for (String key : ItemNames.names) {
				this.put(key, hm.get(key));
			}
		
			Logging.out.println("Prices loaded from file");
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
