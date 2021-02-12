package com.secres;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.DefaultShadowGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.statistics.Regression;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * A Utility class  (i.e. a class that is not meant to have instances)<br>
 * <P>
 * <code>GraphCharts</code> contains static methods for:<br>
 * 1. Creating the View of a <code>JFreeChart</code><br>
 * 2. Creating the Model of the data for that <code>JFreeChart</code><br>
 * 3. Handling all other visualization from the <code>JFreeChart</code>
 * 
 * @author Pranav Amarnath
 *
 */
public class GraphCharts {

	private static TimeSeriesCollection datasetBasicLineChart;
	private static YIntervalSeriesCollection datasetBasicLineChartByYear;
	private static XYSeriesCollection datasetBasicScatterPlotByYear, datasetBasicScatterPlotCoolingDec, datasetBasicScatterPlotCoolingJun;
	private static XYSeriesCollection datasetMultiLineChartByEconomy;
	private static DefaultCategoryDataset datasetBasicBarChartByCountry, datasetDoubleBarChartByCountryGreatest, datasetDoubleBarChartByCountryLeast;
	private static JFreeChart[] charts = new JFreeChart[10];
	private static ChartPanel[] chartPanels = new ChartPanel[10];
	
	/**
	 * Basic line chart
	 * @return the <code>ChartPanel</code>
	 */
	static ChartPanel basicLineChart() {
		datasetBasicLineChart = new TimeSeriesCollection();

		charts[0] = ChartFactory.createTimeSeriesChart("Land Temperature 1750-2015", "Date", "Temperature", datasetBasicLineChart, true, true, false);
		charts[0].setNotify(false);
		
		XYPlot allLinePlot = (XYPlot) charts[0].getXYPlot();
		allLinePlot.setDomainPannable(true);
		allLinePlot.setRangePannable(true);
		
		allLinePlot.getRenderer().setDefaultToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("yyyy-MMM"), NumberFormat.getNumberInstance()));
		
		DateAxis axis = (DateAxis) allLinePlot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MMM"));

        chartPanels[0] = new ChartPanel(charts[0]);
        chartPanels[0].setMouseWheelEnabled(true);

		return chartPanels[0];
	}

	/** Adds data to basic line chart */
	static void updateDataBasicLineChart() {
		TimeSeries basicLine = new TimeSeries("Temperature over time");
		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i++) {
			if(!(((String)View.getGlobalTable().getModel().getValueAt(i, 1)).equals(""))) {
				basicLine.add(new Month(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(5, 7)), Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4))), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)));
			}
		}
		datasetBasicLineChart.addSeries(basicLine);
		charts[0].setNotify(true);
	}

	/**
	 * Average line chart
	 * @return the <code>JPanel</code>
	 */
	static JPanel basicLineChartByYear() {
		datasetBasicLineChartByYear = new YIntervalSeriesCollection();

		charts[1] = ChartFactory.createTimeSeriesChart("Average Land Temperature 1750-2015", "Year", "Average Temperature \u00B0C", datasetBasicLineChartByYear, true, true, false);
		charts[1].setNotify(false);
		XYPlot allLinePlot = (XYPlot) charts[1].getXYPlot();
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
        
		chartPanels[1] = new ChartPanel(charts[1]);
		chartPanels[1].setMouseWheelEnabled(true);
		mainPanel.add(chartPanels[1]);
		
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
		JCheckBox showInterval = new JCheckBox("Show Interval");
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
		checkboxPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		checkboxPanel.add(showUncertainty);
		checkboxPanel.add(showInterval);
		checkboxPanel.setBackground(Color.WHITE);
		showUncertainty.setBackground(Color.WHITE);
		showInterval.setBackground(Color.WHITE);
		mainPanel.add(checkboxPanel, BorderLayout.SOUTH);

		return mainPanel;
	}

	/** Updates the average line chart */
	static void updateDataBasicChartByYear() {
		// Start Scatter Plot
		YIntervalSeries series1 = new YIntervalSeries("Average Temperature per year");
		XYSeries series2 = new XYSeries("Average Temperature per year"); // Init series
		YIntervalSeries uncertaintyPos = new YIntervalSeries("Average Temperature per year"); // Init series
		YIntervalSeries uncertaintyNeg = new YIntervalSeries("Average Temperature per year"); // Init series
		// End Scatter Plot
		double average = 0;
		double averageUncertainty = 0;
		int count = 0;
		XYPlot plot = charts[2].getXYPlot();
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
		((YIntervalSeriesCollection) datasetBasicLineChartByYear).addSeries(series1); // add series to dataset
		((YIntervalSeriesCollection) datasetBasicLineChartByYear).addSeries(uncertaintyPos); // add series to dataset
		((YIntervalSeriesCollection) datasetBasicLineChartByYear).addSeries(uncertaintyNeg); // add series to dataset
		((XYSeriesCollection) datasetBasicScatterPlotByYear).addSeries(series2); // add series to dataset

		// Begin trend display
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotByYear, 0);
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
		datasetBasicScatterPlotByYear.addSeries(trend);
		// End trend display
		// End Scatter Plot
		charts[1].setNotify(true);
		charts[2].setNotify(true);
	}

	/**
	 * Average scatter plot by year
	 * @return the <code>ChartPanel</code>
	 */
	static ChartPanel basicScatterPlotByYear() {
		datasetBasicScatterPlotByYear = new XYSeriesCollection();

		charts[2] = ChartFactory.createScatterPlot("Average Land Temperature 1750-2015", "Year", "Average Temperature \u00B0C", datasetBasicScatterPlotByYear, PlotOrientation.VERTICAL, true, true, false);
		charts[2].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[2].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, true);
		r.setSeriesShapesVisible(1, false);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		chartPanels[2] = new ChartPanel(charts[2]);
		chartPanels[2].setMouseWheelEnabled(true);

		return chartPanels[2];
	}
	
	/**
	 * Basic scatter plot December
	 * @return the <code>ChartPanel</code>
	 */
	static ChartPanel basicScatterPlotCoolingDec() {
		datasetBasicScatterPlotCoolingDec = new XYSeriesCollection();

		charts[3] = ChartFactory.createScatterPlot("December Temperature 1750-2015", "Year", "Temperature \u00B0C", datasetBasicScatterPlotCoolingDec, PlotOrientation.VERTICAL, true, true, false);
		charts[3].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[3].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, true);
		r.setSeriesShapesVisible(1, false);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		chartPanels[3] = new ChartPanel(charts[3]);
		chartPanels[3].setMouseWheelEnabled(true);

		return chartPanels[3];
	}
	
	/** Adds data to scatter plot December */
	static void updateScatterPlotCoolingDec() {
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in December per year"); // Init series
		XYPlot plot = charts[3].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		// End Scatter Plot

		for(int i = 11; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				series.add(Double.parseDouble(((String) View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0,4)), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1))); // add elements
			}
		}

		// Start Scatter Plot
		((XYSeriesCollection) datasetBasicScatterPlotCoolingDec).addSeries(series); // add series to dataset

		// Begin trend display/
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotCoolingDec, 0);
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
		datasetBasicScatterPlotCoolingDec.addSeries(trend);
		// End trend display
		// End Scatter Plot
		charts[3].setNotify(true);
	}
	
	/**
	 * Basic scatter plot June
	 * @return the <code>ChartPanel</code>
	 */
	static ChartPanel basicScatterPlotCoolingJun() {
		datasetBasicScatterPlotCoolingJun = new XYSeriesCollection();

		charts[4] = ChartFactory.createScatterPlot("June Temperature 1750-2015", "Year", "Temperature \u00B0C", datasetBasicScatterPlotCoolingJun, PlotOrientation.VERTICAL, true, true, false);
		charts[4].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[4].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, true);
		r.setSeriesShapesVisible(1, false);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		chartPanels[4] = new ChartPanel(charts[4]);
		chartPanels[4].setMouseWheelEnabled(true);

		return chartPanels[4];
	}
	
	/** Adds data to scatter plot June */
	static void updateScatterPlotCoolingJun() {
		XYPlot plot = charts[4].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in June per year"); // Init series

		for(int i = 5; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				series.add(Double.parseDouble(((String) View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0,4)), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1))); // add elements
			}
		}

		// Start Scatter Plot
		((XYSeriesCollection) datasetBasicScatterPlotCoolingJun).addSeries(series); // add series to dataset

		// Begin trend display
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotCoolingJun, 0);
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
		datasetBasicScatterPlotCoolingJun.addSeries(trend);
		// End trend display
		// End Scatter Plot
		charts[4].setNotify(true);
	}
	
	/**
	 * Average temperature per country bar chart with scroll bar
	 * @return the <code>JPanel</code>
	 */
	static JPanel basicBarChartByCountry() {
		final int NUM_COUNTRIES = 242; // see line 369 (subject to change)
		JScrollBar scroller = new JScrollBar(SwingConstants.HORIZONTAL, 0, 10, 0, NUM_COUNTRIES);
		
		datasetBasicBarChartByCountry = new DefaultCategoryDataset();
		SlidingCategoryDataset dataset = new SlidingCategoryDataset(datasetBasicBarChartByCountry, 0, 10);
		
		charts[5] = ChartFactory.createBarChart("Countries with Highest Overall Temperatures", "Country", "Average Temperature \u00B0C", dataset, PlotOrientation.VERTICAL, true, true, false);
		charts[5].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = charts[5].getCategoryPlot();
		plot.setRangePannable(true);
		
		BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
		/** Reference: @see https://www.jfree.org/forum/viewtopic.php?t=30541 */
		barRenderer.setBarPainter(new StandardBarPainter()); // Flat look - RGB Red: (255, 85, 85)
		
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // Rotates axis labels to 45 degrees
        
		JPanel scrollPanel = new JPanel(new BorderLayout());
		scroller.putClientProperty("JScrollBar.showButtons", true);
		scrollPanel.add(scroller);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 2, 5));
		scrollPanel.setBackground(Color.WHITE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPanel, BorderLayout.SOUTH);
		
		scroller.getModel().addChangeListener(e -> {
			dataset.setFirstCategoryIndex(scroller.getValue());
			// Obtained auto-range Range axis value using {System.out.println(charts[5].getCategoryPlot().getRangeAxis().getUpperBound());}
			//rangeAxis.setRange(0.0, 30.25743265983117); // Update so that user can see bars visibly decreasing
			scroller.repaint(); // removes scroll bar paint artifacts
		});
		
        // Set original Range axis range
		//rangeAxis.setRange(0.0, 30.25743265983117);

		CategoryItemRenderer renderer = plot.getRenderer();
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
		renderer.setDefaultItemLabelGenerator(generator);
		//renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); // just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		// Default item label is above the bar. Use below method for inside bar.
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));

		chartPanels[5] = new ChartPanel(charts[5]);
		chartPanels[5].setMouseWheelEnabled(true);
		mainPanel.add(chartPanels[5]);

		return mainPanel;
	}
	
	/** Adds data to the average temperature by country bar chart */
	static void updateBasicBarChartByCountry() {
		Map<String, List<Double>> entries = new LinkedHashMap<>();
		final int NUM_COUNTRIES = 242; // see line 318 (subject to change)

		// Iterates over whole data 1 time
		for(int i = 0; i < View.getCountryTable().getModel().getRowCount(); i++) {
			if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				if(entries.containsKey((String)View.getCountryTable().getModel().getValueAt(i, 3))) {
					List<Double> list = entries.get((String)View.getCountryTable().getModel().getValueAt(i, 3));
					list.set(0, list.get(0) + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1)));
					list.set(1, list.get(1) + 1.0);
					entries.put((String)View.getCountryTable().getModel().getValueAt(i, 3), list);
				}
				else {
					List<Double> list = new ArrayList<>();
					list.add(0, Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1)));
					list.add(1, 1.0);
					entries.put((String)View.getCountryTable().getModel().getValueAt(i, 3), list);
				}
			}
		}
		// Calculate averages and place into HashMap
		Map<String, Double> mapAvg = new HashMap<>();
		for(int i = 0; i < entries.size(); i++) {
			String country = (String)entries.keySet().toArray()[i];
			// Check if the Country is in the List to avoid NullPointerException
			if(entries.containsKey(country)) {
				mapAvg.put(country, (entries.get(country).get(0)/entries.get(country).get(1)));
			}
		}
		final Map<String, Double> entriesAvgSorted = sortByValueDescending(mapAvg);
		
		for(int i = 0; i < NUM_COUNTRIES; i++) { // for sortByValueDescending()
			if(((String)entriesAvgSorted.keySet().toArray()[i]).length() > 10) {
				datasetBasicBarChartByCountry.addValue(entriesAvgSorted.get((String)entriesAvgSorted.keySet().toArray()[i]), "Average temperature", ((String)entriesAvgSorted.keySet().toArray()[i]).substring(0, 9) + "...");
			}
			else {	
				datasetBasicBarChartByCountry.addValue(entriesAvgSorted.get((String)entriesAvgSorted.keySet().toArray()[i]), "Average temperature", (String)entriesAvgSorted.keySet().toArray()[i]);
			}
		}
		charts[5].setNotify(true);
		chartPanels[5].restoreAutoBounds(); // Auto-ranges axes
	}
	
	/** Reference: @see https://stackoverflow.com/a/23846961/13772184 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
		map = map.entrySet().stream()
			       .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(
			    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return map;
    }
	
	/** Reference: @see https://stackoverflow.com/a/2581754/13772184 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		map = map.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(
			    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return map;
    }
	
	/**
	 * Double bar chart organized by greatest difference in temperature from 1912-2012
	 * @return the <code>ChartPanel</code>
	 */
	static JPanel doubleBarChartByCountryGreatest() {
		final int NUM_COUNTRIES = 240;
		JScrollBar scroller = new JScrollBar(SwingConstants.VERTICAL, 0, 10, 0, NUM_COUNTRIES);
		
		datasetDoubleBarChartByCountryGreatest = new DefaultCategoryDataset();
		SlidingCategoryDataset dataset = new SlidingCategoryDataset(datasetDoubleBarChartByCountryGreatest, 0, 10);
		
		JPanel scrollPanel = new JPanel(new BorderLayout());
		scroller.putClientProperty("JScrollBar.showButtons", true);
		scrollPanel.add(scroller);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(60, 2, 60, 2));
		scrollPanel.setBackground(Color.WHITE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPanel, BorderLayout.EAST);
		
		scroller.getModel().addChangeListener(e -> {
			dataset.setFirstCategoryIndex(scroller.getValue());
			scroller.repaint(); // removes scroll bar paint artifacts
		});

		charts[6] = ChartFactory.createBarChart("Countries with Highest Net Change In Temperature (1912-2012)", "Country", "Average Temperature \u00B0C", dataset, PlotOrientation.HORIZONTAL, true, true, false);
		charts[6].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = charts[6].getCategoryPlot();
		plot.setRangePannable(true);
		// Moves the range axis (temperature) to the bottom
		ValueAxis rangeAxis = plot.getRangeAxis();
		plot.setRangeAxis(0, rangeAxis);
		plot.mapDatasetToRangeAxis(0, 0);
		plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		// Default item label is above the bar. Use below method for inside bar.
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));
		
		BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
		barRenderer.setItemMargin(0.0); // Space between bars in same category
		barRenderer.setBarPainter(new StandardBarPainter()); // Flat look - RGB Red: (255, 85, 85)

		chartPanels[6] = new ChartPanel(charts[6]);
		chartPanels[6].setMouseWheelEnabled(true);
		mainPanel.add(chartPanels[6]);

		return mainPanel;
	}
	
	/**
	 * Double bar chart organized by least difference in temperature from 1912-2012
	 * @return the <code>ChartPanel</code>
	 */
	static JPanel doubleBarChartByCountryLeast() {
		final int NUM_COUNTRIES = 240;
		JScrollBar scroller = new JScrollBar(SwingConstants.VERTICAL, 0, 10, 0, NUM_COUNTRIES);
		
		datasetDoubleBarChartByCountryLeast = new DefaultCategoryDataset();
		SlidingCategoryDataset dataset = new SlidingCategoryDataset(datasetDoubleBarChartByCountryLeast, 0, 10);
		
		JPanel scrollPanel = new JPanel(new BorderLayout());
		scroller.putClientProperty("JScrollBar.showButtons", true);
		scrollPanel.add(scroller);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(60, 2, 60, 2));
		scrollPanel.setBackground(Color.WHITE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPanel, BorderLayout.EAST);
		
		scroller.getModel().addChangeListener(e -> {
			dataset.setFirstCategoryIndex(scroller.getValue());
			scroller.repaint(); // removes scroll bar paint artifacts
		});

		charts[7] = ChartFactory.createBarChart("Countries with Least Net Change In Temperature (1912-2012)", "Country", "Average Temperature \u00B0C", dataset, PlotOrientation.HORIZONTAL, true, true, false);
		charts[7].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = charts[7].getCategoryPlot();
		plot.setRangePannable(true);
		// Moves the range axis (temperature) to the bottom
		ValueAxis rangeAxis = plot.getRangeAxis();
		plot.setRangeAxis(0, rangeAxis);
		plot.mapDatasetToRangeAxis(0, 0);
		plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		
		BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
		barRenderer.setItemMargin(0.0); // Space between bars in same category
		barRenderer.setBarPainter(new StandardBarPainter()); // Flat look - RGB Red: (255, 85, 85)
		
		// Default item label is above the bar. Use below method for inside bar.
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));

		chartPanels[7] = new ChartPanel(charts[7]);
		chartPanels[7].setMouseWheelEnabled(true);
		mainPanel.add(chartPanels[7]);

		return mainPanel;
	}
	
	/** Adds data to both of the double bar charts by country organized by difference */
	static void updateDoubleBarChartByCountry() {
		final int NUM_COUNTRIES = 240;
		
		Map<String, List<Double>> entriesAvg = new LinkedHashMap<>(); // 1912
		Map<String, List<Double>> entriesAvgSecond = new LinkedHashMap<>(); // 2012
		Map<String, Double> entriesAvgSorted = new LinkedHashMap<>(); // 1912 sorted by Greatest to Least
		Map<String, Double> entriesAvgSecondSorted = new LinkedHashMap<>(); // 2012 sorted by Greatest to Least
		Map<String, Double> entriesDifferences = new LinkedHashMap<>(); // Differences 1912-2012 per country sorted by Greatest to Least
		Map<String, Double> entriesAvgLeast = new LinkedHashMap<>(); // 1912 sorted by Least to Greatest
		Map<String, Double> entriesAvgSecondLeast = new LinkedHashMap<>(); // 2012 sorted by Least to Greatest
		Map<String, Double> entriesDifferencesLeast = new LinkedHashMap<>(); // Differences 1912-2012 per country sorted by Least to Greatest
		
		for(int i = 0; i < View.getCountryTable().getModel().getRowCount(); i++) {
			if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				if((((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)).equals("1912")) {
					if(entriesAvg.containsKey((String)View.getCountryTable().getModel().getValueAt(i, 3))) {
						List<Double> list = entriesAvg.get((String)View.getCountryTable().getModel().getValueAt(i, 3));
						list.set(0, list.get(0) + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1)));
						list.set(1, list.get(1) + 1.0);
						entriesAvg.put((String)View.getCountryTable().getModel().getValueAt(i, 3), list);
					}
					else {
						List<Double> list = new ArrayList<>();
						list.add(0, Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1)));
						list.add(1, 1.0);
						entriesAvg.put((String)View.getCountryTable().getModel().getValueAt(i, 3), list);
					}
				}
				else if((((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)).equals("2012")) {
					if(entriesAvgSecond.containsKey((String)View.getCountryTable().getModel().getValueAt(i, 3))) {
						List<Double> list = entriesAvgSecond.get((String)View.getCountryTable().getModel().getValueAt(i, 3));
						list.set(0, list.get(0) + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1)));
						list.set(1, list.get(1) + 1.0);
						entriesAvgSecond.put((String)View.getCountryTable().getModel().getValueAt(i, 3), list);
					}
					else {
						List<Double> list = new ArrayList<>();
						list.add(0, Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1)));
						list.add(1, 1.0);
						entriesAvgSecond.put((String)View.getCountryTable().getModel().getValueAt(i, 3), list);
					}
				}
			}
		}
		
		// Calculate averages and place into HashMap
		Map<String, Double> mapAvg = new HashMap<>();
		for(int i = 0; i < entriesAvg.size(); i++) {
			String country = (String)entriesAvg.keySet().toArray()[i];
			// Check if the Country is in the List to avoid NullPointerException
			if(entriesAvg.containsKey(country)) {
				// Some extra checks
				if((entriesAvg.get(country).get(0))/(entriesAvg.get(country).get(1)) < 100) {
					mapAvg.put(country, (entriesAvg.get(country).get(0))/(entriesAvg.get(country).get(1)));
				}
			}
		}
		
		// Repeat for 2012
		Map<String, Double> mapAvgSecond = new HashMap<>();
		for(int i = 0; i < entriesAvgSecond.size(); i++) {
			String country = (String)entriesAvgSecond.keySet().toArray()[i];
			// Check if the Country is in the List to avoid NullPointerException
			if(entriesAvgSecond.containsKey(country)) {
				// Some extra checks
				if((entriesAvgSecond.get(country).get(0))/(entriesAvgSecond.get(country).get(1)) < 100) {
					mapAvgSecond.put(country, (entriesAvgSecond.get(country).get(0))/(entriesAvgSecond.get(country).get(1)));
				}
			}
		}
		
		// Subtract to get change in temperature
		for(int i = 0; i < mapAvg.size(); i++) {
			if((mapAvg.get((String)mapAvg.keySet().toArray()[i])) < 100) {
				entriesDifferences.put((String)mapAvg.keySet().toArray()[i], mapAvgSecond.get((String)mapAvg.keySet().toArray()[i]) - mapAvg.get((String)mapAvg.keySet().toArray()[i]));
			}
		}
		entriesDifferencesLeast = entriesDifferences;
		entriesAvgLeast = mapAvg;
		entriesAvgSecondLeast = mapAvgSecond;
		
		entriesAvgSorted = sortByValueDescending(mapAvg);
		entriesAvgSecondSorted = sortByValueDescending(mapAvgSecond);
		entriesDifferences = sortByValueDescending(entriesDifferences);
		
		entriesAvgLeast = sortByValue(entriesAvgLeast);
		entriesAvgSecondLeast = sortByValue(entriesAvgSecondLeast);
		entriesDifferencesLeast = sortByValue(entriesDifferencesLeast);
		
		for(int i = 0; i < NUM_COUNTRIES; i++) {
			if(((String)entriesDifferences.keySet().toArray()[i]).length() > 10) {
				datasetDoubleBarChartByCountryGreatest.addValue(entriesAvgSorted.get((String)entriesDifferences.keySet().toArray()[i]), "1912", ((String)entriesDifferences.keySet().toArray()[i]).substring(0, 9) + "...");
				datasetDoubleBarChartByCountryGreatest.addValue(entriesAvgSecondSorted.get((String)entriesDifferences.keySet().toArray()[i]), "2012", ((String)entriesDifferences.keySet().toArray()[i]).substring(0, 9) + "...");
			}
			else {
				datasetDoubleBarChartByCountryGreatest.addValue(entriesAvgSorted.get((String)entriesDifferences.keySet().toArray()[i]), "1912", (String)entriesDifferences.keySet().toArray()[i]);
				datasetDoubleBarChartByCountryGreatest.addValue(entriesAvgSecondSorted.get((String)entriesDifferences.keySet().toArray()[i]), "2012", (String)entriesDifferences.keySet().toArray()[i]);
			}
		}
		for(int i = 0; i < NUM_COUNTRIES; i++) {
			if(((String)entriesDifferencesLeast.keySet().toArray()[i]).length() > 10) {
				datasetDoubleBarChartByCountryLeast.addValue(entriesAvgLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "1912", ((String)entriesDifferencesLeast.keySet().toArray()[i]).substring(0, 9) + "...");
				datasetDoubleBarChartByCountryLeast.addValue(entriesAvgSecondLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "2012", ((String)entriesDifferencesLeast.keySet().toArray()[i]).substring(0, 9) + "...");
			}
			else {
				datasetDoubleBarChartByCountryLeast.addValue(entriesAvgLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "1912", (String)entriesDifferencesLeast.keySet().toArray()[i]);
				datasetDoubleBarChartByCountryLeast.addValue(entriesAvgSecondLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "2012", (String)entriesDifferencesLeast.keySet().toArray()[i]);
			}
		}
		charts[6].setNotify(true);
		charts[7].setNotify(true);
		chartPanels[6].restoreAutoBounds(); // Auto-ranges axes
		chartPanels[7].restoreAutoBounds(); // Auto-ranges axes
		// Switch Red and Blue to make it seem like 1912 was cold as blue and 2012 was warm as red
		/*
		CategoryPlot plot = charts[6].getCategoryPlot();
		plot.getRenderer().setSeriesPaint(0, new Color(85, 85, 255));
		plot.getRenderer().setSeriesPaint(1, new Color(255, 85, 85));
		CategoryPlot plot2 = charts[7].getCategoryPlot();
		plot2.getRenderer().setSeriesPaint(0, new Color(85, 85, 255));
		plot2.getRenderer().setSeriesPaint(1, new Color(255, 85, 85));
		*/
	}
	
	/**
	 * Multi-series line chart for Top 5 economies
	 * @return the <code>JPanel</code>
	 */
	static JPanel multiXYLineChartByEconomy() {
		datasetMultiLineChartByEconomy = new XYSeriesCollection();

		charts[8] = ChartFactory.createXYLineChart("Average Temperatures of Top 5 World Economies (1900-2012)", "Year", "Average Temperature \u00B0C", datasetMultiLineChartByEconomy, PlotOrientation.VERTICAL, true, true, false);
		charts[8].setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[8].getXYPlot();
		//plot.setShadowGenerator(new DefaultShadowGenerator());
		//plot.setDomainGridlinesVisible(false);
		//plot.setRangeGridlinesVisible(false);
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, true);
		r.setSeriesShapesVisible(1, false);
		r.setDefaultOutlineStroke(new BasicStroke(2.0f));
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		chartPanels[8] = new ChartPanel(charts[8]);
		chartPanels[8].setMouseWheelEnabled(true);

        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        Crosshair xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f));
        xCrosshair.setLabelVisible(true);
        Crosshair yCrosshair0 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f));
        yCrosshair0.setLabelVisible(true);
        Crosshair yCrosshair1 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f));
        yCrosshair1.setLabelVisible(true);
        Crosshair yCrosshair2 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f));
        yCrosshair2.setLabelVisible(true);
        Crosshair yCrosshair3 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f));
        yCrosshair3.setLabelVisible(true);
        Crosshair yCrosshair4 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(1f));
        yCrosshair4.setLabelVisible(true);
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair0);
        crosshairOverlay.addRangeCrosshair(yCrosshair1);
        crosshairOverlay.addRangeCrosshair(yCrosshair2);
        crosshairOverlay.addRangeCrosshair(yCrosshair3);
        crosshairOverlay.addRangeCrosshair(yCrosshair4);

        chartPanels[8].addOverlay(crosshairOverlay);
        
        chartPanels[8].addChartMouseListener(new ChartMouseListener() {
			@Override
		    public void chartMouseClicked(ChartMouseEvent event) {
		        // ignore
		    }

		    @Override
		    public void chartMouseMoved(ChartMouseEvent event) {
		        Rectangle2D dataArea = chartPanels[8].getScreenDataArea();
		        JFreeChart chart = event.getChart();
		        XYPlot plot = (XYPlot) chart.getPlot();
		        ValueAxis xAxis = plot.getDomainAxis();
		        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, RectangleEdge.BOTTOM);
		        xCrosshair.setValue(x);
		        double y0 = DatasetUtils.findYValue(plot.getDataset(), 0, x);
		        yCrosshair0.setValue(y0);
		        double y1 = DatasetUtils.findYValue(plot.getDataset(), 1, x);
		        yCrosshair1.setValue(y1);
		        double y2 = DatasetUtils.findYValue(plot.getDataset(), 2, x);
		        yCrosshair2.setValue(y2);
		        double y3 = DatasetUtils.findYValue(plot.getDataset(), 3, x);
		        yCrosshair3.setValue(y3);
		        double y4 = DatasetUtils.findYValue(plot.getDataset(), 4, x);
		        yCrosshair4.setValue(y4);
		    }
		});
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JCheckBox enableCrosshair = new JCheckBox("Enable Crosshairs");
        enableCrosshair.setSelected(true);
        enableCrosshair.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				chartPanels[8].addOverlay(crosshairOverlay);
			}
			else {
				chartPanels[8].removeOverlay(crosshairOverlay);
			}
        });
        JPanel crosshairCheckboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        crosshairCheckboxPanel.add(enableCrosshair);
        crosshairCheckboxPanel.setBackground(Color.WHITE);
        enableCrosshair.setBackground(Color.WHITE);
        crosshairCheckboxPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        
        mainPanel.add(chartPanels[8]);
        mainPanel.add(crosshairCheckboxPanel, BorderLayout.SOUTH);
		
		return mainPanel;
	}
	
	/** Adds data to the multi-series line chart for Top 5 economies */
	static void updateMultiXYLineChartByEconomy() {
		XYPlot plot = charts[8].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		
		final int numSeries = 5;
		
		double[] averages = new double[numSeries];
		int[] counts = new int[numSeries];
		int[] iterCounts = new int[numSeries];
		
		String[] countries = new String[numSeries];
		countries[0] = "China";
		countries[1] = "Germany";
		countries[2] = "India";
		countries[3] = "Japan";
		countries[4] = "United States";
		
		XYSeries[] series = new XYSeries[numSeries];
		series[0] = new XYSeries(countries[0]);
		series[1] = new XYSeries(countries[1]);
		series[2] = new XYSeries(countries[2]);
		series[3] = new XYSeries(countries[3]);
		series[4] = new XYSeries(countries[4]);
		
		for(int i = 0; i < numSeries; i++) {
			datasetMultiLineChartByEconomy.addSeries(series[i]);
		}
		
		for(int i = 0; i < View.getCountryTable().getModel().getRowCount(); i++) {
			// Avoid infinite values where the particular x-value doesn't exist
			if((Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4))) >= 1900) {
				if(View.getCountryTable().getModel().getValueAt(i, 3).equals(countries[0])) {
					iterCounts[0] = iterCounts[0] + 1;
					if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
						counts[0]++;
						averages[0] = averages[0] + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1));
					}
					if(iterCounts[0] == 12) { // count only becomes 12 after 12 *count increase*, does not mean 12 months... UPDATE: Fixed
						averages[0] = averages[0]/counts[0];
						series[0].add(Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)), averages[0]);
						averages[0] = 0;
						counts[0] = 0;
						iterCounts[0] = 0;
					}
				}
				else if(View.getCountryTable().getModel().getValueAt(i, 3).equals(countries[1])) {
					iterCounts[1] = iterCounts[1] + 1;
					if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
						counts[1]++;
						averages[1] = averages[1] + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1));
					}
					if(counts[1] == 12) {
						averages[1] = averages[1]/counts[1];
						series[1].add(Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)), averages[1]);
						averages[1] = 0;
						counts[1] = 0;
						iterCounts[1] = 0;
					}
				}
				else if(View.getCountryTable().getModel().getValueAt(i, 3).equals(countries[2])) {
					iterCounts[2] = iterCounts[2] + 1;
					r.setSeriesPaint(2, new Color(0, 128, 0));
					if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
						counts[2]++;
						averages[2] = averages[2] + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1));
					}
					if(iterCounts[2] == 12) {
						averages[2] = averages[2]/counts[2];
						series[2].add(Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)), averages[2]);
						averages[2] = 0;
						counts[2] = 0;
						iterCounts[2] = 0;
					}
				}
				else if(View.getCountryTable().getModel().getValueAt(i, 3).equals(countries[3])) {
					iterCounts[3] = iterCounts[3] + 1;
					r.setSeriesPaint(3, new Color(255, 120, 0));
					if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
						counts[3]++;
						averages[3] = averages[3] + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1));
					}
					if(iterCounts[3] == 12) {
						averages[3] = averages[3]/counts[3];
						series[3].add(Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)), averages[3]);
						averages[3] = 0;
						counts[3] = 0;
						iterCounts[3] = 0;
					}
				}
				else if(View.getCountryTable().getModel().getValueAt(i, 3).equals(countries[4])) {
					r.setSeriesPaint(4, Color.MAGENTA);
					iterCounts[4] = iterCounts[4] + 1;
					if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
						counts[4]++;
						averages[4] = averages[4] + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1));
					}
					if(iterCounts[4] == 12) {
						averages[4] = averages[4]/counts[4];
						series[4].add(Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)), averages[4]);
						averages[4] = 0;
						counts[4] = 0;
						iterCounts[4] = 0;
					}
				}
			}
		}

		for(int i = 0; i < numSeries; i++) {
			double[] coefficients = Regression.getOLSRegression(datasetMultiLineChartByEconomy, i);
			double b = coefficients[0]; // intercept
			double m = coefficients[1]; // slope
			XYSeries trend = new XYSeries(countries[i] + " Trend");
			double x = series[i].getDataItem(0).getXValue();
			trend.add(x, m * x + b);
			x = series[i].getDataItem(series[i].getItemCount() - 1).getXValue();
			trend.add(x, m * x + b);
			//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
			r.setSeriesStroke(i+numSeries, new BasicStroke(1.5f));
			r.setSeriesPaint(i+numSeries, Color.BLACK);
			r.setSeriesVisibleInLegend(i+numSeries, false, true);
			datasetMultiLineChartByEconomy.addSeries(trend);
			// Prints out slope of trend line for each country:
			//System.out.println(countries[i] + ": " + m);
		}

		charts[8].setNotify(true);
	}

}
