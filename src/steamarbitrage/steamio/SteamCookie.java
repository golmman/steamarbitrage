package steamarbitrage.steamio;


/**
 * 
 * Format of a steam cookie is
 * 
 * <pre>
 * steamMachineAuth76561198121652995=0C76243DE5223C68BC3190660D19F6DAC62B3421
 * \______________/\_______________/ \______________________________________/
 *       key           keySuffix                    value
 * </pre>
 */
public class SteamCookie {
	public String key;
	public String keySuffix;
	public String value;
	
	/**
	 * 
	 * Format of a steam cookie is
	 * 
	 * <pre>
	 * steamMachineAuth76561198121652995=0C76243DE5223C68BC3190660D19F6DAC62B3421
	 * \______________/\_______________/ \______________________________________/
	 *       key           keySuffix                    value
	 * </pre>
	 */
	public SteamCookie(String key, String keySuffix, String value) {
		this.key = key;
		this.keySuffix = keySuffix;
		this.value = value;
	}
}
