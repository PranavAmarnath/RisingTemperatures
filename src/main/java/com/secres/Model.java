package com.secres;

import com.opencsv.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author Pranav Amarnath
 *
 */

public class Model {

	private DefaultTableModel model;
	private Object[] header;
	//private List<String[]> myEntries = new ArrayList<>();
	private CSVReader reader;
	private String[] line;
	private final String LASTDATASET = "/GlobalLandTemperaturesByCountry.csv";
	
	public Model(String path) {
		new SwingWorker<Void, Object[]>() {
			protected Void doInBackground() {
				reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream(path)));
				/*
				try {
					myEntries = reader.readAll();
					header = (String[]) myEntries.get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				*/
				try {
					header = (String[]) reader.readNext();
					SwingUtilities.invokeAndWait(() -> model = new DefaultTableModel(header, 0)); // NOT invokeLater() because model HAS to be initialized immediately on EDT
					if(path.equals(LASTDATASET)) { // Start read on final dataset so that there's only one View instance
						//System.out.println("Create View");
						Main.verifyStartRead();
					}
					//int count = 0;
					while((line = reader.readNext()) != null) {
				        //myEntries.add(line);
						//SwingUtilities.invokeLater(() -> model.addRow(line));
						/*
						try {
							synchronized(model) {
								model.addRow(line);
								//count++;
							}
						} catch(Exception e) {
							e.printStackTrace();
							//System.out.println(count);
						}
						*/
						model.addRow(line);
						//System.out.println(line.toString());
				        //model.fireTableDataChanged();
				    }
				} catch(Exception e) {
					e.printStackTrace();
				}
				/*
				try {
					int i = 0;
					header = (String[]) reader.readNext();
					model = new DefaultTableModel(header, 0);
					System.out.println(reader.readNext());
					while((line = reader.readNext()) != null) {
						myEntries.add(line);
						int columnNumber = 0;
						for (String thisCellValue : (String[])myEntries.get(i)) {
							//publish();
							model.addRow(line);
							//model.setValueAt(thisCellValue, i-1, columnNumber);
					    	columnNumber++;
					    }
						System.out.println(i);
						i++;
					}
				} catch (CsvValidationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				*/
				return null;
			}
			/*
			protected void process(String thisCellValue, int i, int columnNumber) {
		    	model.setValueAt(thisCellValue, i-1, columnNumber);
			}
			*/
			protected void done() {
				try {
					Main.getPB().setValue(Main.getPB().getValue() + 15);
					reader.close();
					//System.out.println("Finished model");
					/*
					if(path.equals("/GlobalTemperatures.csv")) {
						Main.modelCountry = new Model("/GlobalLandTemperaturesByCountry.csv");
					}
					*/
					if(path.equals(LASTDATASET)) { // final dataset
						//System.out.println("Going to update graphs");
						Main.verifyReadFinished();
						//Main.getSplash().dispose();
						//View.getFrame().setVisible(true);
						//JOptionPane.showMessageDialog(View.getFrame(), "Finished loading data");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				model = new DefaultTableModel(header, myEntries.size()-1);
				int rowCount = model.getRowCount();
				for (int i = 1; i < (rowCount+1); i++) {
				    int columnNumber = 0;
				    // if x = 0 this is the first row...skip it... data used for columnNames
				    for (String thiscellvalue : (String[])myEntries.get(i)) {
				    	model.setValueAt(thiscellvalue, i-1, columnNumber);
				    	columnNumber++;
				    }
				    System.out.println(i);
				}
				*/
			}
		}.execute();
	}
	
	public DefaultTableModel getModel() {
		return model;
	}
	
	public Object[] getHeaders() {
		return header;
	}
	
}
