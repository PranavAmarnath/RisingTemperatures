package com.secres;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import org.jfree.chart.JFreeChart;

import com.formdev.flatlaf.*;

/**
 * <code>Main</code> is the main class for the application.
 * <P>
 * This class serves as the Controller, first initializing the splash screen and then notifying
 * {@link GraphCharts} to update the <code>ChartPanel</code> of each <code>JFreeChart</code>.
 * <P>
 * The code also sets {@link System} and {@link UIManager} properties.<br>
 * As the Controller class, <code>Main</code> creates the {@link Model}s and {@link View}
 * 
 * @author Pranav Amarnath
 *
 */
public class Main {

	/** Table names */
	static Model modelGlobal, modelCountry;
	//private static View view;
	/** Splash screen */
	private static JWindow splash;
	/** Progress bar in Splash screen */
	private static JProgressBar pb;
	/** Time for read/splash */
	private int seconds = 3;
	
	/** Default Constructor - reads images, creates {@link Model}s */
	public Main() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		URL imageResource = Main.class.getResource("/gear.png"); // URL: https://cdn.pixabay.com/photo/2012/05/04/10/57/gear-47203_1280.png
		Image image = defaultToolkit.getImage(imageResource);

		initSplash();
		
		try {
			Taskbar taskbar = Taskbar.getTaskbar();
			taskbar.setIconImage(image);
		} catch (UnsupportedOperationException e) {
			splash.setIconImage(image);
		}
		
		modelGlobal = new Model("/GlobalTemperatures.csv");
		modelCountry = new Model("/GlobalLandTemperaturesByCountry.csv");
		/**
		 * For fixed splash time (5 seconds) irrespective of finished parsing through data -> ..
		 * ..will not work always if model is not done by the time the worker ends, this depends on the data..
		 * ..comment if using dynamic option
		 */
		/*
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				Thread.sleep(5000);
				return null;
			}
			protected void done() {
				splash.dispose();
				view.getFrame().setVisible(true);
			}
		};
		worker.execute();
		*/
	}
	
	/** Initializes the splash screen with necessary Swing components */
	private void initSplash() {
		ImageIcon icon = null;
		icon = new ImageIcon(Main.class.getResource("/splashDotsPNG.png")); // URL: https://cdn.pixabay.com/photo/2017/07/01/19/48/background-2462431_1280.jpg
		splash = new JWindow();
		JPanel splashPanel = new JPanel(new BorderLayout());
		
		JPanel timePanel = new JPanel();
		splashPanel.add(timePanel, BorderLayout.NORTH);
		
		JLabel img = new JLabel(icon);
		splashPanel.add(img);
		
		JLabel estTime = new JLabel("Est. Time Remaining: " + seconds + " seconds...");
		timePanel.add(estTime);
		timePanel.setBackground(new Color(134, 169, 181));
		Timer estTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seconds--;
				estTime.setText("Est. Time Remaining: " + seconds + " seconds...");
				if(seconds == 0) {
					((Timer)e.getSource()).stop();
				}
			}
		});
		estTimer.start();
		
		pb = new JProgressBar(JProgressBar.HORIZONTAL);
		//pb.setIndeterminate(true);
		splashPanel.add(pb, BorderLayout.SOUTH);
		
		splash.setContentPane(splashPanel);
		
		splash.setPreferredSize(new Dimension(500, 400));
		splash.pack();
		splash.setLocationRelativeTo(null);
		splash.setVisible(true);
	}
	
	/**
	 * Notifier method that last {@link Model} has started read.
	 */
	static void verifyStartRead() {
		createView();
	}
	
	/**
	 * Called inside <code>Main</code> to create {@link View}
	 */
	private static void createView() {
		//splash.dispose();
		SwingUtilities.invokeLater(() -> {
			new View();
		});
	}
	
	/**
	 * Notifier method that last {@link Model} has finished read.
	 */
	static void verifyReadFinished() {
		/*// Placing Column 23 data into an ArrayList while avoiding empty data
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Object> list = new ArrayList<>();
				//System.out.println(View.getTable().getModel().getRowCount());
				for(int i = 0; i < View.getTable().getModel().getRowCount(); i++) {
					//System.out.println(((String)View.getTable().getModel().getValueAt(i, 23)).length());
					if(!(((String)View.getTable().getModel().getValueAt(i, 23)).equals(""))) {
						list.add(View.getTable().getModel().getValueAt(i, 23)); // get the all row values at column index 23
					}
				}
				//list.forEach(System.out::println);
			}
		});
		thread.start();
		*/
		/*
		SwingUtilities.invokeLater(() -> {
			GraphCharts.updateDataBasicLineChart();
			//GraphCharts.updateDataBasicLineChartByYear();
			//GraphCharts.updateBasicScatterPlotByYear();
			GraphCharts.updateDataBasicChartByYear();
		});
		*/
		/** Start placing data into graphs; all done on EDT. (Quick reads) */
		GraphCharts.updateDataBasicLineChart();
		GraphCharts.updateDataBasicChartByYear();
		GraphCharts.updateScatterPlotCoolingDec();
		GraphCharts.updateScatterPlotCoolingJun();
		GraphCharts.updateBasicBarChartByCountry();
		GraphCharts.updateDoubleBarChartByCountry();
		GraphCharts.updateMultiXYLineChartByEconomy();
 	}
	
	/**
	 * @return {@link JWindow} splash screen
	 */
	static JWindow getSplash() {
		return splash;
	}
	
	/**
	 * @return {@link JProgressBar} splash screen.
	 */
	static JProgressBar getPB() {
		return pb;
	}
	
	/**
	 * Main method of GUI.
	 * @param args  not used
	 */
	public static void main(String[] args) {
		/** For picky mac users */
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		/** Mac header on mac menubar */
		System.setProperty("apple.awt.application.name", "Secres");
		/** Acceleration of graphics, should ONLY be used by developers */
		//System.setProperty("apple.awt.graphics.EnableQ2DX","true");
		System.setProperty("apple.awt.antialiasing", "true");
		System.setProperty("apple.awt.textantialiasing", "true");
		
		if(System.getProperty("os.name").toString().contains("Mac")) {
			try {				
				SwingUtilities.invokeLater(() -> {
					Desktop desktop = Desktop.getDesktop();
					
			        desktop.setAboutHandler(e -> {
			        	View.createAbout();
			        });
			        desktop.setPreferencesHandler(e -> {
			            View.createPreferences();
			        });
			        desktop.setQuitHandler((e,r) -> {
			        	View.createQuit();
			        });
				});
			} catch (Exception e) { e.printStackTrace(); }
		}
		FlatLightLaf.install();
		//UIManager.put("ScrollBar.thumbArc", 999);
		//UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
		//UIManager.put("ScrollBar.width", 13);
		//UIManager.put("SplitPaneDivider.style", "plain");
		//UIManager.put("TabbedPane.selectedBackground", new Color(218, 228, 237));
		SwingUtilities.invokeLater(() -> {
			new Main();
		});
	}

}
