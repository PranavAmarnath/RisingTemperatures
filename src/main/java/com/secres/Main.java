package com.secres;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import com.formdev.flatlaf.*;

public class Main {

	static Model modelGlobal, modelCountry;
	private static View view;
	private static JWindow splash;
	private static JProgressBar pb;
	private int seconds = 22;
	
	public Main() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		URL imageResource = View.class.getResource("/gear.png"); // URL: https://cdn.pixabay.com/photo/2012/05/04/10/57/gear-47203_1280.png
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
		/*
		// For fixed splash time (5 seconds) irrespective of finished parsing through data -> ..
		// ..will not work always if model is not done by the time the worker ends, this depends on the data..
		// ..comment if using dynamic option
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
	
	static void verifyStartRead() {
		createView();
	}
	
	private static void createView() {
		//splash.dispose();
		view = new View();
	}
	
	static void verifyReadFinished() {
		/*// Placing Column 23 data into an ArrayList while avoiding empty data
		Thread thread = new Thread(new Runnable() {
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
		
		GraphCharts.updateDataBasicLineChart();
		GraphCharts.updateDataBasicChartByYear();
		GraphCharts.updateScatterPlotCoolingDec();
		GraphCharts.updateScatterPlotCoolingJun();
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				GraphCharts.updateBasicBarChartByCountry();
				GraphCharts.updateDoubleBarChartByCountry();
				return null;
			}
			protected void done() {
				Main.getPB().setValue(100);
				splash.dispose();
				View.getFrame().setVisible(true);
			}
		}.execute();
		
 	}
	
	public static JWindow getSplash() {
		return splash;
	}
	
	public static JProgressBar getPB() {
		return pb;
	}
	
	public static void main(String[] args) {
		///*
		System.setProperty("apple.laf.useScreenMenuBar", "true"); // for picky mac users
		System.setProperty("apple.awt.application.name", "Secres"); // mac header on mac menubar
		if(System.getProperty("os.name").toString().contains("Mac")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				
				SwingUtilities.invokeLater(() -> {
					Desktop desktop = Desktop.getDesktop();
					
			        desktop.setAboutHandler(e ->
			            JOptionPane.showMessageDialog(null, "About dialog")
			        );
			        desktop.setPreferencesHandler(e ->
			            JOptionPane.showMessageDialog(null, "Preferences dialog")
			        );
			        desktop.setQuitHandler((e,r) -> {
			        	int input = JOptionPane.showConfirmDialog(View.getFrame(), "Are you sure you want to quit?");
			        	if(input == 0) {
			        		System.exit(0);
			            }
			        });
				});
			} catch (Exception e) { e.printStackTrace(); }
		}
		else {
			FlatLightLaf.install();
		}
		/*
		UIManager.put("ScrollBar.thumbArc", 999);
		UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
		UIManager.put("ScrollBar.width", 13);
		UIManager.put( "SplitPaneDivider.style", "plain" );
		*/
		SwingUtilities.invokeLater(() -> {
			new Main();
		});
	}

}
