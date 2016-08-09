package steamarbitrage.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import steamarbitrage.Logging;
import steamarbitrage.database.steam.Price;
import steamarbitrage.database.steam.PriceData;
import steamarbitrage.steamio.SteamIO;
import steamarbitrage.steamio.UpdateInputProcessor;
import steamarbitrage.steamio.UpdateRecord;


public class ItemNames {
	public static final int TEST = 0;
	
	public static final int NO_STAT = 0;
	public static final int STAT = 1;
	
	public static final int CZ75_AUTO = 0;
	public static final int DESERT_EAGLE = 1;
	public static final int DUAL_BERETTAS = 2;
	public static final int FIVE_SEVEN = 3;
	public static final int GLOCK_18 = 4;
	public static final int P2000 = 5;
	public static final int P250 = 6;
	public static final int TEC_9 = 7;
	public static final int USP_S = 8;
	public static final int AK_47 = 9;
	public static final int AUG = 10;
	public static final int AWP = 11;
	public static final int FAMAS = 12;
	public static final int G3SG1 = 13;
	public static final int GALIL_AR = 14;
	public static final int M4A1_S = 15;
	public static final int M4A4 = 16;
	public static final int SCAR_20 = 17;
	public static final int SG_553 = 18;
	public static final int SSG_08 = 19;
	public static final int MAC_10 = 20;
	public static final int MP7 = 21;
	public static final int MP9 = 22;
	public static final int PP_BIZON = 23;
	public static final int P90 = 24;
	public static final int UMP_45 = 25;
	public static final int MAG_7 = 26;
	public static final int NOVA = 27;
	public static final int SAWED_OFF = 28;
	public static final int XM1014 = 29;
	public static final int M249 = 30;
	public static final int NEGEV = 31;
	public static final int BAYONET = 32;
	public static final int BUTTERFLY_KNIFE = 33;
	public static final int FLIP_KNIFE = 34;
	public static final int GUT_KNIFE = 35;
	public static final int HUNTSMAN_KNIFE = 36;
	public static final int KARAMBIT = 37;
	public static final int M9_BAYONET = 38;
	public static final int CS_GO_WEAPON_CASE = 39;
	public static final int CS_GO_WEAPON_CASE_2 = 40;
	public static final int CS_GO_WEAPON_CASE_3 = 41;
	public static final int CHROMA_CASE = 42;
	public static final int ESPORTS_2013_CASE = 43;
	public static final int ESPORTS_2013_WINTER_CASE = 44;
	public static final int ESPORTS_2014_SUMMER_CASE = 45;
	public static final int HUNTSMAN_WEAPON_CASE = 46;
	public static final int OPERATION_BRAVO_CASE = 47;
	public static final int OPERATION_BREAKOUT_WEAPON_CASE = 48;
	public static final int OPERATION_PHOENIX_WEAPON_CASE = 49;
	public static final int OPERATION_VANGUARD_WEAPON_CASE = 50;
	public static final int WINTER_OFFENSIVE_WEAPON_CASE = 51;
	
	
	public static final int FACTORY_NEW = 0;
	public static final int MINIMAL_WEAR = 1;
	public static final int FIELD_TESTED = 2;
	public static final int WELL_WORN = 3;
	public static final int BATTLE_SCARRED = 4;
	
	
	public final static String STAT_NAMES[] = {
		"",
		"StatTrak™ "
	};
	
	public final static String WEAR_NAMES[] = {
		"Factory New",
		"Minimal Wear",
		"Field-Tested",
		"Well-Worn",
		"Battle-Scarred"
	};
	
