package com.secres;

import com.opencsv.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class ReadData {

	private DefaultTableModel model;
	private Object[] header;
	List<String[]> myEntries;
	
	public ReadData() {
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() {
				CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("/MissingMigrants.csv")));
				try {
					myEntries = reader.readAll();
					header = (String[]) myEntries.get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			protected void done() {
				model = new DefaultTableModel(header, myEntries.size()-1);
				int rowCount = model.getRowCount();
				for (int i = 1; i < (rowCount+1); i++) {
				    int columnNumber = 0;
				    // if x = 0 this is the first row...skip it... data used for columnNames
				    for (String thiscellvalue : (String[])myEntries.get(i)) {
				    	model.setValueAt(thiscellvalue, i-1, columnNumber);
				    	columnNumber++;
				    }
				}
				View.showGUIAfterRead();
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
