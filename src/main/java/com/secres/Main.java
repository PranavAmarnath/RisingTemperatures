package com.secres;

import java.awt.*;
import javax.swing.*;
import com.formdev.flatlaf.*;

public class Main {

	public Main() {
		new View();
	}
	
	public static void main(String[] args) {
		///*
		System.setProperty("apple.laf.useScreenMenuBar", "true"); // for picky mac users
		System.setProperty("apple.awt.application.name", "Secres"); // mac header on mac menubar
		if(System.getProperty("os.name").toString().contains("mac")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				
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
			} catch (Exception e) { e.printStackTrace(); }
		}
		else {
			FlatLightLaf.install();
		}
		UIManager.put("ScrollBar.thumbArc", 999);
		UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
		UIManager.put("ScrollBar.width", 13);
		//*/
		SwingUtilities.invokeLater(() -> {
			new Main();
		});
	}

}
