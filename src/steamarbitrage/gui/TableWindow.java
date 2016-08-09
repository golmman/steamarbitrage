package steamarbitrage.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import steamarbitrage.Evaluation;
import steamarbitrage.SteamArbitrage;
import steamarbitrage.database.ItemNames;
import steamarbitrage.database.steam.PriceData;
import steamarbitrage.database.steamanalyst.SteamAnalyst;
import steamarbitrage.database.trades.TradeHistory;



public class TableWindow extends JDialog implements ItemListener, DocumentListener {
	
	private static final long serialVersionUID = -2956976047765518172L;
	
	private TableModel tableModel;
	
	private JTextField textFilter = new JTextField();
	private JComboBox<String> comboDatabase = new JComboBox<String>();
	private JTable table = new JTable();
	
	
	private static final int PRICE_DATA = 0;
	private static final int STEAM_ANALYST = 1;
	private static final int TRADE_HISTORY_BUY = 2;
	private static final int TRADE_HISTORY_SELL = 3;
	private static final int EVALUATION = 4;
	private static final int ITEM_NAMES = 5;
	
	private static final String[] databaseNames = {
		"PriceData",
		"SteamAnalyst",
		"TradeHistory.buy",
		"TradeHistory.sell",
		"Evaluation",
		"ItemNames"
	};



	public TableWindow(JFrame owner) {
		super(owner, "Table", true);
		this.setLayout(new BorderLayout());
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 1000, 500);
		
		
		textFilter.setPreferredSize(new Dimension(200, 25));
		textFilter.getDocument().addDocumentListener(this);
		
		for (int i = 0; i < databaseNames.length; i++) {
			comboDatabase.addItem(databaseNames[i]);
		}
		
		comboDatabase.addItemListener(this);
		
		
		//table.setAutoCreateRowSorter(true);
		
		updateTableModel(PRICE_DATA);
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel panelNorth = new JPanel();
		panelNorth.add(comboDatabase);
		panelNorth.add(textFilter);
		
		
		
		this.add(panelNorth, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("parent");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0, 100, 100);
		frame.setVisible(true);
		
		SteamAnalyst.load(new StringBuilder());
		ItemNames.loadNames();
		TradeHistory.load();
		SteamArbitrage.priceData = new PriceData();
		SteamArbitrage.priceData.loadPrices();
		
		Evaluation.setParameters(0.5f, 1000.0f, 1000, 100000);
		Evaluation.evaluate();
		
