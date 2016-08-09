package steamarbitrage.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import steamarbitrage.Logging;
import steamarbitrage.SteamArbitrage;
import steamarbitrage.database.ItemNames;
import steamarbitrage.database.steam.Price;
import steamarbitrage.database.trades.TradeHistory;
import steamarbitrage.steamio.SteamIO;
import steamarbitrage.steamio.SteamSession;
import steamarbitrage.task.SaveTask;



public class MainWindow extends JFrame implements ActionListener, ItemListener, WindowListener {
	
	private static final long serialVersionUID = 4965744061795693928L;
	
	// Components
	private JComboBox<String> comboBase = new JComboBox<String>(ItemNames.BASE_NAMES);
	private JComboBox<String> comboSkin = new JComboBox<String>(ItemNames.SKIN_NAMES[0]);
	private JComboBox<String> comboWear = new JComboBox<String>(ItemNames.WEAR_NAMES);
	private JCheckBox checkStat = new JCheckBox("StarTrek");
	
	public static JTextArea areaOutput = new JTextArea();
	public static JTextArea areaError = new JTextArea();
	private JScrollPane scrollOutput = new JScrollPane(areaOutput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JScrollPane scrollError = new JScrollPane(areaError, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	private JLabel labelLow = new JLabel("Low: ");
	private JLabel labelMedian = new JLabel("Median: ");
	private JLabel labelVolume = new JLabel("Volume: ");
	private JLabel labelDate = new JLabel("Date: ");
	private JLabel labelScore = new JLabel("Score: ");
	
	public static JTextField textCurrent = new JTextField();
	public static JLabel labelOnlineStatus = new JLabel();
	public static JLabel labelBalance = new JLabel("Balance: 0.0€");
	
	public static JProgressBar progressBar = new JProgressBar(0, 1);
	
	private ImageIcon iconStart = new ImageIcon("start40.png");
	private ImageIcon iconPause = new ImageIcon("pause40.png");
	
	private JButton butStart = new JButton(iconStart);
	private JButton butStop = new JButton(new ImageIcon("stop40.png"));
	
	
	
	
	
	// Menu
	private JMenuBar menuBar = new JMenuBar();
		
	private JMenu menuFile = new JMenu("File");
		private JMenuItem menuItemPref = new JMenuItem("Preferences");
		private JMenuItem menuItemExit = new JMenuItem("Exit");

	private JMenu menuHelp = new JMenu("Help");
		private JMenuItem menuItemDebug = new JMenuItem("Debug");
		private JMenuItem menuItemTables = new JMenuItem("Tables");
		private JMenuItem menuItemAbout = new JMenuItem("About");
		
		
	
	
	//private PriceData priceData = null;
	//private TaskManager_old taskManager = null;
	
	
	
	

	public MainWindow(String title) throws IOException {
		
		
		
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(200, 100, 760, 530);
		this.setResizable(false);
		this.addWindowListener(this);
		
		
		comboBase.setBounds(120, 10, 200, 25);
		comboSkin.setBounds(330, 10, 200, 25);
		comboWear.setBounds(540, 10, 200, 25);
		checkStat.setBounds(10, 10, 100, 25);
		
		
		areaOutput.setFont(new Font("Courier New", Font.PLAIN, 12));
		areaOutput.setEditable(false);
		areaError.setFont(new Font("Courier New", Font.PLAIN, 12));
		areaError.setForeground(Color.RED);
		areaError.setEditable(false);
		
		
		// redirect System.out and System.err
		//PrintStream printStream = new PrintStream(new CustomOutputStream(areaOutput));
		//System.setOut(printStream);
		//PrintStream printStream2 = new PrintStream(new JTextAreaOutputStream(areaError));
		//System.setErr(printStream2);
		
		scrollOutput.setBounds(10, 115, 730, 200);
		scrollError.setBounds(10, 320, 730, 100);
		
//		butTest1.setBounds(450, 425, 50, 40);
//		butTest2.setBounds(510, 425, 50, 40);
//		butTest3.setBounds(570, 425, 50, 40);
//		butTest4.setBounds(630, 425, 50, 40);
//		butTest5.setBounds(690, 425, 50, 40);
		

		
		butStart.setBounds(10, 425, 40, 40);
		butStop.setBounds(60, 425, 40, 40);
		
		//butLogin.setBounds(150, 425, 100, 40);
		labelOnlineStatus.setBounds(260, 425, 100, 40);
		labelOnlineStatus.setFont(new Font("Courier New", Font.BOLD, 18));
		labelBalance.setBounds(370, 425, 200, 40);
		labelBalance.setFont(new Font("Courier New", Font.BOLD, 18));
		
		labelLow.setBounds(10, 45, 100, 25);
		labelMedian.setBounds(120, 45, 100, 25);
		labelVolume.setBounds(230, 45, 100, 25);
		labelDate.setBounds(340, 45, 290, 25);
		labelScore.setBounds(640, 45, 100, 25);
		
		textCurrent.setBounds(10, 80, 450, 30);
		textCurrent.setEditable(false);
		
		progressBar.setBounds(465, 80, 275, 30);
		progressBar.setStringPainted(true);
		
		

		
		butStart.addActionListener(this);
		butStop.addActionListener(this);
		
		
		comboBase.addItemListener(this);
		comboSkin.addItemListener(this);
		comboWear.addItemListener(this);
		checkStat.addItemListener(this);
		
		//((AbstractDocument)areaOutput.getDocument()).setDocumentFilter(new OutputDocumentFiler(1000));
		//((AbstractDocument)areaError.getDocument()).setDocumentFilter(new OutputDocumentFiler(1000));
		
		
		
		//Menu
		this.setJMenuBar(menuBar);
		
		menuBar.add(menuFile);
			menuFile.add(menuItemPref);
			menuFile.add(menuItemExit);
		
		menuBar.add(menuHelp);
			menuHelp.add(menuItemDebug);
			menuHelp.add(menuItemTables);
			menuHelp.add(menuItemAbout);
		
		menuItemPref.addActionListener(this);
		menuItemDebug.addActionListener(this);
		menuItemTables.addActionListener(this);
		menuItemAbout.addActionListener(this);
		
		this.setLayout(null);
		
		this.add(comboBase);
		this.add(comboSkin);
		this.add(comboWear);
		this.add(checkStat);
		this.add(labelLow);
		this.add(labelMedian);
		this.add(labelVolume);
		this.add(labelDate);
		this.add(textCurrent);
		this.add(scrollOutput);
		this.add(scrollError);
		this.add(labelScore);
		this.add(butStart);
		this.add(butStop);
		this.add(labelOnlineStatus);
		this.add(labelBalance);
		this.add(progressBar);
		
		this.setVisible(true);
		
		
		// load names
		
		

		
		
		// init PriceUpdater
		//PriceUpdater.init(prices, progressBar, PriceUpdater.ONCE_FETCHING);
		//priceUpdater = new PriceUpdater[PRICE_UPDATERS];
		
		
		
		// Test
		//new TestThread().start();
		
		
		//new Task(Task.SAVE, priceData).start();
		
		
		
	}
	
	
	
	
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println("miau");
		

			
			//new Task_old(Task_old.UPDATE_PRICES).start();
			
		if (e.getSource().equals(butStart)) {
			
			
//			if (
//					!System.getProperty("user.name").toLowerCase().contains(
//					Preferences.getInstance().activeProfile.toLowerCase()) ||
//					!Preferences.getInstance().activeProfile.toLowerCase().contains(
//					System.getProperty("user.name").toLowerCase())) {
//				JOptionPane.showMessageDialog(this, "Error: Profile does not match computer user.");
//			}
			
			
			if (butStart.getIcon().equals(iconStart)) {
				butStart.setIcon(iconPause);
				
				SteamArbitrage.taskManager.setStopped(false);
				SteamIO.setQueuePaused(false);
				
			} else {
				butStart.setIcon(iconStart);
				
				SteamIO.setQueuePaused(true);
			}
			
		} else if (e.getSource().equals(butStop)) {
			butStart.setIcon(iconStart);
			SteamArbitrage.taskManager.setStopped(true);
			SteamIO.clearQueue();
			
			
		} else if (e.getSource().equals(menuItemPref)) {
			new PrefWindow(this);
		} else if (e.getSource().equals(menuItemDebug)) {
			new DebugWindow(this);
		} else if (e.getSource().equals(menuItemTables)) {
			new TableWindow(this);
		} else if (e.getSource().equals(menuItemAbout)) {
			JOptionPane.showMessageDialog(this, 
					"....................../´¯/) \n"+
					"....................,/¯../ \n"+
					".................../..../ \n"+
					"............./´¯/'...'/´¯¯`·¸ \n"+
					"........../'/.../..../......./¨¯\\ \n"+
					"........('(...´...´.... ¯~/'...') \n"+
					".........\\.................'...../ \n"+
					"..........''...\\.......... _.·´ \n"+
					"............\\..............( \n"+
					"..............\\.............\\...");
		}
			
			
	}


	
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (e.getSource().equals(comboBase)) {
					
				int index_base = comboBase.getSelectedIndex();
				
				if (index_base >= ItemNames.CS_GO_WEAPON_CASE) {
					// Cases
					checkStat.setEnabled(false);
					comboSkin.setEnabled(false);
					comboWear.setEnabled(false);
					
					checkStat.setSelected(false);
					comboSkin.setSelectedIndex(0);
					comboWear.setSelectedIndex(0);
				} else {
					// Weapons
					// reload the skin-combobox
					comboSkin.removeAllItems();
					
					int iskin = 0;
					do {
						comboSkin.addItem(ItemNames.SKIN_NAMES[index_base][iskin]);
						++iskin;
					} while(!ItemNames.SKIN_NAMES[index_base][iskin].equals(""));
					
					checkStat.setEnabled(true);
					comboSkin.setEnabled(true);
					comboWear.setEnabled(true);
					
				}
			}
			
			
			updatePriceLabels();
			
		} else if (e.getStateChange() == ItemEvent.DESELECTED) {
			
			if (e.getSource().equals(checkStat)) {
				updatePriceLabels();
			}
		}
		
