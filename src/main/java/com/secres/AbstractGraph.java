package com.secres;

import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

/**
 * An abstract class that is the superclass of every chart class.
 * 
 * @author Pranav Amarnath
 *
 */
public abstract class AbstractGraph {
	
	JPanel updateView() {
		return null;
	}
	
	void updateModel() {
		
	}
	
	public Dataset getDataset() {
		return null;
	}
	
	public JFreeChart getChart() {
		return null;
	}
	
	public JPanel getPanel() {
		return null;
	}
	
}