		new TableWindow(null);
	}
	
	
	@SuppressWarnings("unused")
	private <V> TableModel getModelFromData(Collection<V> c) {
		HashMap<Integer, V> map = new HashMap<Integer, V>(c.size());
		
		int i = 0;
		for (V value : c) {
			map.put(i, value);
			++i;
		}
		
		return getModelFromData(map);
	}
	
	
	
	private <K, V> TableModel getModelFromData(HashMap<K, V> map) {
		
		if (map.keySet().size() == 0) {
			return new DefaultTableModel();
		}
		
		// get samples of K and V to determine their class
		K k = map.keySet().iterator().next();
		V v = map.get(k);
		
		Field[] fields = v.getClass().getFields();
		
		
		// columns
		String[] columns = new String[fields.length + 1];
		
		columns[0] = "Key";
		
		if (fields.length > 1) {
			for (int i = 1; i <= fields.length; i++) {
				columns[i] = fields[i-1].getName();
			}
		} else {
			columns[1] = v.getClass().getSimpleName();
		}
		
		
		// rows
		Object[][] data = new Object[map.size()][fields.length + 1];
		int i = 0;
		
		for (K key : map.keySet()) {
			
			V value = map.get(key);
			
			
			data[i][0] = key.toString();
			
			if (fields.length > 1) {
				for (int j = 1; j <= fields.length; j++) {
					try {
						data[i][j] = fields[j-1].get(value);
//						if (fields[j-1].getType().equals(Integer.TYPE)) {
//							data[i][j] = fields[j-1].getInt(value);
//						} else {
//							data[i][j] = fields[j-1].get(value).toString();
//						}
						
						
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			} else {
				data[i][1] = value.toString();
			}
			
			++i;
		}
		
		
		tableModel = new DefaultTableModel(data, columns) {
			private static final long serialVersionUID = 4743879108743341137L;
			
			// make cell non-editable
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
			
			// return the correct column class for sorting issues
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				
				if (columnIndex == 0) return String.class;
				
				
				if (fields[columnIndex-1].getType().equals(Boolean.TYPE)) {
					return Boolean.class;
				} else if (fields[columnIndex-1].getType().equals(Byte.TYPE)) {
					return Byte.class;
				} else if (fields[columnIndex-1].getType().equals(Character.TYPE)) {
					return Character.class;
				} else if (fields[columnIndex-1].getType().equals(Short.TYPE)) {
					return Short.class;
				} else if (fields[columnIndex-1].getType().equals(Integer.TYPE)) {
					return Integer.class;
				} else if (fields[columnIndex-1].getType().equals(Long.TYPE)) {
					return Long.class;
				} else if (fields[columnIndex-1].getType().equals(Float.TYPE)) {
					return Float.class;
				} else if (fields[columnIndex-1].getType().equals(Double.TYPE)) {
					return Double.class;
				}
				
				
				return super.getColumnClass(columnIndex);
			}
		};
		
		
		
		
		

		

		return tableModel;
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (e.getSource().equals(comboDatabase)) {
				
				int index = comboDatabase.getSelectedIndex();
				
				updateTableModel(index);
				
			}
		}
		
	}
	
	
	
	
	
	private void updateTableModel(int tableID) {
		Runnable runnable = null;
		
		if (tableID == PRICE_DATA) {
			runnable = updateTableModelThread(SteamArbitrage.priceData);
		} else if (tableID == STEAM_ANALYST) {
			runnable = updateTableModelThread(SteamAnalyst.data);
		} else if (tableID == TRADE_HISTORY_BUY) {
			runnable = updateTableModelThread(TradeHistory.buy);
		} else if (tableID == TRADE_HISTORY_SELL) {
			runnable = updateTableModelThread(TradeHistory.sell);
		} else if (tableID == EVALUATION) {
			runnable = updateTableModelThread(Evaluation.evaluatedNames);
		} else if (tableID == ITEM_NAMES) {
			runnable = updateTableModelThread(ItemNames.names);
		}
		
		SwingUtilities.invokeLater(runnable);
	}
	
	
	private <K, V> Runnable updateTableModelThread(HashMap<K, V> map) {
		
		TableModel tm = getModelFromData(map);
		
		Thread runnable = new Thread() {
			@Override public void run() {
				table.setModel(tm);
				updateRowFilter();
			}
		};
		runnable.setDaemon(true);
		
		return runnable;
	}
	
	private <V> Runnable updateTableModelThread(Collection<V> c) {
		
		HashMap<Integer, V> map = new HashMap<Integer, V>(c.size());
		
		int i = 0;
		for (V value : c) {
			map.put(i, value);
			++i;
		}
		
		return updateTableModelThread(map);
	}



	private void updateRowFilter() {
		

		String text = textFilter.getText();
		// System.out.println(text);

		RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
			public boolean include(Entry<?, ?> entry) {

				for (int i = 0; i < entry.getValueCount(); i++) {
					if (entry.getValue(i).toString().contains(text)) {
						return true;
					}
				}

				return false;
			}
		};

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				tableModel);
		sorter.setRowFilter(filter);
		table.setRowSorter(sorter);
	}


	
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
	}


	@Override
	public void insertUpdate(DocumentEvent arg0) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				updateRowFilter();
			}
		};
		thread.setDaemon(true);
		
		SwingUtilities.invokeLater(thread);
	}


	@Override
	public void removeUpdate(DocumentEvent arg0) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				updateRowFilter();
			}
		};
		thread.setDaemon(true);
		
		SwingUtilities.invokeLater(thread);
	}

	
	
	
}










