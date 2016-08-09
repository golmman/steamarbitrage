package steamarbitrage.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import steamarbitrage.Evaluation;
import steamarbitrage.SteamArbitrage;
import steamarbitrage.database.ItemNames;
import steamarbitrage.database.steam.PriceData;
import steamarbitrage.database.steamanalyst.SteamAnalyst;
import steamarbitrage.database.trades.TradeHistory;
import steamarbitrage.steamio.SteamCookieManager;


public class PrefWindow extends JDialog implements ActionListener, ListSelectionListener, ChangeListener {

	private static final long serialVersionUID = 6331344891821451156L;
	
	private static final double SLIDER_MIN_VOL_BASE = 1.011;
	private static final double SLIDER_MIN_EST_BASE = 1.003;
	
	private String oldSelection = "";
	
	private Preferences preferences;
	
	// profile panel
	private JPanel panelProfile = new JPanel(null);
	
	DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> listProfileNames = new JList<String>(listModel);
	
	private JLabel labelProfile = new JLabel("Active profile ");
	private JLabel labelUsername = new JLabel("Username ");
	private JLabel labelPassword = new JLabel("Password ");
	private JLabel labelEmail = new JLabel("EMail ");
	private JLabel labelMachineAuthKey = new JLabel("steamMachineAuth Key ");
	private JLabel labelMachineAuthValue = new JLabel("steamMachineAuth Value ");
	
	private JTextField textProfile =  new JTextField();
	private JTextField textUsername =  new JTextField();
	private JTextField textPassword =  new JTextField();
	private JTextField textEmail =  new JTextField();
	private JTextField textMachineAuthKey =  new JTextField();
	private JTextField textMachineAuthValue =  new JTextField();
	
	private JButton butAddProfile = new JButton("Add");
	private JButton butRemoveProfile = new JButton("Remove");
	private JButton butSetProfile = new JButton("Set Active");
	private JButton butSaveProfile = new JButton("Save");
	
	// panel buy at
	private JPanel panelBuyAt = new JPanel(null); 
	private JSlider sliderBuyAt = new JSlider(0, 100);
	private JTextField textBuyAt = new JTextField();
	private JLabel labelWarning = new JLabel("15% tax!");
	
	// panel min volume
	private JPanel panelMinVolume = new JPanel(null); 
	private JSlider sliderMinVolume = new JSlider(0, 1000);
	private JTextField textMinVolume = new JTextField();
	
	// panel min estimate
	private JPanel panelMinEstimate = new JPanel(null); 
	private JSlider sliderMinEstimate = new JSlider(0, 1000);
	private JTextField textMinEstimate = new JTextField();
	
	// panel test
	private JPanel panelTest = new JPanel(null); 
	private JTextField textTest = new JTextField();
	private JButton butTest = new JButton("estimate skins/run");
	
	// this frame
	private JButton butOK = new JButton("OK");
	private JButton butCancel = new JButton("Cancel");
	
	private JTextField textSearchRequestDelay = new JTextField();
	
	
	
