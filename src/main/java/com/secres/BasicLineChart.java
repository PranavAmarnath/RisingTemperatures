package com.secres;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * A class containing raw data points from the global dataset in a chart.
 * 
 * @author Pranav Amarnath
 *
 */
public class BasicLineChart extends AbstractGraph {

	private TimeSeriesCollection dataset;
	private JFreeChart chart;
	private ChartPanel panel;
	
	/**
	 * Basic line chart
	 * @return the <code>ChartPanel</code>
	 */
	@Override
	JPanel updateView() {
		dataset = new TimeSeriesCollection();

		chart = ChartFactory.createTimeSeriesChart("Land Temperature 1750-2015", "Date", "Temperature", dataset, true, true, false);
		chart.setNotify(false);
		
		XYPlot allLinePlot = (XYPlot) chart.getXYPlot();
		allLinePlot.setDomainPannable(true);
		allLinePlot.setRangePannable(true);
		
		allLinePlot.getRenderer().setDefaultToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("yyyy-MMM"), NumberFormat.getNumberInstance()));
		
		DateAxis axis = (DateAxis) allLinePlot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MMM"));

        panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);

		return panel;
	}
	
	/** Adds data to basic line chart */
	@Override
	void updateModel() {
		TimeSeries basicLine = new TimeSeries("Temperature over time");
		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i++) {
			if(!(((String)View.getGlobalTable().getModel().getValueAt(i, 1)).equals(""))) {
				basicLine.add(new Month(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(5, 7)), Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4))), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)));
			}
		}
		dataset.addSeries(basicLine);
		chart.setNotify(true);
	}
	
	/**
	 * Returns the dataset
	 * @return TimeSeriesCollection
	 */
	@Override
	public TimeSeriesCollection getDataset() {
		return dataset;
	}
	
	/**
	 * Returns the chart
	 * @return JFreeChart
	 */
	@Override
	public JFreeChart getChart() {
		return chart;
	}
	
	/**
	 * Returns the panel
	 * @return JPanel
	 */
	@Override
	public JPanel getPanel() {
		return panel;
	}
	
}
