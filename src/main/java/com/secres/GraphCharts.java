package com.secres;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * 
 * @author Pranav Amarnath
 *
 */

public class GraphCharts {

	private static DefaultCategoryDataset datasetBasicLineChart, datasetBasicLineChartByYear;
	private static XYSeriesCollection datasetBasicScatterPlotByYear, datasetBasicScatterPlotCoolingDec, datasetBasicScatterPlotCoolingJun;
	private static XYSeriesCollection datasetMultiLineChartByEconomy;
	private static DefaultCategoryDataset datasetBasicBarChartByCountry, datasetDoubleBarChartByCountryGreatest, datasetDoubleBarChartByCountryLeast;
	private static JFreeChart linePlot; // specific for multi line plot by economy
	
	static ChartPanel basicLineChart() {
		datasetBasicLineChart = new DefaultCategoryDataset();
		/*// dummy data
		dataset.addValue( 15 , "schools" , "1970" );
		dataset.addValue( 30 , "schools" , "1980" );
		dataset.addValue( 60 , "schools" ,  "1990" );
		dataset.addValue( 120 , "schools" , "2000" );
		dataset.addValue( 240 , "schools" , "2010" );
		dataset.addValue( 300 , "schools" , "2014" );
		*/

		JFreeChart allLineChart = ChartFactory.createLineChart("All Temperatures 1750-2015", "Date", "Temperature", datasetBasicLineChart, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot allLinePlot = (CategoryPlot) allLineChart.getCategoryPlot();
		allLinePlot.setRangePannable(true);		

		ChartPanel chartPanel = new ChartPanel(allLineChart);
		chartPanel.setMouseWheelEnabled(true);
		//chartPanel.setDomainZoomable(true);

		return chartPanel;
	}

	static void updateDataBasicLineChart() {
		//System.out.println("Entered updateDataBasicLineChart()");
		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i++) {
			if(!(((String)View.getGlobalTable().getModel().getValueAt(i, 1)).equals(""))) {
				datasetBasicLineChart.addValue(Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)), "Temperature over time", (String)View.getGlobalTable().getModel().getValueAt(i, 0));
			}
		}
	}

	static ChartPanel basicLineChartByYear() {
		datasetBasicLineChartByYear = new DefaultCategoryDataset();
		/*// dummy data
		dataset.addValue( 15 , "schools" , "1970" );
		dataset.addValue( 30 , "schools" , "1980" );
		dataset.addValue( 60 , "schools" ,  "1990" );
		dataset.addValue( 120 , "schools" , "2000" );
		dataset.addValue( 240 , "schools" , "2010" );
		dataset.addValue( 300 , "schools" , "2014" );
		*/

		JFreeChart allLineChart = ChartFactory.createLineChart("Average Temperatures 1750-2015", "Year", "Average Temperature \u00B0C", datasetBasicLineChartByYear, PlotOrientation.VERTICAL, true, true, false);
		//allLineChart.getCategoryPlot().getRangeAxis().setLowerBound(6.0);
		//allLineChart.getCategoryPlot().getRangeAxis().setUpperBound(12.0);
		CategoryPlot allLinePlot = (CategoryPlot) allLineChart.getCategoryPlot();
		allLinePlot.setRangePannable(true);

		/*
		CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		Crosshair crosshairDomain = new Crosshair(Double.NaN, Color.DARK_GRAY, new BasicStroke(0f));
		Crosshair crosshairRange = new Crosshair(Double.NaN, Color.DARK_GRAY, new BasicStroke(0f));
		crosshairOverlay.addDomainCrosshair(crosshairDomain);
		crosshairOverlay.addRangeCrosshair(crosshairRange);
		*/
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true

		ChartPanel chartPanel = new ChartPanel(allLineChart);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}

	static void updateDataBasicChartByYear() {
		//System.out.println("Entered updateDataBasicChartByYear()");
		/** Start Scatter Plot */
		XYSeries series = new XYSeries("Average temperature per year"); // Init series
		/** End Scatter Plot */
		Map<String, Double> entries = new LinkedHashMap<>();
		List<Double> entriesList = new ArrayList<>();
		double average = 0;
		int count = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/** Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets */
		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			for(int j = 0; j < 12; j++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i+j, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i+j, 1)).trim().isEmpty()) {
					entries.put((String)View.getGlobalTable().getModel().getValueAt(i+j, 0), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i+j, 1)));
					//System.out.println("i: " + i + ", j: " + j);
					count++;
					average = average + entries.get((String)View.getGlobalTable().getModel().getValueAt(i+j, 0));
				}
			}
			//System.out.println(entries.size());
			//count = 0;
			/*
			for(int k = 0; k < entries.size(); k++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i+k, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i+k, 1)).trim().isEmpty()) {
					//entriesList.add(entries.get((String)View.getGlobalTable().getModel().getValueAt(i+k, 0)));
					count++;
					average = average + entries.get((String)View.getGlobalTable().getModel().getValueAt(i+k, 0));
					//System.out.println("Year: " + (String)View.getGlobalTable().getModel().getValueAt(i+k, 0) + "Average: " + average);
				}
			}
			*/
			//System.out.println(count);
			//System.out.println(average);
			//System.out.println(Arrays.toString(entriesList.toArray()));
			/*
			for(int l = 0; l < entriesList.size(); l++) {
				average = average + entriesList.get(l);
			}
			*/
			average = average/count;
			//entriesAvg.add(average);
			/** I do the substring(0, 4) because each year will be 1750-01-01, 1751-01-01 etc. -> to shorten to 1750, 1751 etc. */
			datasetBasicLineChartByYear.addValue(average, "Average temperature per year", ((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4));
			/** Start Scatter Plot */
			series.add(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)), average); // add elements
			/** End Scatter Plot */
			average = 0;
			count = 0;
			entries.clear();
			entriesList.clear();
		}

		/** Start Scatter Plot */
		((XYSeriesCollection) datasetBasicScatterPlotByYear).addSeries(series); // add series to dataset

		/** Begin trend display */
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotByYear, 0);
		double b = coefficients[0]; // intercept
		double m = coefficients[1]; // slope
		XYSeries trend = new XYSeries("Trend");
		double x = series.getDataItem(0).getXValue();
		trend.add(x, m * x + b);
		x = series.getDataItem(series.getItemCount() - 1).getXValue();
		trend.add(x, m * x + b);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		datasetBasicScatterPlotByYear.addSeries(trend);
		/** End trend display */
		/** End Scatter Plot */
	}

	static ChartPanel basicScatterPlotByYear() {
		datasetBasicScatterPlotByYear = new XYSeriesCollection();

		JFreeChart scatterPlot = ChartFactory.createScatterPlot("Average Temperatures 1750-2015", "Year", "Average Temperature \u00B0C", datasetBasicScatterPlotByYear, PlotOrientation.VERTICAL, true, true, false);

		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = scatterPlot.getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(scatterPlot);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static ChartPanel basicScatterPlotCoolingDec() {
		datasetBasicScatterPlotCoolingDec = new XYSeriesCollection();

		JFreeChart scatterPlot = ChartFactory.createScatterPlot("December Temperatures 1750-2015", "Year", "Temperature \u00B0C", datasetBasicScatterPlotCoolingDec, PlotOrientation.VERTICAL, true, true, false);

		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = scatterPlot.getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(scatterPlot);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static void updateScatterPlotCoolingDec() {
		//System.out.println("Entered updateScatterPlotCoolingDec()");
		/** Start Scatter Plot */
		XYSeries series = new XYSeries("Temperature in December per year"); // Init series
		/** End Scatter Plot */
		Map<String, Double> entries = Collections.synchronizedMap(new LinkedHashMap<>());
		List<Double> entriesList = new ArrayList<>();
		//double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/** Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets */
		for(int i = 11; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entries.put((String)View.getGlobalTable().getModel().getValueAt(i, 0), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)));
			}
			//System.out.println(entries.size());
			//int count = 0;
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entriesList.add(entries.get((String)View.getGlobalTable().getModel().getValueAt(i, 0)));
				//count++;
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			//entriesAvg.add(average);
			/** Start Scatter Plot */
			for(int l = 0; l < entriesList.size(); l++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
					series.add(Double.parseDouble(((String) View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0,4)), entriesList.get(l)); // add elements
				}
			}
			/** End Scatter Plot */
			entries.clear();
			entriesList.clear();
		}

		/** Start Scatter Plot */
		((XYSeriesCollection) datasetBasicScatterPlotCoolingDec).addSeries(series); // add series to dataset

		/** Begin trend display */
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotCoolingDec, 0);
		double b = coefficients[0]; // intercept
		double m = coefficients[1]; // slope
		XYSeries trend = new XYSeries("Trend");
		double x = series.getDataItem(0).getXValue();
		trend.add(x, m * x + b);
		x = series.getDataItem(series.getItemCount() - 1).getXValue();
		trend.add(x, m * x + b);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		datasetBasicScatterPlotCoolingDec.addSeries(trend);
		/** End trend display */
		/** End Scatter Plot */
	}
	
	static ChartPanel basicScatterPlotCoolingJun() {
		datasetBasicScatterPlotCoolingJun = new XYSeriesCollection();

		JFreeChart scatterPlot = ChartFactory.createScatterPlot("June Temperatures 1750-2015", "Year", "Temperature \u00B0C", datasetBasicScatterPlotCoolingJun, PlotOrientation.VERTICAL, true, true, false);

		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = scatterPlot.getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(scatterPlot);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static void updateScatterPlotCoolingJun() {
		//System.out.println("Entered updateScatterPlotCoolingJun()");
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in June per year"); // Init series
		// End Scatter Plot
		Map<String, Double> entries = Collections.synchronizedMap(new LinkedHashMap<>());
		List<Double> entriesList = new ArrayList<>();
		//double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/** Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets */
		for(int i = 5; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entries.put((String)View.getGlobalTable().getModel().getValueAt(i, 0), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)));
			}
			//System.out.println(entries.size());
			//int count = 0;
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entriesList.add(entries.get((String)View.getGlobalTable().getModel().getValueAt(i, 0)));
				//count++;
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			//entriesAvg.add(average);
			// Start Scatter Plot
			for(int l = 0; l < entriesList.size(); l++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
					series.add(Double.parseDouble(((String) View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0,4)), entriesList.get(l)); // add elements
				}
			}
			// End Scatter Plot
			entries.clear();
			entriesList.clear();
		}

		// Start Scatter Plot
		((XYSeriesCollection) datasetBasicScatterPlotCoolingJun).addSeries(series); // add series to dataset

		// Begin trend display
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotCoolingJun, 0);
		double b = coefficients[0]; // intercept
		double m = coefficients[1]; // slope
		XYSeries trend = new XYSeries("Trend");
		double x = series.getDataItem(0).getXValue();
		trend.add(x, m * x + b);
		x = series.getDataItem(series.getItemCount() - 1).getXValue();
		trend.add(x, m * x + b);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		datasetBasicScatterPlotCoolingJun.addSeries(trend);
		// End trend display
		// End Scatter Plot
		Main.getPB().setValue(Main.getPB().getValue() + 20);
	}
	
	static ChartPanel basicBarChartByCountry() {
		datasetBasicBarChartByCountry = new DefaultCategoryDataset();

		JFreeChart barChart = ChartFactory.createBarChart("Countries with Highest Overall Temperatures", "Country", "Average Temperature \u00B0C", datasetBasicBarChartByCountry, PlotOrientation.VERTICAL, true, true, false);

		/** @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setRangePannable(true);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		//renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		/** Default item label is above the bar. Use below method for inside bar. */
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static void updateBasicBarChartByCountry() {
		//List<Double> entriesList = new ArrayList<>();
		double average = 0;
		int count = 0;
		Map<String, Double> entriesAvg = new LinkedHashMap<>();
		
		/*// dummy data
		datasetBasicBarChartByCountry.addValue(10, "USA", "2005");
		datasetBasicBarChartByCountry.addValue(15, "India", "2005");
		datasetBasicBarChartByCountry.addValue(20, "China", "2005");
		*/
		
		Set<String> set = new LinkedHashSet<>();
		for(int i = 0; i<View.getCountryTable().getModel().getRowCount();i++){
		    String obj = (String)View.getCountryTable().getModel().getValueAt(i, 3);
		    if(!set.add(obj)){
		        continue;
		    }
		}
		for(Iterator<String> i = set.iterator(); i.hasNext(); ) {
			String country = i.next();
			for(int j = 0; j < View.getCountryTable().getModel().getRowCount(); j++) {
				//System.out.println(View.getCountryTable().getModel().getValueAt(i, 3));
				if(View.getCountryTable().getModel().getValueAt(j, 3).equals(country)) {
					//System.out.println("Entered outer 'if'");
					if((String)View.getCountryTable().getModel().getValueAt(j, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(j, 1)).trim().isEmpty()) {
						count = count + 1;
						average = average + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(j, 1));
						//System.out.println(count);
					}
				}
			}
			//System.out.println(count);
			/*
			for(int k = 0; k < count; k++) {
				if((String)View.getCountryTable().getModel().getValueAt(k, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(k, 1)).trim().isEmpty()) {
					average = average + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(k, 1));
				}
			}
			*/
			//System.out.println(average);
			average = average/count;
			//System.out.println(average);
			entriesAvg.put(country, average);
			//datasetBasicBarChartByCountry.addValue(average, country, country);
			//System.out.println(entriesList.size());
			average = 0;
			count = 0;
		}
		entriesAvg = sortByValueDescending(entriesAvg);
		for(int i = 0; i < 10; i++) { // for sortByValueDescending()
			if((entriesAvg.get((String)entriesAvg.keySet().toArray()[i])) < 100) { // Antarctica returns infinity because starts from 1950
				datasetBasicBarChartByCountry.addValue(entriesAvg.get((String)entriesAvg.keySet().toArray()[i]), "Average temperature", (String)entriesAvg.keySet().toArray()[i]);
			}
		}
		/*
		entriesAvg = sortByValue(entriesAvg);
		for(int i = 0; i < 10; i++) { // for sortByValue()
			datasetBasicBarChartByCountry.addValue(entriesAvg.get((String)entriesAvg.keySet().toArray()[i]), (String)entriesAvg.keySet().toArray()[i], (String)entriesAvg.keySet().toArray()[i]);
		}
		*/
		SwingUtilities.invokeLater(() -> {
			Main.getPB().setValue(Main.getPB().getValue() + 30);
		});
	}
	
	/** @see https://stackoverflow.com/a/23846961/13772184 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
		map = map.entrySet().stream()
			       .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(
			    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return map;
    }
	
	/** @see https://stackoverflow.com/a/2581754/13772184 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		map = map.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(
			    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return map;
    }
	
	static ChartPanel doubleBarChartByCountryGreatest() {
		datasetDoubleBarChartByCountryGreatest = new DefaultCategoryDataset();

		JFreeChart barChart = ChartFactory.createBarChart("Countries with Greatest Change In Temperature Over a Century (1912-2012)", "Country", "Average Temperature \u00B0C", datasetDoubleBarChartByCountryGreatest, PlotOrientation.HORIZONTAL, true, true, false);

		/** @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setRangePannable(true);
		//Font font = new Font("SansSerif", Font.BOLD, 15);
		//plot.getDomainAxis().setTickLabelFont(font);
		/** Moves the range axis (temperature) to the bottom */
		ValueAxis rangeAxis = plot.getRangeAxis();
		plot.setRangeAxis(0, rangeAxis);
		plot.mapDatasetToRangeAxis(0, 0);
		plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		/** Default item label is above the bar. Use below method for inside bar. */
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static ChartPanel doubleBarChartByCountryLeast() {
		datasetDoubleBarChartByCountryLeast = new DefaultCategoryDataset();

		JFreeChart barChart = ChartFactory.createBarChart("Countries with Least Change In Temperature Over a Century (1912-2012)", "Country", "Average Temperature \u00B0C", datasetDoubleBarChartByCountryLeast, PlotOrientation.HORIZONTAL, true, true, false);

		/** @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setRangePannable(true);
		//Font font = new Font("SansSerif", Font.BOLD, 15);
		//plot.getDomainAxis().setTickLabelFont(font);
		/*
		 * Moves the range axis (temperature) to the bottom
		 */
		ValueAxis rangeAxis = plot.getRangeAxis();
		plot.setRangeAxis(0, rangeAxis);
		plot.mapDatasetToRangeAxis(0, 0);
		plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		/** Default item label is above the bar. Use below method for inside bar. */
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static void updateDoubleBarChartByCountry() {
		//List<Double> entriesList = new ArrayList<>();
		double average = 0;
		int count = 0;
		double averageSecond = 0;
		int countSecond = 0;
		Map<String, Double> entriesAvg = new LinkedHashMap<>();
		Map<String, Double> entriesAvgSecond = new LinkedHashMap<>();
		Map<String, Double> entriesDifferences = new LinkedHashMap<>();
		Map<String, Double> entriesAvgLeast = new LinkedHashMap<>();
		Map<String, Double> entriesAvgSecondLeast = new LinkedHashMap<>();
		Map<String, Double> entriesDifferencesLeast = new LinkedHashMap<>();
		
		/*// dummy data
		datasetDoubleBarChartByCountry.addValue(10, "USA", "2005");
		datasetDoubleBarChartByCountry.addValue(15, "India", "2005");
		datasetDoubleBarChartByCountry.addValue(20, "China", "2005");
		*/
		
		Set<String> set = new LinkedHashSet<>();
		for(int i = 0; i<View.getCountryTable().getModel().getRowCount();i++){
		    String obj = (String)View.getCountryTable().getModel().getValueAt(i, 3);
		    if(!set.add(obj)){
		        continue;
		    }
		}
		for(Iterator<String> i = set.iterator(); i.hasNext(); ) {
			String country = i.next();
			for(int j = 0; j < View.getCountryTable().getModel().getRowCount(); j++) {
				//System.out.println(View.getCountryTable().getModel().getValueAt(i, 3));
				if(View.getCountryTable().getModel().getValueAt(j, 3).equals(country)) {
					//System.out.println("Entered outer 'if'");
					if((((String)View.getCountryTable().getModel().getValueAt(j, 0)).substring(0, 4)).equals("1912")) {
						if((String)View.getCountryTable().getModel().getValueAt(j, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(j, 1)).trim().isEmpty()) {
							count = count + 1;
							average = average + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(j, 1));
							//System.out.println(count);
						}
					}
					else if((((String)View.getCountryTable().getModel().getValueAt(j, 0)).substring(0, 4)).equals("2012")) {
						if((String)View.getCountryTable().getModel().getValueAt(j, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(j, 1)).trim().isEmpty()) {
							countSecond = countSecond + 1;
							averageSecond = averageSecond + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(j, 1));
							//System.out.println(count);
						}
					}
				}
			}
			//System.out.println(count);
			/*
			for(int k = 0; k < count; k++) {
				if((String)View.getCountryTable().getModel().getValueAt(k, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(k, 1)).trim().isEmpty()) {
					average = average + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(k, 1));
				}
			}
			*/
			//System.out.println(average);
			average = average/count;
			averageSecond = averageSecond/count;
			//System.out.println(average);
			entriesAvg.put(country, average);
			entriesAvgSecond.put(country, averageSecond);
			//datasetBasicBarChartByCountry.addValue(average, country, country);
			//System.out.println(entriesList.size());
			average = 0;
			count = 0;
			averageSecond = 0;
			countSecond = 0;
		}
		for(int i = 0; i < entriesAvg.size(); i++) {
			if((entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i])) < 100) {
				entriesDifferences.put((String)entriesAvgSecond.keySet().toArray()[i], entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i]) - entriesAvg.get((String)entriesAvg.keySet().toArray()[i]));
			}
		}
		entriesDifferencesLeast = entriesDifferences;
		entriesAvgLeast = entriesAvg;
		entriesAvgSecondLeast = entriesAvgSecond;
		
		entriesDifferences = sortByValueDescending(entriesDifferences);
		entriesAvg = sortByValueDescending(entriesAvg);
		entriesAvgSecond = sortByValueDescending(entriesAvgSecond);
		
		entriesDifferencesLeast = sortByValue(entriesDifferencesLeast);
		entriesAvgLeast = sortByValue(entriesAvgLeast);
		entriesAvgSecondLeast = sortByValue(entriesAvgSecondLeast);
		
		for(int i = 0; i < 19; i++) {
			if((entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i])) < 100) {
				if(((String)entriesDifferences.keySet().toArray()[i]).equals("Canada")) {
					continue;
				}
				else {
					datasetDoubleBarChartByCountryGreatest.addValue(entriesAvg.get((String)entriesDifferences.keySet().toArray()[i]), "1912", (String)entriesDifferences.keySet().toArray()[i]);
					datasetDoubleBarChartByCountryGreatest.addValue(entriesAvgSecond.get((String)entriesDifferences.keySet().toArray()[i]), "2012", (String)entriesDifferences.keySet().toArray()[i]);
				}
			}
		}
		for(int i = 0; i < 15; i++) {
			if((entriesAvgSecondLeast.get((String)entriesAvgSecondLeast.keySet().toArray()[i])) < 100) {
				if(((String)entriesDifferencesLeast.keySet().toArray()[i]).equals("Congo (Democratic Republic Of The)")) {
					datasetDoubleBarChartByCountryLeast.addValue(entriesAvgLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "1912", "D.R. Congo");
					datasetDoubleBarChartByCountryLeast.addValue(entriesAvgSecondLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "2012", "D.R. Congo");
				}
				else {
					datasetDoubleBarChartByCountryLeast.addValue(entriesAvgLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "1912", (String)entriesDifferencesLeast.keySet().toArray()[i]);
					datasetDoubleBarChartByCountryLeast.addValue(entriesAvgSecondLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "2012", (String)entriesDifferencesLeast.keySet().toArray()[i]);
				}
			}
		}
		/*
		for(int i = 0; i < 50; i++) {
			if((entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i])) < 100) {
				datasetDoubleBarChartByCountry.addValue(entriesAvg.get((String)entriesAvg.keySet().toArray()[i]), "1912", (String)entriesAvg.keySet().toArray()[i]);
				datasetDoubleBarChartByCountry.addValue(entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i]), "2012", (String)entriesAvgSecond.keySet().toArray()[i]);
			}
		}
		*/
		/*
		for(int i = 0; i < 50; i++) {
			if((entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i])) < 100) {
				datasetDoubleBarChartByCountry.addValue(entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i]), "2012", (String)entriesAvgSecond.keySet().toArray()[i]);
			}
		}
		*/
		/*
		entriesAvg = sortByValue(entriesAvg);
		for(int i = 0; i < 10; i++) {
			datasetBasicBarChartByCountry.addValue(entriesAvg.get((String)entriesAvg.keySet().toArray()[i]), (String)entriesAvg.keySet().toArray()[i], (String)entriesAvg.keySet().toArray()[i]);
		}
		*/
		SwingUtilities.invokeLater(() -> {
			Main.getPB().setValue(Main.getPB().getValue() + 30);
		});
		//System.out.println("Reached view visible.");
	}
	
	static ChartPanel multiXYLineChartByEconomy() {
		datasetMultiLineChartByEconomy = new XYSeriesCollection();

		linePlot = ChartFactory.createXYLineChart("Average Temperatures of Top 5 World Economies (1900-2012)", "Year", "Average Temperature \u00B0C", datasetMultiLineChartByEconomy, PlotOrientation.VERTICAL, true, true, false);

		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = linePlot.getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		r.setDefaultOutlineStroke(new BasicStroke(2.0f));
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(linePlot);
		chartPanel.setMouseWheelEnabled(true);

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

        chartPanel.addOverlay(crosshairOverlay);
        
        chartPanel.addChartMouseListener(new ChartMouseListener() {
			@Override
		    public void chartMouseClicked(ChartMouseEvent event) {
		        // ignore
		    }

		    @Override
		    public void chartMouseMoved(ChartMouseEvent event) {
		        Rectangle2D dataArea = chartPanel.getScreenDataArea();
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
		
		return chartPanel;
	}
	
	static void updateMultiXYLineChartByEconomy() {
		XYPlot plot = linePlot.getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		
		double[] averages = new double[5];
		//double average = 0;
		int[] counts = new int[5];
		int[] iterCounts = new int[5];
		//int count = 0;
		
		String[] countries = new String[5];
		countries[0] = "China";
		countries[1] = "Germany";
		countries[2] = "India";
		countries[3] = "Japan";
		countries[4] = "United States";
		
		XYSeries[] series = new XYSeries[5];
		series[0] = new XYSeries(countries[0]);
		series[1] = new XYSeries(countries[1]);
		series[2] = new XYSeries(countries[2]);
		series[3] = new XYSeries(countries[3]);
		series[4] = new XYSeries(countries[4]);

		for(int i = 0; i < View.getCountryTable().getModel().getRowCount(); i++) {
			if((Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4))) >= 1900) {
				if(View.getCountryTable().getModel().getValueAt(i, 3).equals(countries[0])) {
					//System.out.println("United States exists.");
					//System.out.println("Entered first time");
					iterCounts[0] = iterCounts[0] + 1;
					//System.out.println(iterCounts[0]);
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
					//System.out.println("United States exists.");
					//System.out.println("Entered first time");
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
					//System.out.println("India exists.");
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
					//System.out.println("United States exists.");
					//System.out.println("Entered first time");
					iterCounts[3] = iterCounts[3] + 1;
					r.setSeriesPaint(3, new Color(255, 153, 0));
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
					//System.out.println("United States exists.");
					//System.out.println("Entered first time");
					r.setSeriesPaint(4, Color.MAGENTA);
					iterCounts[4] = iterCounts[4] + 1;
					if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
						counts[4]++;
						averages[4] = averages[4] + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(i, 1));
					}
					if(iterCounts[4] == 12) {
						averages[4] =averages[4]/counts[4];
						series[4].add(Double.parseDouble(((String)View.getCountryTable().getModel().getValueAt(i, 0)).substring(0, 4)), averages[4]);
						averages[4] = 0;
						counts[4] = 0;
						iterCounts[4] = 0;
					}
				}
			}
		}
		//System.out.println("Exited loop.");

		for(int i = 0; i < 5; i++) {
			datasetMultiLineChartByEconomy.addSeries(series[i]);
		}

		for(int i = 0; i < 5; i++) {
			double[] coefficients = Regression.getOLSRegression(datasetMultiLineChartByEconomy, i);
			double b = coefficients[0]; // intercept
			double m = coefficients[1]; // slope
			XYSeries trend = new XYSeries(countries[i] + " Trend");
			double x = series[i].getDataItem(0).getXValue();
			trend.add(x, m * x + b);
			x = series[i].getDataItem(series[i].getItemCount() - 1).getXValue();
			trend.add(x, m * x + b);
			//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
			r.setSeriesOutlineStroke(i+5, new BasicStroke(3.0f));
			r.setSeriesPaint(i+5, Color.BLACK);
			plot.getRenderer().setSeriesVisibleInLegend(i+5, Boolean.FALSE, true);
			datasetMultiLineChartByEconomy.addSeries(trend);
			/** Prints out slope of trend line for each country. */
			//System.out.println(countries[i] + ": " + m);
		}
		
		/*
		SwingUtilities.invokeLater(() -> {
			Main.getPB().setValue(Main.getPB().getValue() + 20);
		});
		*/
	}

	/*
	static void updateBasicScatterPlotByYear() {
		XYSeries series = new XYSeries("Average temperature per year");
		Map<String, Double> entries = Collections.synchronizedMap(new LinkedHashMap<>());
		List<Double> entriesList = new ArrayList<>();
		double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();
		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			for(int j = 0; j < 12; j++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i+j, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i+j, 1)).trim().isEmpty()) {
					entries.put((String)View.getGlobalTable().getModel().getValueAt(i+j, 0), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i+j, 1)));
				}
				else {
					continue;
				}
			}
			//System.out.println(entries.size());
			int count = 0;
			for(int k = 0; k < entries.size(); k++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i+k, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i+k, 1)).trim().isEmpty()) {
					entriesList.add(entries.get((String)View.getGlobalTable().getModel().getValueAt(i+k, 0)));
					count++;
				}
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			for(int l = 0; l < entriesList.size(); l++) {
				average = average + entriesList.get(l);
			}
			average = average/count;
			//entriesAvg.add(average);
			series.add(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)), average);
			entries.clear();
			entriesList.clear();
		}
		((XYSeriesCollection) datasetBasicScatterPlotByYear).addSeries(series);

		// dummy data
		//XYSeries series1 = new XYSeries("Boys");
		//series1.add(1, 72.9);
		//series1.add(2, 81.6);
		//series1.add(3, 88.9);
		//series1.add(4, 96);
		//series1.add(5, 102.1);
		//series1.add(6, 108.5);
		//series1.add(7, 113.9);
		//series1.add(8, 119.3);
		//series1.add(9, 123.8);
		//series1.add(10, 124.4);
		//datasetBasicScatterPlotByYear.addSeries(series1);

		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotByYear, 0);
		double b = coefficients[0]; // intercept
		double m = coefficients[1]; // slope
		XYSeries trend = new XYSeries("Trend");
		double x = series.getDataItem(0).getXValue();
		trend.add(x, m * x + b);
		x = series.getDataItem(series.getItemCount() - 1).getXValue();
		trend.add(x, m * x + b);
		datasetBasicScatterPlotByYear.addSeries(trend);

		Main.getSplash().dispose();
		View.getFrame().setVisible(true);
	}
	*/

}
