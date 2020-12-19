package com.secres;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class View {

	private static JFrame frame;
	private static JPanel mainPanel;
	private static JPanel treePanel;
	private static JPanel graphPanel;
	private static JPanel cardsPanel;
	private static JPanel emptyPanel;
	private static JPanel tablePanel;
	private final static String EMPTYPANEL = "Card that is empty";
	private final static String TABLEPANEL = "Card with Data Table";
	private static JSplitPane splitPane;
	private JScrollPane treeScroll;
	private static JScrollPane tableScroll;
	private JTree componentTree;
	private DefaultMutableTreeNode root, tableNode;
	private static JTable tableData;
	private JMenuBar menuBar;
	private JMenu view;
	private JMenuItem light, dark, system;
	private FlatLightLaf lightLaf = new FlatLightLaf();
	private FlatDarkLaf darkLaf = new FlatDarkLaf();
	private static ReadData readObject;
	
	public View() {
		createAndShowGUI();
	}
	
	public void createAndShowGUI() {
		frame = new JFrame("Secres GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		view = new JMenu("View");
		menuBar.add(view);
		light = new JMenuItem("Light");
		light.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(lightLaf);
				} catch (UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				tableData.setToolTipText("Display of CSV Data in Table format.\nDouble-click on a cell to copy text.\nPress CTRL+C to copy the whole row.");
				SwingUtilities.updateComponentTreeUI(frame);
			}
		});
		dark = new JMenuItem("Dark");
		dark.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(darkLaf);
				} catch (UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				tableData.setToolTipText("Display of CSV Data in Table format.\nDouble-click on a cell to copy text.\nPress CTRL+C to copy the whole row.");
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
					tableData.setToolTipText("Display of CSV Data in Table format.\n Double-click on a cell to copy text.\n Press CTRL+C to copy the whole row.");
				} catch (Exception e3) { e3.printStackTrace(); }
				SwingUtilities.updateComponentTreeUI(frame);
			}
		});
		view.add(light);
		view.add(dark);
		view.add(system);
		
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
				if(node == tableNode) {
					//System.out.println("Table will be shown!");
					CardLayout cl = (CardLayout)(cardsPanel.getLayout());
				    cl.show(cardsPanel, TABLEPANEL);
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
		
		tablePanel = new JPanel(new BorderLayout());
		
		/* // taken from Andrew Thompson's nested layout example: https://stackoverflow.com/a/5630271/13772184
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
		
		readObject = new ReadData();
		
	}
	
	@SuppressWarnings("serial")
	static void showGUIAfterRead() {
		tableData = new JTable(readObject.getModel()) {
			
			public boolean isCellEditable(int row, int column) {  
				return false;
			}
			
		};
		
		if(System.getProperty("os.name").toString().contains("Mac")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				tableData.setToolTipText("Display of CSV Data in Table format.\n Double-click on a cell to copy text.\n Press CTRL+C to copy the whole row.");
			} catch (Exception e) { e.printStackTrace(); }
		}
		else {
			FlatLightLaf.install();
			tableData.setToolTipText("Display of CSV Data in Table format.\nDouble-click on a cell to copy text.\nPress CTRL+C to copy the whole row.");
		}
		
        tableData.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) { //e.getClickCount() == 2 -> double-click
		            //System.out.println("right click");
		            Point p = e.getPoint();
		            int row = tableData.rowAtPoint(p);
		            int col = tableData.columnAtPoint(p);
		            Object value = tableData.getValueAt(row, col);
		            StringSelection stringSelection = new StringSelection(value.toString());
		            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		            clipboard.setContents(stringSelection, stringSelection);
		            JOptionPane.showMessageDialog(frame, "Copied text!\nHover the mouse over the table for more info.");
		        }
		    }
		});
		
		tableScroll = new JScrollPane(tableData);
		//tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// Customization of JTable
		tableData.setAutoCreateRowSorter(true);
		tableData.setAutoResizeMode(0);
		tableData.getTableHeader().setReorderingAllowed(false);
		// End customization
		
		tablePanel.add(tableScroll);
		
		emptyPanel = new JPanel();
		//emptyPanel.setBackground(Color.WHITE);
		cardsPanel.add(emptyPanel, EMPTYPANEL);
		cardsPanel.add(tablePanel, TABLEPANEL);
		CardLayout cl = (CardLayout)(cardsPanel.getLayout());
	    cl.show(cardsPanel, EMPTYPANEL);
	    
		graphPanel.add(cardsPanel);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, graphPanel);
		mainPanel.add(splitPane);
		
		frame.pack();
		splitPane.setResizeWeight(0.25);
		splitPane.setDividerLocation(0.25);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
	
	private DefaultMutableTreeNode setTreeModel() {
		root = new DefaultMutableTreeNode("Data");
        //create the child nodes
        tableNode = new DefaultMutableTreeNode("Table");
        //add the child nodes to the root node
        root.add(tableNode);
        
        return root;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
}
