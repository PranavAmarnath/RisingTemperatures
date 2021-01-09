package com.secres;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class View {

	private static JFrame frame;
	
	private JPanel mainPanel;
	private JPanel treePanel;
	private JPanel graphPanel;
	private JPanel cardsPanel;
	private JPanel emptyPanel;
	private JPanel tableGlobalPanel, tableCountryPanel;
	
	private final String EMPTYPANEL = "Card that is empty";
	private final String TABLEGLOBALPANEL = "Card with Global Data Table";
	private final String TABLECOUNTRYPANEL = "Card with Country Data Table";
	
	private final String ALLLINEPANEL = "Card with Basic Line Chart";
	private final String AVGLINEPANEL = "Card with Average Temperature Line Chart";
	private final String AVGSCATTERPANEL = "Card with Average Temperature Scatter Plot";
	private final String DECSCATTERPANEL = "Card with Temperatures from December Scatter Plot";
	private final String JUNSCATTERPANEL = "Card with Temperatures from June Scatter Plot";
	private final String AVGBARPANEL = "Card with Average Temperature Bar Chart";
	private final String DOUBLEBARGREATESTPANEL = "Card with Most Difference in a century Bar Chart";
	private final String DOUBLEBARLEASTPANEL = "Card with Least Difference in a century Bar Chart";
	private final String MULTILINEECONOMYPANEL = "Card with Top 5 Economies Line Chart";
	
	private JSplitPane splitPane;
	private JScrollPane treeScroll;
	private JScrollPane tableGlobalScroll, tableCountryScroll;
	private JTree componentTree;
	private DefaultMutableTreeNode root, dataRootNode, globalTableNode, countryTableNode;
	private DefaultMutableTreeNode graphRootNode;
	private DefaultMutableTreeNode globalNode, basicLineNode, basicLineByYearNode, basicScatterByYearNode, basicScatterCoolingDecNode, basicScatterCoolingJunNode;
	private DefaultMutableTreeNode countryNode, basicBarNode, doubleBarGreatestNode, doubleBarLeastNode, multiLineEconomyNode;
	private static JTable tableGlobalData, tableCountryData;
	private JMenuBar menuBar;
	private JMenu file, view;
	private JMenuItem light, dark, system, close;
	private FlatLightLaf lightLaf = new FlatLightLaf();
	private FlatIntelliJLaf intellijLaf = new FlatIntelliJLaf();
	private FlatDarkLaf darkLaf = new FlatDarkLaf();
	private FlatDarculaLaf darculaLaf = new FlatDarculaLaf();
	
	public View() {
		createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		frame = new JFrame("Secres GUI") {
			public Dimension getPreferredSize() {
				return new Dimension(800, 600);
			}
		};
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int input = JOptionPane.showConfirmDialog(View.getFrame(), "Are you sure you want to quit?");
	        	if(input == 0) {
	        		System.exit(0);
	            }
	        	else {
	        		frame.setVisible(true);
	        	}
			}
		});

		/*
		 * @see https://stackoverflow.com/a/56924202/13772184
		 */
		// loading an image from a file
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		URL imageResource = View.class.getResource("/gear.png"); // URL: https://cdn.pixabay.com/photo/2012/05/04/10/57/gear-47203_1280.png
		Image image = defaultToolkit.getImage(imageResource);

		try {
			// this is new since JDK 9
			Taskbar taskbar = Taskbar.getTaskbar();
			// set icon for mac os (and other systems which do support this method)
			taskbar.setIconImage(image);
		} catch (UnsupportedOperationException e) {
			// set icon for windows (and other systems which do support this method)
			frame.setIconImage(image);
		}
		// end citation

		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		file = new JMenu("File");
		view = new JMenu("View");
		menuBar.add(file);
		menuBar.add(view);
		
		light = new JMenuItem("Light");
		light.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if((System.getProperty("os.name").toString()).contains("Mac")) {
					try {
						UIManager.setLookAndFeel(intellijLaf);
					} catch (UnsupportedLookAndFeelException e1) {
						e1.printStackTrace();
					}
				}
				else {
					try {
						UIManager.setLookAndFeel(lightLaf);
					} catch (UnsupportedLookAndFeelException e1) {
						e1.printStackTrace();
					}
				}
				SwingUtilities.updateComponentTreeUI(frame);
			}
		});
		dark = new JMenuItem("Dark");
		dark.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if((System.getProperty("os.name").toString()).contains("Mac")) {
					try {
						UIManager.setLookAndFeel(darculaLaf);
					} catch (UnsupportedLookAndFeelException e1) {
						e1.printStackTrace();
					}
				}
				else {
					try {
						UIManager.setLookAndFeel(darkLaf);
					} catch (UnsupportedLookAndFeelException e1) {
						e1.printStackTrace();
					}
				}
				SwingUtilities.updateComponentTreeUI(frame);
			}
		});
		system = new JMenuItem(System.getProperty("os.name").toString());
		system.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e3) {
					e3.printStackTrace();
				}
				SwingUtilities.updateComponentTreeUI(frame);
			}
		});
		view.add(light);
		view.add(dark);
		view.add(system);
		
		close = new JMenuItem("Close");
		file.add(close);
		close.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int input = JOptionPane.showConfirmDialog(View.getFrame(), "Are you sure you want to quit?");
	        	if(input == 0) {
	        		System.exit(0);
	            }
			}
		});
		
		mainPanel = new JPanel(new BorderLayout());
		frame.add(mainPanel);
		
		treePanel = new JPanel(new BorderLayout());
		treeScroll = new JScrollPane();
		
		componentTree = new JTree(setTreeModel());
		componentTree.setShowsRootHandles(true);
		componentTree.collapseRow(0);
		componentTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) componentTree.getLastSelectedPathComponent();
				componentTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				if(node == globalTableNode) {
					//System.out.println("Table will be shown!");
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, TABLEGLOBALPANEL);
				}
				else if(node == countryTableNode) {
					//System.out.println("Table will be shown!");
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, TABLECOUNTRYPANEL);
				}
				else if(node == basicLineNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, ALLLINEPANEL);
				}
				else if(node == basicLineByYearNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, AVGLINEPANEL);
				}
				else if(node == basicScatterByYearNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, AVGSCATTERPANEL);
				}
				else if(node == basicScatterCoolingDecNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, DECSCATTERPANEL);
				}
				else if(node == basicScatterCoolingJunNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, JUNSCATTERPANEL);
				}
				else if(node == basicBarNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, AVGBARPANEL);
				}
				else if(node == doubleBarGreatestNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, DOUBLEBARGREATESTPANEL);
				}
				else if(node == doubleBarLeastNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
					cl.show(cardsPanel, DOUBLEBARLEASTPANEL);
				}
				else if(node == multiLineEconomyNode) {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
					cl.show(cardsPanel, MULTILINEECONOMYPANEL);
				}
				else {
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, EMPTYPANEL);
				}
			}
		});
		
		treeScroll.setViewportView(componentTree);
		treePanel.add(treeScroll);
		
		graphPanel = new JPanel(new BorderLayout());
		cardsPanel = new JPanel(new CardLayout());
		//cardsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		tableGlobalPanel = new JPanel(new BorderLayout());
		tableCountryPanel = new JPanel(new BorderLayout());
		
		/*
		 * @see https://stackoverflow.com/a/5630271/13772184
		 */
		/*// Start citation
		String[] header = {"Name", "Value"};
        String[] a = new String[0];
        String[] names = System.getProperties().stringPropertyNames().toArray(a);
        String[][] data = new String[names.length][2];
        for (int ii=0; ii<names.length; ii++) {
            data[ii][0] = names[ii];
            data[ii][1] = System.getProperty(names[ii]);
        }
        DefaultTableModel model = new DefaultTableModel(data, header);
        // end citation */
		
		tableGlobalData = new JTable(Main.modelGlobal.getModel()) {
			public boolean isCellEditable(int row, int column) {  
				return false;
			}
		};
		
		tableCountryData = new JTable(Main.modelCountry.getModel()) {
			public boolean isCellEditable(int row, int column) {  
				return false;
			}
		};
		//System.out.println("Reached second table.");
		
		if(System.getProperty("os.name").toString().contains("Mac")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			FlatLightLaf.install();
		}
		
		///*
		tableGlobalData.setToolTipText("<html>Display of CSV Data in Table format.<br>Double-click on a cell to copy text.<br>Press CTRL+C to copy row(s).</html>");
        tableGlobalData.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) { //e.getClickCount() == 2 -> double-click
		            //System.out.println("right click");
		            Point p = e.getPoint();
		            int row = tableGlobalData.rowAtPoint(p);
		            int col = tableGlobalData.columnAtPoint(p);
		            Object value = tableGlobalData.getValueAt(row, col);
		            StringSelection stringSelection = new StringSelection(value.toString());
		            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		            clipboard.setContents(stringSelection, stringSelection);
		            JOptionPane.showMessageDialog(frame, "Copied text!\nHover the mouse over the table for more info.");
		        }
		    }
		});
		tableCountryData.setToolTipText("<html>Display of CSV Data in Table format.<br>Double-click on a cell to copy text.<br>Press CTRL+C to copy row(s).</html>");
        tableCountryData.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) { //e.getClickCount() == 2 -> double-click
		            //System.out.println("right click");
		            Point p = e.getPoint();
		            int row = tableCountryData.rowAtPoint(p);
		            int col = tableCountryData.columnAtPoint(p);
		            Object value = tableCountryData.getValueAt(row, col);
		            StringSelection stringSelection = new StringSelection(value.toString());
		            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		            clipboard.setContents(stringSelection, stringSelection);
		            JOptionPane.showMessageDialog(frame, "Copied text!\nHover the mouse over the table for more info.");
		        }
		    }
		});
		//*/
		
		tableGlobalScroll = new JScrollPane(tableGlobalData);
		//tableGlobalScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableCountryScroll = new JScrollPane(tableCountryData);
		
		tableGlobalData.setAutoCreateRowSorter(true);
		tableGlobalData.setAutoResizeMode(0);
		tableGlobalData.getTableHeader().setReorderingAllowed(false);
		
		tableCountryData.setAutoCreateRowSorter(true);
		tableCountryData.setAutoResizeMode(0);
		tableCountryData.getTableHeader().setReorderingAllowed(false);
		
		tableGlobalPanel.add(tableGlobalScroll);
		tableCountryPanel.add(tableCountryScroll);
		
		emptyPanel = new JPanel(new BorderLayout());
		//emptyPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
		//emptyPanel.setBackground(Color.WHITE);
		JLabel emptyLabel = new JLabel("Click nodes on the left to display items.", JLabel.CENTER);
		emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		emptyLabel.setForeground(new Color(150, 150, 150));
		emptyPanel.add(emptyLabel);
		
		cardsPanel.add(emptyPanel, EMPTYPANEL);
		cardsPanel.add(tableGlobalPanel, TABLEGLOBALPANEL);
		cardsPanel.add(tableCountryPanel, TABLECOUNTRYPANEL);
		CardLayout cl = (CardLayout)(cardsPanel.getLayout());
	    cl.show(cardsPanel, EMPTYPANEL);
		
	    SwingUtilities.invokeLater(() -> {
	    	cardsPanel.add(GraphCharts.basicLineChart(), ALLLINEPANEL);
			cardsPanel.add(GraphCharts.basicLineChartByYear(), AVGLINEPANEL);
			cardsPanel.add(GraphCharts.basicScatterPlotByYear(), AVGSCATTERPANEL);
			cardsPanel.add(GraphCharts.basicScatterPlotCoolingDec(), DECSCATTERPANEL);
			cardsPanel.add(GraphCharts.basicScatterPlotCoolingJun(), JUNSCATTERPANEL);
			cardsPanel.add(GraphCharts.basicBarChartByCountry(), AVGBARPANEL);
			cardsPanel.add(GraphCharts.doubleBarChartByCountryGreatest(), DOUBLEBARGREATESTPANEL);
			cardsPanel.add(GraphCharts.doubleBarChartByCountryLeast(), DOUBLEBARLEASTPANEL);
			cardsPanel.add(GraphCharts.multiXYLineChartByEconomy(), MULTILINEECONOMYPANEL);
	    });
	    /*// Code below incorrectly synchronized
	    cardsPanel.add(GraphCharts.basicLineChart(), ALLLINEPANEL);
		cardsPanel.add(GraphCharts.basicLineChartByYear(), AVGLINEPANEL);
		cardsPanel.add(GraphCharts.basicScatterPlotByYear(), AVGSCATTERPANEL);
		*/
		
		graphPanel.add(cardsPanel);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, graphPanel);
		splitPane.setContinuousLayout(true);
		mainPanel.add(splitPane);
		
		frame.pack();
		splitPane.setResizeWeight(0.25);
		splitPane.setDividerLocation(0.25);
		frame.setLocationByPlatform(true);
	}
	
	private DefaultMutableTreeNode setTreeModel() {
		root = new DefaultMutableTreeNode("Access");
		dataRootNode = new DefaultMutableTreeNode("Data");
		root.add(dataRootNode);
		
		globalTableNode = new DefaultMutableTreeNode("Global Data");
        dataRootNode.add(globalTableNode);
        countryTableNode = new DefaultMutableTreeNode("Country Data");
        dataRootNode.add(countryTableNode);
        
		graphRootNode = new DefaultMutableTreeNode("Graphs");
		root.add(graphRootNode);
		
		globalNode = new DefaultMutableTreeNode("Global Data Graphs");
		basicLineNode = new DefaultMutableTreeNode("Basic Line Chart");
		globalNode.add(basicLineNode);
		basicLineByYearNode = new DefaultMutableTreeNode("Line Chart By Year");
		globalNode.add(basicLineByYearNode);
		basicScatterByYearNode = new DefaultMutableTreeNode("Scatter Plot By Year");
		globalNode.add(basicScatterByYearNode);
		basicScatterCoolingDecNode = new DefaultMutableTreeNode("Scatter Plot In December");
		globalNode.add(basicScatterCoolingDecNode);
		basicScatterCoolingJunNode = new DefaultMutableTreeNode("Scatter Plot In June");
		globalNode.add(basicScatterCoolingJunNode);
		
		countryNode = new DefaultMutableTreeNode("Country Data Graphs");
		basicBarNode = new DefaultMutableTreeNode("Bar Chart By Country");
		countryNode.add(basicBarNode);
		doubleBarGreatestNode = new DefaultMutableTreeNode("Greatest Increase in Temp.");
		countryNode.add(doubleBarGreatestNode);
		doubleBarLeastNode = new DefaultMutableTreeNode("Least Increase in Temp.");
		countryNode.add(doubleBarLeastNode);
		multiLineEconomyNode = new DefaultMutableTreeNode("Top 5 Economies Temp.");
		countryNode.add(multiLineEconomyNode);
		
		graphRootNode.add(globalNode);
		graphRootNode.add(countryNode);
        
        return root;
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
	public static JTable getGlobalTable() {
		return tableGlobalData;
	}
	
	public static JTable getCountryTable() {
		return tableCountryData;
	}
	
}