	public static void main(String[] args) {
		
		SteamAnalyst.load(new StringBuilder());
		ItemNames.loadNames();
		TradeHistory.load();
		SteamArbitrage.priceData = new PriceData();
		SteamArbitrage.priceData.loadPrices();
		
		
		
		new PrefWindow(null);
	}

	
	public PrefWindow(JFrame owner) {
		super(owner, "Preferences", true);
		preferences = Preferences.getInstance();
		this.setLayout(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		if (owner != null) {
			this.setLocation(owner.getX() + 100, owner.getY() + 100);
		} else {
			this.setLocation(100, 100);
		}
		this.setSize(700, 610);
		
		
		// panel profile
		panelProfile.setBorder(BorderFactory.createTitledBorder("Profile"));
		panelProfile.setBounds(10, 10, 670, 300);
		
		listProfileNames.setBounds(10, 20, 100, 220);
		
		labelProfile.setBounds(150, 20, 120, 25);
		labelUsername.setBounds(150, 55, 120, 25);
		labelPassword.setBounds(150, 90, 120, 25);
		labelEmail.setBounds(150, 125, 120, 25);
		labelMachineAuthKey.setBounds(150, 160, 150, 25);
		labelMachineAuthValue.setBounds(150, 195, 155, 25);
		
		textProfile.setBounds(310, 20, 350, 25);
		textUsername.setBounds(310, 55, 350, 25);
		textPassword.setBounds(310, 90, 350, 25);
		textEmail.setBounds(310, 125, 350, 25);
		textMachineAuthKey.setBounds(310, 160, 350, 25);
		textMachineAuthValue.setBounds(310, 195, 350, 25);
		
		butSaveProfile.setBounds(350, 230, 100, 25);
		butSetProfile.setBounds(560, 230, 100, 25);
		
		butAddProfile.setBounds(10, 240, 100, 25);
		butRemoveProfile.setBounds(10, 265, 100, 25);
		
		panelProfile.add(listProfileNames);
		panelProfile.add(labelProfile);
		panelProfile.add(labelUsername);
		panelProfile.add(labelPassword);
		panelProfile.add(labelEmail);
		panelProfile.add(labelMachineAuthKey);
		panelProfile.add(labelMachineAuthValue);
		panelProfile.add(textProfile);
		panelProfile.add(textUsername);
		panelProfile.add(textPassword);
		panelProfile.add(textEmail);
		panelProfile.add(textMachineAuthKey);
		panelProfile.add(textMachineAuthValue);
		panelProfile.add(butAddProfile);
		panelProfile.add(butRemoveProfile);
		panelProfile.add(butSetProfile);
		//panelProfile.add(butSaveProfile);
		
		// panel buy at
		panelBuyAt.setBorder(BorderFactory.createTitledBorder("Buy At"));
		panelBuyAt.setBounds(10, 320, 670, 60);
		
		sliderBuyAt.setBounds(10, 20, 400, 25);
		textBuyAt.setBounds(420, 20, 50, 25);
		labelWarning.setBounds(470, 20, 100, 25);
		
		panelBuyAt.add(sliderBuyAt);
		panelBuyAt.add(textBuyAt);
		panelBuyAt.add(labelWarning);
		
		// panel min volume
		panelMinVolume.setBorder(BorderFactory.createTitledBorder("Evaluation minimal trade volume"));
		panelMinVolume.setBounds(10, 390, 490, 60);
		
		sliderMinVolume.setBounds(10, 20, 400, 25);
		textMinVolume.setBounds(420, 20, 50, 25);
		
		panelMinVolume.add(sliderMinVolume);
		panelMinVolume.add(textMinVolume);
		
		// panel min estimate
		panelMinEstimate.setBorder(BorderFactory.createTitledBorder("Evaluation minimal price estimate"));
		panelMinEstimate.setBounds(10, 460, 490, 60);
		
		sliderMinEstimate.setBounds(10, 20, 400, 25);
		textMinEstimate.setBounds(420, 20, 50, 25);
		
		panelMinEstimate.add(sliderMinEstimate);
		panelMinEstimate.add(textMinEstimate);
		
		// panel test
		panelTest.setBorder(BorderFactory.createTitledBorder("Test Evaluation"));
		panelTest.setBounds(510, 390, 180, 130);
		
		textTest.setBounds(20, 35, 140, 25);
		butTest.setBounds(20, 80, 140, 25);
		
		panelTest.add(textTest);
		panelTest.add(butTest);
		
		// this frame
		butOK.setBounds(10, 540, 80, 25);
		butCancel.setBounds(100, 540, 80, 25);
		
		textSearchRequestDelay.setBounds(300, 540, 80, 25);
		
		
		textProfile.setEditable(false);
		textBuyAt.setEditable(false);
		textMinVolume.setEditable(false);
		textMinEstimate.setEditable(false);
		textTest.setEditable(false);
		
		butAddProfile.addActionListener(this);
		butRemoveProfile.addActionListener(this);
		butSetProfile.addActionListener(this);
		butSaveProfile.addActionListener(this);
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		
		listProfileNames.addListSelectionListener(this);
		
		sliderBuyAt.addChangeListener(this);
		labelWarning.setForeground(Color.RED);
		labelWarning.setVisible(false);
		
		sliderMinVolume.addChangeListener(this);
		sliderMinEstimate.addChangeListener(this);
		
		butTest.addActionListener(this);
		
		this.add(butOK);
		this.add(butCancel);
		
		this.add(textSearchRequestDelay);
		
		
		this.add(panelProfile);
		this.add(panelBuyAt);
		this.add(panelMinVolume);
		this.add(panelMinEstimate);
		this.add(panelTest);
		
		
		sliderBuyAt.setValue((int)(preferences.buyAt * 100));
		textBuyAt.setText("" + sliderBuyAt.getValue() + "%");
		
		sliderMinVolume.setValue((int)(Math.log(preferences.minEvalVolume) / Math.log(SLIDER_MIN_VOL_BASE)));
		textMinVolume.setText("" + preferences.minEvalVolume);
		
		sliderMinEstimate.setValue((int)(Math.log(preferences.minEvalEstimate + 1.0f) / Math.log(SLIDER_MIN_EST_BASE)));
		textMinEstimate.setText(String.format("%3.2f", preferences.minEvalEstimate));
		
		textSearchRequestDelay.setText("" + preferences.searchRequestDelay);
		
		
		textProfile.setText(preferences.activeProfile);
		updateList(preferences.activeProfile);
		
		
		this.setVisible(true);
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(butAddProfile)) {
			String name = JOptionPane.showInputDialog("Profile name: ");
			
			int size = preferences.accounts.size();
			
			if (!preferences.accounts.containsKey(name)) {
				
				preferences.accounts.put(name, new Account());
				if (size != preferences.accounts.size()) {
					updateList(name);
				}
			}
			
			
		} else if (e.getSource().equals(butRemoveProfile)) {
			
			if (JOptionPane.showConfirmDialog(this, "remove?", "Remove Profile", JOptionPane.OK_CANCEL_OPTION)
					== JOptionPane.OK_OPTION) {
				preferences.accounts.remove(listProfileNames.getSelectedValue());
				updateList(null);
			}
			
			
		} else if (e.getSource().equals(butSetProfile)) {
			
			preferences.activeProfile = listProfileNames.getSelectedValue();
			textProfile.setText(preferences.activeProfile);
			
		} else if (e.getSource().equals(butSaveProfile)) {
			
			saveAccounts(listProfileNames.getSelectedValue());
			
		} else if (e.getSource().equals(butOK)) {
			
			saveAccounts(listProfileNames.getSelectedValue());
			Account account = preferences.accounts.get(preferences.activeProfile);
			SteamCookieManager.setMachineAuth(account.machineAuthKey, account.machineAuthValue);
			
			preferences.searchRequestDelay = Integer.parseInt(textSearchRequestDelay.getText());
			
			preferences.save();
			this.dispose();
			
		} else if (e.getSource().equals(butCancel)) {
			this.dispose();
		
		}  else if (e.getSource().equals(butTest)) {
			float searchRequestDelaySeconds = (float)Integer.parseInt(textSearchRequestDelay.getText()) / 1000.0f;
			
			int sum = Evaluation.getScoreSum(
					Float.parseFloat(textMinEstimate.getText()), 1000.0f, 
					Integer.parseInt(textMinVolume.getText()), 100000);
			textTest.setText("" + sum + " ~ " + (sum / (60.0f / searchRequestDelaySeconds)) + " min");
		} 
		
	}
	
	
	private void updateList(String select) {
		String selected;
		if (select == null) {
			selected = listProfileNames.getSelectedValue();
		} else {
			selected = select;
		}
		
		
		listModel.clear();
		ArrayList<String> keySet = new ArrayList<String>(preferences.accounts.keySet());
		keySet.sort(null);
		
		
		int i = 0;
		for (String s : keySet) {
			listModel.addElement(s);
			if (s.equals(selected)) {
				listProfileNames.setSelectedIndex(i);
			}
			
			++i;
		}
		
		
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		if (e.getSource().equals(listProfileNames)) {
			
			// save hashmap if list selection changes
			saveAccounts(oldSelection);
			
			
			
			String key = listProfileNames.getSelectedValue();
			Account acc = preferences.accounts.get(key);
			
			if (acc != null) {			
				textUsername.setText(acc.username);
				textPassword.setText(acc.password);
				textEmail.setText(acc.email);
				textMachineAuthKey.setText(acc.machineAuthKey);
				textMachineAuthValue.setText(acc.machineAuthValue);
			}
			
			oldSelection = key;
		}
		
		
	}

	
	private void saveAccounts(String key) {
		if (preferences.accounts.containsKey(key)) {
		
			preferences.accounts.put(key, 
					new Account(
							textUsername.getText(),
							textPassword.getText(),
							textEmail.getText(),
							textMachineAuthKey.getText(),
							textMachineAuthValue.getText()));
			
		}
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(sliderBuyAt)) {
			preferences.buyAt = (float)sliderBuyAt.getValue() / 100;
			textBuyAt.setText("" + sliderBuyAt.getValue() + "%");
			
			if (sliderBuyAt.getValue() >= 85) {
				textBuyAt.setForeground(Color.RED);
				labelWarning.setVisible(true);
			} else {
				textBuyAt.setForeground(Color.BLACK);
				labelWarning.setVisible(false);
			}
		
		} else if (e.getSource().equals(sliderMinVolume)) {
			preferences.minEvalVolume = (int)Math.pow(SLIDER_MIN_VOL_BASE, (double)sliderMinVolume.getValue());
			textMinVolume.setText("" + preferences.minEvalVolume);
			
		} else if (e.getSource().equals(sliderMinEstimate)) {
			preferences.minEvalEstimate = (float)(Math.pow(SLIDER_MIN_EST_BASE, (double)sliderMinEstimate.getValue()) - 1.0);
			textMinEstimate.setText(String.format("%3.2f" , preferences.minEvalEstimate));
			
		}
	}

	
}














