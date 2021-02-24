package com.secres;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A class containing an interactive panel with buttons to choose for different charts with one month representing each season.
 * 
 * @author Pranav Amarnath
 *
 */
public class SeasonsChart extends AbstractGraph {

	private JPanel seasonsPanel = new JPanel(new CardLayout());
	private XYSeriesCollection datasetMar, datasetJun, datasetSep, datasetDec;
	private JFreeChart[] charts = new JFreeChart[4];
	private ChartPanel[] chartPanels = new ChartPanel[4];
	
	/**
	 * Home page for Seasons Charts
	 * @return Home Page <code>JPanel</code>
	 */
	@Override
	JPanel updateView() {
		final String DECEMBERPLOT = "December Plot";
		final String JUNEPLOT = "June Plot";
		final String MARPLOT = "March Plot";
		final String SEPPLOT = "September Plot";
		final String HOMEPAGE = "Home Page";
		JPanel homePage = new JPanel(new GridLayout(2, 2));
		seasonsPanel.add(basicScatterPlotCoolingSeason("March", 2), MARPLOT);
		seasonsPanel.add(basicScatterPlotCoolingSeason("June", 1), JUNEPLOT);
		seasonsPanel.add(basicScatterPlotCoolingSeason("September", 3), SEPPLOT);
		seasonsPanel.add(basicScatterPlotCoolingSeason("December", 0), DECEMBERPLOT);
		
		JButton december = new JButton("December");
		december.addActionListener(e -> {
			CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
		    cl.show(seasonsPanel, DECEMBERPLOT);
		});
		JButton june = new JButton("June");
		june.addActionListener(e -> {
			CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
		    cl.show(seasonsPanel, JUNEPLOT);
		});
		JButton march = new JButton("March");
		march.addActionListener(e -> {
			CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
		    cl.show(seasonsPanel, MARPLOT);
		});
		JButton september = new JButton("September");
		september.addActionListener(e -> {
			CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
		    cl.show(seasonsPanel, SEPPLOT);
		});
		homePage.add(march);
		homePage.add(june);
		homePage.add(september);
		homePage.add(december);
		
		seasonsPanel.add(homePage, HOMEPAGE);
		CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
	    cl.show(seasonsPanel, HOMEPAGE);
		
		return seasonsPanel;
	}
	
	/**
	 * Basic scatter plot month
	 * @param month  the name of the month
	 * @param index  the index in the <code>charts</code> array and <code>chartPanels</code> array
	 * @return the <code>JPanel</code>
	 */
	private JPanel basicScatterPlotCoolingSeason(String month, int index) {
		if(index == 0) {
			datasetDec = new XYSeriesCollection();
			charts[index] = ChartFactory.createScatterPlot(month + " Temperature 1750-2015", "Year", "Temperature \u00B0C", datasetDec, PlotOrientation.VERTICAL, true, true, false);
		}
		else if(index == 1) {
			datasetJun = new XYSeriesCollection();
			charts[index] = ChartFactory.createScatterPlot(month + " Temperature 1750-2015", "Year", "Temperature \u00B0C", datasetJun, PlotOrientation.VERTICAL, true, true, false);
		}
		else if(index == 2) {
			datasetMar = new XYSeriesCollection();
			charts[index] = ChartFactory.createScatterPlot(month + " Temperature 1750-2015", "Year", "Temperature \u00B0C", datasetMar, PlotOrientation.VERTICAL, true, true, false);
		}
		else if(index == 3) {
			datasetSep = new XYSeriesCollection();
			charts[index] = ChartFactory.createScatterPlot(month + " Temperature 1750-2015", "Year", "Temperature \u00B0C", datasetSep, PlotOrientation.VERTICAL, true, true, false);
		}

		charts[index].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[index].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, true);
		r.setSeriesShapesVisible(1, false);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		chartPanels[index] = new ChartPanel(charts[index]);
		chartPanels[index].setMouseWheelEnabled(true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(chartPanels[index]);
		
		JPanel returnHomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton previous = new JButton("<< Back");
		returnHomePanel.add(previous);
		previous.addActionListener(e -> {
			CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
			cl.previous(seasonsPanel);
		});
		JButton returnHome = new JButton("Home");
		returnHomePanel.add(returnHome);
		returnHome.addActionListener(e -> {
			CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
		    cl.show(seasonsPanel, "Home Page");
		});
		JButton next = new JButton("Next >>");
		returnHomePanel.add(next);
		next.addActionListener(e -> {
			CardLayout cl = (CardLayout)(seasonsPanel.getLayout());
			cl.next(seasonsPanel);
		});
		mainPanel.add(returnHomePanel, BorderLayout.SOUTH);

		return mainPanel;
	}
	
	/** @return <code>XYSeriesCollection</code> */
	public XYSeriesCollection getMarchSeries() {
		return datasetMar;
	}
	
	/** @return <code>XYSeriesCollection</code> */
	public XYSeriesCollection getJuneSeries() {
		return datasetJun;
	}
	
	/** @return <code>XYSeriesCollection</code> */
	public XYSeriesCollection getSeptemberSeries() {
		return datasetSep;
	}
	
	/** @return <code>XYSeriesCollection</code> */
	public XYSeriesCollection getDecemberSeries() {
		return datasetDec;
	}
	
	/**
	 * Adds data to scatter plot for a month
	 * @param dataset  the dataset
	 * @param startIndex  the month index (0 - 11)
	 * @param month  the name of the month
	 * @param index  the index in the <code>charts</code> array and <code>chartPanels</code> array
	 */
	void updateModel(XYSeriesCollection dataset, int startIndex, String month, int index) {
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in " + month + " per year"); // Init series
		XYPlot plot = charts[index].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		// End Scatter Plot

		for(int i = startIndex; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				series.add(Double.parseDouble(((String) View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0,4)), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1))); // add elements
			}
		}

		// Start Scatter Plot
		((XYSeriesCollection) dataset).addSeries(series); // add series to dataset

		// Begin trend display/
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(dataset, 0);
		double b = coefficients[0]; // intercept
		double m = coefficients[1]; // slope
		XYSeries trend = new XYSeries("Trend");
		double x = series.getDataItem(0).getXValue();
		trend.add(x, m * x + b);
		x = series.getDataItem(series.getItemCount() - 1).getXValue();
		trend.add(x, m * x + b);
		r.setSeriesStroke(1, new BasicStroke(1.5f));
		r.setSeriesPaint(1, Color.BLACK);
		r.setSeriesVisibleInLegend(1, false, true);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		dataset.addSeries(trend);
		// End trend display
		// End Scatter Plot
		charts[index].setNotify(true);
	}
	
	/**
	 * Returns the chart
	 * @return JFreeChart
	 */
	public JFreeChart[] getCharts() {
		return charts;
	}
	
	/**
	 * Returns the panel
	 * @return JPanel
	 */
	@Override
	public JPanel getPanel() {
		return seasonsPanel;
	}
	
}
