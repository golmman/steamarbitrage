package steamarbitrage.gui;

import java.io.Serializable;

public class Account implements Serializable {

	private static final long serialVersionUID = -6853088008155296638L;
	
	public String username;
	public String password;
	public String email;
	public String machineAuthKey;
	public String machineAuthValue;
	
	
	public Account() {
		username = "";
		password = "";
		email = "";
		machineAuthKey = "";
		machineAuthValue = "";
	}
	
	public Account(String username, String password, String email,
			String machineAuthKey, String machineAuthValue) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.machineAuthKey = machineAuthKey;
		this.machineAuthValue = machineAuthValue;
	}
	
	
}