		//updatePriceLabels();
	}
	
	
	public void updatePriceLabels() {
		int index_stat = checkStat.isSelected() ? ItemNames.STAT : ItemNames.NO_STAT;
		int index_base = comboBase.getSelectedIndex();
		int index_skin = comboSkin.getSelectedIndex();
		int index_wear = comboWear.getSelectedIndex();
		
		String full_name = ItemNames.getFullName(index_stat, index_base, index_skin, index_wear);
		
		Price p = SteamArbitrage.priceData.get(full_name);
		labelLow.setText("Low: " + p.low + "€");
		labelMedian.setText("Median: " + p.median + "€");
		labelVolume.setText("Volume: " + p.volume);
		labelDate.setText("Date: " + p.date);
		labelScore.setText("Score: " + p.score);
	}









	@Override
	public void windowActivated(WindowEvent e) {
	}




	@Override
	public void windowClosed(WindowEvent e) {
	}



	@Override
	public void windowClosing(WindowEvent e) {
		
		SteamArbitrage.priceData.savePrices();
		
		TradeHistory.save();

		SteamArbitrage.taskManager.setStopped(true);
		SteamIO.clearQueue();
		
		if (SteamSession.getOnlineStatus() != SteamSession.STATUS_OFFLINE) {
			SteamSession.doLogout();
			Logging.out.println("logged out");
		}
		
		new SaveTask().run();
		
		
		JOptionPane.showMessageDialog(null, "Adios", "!", JOptionPane.PLAIN_MESSAGE);
	}









	@Override
	public void windowDeactivated(WindowEvent e) {
	}




	@Override
	public void windowDeiconified(WindowEvent e) {
	}




	@Override
	public void windowIconified(WindowEvent e) {
	}


	@Override
	public void windowOpened(WindowEvent e) {
	}


	
	

}
