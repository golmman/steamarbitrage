package steamarbitrage.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import steamarbitrage.Logging;
import steamarbitrage.steamio.SteamCookieManager;


public class Preferences implements Serializable {
	
	private static final long serialVersionUID = -7882647609758811226L;
	private static final Preferences INSTANCE = load();
	
	
	// override put since we don't want crazy profile names
	public HashMap<String, Account> accounts = new HashMap<String, Account>(32) {
		
		private static final long serialVersionUID = 7257028451942833556L;

		@Override
		public Account put(String key, Account value) {
			if (key != null && key.trim().length() >= 1) {
				return super.put(key, value);
			} else {
				return null;
			}
		};
	};
	
	public String activeProfile;
	public float buyAt = 0.7f;
	
	public int minEvalVolume = 1000;
	public float minEvalEstimate = 0.5f;
	
	public int searchRequestDelay = 1000;
	
	
	private Preferences() {}
	
	public static Preferences getInstance() {
		return INSTANCE;
	}
	
	
	private static Preferences load() {
		
		Preferences result = new Preferences();
		
		try {
			File file = new File("preferences");
			FileInputStream f = new FileInputStream(file);

			ObjectInputStream s = new ObjectInputStream(f);
			
			result = (Preferences)s.readObject();
			
			s.close();
			f.close();
		
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Account account = result.accounts.get(result.activeProfile);
		SteamCookieManager.setMachineAuth(account.machineAuthKey, account.machineAuthValue);
		
		return result;
	}
	
	
	
	public void save() {
		
		Logging.debug.println("Preferences saved");

		try {
			File file = new File("preferences");
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(INSTANCE);
			s.close();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
