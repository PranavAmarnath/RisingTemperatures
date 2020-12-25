package com.secres;

import java.awt.*;
import java.net.URL;

import javax.swing.*;
import com.formdev.flatlaf.*;

public class Main {

	private static Model model;
	private static View view;
	private static JWindow splash;
	
	public Main() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		URL imageResource = View.class.getResource("/gear.png"); // URL: https://cdn.pixabay.com/photo/2012/05/04/10/57/gear-47203_1280.png
		Image image = defaultToolkit.getImage(imageResource);

		Taskbar taskbar = Taskbar.getTaskbar();

		initSplash();
		
		try {
			taskbar.setIconImage(image);
		} catch (UnsupportedOperationException e) {
			splash.setIconImage(image);
		}
		
		model = new Model("/MissingMigrants.csv");
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
		
		JLabel img = new JLabel(icon);
		splashPanel.add(img);
		
		JProgressBar pb = new JProgressBar(JProgressBar.HORIZONTAL);
		pb.setIndeterminate(true);
		splashPanel.add(pb, BorderLayout.SOUTH);
		
		splash.setContentPane(splashPanel);
		
		splash.setPreferredSize(new Dimension(500, 400));
		splash.pack();
		splash.setLocationRelativeTo(null);
		splash.setVisible(true);
	}
	
	static void verifyTableModelFinish() {
		createView();
	}
	
	private static void createView() {
		splash.dispose(); // statement to comment if going with worker option
		view = new View(model.getModel());
		view.getFrame().setVisible(true); // statement to comment if going with worker option
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
			                JOptionPane.showMessageDialog(null, "Quit dialog");
			                System.exit(0);
			            }
			        );
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
		//*/
		SwingUtilities.invokeLater(() -> {
			new Main();
		});
	}

}
