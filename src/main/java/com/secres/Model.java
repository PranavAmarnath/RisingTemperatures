package com.secres;

import com.opencsv.*;

import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * The <code>Model</code> class defines all I/O from the CSV files.
 * <P>
 * Each instance of <code>Model</code> spawns a new {@link SwingWorker} for adding each new row to the current <code>JTable</code>'s <code>TableModel</code>
 * <P>
 * After starting and finishing ALL reads, the <code>Model</code> notifies {@link Main}.<br>
 * 1. After <i>starting</i> the last read, {@link Main} creates a new instance of {@link View}.<br>
 * 2. After <i>finishing</i> the last read, {@link Main} starts updating each <code>JFreeChart</code> for each <code>ChartPanel</code> that {@link View} created.
 * 
 * @author Pranav Amarnath
 *
 */
public class Model {

	/** Table model */
	private DefaultTableModel model;
	/** Table header */
	private Object[] header;
	//private List<String[]> myEntries = new ArrayList<>();
	/** OpenCSV parser */
	private CSVReader reader;
	/** Current line */
	private String[] line;
	/** Last dataset */
	private final String LASTDATASET = "/GlobalLandTemperaturesByCountry.csv";
	
	/**
	 * Model constructor
	 * @param path  Path to file
	 */
	public Model(String path) {
		new SwingWorker<Void, Object[]>() {
			protected Void doInBackground() {
				reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream(path)));
				try {
					header = (String[]) reader.readNext();
					//SwingUtilities.invokeAndWait(() -> model = new DefaultTableModel(header, 0)); // NOT invokeLater() because model HAS to be initialized immediately on EDT
					model = new DefaultTableModel(header, 0);
					if(path.equals(LASTDATASET)) { // Start read on final dataset so that there's only one View instance
						Main.verifyStartRead();
					}
					while((line = reader.readNext()) != null) {
						model.addRow(line);
				    }
				} catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void done() {
				try {
					//Main.getPB().setValue(Main.getPB().getValue() + 15);
					reader.close();
					if(path.equals(LASTDATASET)) { // final dataset
						Main.verifyReadFinished();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	/**
	 * Returns table model
	 * @return <code>DefaultTableModel</code> - table model
	 */
	public DefaultTableModel getModel() {
		return model;
	}
	
	/**
	 * Returns table header
	 * @return <code>Object[]</code> - header
	 */
	public Object[] getHeaders() {
		return header;
	}
	
}