	public final static String SKIN_NAMES[][] = {
	    /* CZ75_AUTO */ {"Victoria", "The Fuschia Is Now", "Tigris", "Tread Plate", "Chalice", "Hexane", "Twist", "Poison Dart", "Crimson Web", "Nitro", "Tuxedo", "Green Plaid", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* DESERT_EAGLE */ {"Golden Koi", "Conspiracy", "Cobalt Disruption", "Hypnotic", "Naga", "Crimson Web", "Heirloom", "Pilot", "Hand Cannon", "Blaze", "Meteorite", "Urban Rubble", "Urban DDPAT", "Mudder", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* DUAL_BERETTAS */ {"Urban Shock", "Marina", "Hemoglobin", "Cobalt Quartz", "Demolition", "Retribution", "Panther", "Black Limba", "Anodized Navy", "Stained", "Briar", "Contractor", "Colony", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* FIVE_SEVEN */ {"Fowl Play", "Copper Galaxy", "Case Hardened", "Urban Hazard", "Kami", "Nightshade", "Silver Quartz", "Hot Shot", "Orange Peel", "Candy Apple", "Contractor", "Forest Night", "Anodized Gunmetal", "Jungle", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* GLOCK_18 */ {"Water Elemental", "Grinder", "Steel Disruption", "Dragon Tattoo", "Fade", "Brass", "Catacombs", "Blue Fissure", "Reactor", "Candy Apple", "Night", "Death Rattle", "Groundwater", "Sand Dune", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* P2000 */ {"Fire Elemental", "Corticera", "Ocean Foam", "Amber Fade", "Scorpion", "Ivory", "Pulse", "Red FragCam", "Chainmail", "Silver", "Coach Class", "Grassland", "Granite Marbleized", "Grassland Leaves", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* P250 */ {"Muertos", "Cartel", "Undertow", "Mehndi", "Franklin", "Supernova", "Splash", "Nuclear Threat", "Steel Disruption", "Hive", "Modern Hunter", "Contamination", "Metallic DDPAT", "Facets", "Gunsmoke", "Sand Dune", "Boreal Forest", "Bone Mask", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* TEC_9 */ {"Titanium Bit", "Red Quartz", "Nuclear Threat", "Isaac", "Sandstorm", "Blue Titanium", "Toxic", "Brass", "Ossified", "VariCamo", "Urban DDPAT", "Groundwater", "Army Mesh", "Tornado", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* USP_S */ {"Caiman", "Orion", "Serum", "Guardian", "Overgrowth", "Dark Water", "Road Rash", "Blood Tiger", "Stainless", "Business Class", "Night Ops", "Royal Blue", "Forest Leaves", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* AK_47 */ {"Wasteland Rebel", "Jaguar", "Vulcan", "Fire Serpent", "Cartel", "Redline", "Red Laminate", "Case Hardened", "Jet Set", "Blue Laminate", "First Class", "Emerald Pinstripe", "Black Laminate", "Safari Mesh", "Jungle Spray", "Predator", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* AUG */ {"Chameleon", "Bengal Tiger", "Torque", "Wings", "Anodized Navy", "Hot Rod", "Copperhead", "Radiation Hazard", "Condemned", "Contractor", "Storm", "Colony", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* AWP */ {"Man-o&#039;-war", "Asiimov", "Lightning Strike", "Dragon Lore", "Corticera", "Redline", "Electric Hive", "Graphite", "BOOM", "Pink DDPAT", "Pit Viper", "Snake Camo", "Safari Mesh", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* FAMAS */ {"Afterimage", "Sergeant", "Pulse", "Styx", "Spitfire", "Hexane", "Doomkitty", "Teardown", "Cyanospatter", "Colony", "Contrast Spray", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* G3SG1 */ {"Murky", "Azure Zebra", "Demeter", "Green Apple", "VariCamo", "Arctic Camo", "Contractor", "Desert Storm", "Jungle Dashed", "Safari Mesh", "Polar Camo", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* GALIL_AR */ {"Chatterbox", "Orange DDPAT", "Cerberus", "Kami", "Sandstorm", "Blue Titanium", "Shattered", "Tuxedo", "VariCamo", "Winter Forest", "Sage Spray", "Hunting Blind", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* M4A1_S */ {"Cyrex", "Atomic Alloy", "Guardian", "Knight", "Master Piece", "Basilisk", "Bright Water", "Dark Water", "Nitro", "Blood Tiger", "VariCamo", "Boreal Forest", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* M4A4 */ {"Howl", "Bullet Rain", "Desert-Strike", "Asiimov", "X-Ray", "?? (Dragon King)", "Griffin", "Zirka", "Modern Hunter", "Faded Zebra", "Radiation Hazard", "Urban DDPAT", "Tornado", "Jungle Tiger", "Desert Storm", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* SCAR_20 */ {"Cardiac", "Cyrex", "Splash Jam", "Emerald", "Grotto", "Crimson Web", "Carbon Fiber", "Palm", "Storm", "Sand Mesh", "Contractor", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* SG_553 */ {"Pulse", "Wave Spray", "Ultraviolet", "Damascus Steel", "Anodized Navy", "Fallout Warning", "Traveler", "Gator Mesh", "Army Sheen", "Waves Perforated", "Tornado", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* SSG_08 */ {"Blood in the Water", "Dark Water", "Abyss", "Slashed", "Detour", "Acid Fade", "Tropical Storm", "Mayan Dreams", "Sand Dune", "Blue Spruce", "Lichen Dashed", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* MAC_10 */ {"Malachite", "Tatter", "Curse", "Heat", "Graven", "Ultraviolet", "Nuclear Garden", "Amber Fade", "Commuter", "Silver", "Palm", "Candy Apple", "Indigo", "Tornado", "Urban DDPAT", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* MP7 */ {"Ocean Foam", "Urban Hazard", "Skulls", "Anodized Navy", "Whiteout", "Gunsmoke", "Orange Peel", "Olive Plaid", "Forest DDPAT", "Army Recon", "Groundwater", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* MP9 */ {"Rose Iron", "Hypnotic", "Bulldozer", "Deadly Poison", "Dart", "Setting Sun", "Dark Age", "Hot Rod", "Orange Peel", "Green Plaid", "Storm", "Sand Dashed", "Dry Season", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* PP_BIZON */ {"Blue Streak", "Osiris", "Antique", "Cobalt Halftone", "Water Sigil", "Brass", "Rust Coat", "Modern Hunter", "Chemical Green", "Night Ops", "Carbon Fiber", "Sand Dashed", "Urban Dashed", "Irradiated Alert", "Forest Leaves", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* P90 */ {"Asiimov", "Death by Kitty", "Trigon", "Cold Blooded", "Emerald Dragon", "Virus", "Blind Spot", "Module", "Desert Warfare", "Teardown", "Glacier Mesh", "Leather", "Ash Wood", "Fallout Warning", "Storm", "Sand Spray", "Scorched", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* UMP_45 */ {"Delusion", "Labyrinth", "Corporal", "Bone Pile", "Blaze", "Carbon Fiber", "Gunsmoke", "Fallout Warning", "Indigo", "Scorched", "Urban DDPAT", "Caramel", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* MAG_7 */ {"Bulldozer", "Firestarter", "Heaven Guard", "Memento", "Hazard", "Silver", "Metallic DDPAT", "Storm", "Irradiated Alert", "Sand Dune", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* NOVA */ {"Bloomstick", "Antique", "Koi", "Rising Skull", "Graphite", "Ghost Camo", "Tempest", "Modern Hunter", "Blaze Orange", "Green Apple", "Caged Steel", "Candy Apple", "Predator", "Sand Dune", "Polar Mesh", "Walnut", "Forest Leaves", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* SAWED_OFF */ {"The Kraken", "Serenity", "Highwayman", "Orange DDPAT", "First Class", "Full Stop", "Amber Fade", "Copper", "Rust Coat", "Snake Camo", "Mosaico", "Sage Spray", "Forest DDPAT", "Irradiated Alert", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* XM1014 */ {"Tranquility", "Heaven Guard", "Quicksilver", "Red Python", "Bone Machine", "Red Leather", "VariCamo Blue", "Blaze Orange", "CaliCamo", "Blue Steel", "Fallout Warning", "Blue Spruce", "Jungle", "Grassland", "Urban Perforated", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* M249 */ {"System Lock", "Magma", "Gator Mesh", "Blizzard Marbleized", "Contrast Spray", "Jungle DDPAT", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* NEGEV */ {"Bratatat", "Desert-Strike", "Terrain", "Anodized Navy", "Nuclear Waste", "CaliCamo", "Palm", "Army Sheen", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* BAYONET */ {"Damascus Steel", "Doppler", "Marble Fade", "Tiger Tooth", "Rust Coat", "Ultraviolet", "?", "Blue Steel", "Boreal Forest", "Case Hardened", "Crimson Web", "Fade", "Forest DDPAT", "Night", "Safari Mesh", "Scorched", "Slaughter", "Stained", "Urban Masked", "", "", "", "", "", "", "", "", "", "", ""},
	    /* BUTTERFLY_KNIFE */ {"?", "Blue Steel", "Boreal Forest", "Case Hardened", "Crimson Web", "Fade", "Forest DDPAT", "Night", "Safari Mesh", "Scorched", "Slaughter", "Stained", "Urban Masked", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* FLIP_KNIFE */ {"Damascus Steel", "Doppler", "Marble Fade", "Tiger Tooth", "Rust Coat", "Ultraviolet", "?", "Blue Steel", "Boreal Forest", "Case Hardened", "Crimson Web", "Fade", "Forest DDPAT", "Night", "Safari Mesh", "Scorched", "Slaughter", "Stained", "Urban Masked", "", "", "", "", "", "", "", "", "", "", ""},
	    /* GUT_KNIFE */ {"Damascus Steel", "Doppler", "Marble Fade", "Tiger Tooth", "Rust Coat", "Ultraviolet", "?", "Blue Steel", "Boreal Forest", "Case Hardened", "Crimson Web", "Fade", "Forest DDPAT", "Night", "Safari Mesh", "Scorched", "Slaughter", "Stained", "Urban Masked", "", "", "", "", "", "", "", "", "", "", ""},
	    /* HUNTSMAN_KNIFE */ {"?", "Blue Steel", "Boreal Forest", "Case Hardened", "Crimson Web", "Fade", "Forest DDPAT", "Night", "Safari Mesh", "Scorched", "Slaughter", "Stained", "Urban Masked", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
	    /* KARAMBIT */ {"Damascus Steel", "Doppler", "Marble Fade", "Tiger Tooth", "Rust Coat", "Ultraviolet", "?", "Blue Steel", "Boreal Forest", "Case Hardened", "Crimson Web", "Fade", "Forest DDPAT", "Night", "Safari Mesh", "Scorched", "Slaughter", "Stained", "Urban Masked", "", "", "", "", "", "", "", "", "", "", ""},
	    /* M9_BAYONET */ {"Damascus Steel", "Doppler", "Marble Fade", "Tiger Tooth", "Rust Coat", "Ultraviolet", "?", "Blue Steel", "Boreal Forest", "Case Hardened", "Crimson Web", "Fade", "Forest DDPAT", "Night", "Safari Mesh", "Scorched", "Slaughter", "Stained", "Urban Masked", "", "", "", "", "", "", "", "", "", "", ""}
	};


	public static final String[] BASE_NAMES = { "CZ75-Auto", "Desert Eagle", "Dual Berettas",
			"Five-SeveN", "Glock-18", "P2000", "P250", "Tec-9", "USP-S",

			"AK-47", "AUG", "AWP", "FAMAS", "G3SG1", "Galil AR", "M4A1-S",
			"M4A4", "SCAR-20", "SG 553", "SSG 08",

			"MAC-10", "MP7", "MP9", "PP-Bizon", "P90", "UMP-45",

			"MAG-7", "Nova", "Sawed-Off", "XM1014", "M249", "Negev",

			"Bayonet", "Butterfly Knife", "Flip Knife", "Gut Knife",
			"Huntsman Knife", "Karambit", "M9 Bayonet",

			"CS:GO Weapon Case", "CS:GO Weapon Case 2", "CS:GO Weapon Case 3",
			"Chroma Case", "eSports 2013 Case", "eSports 2013 Winter Case",
			"eSports 2014 Summer Case", "Huntsman Weapon Case",
			"Operation Bravo Case", "Operation Breakout Weapon Case",
			"Operation Phoenix Weapon Case", "Operation Vanguard Weapon Case",
			"Winter Offensive Weapon Case" };

	public static final int BASES = BASE_NAMES.length;
	public static final int SKINS = SKIN_NAMES[0].length;
	public static final int WEARS = WEAR_NAMES.length;
	public static final int STATS = STAT_NAMES.length;
	
	
	public static ArrayList<String> names = null;
	
	public static void loadNames() {
		names = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("names.txt"));
			for(String line; (line = br.readLine()) != null;) {
				names.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void saveExistingNames() {
		
		PriceData localPriceData = new PriceData();
		
		// generate all possible names
		String full_name = "";
		for (int index_stat = 0; index_stat < ItemNames.STATS; ++index_stat) {
			for (int index_wear = 0; index_wear < ItemNames.WEARS; ++index_wear) {
				for (int index_base = 0; index_base < ItemNames.CS_GO_WEAPON_CASE; ++index_base) {
					
					int index_skin = 0;
					
					do {
						
						full_name = ItemNames.getFullName(index_stat, index_base, index_skin, index_wear);
						
						localPriceData.put(full_name, new Price());
						
//						if (index_base >= ItemNames.CS_GO_WEAPON_CASE) {
//							break;
//						}
						
						++index_skin;
					} while (!ItemNames.SKIN_NAMES[index_base][index_skin].equals(""));
					
				}
			}
		}
		
		
		Logging.out.println("Filtering existing prices...");

		SteamIO.setProperties(200, 5*60*1000, 6, 4);
		
		// find names which have prices
		for (String name : localPriceData.keySet()) {
			
			String urlstr = null;
			try {
				urlstr = URLEncoder.encode(name, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			
			SteamIO.queueRequest(
					"http://steamcommunity.com/market/priceoverview/?country=EN&currency=3&appid=730&market_hash_name=" + urlstr,
					null, 
					null, 
					new UpdateInputProcessor(
						new UpdateRecord(localPriceData, name)));	
			
		}
		
		
		
		Thread thread = new Thread() {
			
			@Override
			public void run() {
				
				// wait until we have all the data
				do {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while (SteamIO.getThreadDeque().size() > 0);
				
				
				// write names which have prices to file
				Logging.out.println("saving names...");
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("names.txt", false)))) {
					
					Price p = null;
					for (String name : localPriceData.keySet()) {
						p = localPriceData.get(name);
						if (p.low != 0.0f && p.median != 0.0f && p.volume != 0) {
							out.println(name);
						}
					}
					
				} catch (IOException e) {
				    e.printStackTrace();
				}
				
			}
		};
		
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public static void printSkinNames() {

		try {

			Logging.out.println("public final static String SKIN_NAMES[][] = {");

			for (int itemindex = 0; itemindex < BASE_NAMES.length; ++itemindex) {

				// URL url = new
				// URL("http://steamcommunity.com/market/search/render/?query=&start=0&count=10");
				URL url = new URL("http://csgostash.com/weapon/"
						+ URLEncoder.encode(BASE_NAMES[itemindex], "UTF-8"));

				URLConnection con = url.openConnection();
				con.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				con.connect();

				Pattern p = Pattern
						.compile("text/html;\\s+charset=([^\\s]+)\\s*");
				Matcher m = p.matcher(con.getContentType());
				String charset = m.matches() ? m.group(1) : "ISO-8859-1";
				Reader r = new InputStreamReader(con.getInputStream(), charset);
				StringBuilder buf = new StringBuilder();
				while (true) {
					int ch = r.read();
					if (ch < 0)
						break;
					buf.append((char) ch);
				}
				String str = buf.toString();

				String search = "<h3><a href=\"http://csgostash.com/family/";
				int len = search.length();
				int index = str.indexOf(search, 0);

				String varname = BASE_NAMES[itemindex].replace(' ', '_')
						.replace('-', '_').replace(':', '_').toUpperCase();
				Logging.out.print("    /* " + varname + " */ {");

				for (int k = 0; k < 30; ++k) {

					if (index != -1) {
						int start = str.indexOf('>', index + len);
						int end = str.indexOf('<', index + len);

						Logging.out.print("\"" + str.substring(start + 1, end)
								+ "\"");

						index = str.indexOf(search, index + 1);
					} else {
						Logging.out.print("\"\"");
					}

					if (k != 29) {
						Logging.out.print(", ");
					}
				}

				Logging.out.print("}");

				if (BASE_NAMES[itemindex].equals("M9 Bayonet")) {
					break;
				} else {
					Logging.out.println(",");
				}
			}

			Logging.out.println();
			Logging.out.println("};");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void printItemNames() {
		// System.out.println("enum ITEM_INDEX {");

		for (int k = 0; k < BASE_NAMES.length; ++k) {
			String varname = BASE_NAMES[k].replace(' ', '_').replace('-', '_')
					.replace(':', '_').toUpperCase();
			Logging.out.println("public static final int " + varname + " = " + k + ";");

			if (k != BASE_NAMES.length - 1) {
				// System.out.println(",");
			}
		}

		// System.out.println();
		// System.out.println("}");
	}
	
	
	public static String getFullName(int index_stat, int index_base, int index_skin, int index_wear) {

		String full_name = "";

		if (index_base < ItemNames.CS_GO_WEAPON_CASE) {
			// weapons
			String stat = ItemNames.STAT_NAMES[index_stat];
			String base = ItemNames.BASE_NAMES[index_base];
			String skin = ItemNames.SKIN_NAMES[index_base][index_skin];
			String wear = ItemNames.WEAR_NAMES[index_wear];

			full_name = stat + base + " | " + skin + " (" + wear + ")";
		} else {
			// cases
			full_name = ItemNames.BASE_NAMES[index_base];
		}

		return full_name;
	}
}
