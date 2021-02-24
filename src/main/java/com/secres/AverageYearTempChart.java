package com.secres;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.Regression;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

/**
 * The Charts for Average Yearly Temperature - line & scatter.
 * 
 * @author Pranav Amarnath
 *
 */
public class AverageYearTempChart extends AbstractGraph {

	private YIntervalSeriesCollection datasetLineChartByYear = new YIntervalSeriesCollection();
	private XYSeriesCollection datasetScatterPlotByYear = new XYSeriesCollection();
	private JFreeChart chart1;
	private JFreeChart chart2;
	private ChartPanel panel1;
	private ChartPanel panel2;
	
	/**
	 * Average line chart
	 * @return the <code>JPanel</code>
	 */
	JPanel updateViewLine() {
		chart1 = ChartFactory.createTimeSeriesChart("Average Land Temperature 1750-2015", "Year", "Average Temperature \u00B0C", datasetLineChartByYear, true, true, false);
		chart1.setNotify(false);
		XYPlot allLinePlot = (XYPlot) chart1.getXYPlot();
		allLinePlot.setDomainPannable(true);
		allLinePlot.setRangePannable(true);
		
		DateAxis axis = (DateAxis) allLinePlot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy"));
        
        DeviationRenderer renderer = new DeviationRenderer(true, false);
        renderer.setSeriesFillPaint(0, new Color(127, 255, 255)); // Filled portion
        renderer.setSeriesStroke(0, new BasicStroke(1.75f));
        //renderer.setSeriesPaint(0, new Color(199, 121, 93));
        renderer.setAlpha(0.6f);
        renderer.setSeriesVisibleInLegend(1, false);
        renderer.setSeriesVisibleInLegend(2, false);
        renderer.setSeriesPaint(1, new Color(0, 255, 255)); // Series top (+)
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesPaint(2, new Color(0, 255, 255)); // Series bottom (-)
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("yyyy"), NumberFormat.getNumberInstance()));
        allLinePlot.setRenderer(renderer);
        
        Marker domainMarker = new IntervalMarker(new Year(1975).getFirstMillisecond(), new Year(2015).getFirstMillisecond());
        domainMarker.setAlpha(0.2f);
        domainMarker.setPaint(Color.RED);
        //domainMarker.setLabel("High Temperature Change");
        allLinePlot.addDomainMarker(domainMarker);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
		panel1 = new ChartPanel(chart1);
		panel1.setMouseWheelEnabled(true);
		mainPanel.add(panel1);
		
		JCheckBox showUncertainty = new JCheckBox("Show Uncertainty");
		showUncertainty.setSelected(true);
		showUncertainty.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					renderer.setSeriesVisible(1, true);
					renderer.setSeriesVisible(2, true);
					renderer.setAlpha(0.6f);
				}
				else {
					renderer.setSeriesVisible(1, false);
					renderer.setSeriesVisible(2, false);
					renderer.setAlpha(0.0f);
				}
			}
		});
		JCheckBox showInterval = new JCheckBox("Show Interval (Trends)");
		showInterval.setSelected(true);
		showInterval.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					domainMarker.setAlpha(0.2f);
				}
				else {
					domainMarker.setAlpha(0.0f);
				}
			}
		});
		JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		checkboxPanel.add(showUncertainty);
		checkboxPanel.add(showInterval);
		mainPanel.add(checkboxPanel, BorderLayout.SOUTH);

		return mainPanel;
	}
	
	/**
	 * Average scatter plot by year
	 * @return the <code>ChartPanel</code>
	 */
	JPanel updateViewScatter() {
		datasetScatterPlotByYear = new XYSeriesCollection();

		chart2 = ChartFactory.createScatterPlot("Average Land Temperature 1750-2015", "Year", "Average Temperature \u00B0C", datasetScatterPlotByYear, PlotOrientation.VERTICAL, true, true, false);
		chart2.setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = chart2.getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, true);
		r.setSeriesShapesVisible(1, false);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
		Marker marker1 = new IntervalMarker(1920, 1955);
		marker1.setAlpha(0.2f);
		marker1.setPaint(Color.RED);
        plot.addDomainMarker(marker1);
        
        Marker marker2 = new IntervalMarker(1885, 1914);
        marker2.setAlpha(0.2f);
        marker2.setPaint(Color.RED);
        plot.addDomainMarker(marker2);

        Marker marker3 = new IntervalMarker(1976, 1994);
        marker3.setAlpha(0.2f);
        marker3.setPaint(Color.RED);
        plot.addDomainMarker(marker3);

		panel2 = new ChartPanel(chart2);
		panel2.setMouseWheelEnabled(true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(panel2);
		
		JCheckBox showInterval = new JCheckBox("Show Interval");
		showInterval.setSelected(true);
		showInterval.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					marker1.setAlpha(0.2f);
					marker2.setAlpha(0.2f);
					marker3.setAlpha(0.2f);
				}
				else {
					marker1.setAlpha(0.0f);
					marker2.setAlpha(0.0f);
					marker3.setAlpha(0.0f);
				}
			}
		});
		JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		checkboxPanel.add(showInterval);
		mainPanel.add(checkboxPanel, BorderLayout.SOUTH);

		return mainPanel;
	}
	
	/** Updates the average line and scatter charts */
	void updateModel() {
		// Start Scatter Plot
		YIntervalSeries series1 = new YIntervalSeries("Average Temperature per year");
		XYSeries series2 = new XYSeries("Average Temperature per year"); // Init series
		YIntervalSeries uncertaintyPos = new YIntervalSeries("Average Temperature per year"); // Init series
		YIntervalSeries uncertaintyNeg = new YIntervalSeries("Average Temperature per year"); // Init series
		// End Scatter Plot
		double average = 0;
		double averageUncertainty = 0;
		int count = 0;
		XYPlot plot = chart2.getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();

		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			for(int j = 0; j < 12; j++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i+j, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i+j, 1)).trim().isEmpty()) {
					count++;
					average = average + Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i+j, 1));
					averageUncertainty = averageUncertainty + Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i+j, 2));
				}
			}
			average = average/count;
			averageUncertainty = averageUncertainty/count;
			// I do the substring(0, 4) because each year will be 1750-01-01, 1751-01-01 etc. -> to shorten to 1750, 1751 etc.
			series1.add((new Year(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)))).getFirstMillisecond(), average, average - averageUncertainty, average + averageUncertainty);
			// Start Scatter Plot
			series2.add(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)), average); // add elements
			// End Scatter Plot
			uncertaintyPos.add((new Year(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)))).getFirstMillisecond(), average + averageUncertainty, average + averageUncertainty, average + averageUncertainty);
			uncertaintyNeg.add((new Year(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)))).getFirstMillisecond(), average - averageUncertainty, average - averageUncertainty, average - averageUncertainty);
			average = 0;
			count = 0;
		}

		// Start Scatter Plot
		((YIntervalSeriesCollection) datasetLineChartByYear).addSeries(series1); // add series to dataset
		((YIntervalSeriesCollection) datasetLineChartByYear).addSeries(uncertaintyPos); // add series to dataset
		((YIntervalSeriesCollection) datasetLineChartByYear).addSeries(uncertaintyNeg); // add series to dataset
		((XYSeriesCollection) datasetScatterPlotByYear).addSeries(series2); // add series to dataset

		// Begin trend display
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetScatterPlotByYear, 0);
		double b = coefficients[0]; // intercept
		double m = coefficients[1]; // slope
		XYSeries trend = new XYSeries("Trend");
		double x = series2.getDataItem(0).getXValue();
		trend.add(x, m * x + b);
		//System.out.println("Slope: " + m + " X: " + x + " B: " + b);
		x = series2.getDataItem(series2.getItemCount() - 1).getXValue();
		trend.add(x, m * x + b);
		r.setSeriesStroke(1, new BasicStroke(1.5f));
		r.setSeriesPaint(1, Color.BLACK);
		r.setSeriesVisibleInLegend(1, false, true);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		datasetScatterPlotByYear.addSeries(trend);
		// End trend display
		
		// End Scatter Plot
		chart1.setNotify(true);
		chart2.setNotify(true);
	}
	
	public YIntervalSeriesCollection getDatasetLine() {
		return datasetLineChartByYear;
	}
	
	public XYSeriesCollection getDatasetScatter() {
		return datasetScatterPlotByYear;
	}
	
}
