package com.secres;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
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
import org.jfree.chart.util.DefaultShadowGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.statistics.Regression;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
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

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * 
 * @author Pranav Amarnath
 *
 */

public class GraphCharts {

	//private static DefaultCategoryDataset datasetBasicLineChart, datasetBasicLineChartByYear;
	private static TimeSeriesCollection datasetBasicLineChart, datasetBasicLineChartByYear;
	private static XYSeriesCollection datasetBasicScatterPlotByYear, datasetBasicScatterPlotCoolingDec, datasetBasicScatterPlotCoolingJun;
	private static XYSeriesCollection datasetMultiLineChartByEconomy;
	private static DefaultCategoryDataset datasetBasicBarChartByCountry, datasetDoubleBarChartByCountryGreatest, datasetDoubleBarChartByCountryLeast;
	//private static JFreeChart linePlot; // specific for multi line plot by economy
	static JFreeChart[] charts = new JFreeChart[10];
	//static int timerIndex = 0;
	/**
	 * @see https://www.jfree.org/forum/viewtopic.php?p=36292#p36292
	 * @see https://stackoverflow.com/a/12074860/13772184
	 */
	
	static ChartPanel basicLineChart() {
		datasetBasicLineChart = new TimeSeriesCollection();
		/*// dummy data
		dataset.addValue( 15 , "schools" , "1970" );
		dataset.addValue( 30 , "schools" , "1980" );
		dataset.addValue( 60 , "schools" ,  "1990" );
		dataset.addValue( 120 , "schools" , "2000" );
		dataset.addValue( 240 , "schools" , "2010" );
		dataset.addValue( 300 , "schools" , "2014" );
		*/

		charts[0] = ChartFactory.createTimeSeriesChart("All Temperatures 1750-2015", "Date", "Temperature", datasetBasicLineChart, true, true, false);
		charts[0].setNotify(false);
		
		XYPlot allLinePlot = (XYPlot) charts[0].getXYPlot();
		allLinePlot.setDomainPannable(true);
		allLinePlot.setRangePannable(true);
		
		DateAxis axis = (DateAxis) allLinePlot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MMM"));

		ChartPanel chartPanel = new ChartPanel(charts[0]);
		chartPanel.setMouseWheelEnabled(true);
		//chartPanel.setDomainZoomable(true);

		return chartPanel;
	}

	static void updateDataBasicLineChart() {
		TimeSeries basicLine = new TimeSeries("Temperature over time");
		//System.out.println("Entered updateDataBasicLineChart()");
		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i++) {
			if(!(((String)View.getGlobalTable().getModel().getValueAt(i, 1)).equals(""))) {
				basicLine.add(new Month(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(5, 7)), Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4))), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)));
			}
		}
		datasetBasicLineChart.addSeries(basicLine);
		charts[0].setNotify(true);
	}

	static ChartPanel basicLineChartByYear() {
		datasetBasicLineChartByYear = new TimeSeriesCollection();
		/*// dummy data
		dataset.addValue( 15 , "schools" , "1970" );
		dataset.addValue( 30 , "schools" , "1980" );
		dataset.addValue( 60 , "schools" ,  "1990" );
		dataset.addValue( 120 , "schools" , "2000" );
		dataset.addValue( 240 , "schools" , "2010" );
		dataset.addValue( 300 , "schools" , "2014" );
		*/

		charts[1] = ChartFactory.createTimeSeriesChart("Average Temperatures 1750-2015", "Year", "Average Temperature \u00B0C", datasetBasicLineChartByYear, true, true, false);
		charts[1].setNotify(false);
		//allLineChart.getCategoryPlot().getRangeAxis().setLowerBound(6.0);
		//allLineChart.getCategoryPlot().getRangeAxis().setUpperBound(12.0);
		XYPlot allLinePlot = (XYPlot) charts[1].getXYPlot();
		allLinePlot.setDomainPannable(true);
		allLinePlot.setRangePannable(true);
		
		DateAxis axis = (DateAxis) allLinePlot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy"));
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true

		ChartPanel chartPanel = new ChartPanel(charts[1]);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}

	static void updateDataBasicChartByYear() {
		//System.out.println("Entered updateDataBasicChartByYear()");
		/** Start Scatter Plot */
		TimeSeries series1 = new TimeSeries("Average Temperature per year");
		XYSeries series2 = new XYSeries("Average Temperature per year"); // Init series
		/** End Scatter Plot */
		//Map<String, Double> entries = new LinkedHashMap<>();
		//List<Double> entriesList = new ArrayList<>();
		double average = 0;
		int count = 0;
		XYPlot plot = charts[2].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		//List<Double> entriesAvg = new ArrayList<>();

		for(int i = 0; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			for(int j = 0; j < 12; j++) {
				if((String)View.getGlobalTable().getModel().getValueAt(i+j, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i+j, 1)).trim().isEmpty()) {
					//entries.put((String)View.getGlobalTable().getModel().getValueAt(i+j, 0), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i+j, 1)));
					//System.out.println("i: " + i + ", j: " + j);
					count++;
					average = average + Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i+j, 1));
				}
			}
			average = average/count;
			//entriesAvg.add(average);
			/** I do the substring(0, 4) because each year will be 1750-01-01, 1751-01-01 etc. -> to shorten to 1750, 1751 etc. */
			series1.add(new Year(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4))), average);
			/** Start Scatter Plot */
			series2.add(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)), average); // add elements
			/** End Scatter Plot */
			average = 0;
			count = 0;
			//entries.clear();
			//entriesList.clear();
		}

		/** Start Scatter Plot */
		((TimeSeriesCollection) datasetBasicLineChartByYear).addSeries(series1); // add series to dataset
		((XYSeriesCollection) datasetBasicScatterPlotByYear).addSeries(series2); // add series to dataset

		/** Begin trend display */
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		double[] coefficients = Regression.getOLSRegression(datasetBasicScatterPlotByYear, 0);
		double b = coefficients[0]; // intercept
		double m = coefficients[1]; // slope
		XYSeries trend = new XYSeries("Trend");
		double x = (double) series2.getDataItem(0).getXValue();
		trend.add(x, m * x + b);
		x = (double) series2.getDataItem(series2.getItemCount() - 1).getXValue();
		trend.add(x, m * x + b);
		r.setSeriesStroke(1, new BasicStroke(1.5f));
		r.setSeriesPaint(1, Color.BLACK);
		r.setSeriesVisibleInLegend(1, Boolean.FALSE, true);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		datasetBasicScatterPlotByYear.addSeries(trend);
		/** End trend display */
		/** End Scatter Plot */
		charts[1].setNotify(true);
		charts[2].setNotify(true);
	}

	static ChartPanel basicScatterPlotByYear() {
		datasetBasicScatterPlotByYear = new XYSeriesCollection();

		charts[2] = ChartFactory.createScatterPlot("Average Temperatures 1750-2015", "Year", "Average Temperature \u00B0C", datasetBasicScatterPlotByYear, PlotOrientation.VERTICAL, true, true, false);
		charts[2].setNotify(false);
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[2].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		//r.setSeriesLinesVisible(0, Boolean.TRUE);
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(charts[2]);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static ChartPanel basicScatterPlotCoolingDec() {
		datasetBasicScatterPlotCoolingDec = new XYSeriesCollection();

		charts[3] = ChartFactory.createScatterPlot("December Temperatures 1750-2015", "Year", "Temperature \u00B0C", datasetBasicScatterPlotCoolingDec, PlotOrientation.VERTICAL, true, true, false);
		charts[3].setNotify(false);
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[3].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(charts[3]);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static void updateScatterPlotCoolingDec() {
		//System.out.println("Entered updateScatterPlotCoolingDec()");
		/** Start Scatter Plot */
		XYSeries series = new XYSeries("Temperature in December per year"); // Init series
		XYPlot plot = charts[3].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		/** End Scatter Plot */
		//Map<String, Double> entries = Collections.synchronizedMap(new LinkedHashMap<>());
		//List<Double> entriesList = new ArrayList<>();
		//double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/** Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets */
		for(int i = 11; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				//entries.put((String)View.getGlobalTable().getModel().getValueAt(i, 0), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)));
				series.add(Double.parseDouble(((String) View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0,4)), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1))); // add elements
			}
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
		r.setSeriesStroke(1, new BasicStroke(1.5f));
		r.setSeriesPaint(1, Color.BLACK);
		r.setSeriesVisibleInLegend(1, Boolean.FALSE, true);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		datasetBasicScatterPlotCoolingDec.addSeries(trend);
		/** End trend display */
		/** End Scatter Plot */
		charts[3].setNotify(true);
	}
	
	static ChartPanel basicScatterPlotCoolingJun() {
		datasetBasicScatterPlotCoolingJun = new XYSeriesCollection();

		charts[4] = ChartFactory.createScatterPlot("June Temperatures 1750-2015", "Year", "Temperature \u00B0C", datasetBasicScatterPlotCoolingJun, PlotOrientation.VERTICAL, true, true, false);
		charts[4].setNotify(false);
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[4].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(charts[4]);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static void updateScatterPlotCoolingJun() {
		XYPlot plot = charts[4].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		
		//System.out.println("Entered updateScatterPlotCoolingJun()");
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in June per year"); // Init series
		// End Scatter Plot
		//Map<String, Double> entries = Collections.synchronizedMap(new LinkedHashMap<>());
		//List<Double> entriesList = new ArrayList<>();
		//double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/** Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets */
		for(int i = 5; i < View.getGlobalTable().getModel().getRowCount(); i+=12) {
			if((String)View.getGlobalTable().getModel().getValueAt(i, 1) != null && !((String)View.getGlobalTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				//entries.put((String)View.getGlobalTable().getModel().getValueAt(i, 0), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1)));
				series.add(Double.parseDouble(((String) View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0,4)), Double.parseDouble((String)View.getGlobalTable().getModel().getValueAt(i, 1))); // add elements
			}
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
		r.setSeriesStroke(1, new BasicStroke(1.5f));
		r.setSeriesPaint(1, Color.BLACK);
		r.setSeriesVisibleInLegend(1, Boolean.FALSE, true);
		//System.out.println(SwingUtilities.isEventDispatchThread()); // prints true
		datasetBasicScatterPlotCoolingJun.addSeries(trend);
		// End trend display
		// End Scatter Plot
		Main.getPB().setValue(Main.getPB().getValue() + 30);
		charts[4].setNotify(true);
	}
	
	static ChartPanel basicBarChartByCountry() {
		datasetBasicBarChartByCountry = new DefaultCategoryDataset();

		charts[5] = ChartFactory.createBarChart("Countries with Highest Overall Temperatures", "Country", "Average Temperature \u00B0C", datasetBasicBarChartByCountry, PlotOrientation.VERTICAL, true, true, false);
		charts[5].setNotify(false);
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = charts[5].getCategoryPlot();
		plot.setRangePannable(true);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		//renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		/** Default item label is above the bar. Use below method for inside bar. */
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));

		ChartPanel chartPanel = new ChartPanel(charts[5]);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static void updateBasicBarChartByCountry() {
		new SwingWorker<Map<String, Double>, Void>() {
			@Override
			protected Map<String, Double> doInBackground() {
				//Map<String, Double> entriesAvg = new LinkedHashMap<>();
				Map<String, List<Double>> entries = new LinkedHashMap<>();
				//double average = 0;
				//int count = 0;
				//Set<String> set = new LinkedHashSet<>();
				
				/*
				for(int i = 0; i < View.getCountryTable().getModel().getRowCount(); i++) {
				    String obj = (String)View.getCountryTable().getModel().getValueAt(i, 3);
				    if(!set.add(obj)) {
				        continue;
				    }
				}
				//System.out.println(set.size());
				*/
				/*// Iterates over whole data 243 times
				for(Iterator<String> i = set.iterator(); i.hasNext(); ) {
					String country = i.next();
					for(int j = 0; j < View.getCountryTable().getModel().getRowCount(); j++) {
						if(View.getCountryTable().getModel().getValueAt(j, 3).equals(country)) {
							if((String)View.getCountryTable().getModel().getValueAt(j, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(j, 1)).trim().isEmpty()) {
								count = count + 1;
								average = average + Double.parseDouble((String)View.getCountryTable().getModel().getValueAt(j, 1));
							}
						}
					}
					average = average/count;
					entriesAvg.put(country, average);
					average = 0;
					count = 0;
				}
				*/
				// Iterates over whole data 1 time
				/** Currently only sum of all values for each country, i.e. not averages */
				for(int i = 0; i < View.getCountryTable().getModel().getRowCount(); i++) {
					if((String)View.getCountryTable().getModel().getValueAt(i, 1) != null && !((String)View.getCountryTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
						/** Check if the Country already exists to avoid <code>NullPointerException</code> */
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
				//System.out.println(entries.size());
				/** Calculate averages and place into <code>HashMap</code> */
				Map<String, Double> mapAvg = new HashMap<>();
				for(int i = 0; i < entries.size(); i++) {
					String country = (String)entries.keySet().toArray()[i];
					//System.out.println(country);
					//System.out.println((entries.get(country).get(0))/(entries.get(country).get(1)));
					/** Check if the Country is in the <code>List</> to avoid <code>NullPointerException</code> */
					if(entries.containsKey(country)) {
						// Some extra checks
						if((entries.get(country).get(0))/(entries.get(country).get(1)) < 100) {
							mapAvg.put(country, (entries.get(country).get(0)/entries.get(country).get(1)));
						}
					}
				}
				//System.out.println(mapAvg);
				final Map<String, Double> entriesAvgSorted = sortByValueDescending(mapAvg);
				//System.out.println(entriesAvgSorted);
				return entriesAvgSorted;
			}
			@Override
			protected void done() {
				for(int i = 0; i < 10; i++) { // for sortByValueDescending()
					//System.out.println("Reached loop.");
					try {
						//System.out.println((get().get((String)get().keySet().toArray()[i])));
						if((get().get((String)get().keySet().toArray()[i])) < 100) { // Antarctica returns infinity because starts from 1950
							if(((String)get().keySet().toArray()[i]).equals("United Arab Emirates")) {
								datasetBasicBarChartByCountry.addValue(get().get((String)get().keySet().toArray()[i]), "Average temperature", "UAE");
							}
							else {	
								datasetBasicBarChartByCountry.addValue(get().get((String)get().keySet().toArray()[i]), "Average temperature", (String)get().keySet().toArray()[i]);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				/** Timer for pacing time. Comment <code>setNotify(boolean)</code> to use. */
				/*
				Timer timer = new Timer(100, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							if((get().get((String)get().keySet().toArray()[timerIndex])) < 100) { // Antarctica returns infinity because starts from 1950
								datasetBasicBarChartByCountry.addValue(get().get((String)get().keySet().toArray()[timerIndex]), "Average temperature", (String)get().keySet().toArray()[timerIndex]);
							}
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							e1.printStackTrace();
						}
						timerIndex++;
						if(timerIndex >= 10) ((Timer)e.getSource()).stop();
					}
				});
				timer.start();
				*/
				//Main.getPB().setValue(Main.getPB().getValue() + 30);
				charts[5].setNotify(true);
			}
		}.execute();
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

		charts[6] = ChartFactory.createBarChart("Countries with Greatest Change In Temperature Over a Century (1912-2012)", "Country", "Average Temperature \u00B0C", datasetDoubleBarChartByCountryGreatest, PlotOrientation.HORIZONTAL, true, true, false);
		charts[6].setNotify(false);
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = charts[6].getCategoryPlot();
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

		ChartPanel chartPanel = new ChartPanel(charts[6]);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	static ChartPanel doubleBarChartByCountryLeast() {
		datasetDoubleBarChartByCountryLeast = new DefaultCategoryDataset();

		charts[7] = ChartFactory.createBarChart("Countries with Least Change In Temperature Over a Century (1912-2012)", "Country", "Average Temperature \u00B0C", datasetDoubleBarChartByCountryLeast, PlotOrientation.HORIZONTAL, true, true, false);
		charts[7].setNotify(false);
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = charts[7].getCategoryPlot();
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

		ChartPanel chartPanel = new ChartPanel(charts[7]);
		chartPanel.setMouseWheelEnabled(true);

		return chartPanel;
	}
	
	/**
	 * Done in background thread.
	 */
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
		for(int i = 0; i < View.getCountryTable().getModel().getRowCount(); i++){
		    String obj = (String)View.getCountryTable().getModel().getValueAt(i, 3);
		    if(!set.add(obj)) {
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
				else if(((String)entriesDifferences.keySet().toArray()[i]).equals("Bosnia And Herzegovina")) {
					datasetDoubleBarChartByCountryGreatest.addValue(entriesAvg.get((String)entriesDifferences.keySet().toArray()[i]), "1912", "Bos. & Herz.");
					datasetDoubleBarChartByCountryGreatest.addValue(entriesAvgSecond.get((String)entriesDifferences.keySet().toArray()[i]), "2012", "Bos. & Herz.");
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
		SwingUtilities.invokeLater(() -> {
			Main.getPB().setValue(Main.getPB().getValue() + 30);
			charts[6].setNotify(true);
			charts[7].setNotify(true);
			Main.getPB().setValue(100);
			Main.getSplash().dispose();
			View.getFrame().setVisible(true);
		});
	}
	
	static ChartPanel multiXYLineChartByEconomy() {
		datasetMultiLineChartByEconomy = new XYSeriesCollection();

		charts[8] = ChartFactory.createXYLineChart("Average Temperatures of Top 5 World Economies (1900-2012)", "Year", "Average Temperature \u00B0C", datasetMultiLineChartByEconomy, PlotOrientation.VERTICAL, true, true, false);
		charts[8].setNotify(false);
		/** @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = charts[8].getXYPlot();
		//plot.setShadowGenerator(new DefaultShadowGenerator());
		//plot.setDomainGridlinesVisible(false);
		//plot.setRangeGridlinesVisible(false);
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, Boolean.TRUE);
		r.setSeriesShapesVisible(1, Boolean.FALSE);
		r.setDefaultOutlineStroke(new BasicStroke(2.0f));
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		ChartPanel chartPanel = new ChartPanel(charts[8]);
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
	
	/**
	 * Done in background thread.
	 */
	static void updateMultiXYLineChartByEconomy() {
		XYPlot plot = charts[8].getXYPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		
		final int numSeries = 5;
		
		double[] averages = new double[numSeries];
		//double average = 0;
		int[] counts = new int[numSeries];
		int[] iterCounts = new int[numSeries];
		//int count = 0;
		
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
			/** Avoid infinite values where the particular x-value doesn't exist */
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
					//System.out.println("United States exists.");
					//System.out.println("Entered first time");
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
		//System.out.println("Exited loop.");

		SwingUtilities.invokeLater(() -> {
			/*
			for(int i = 0; i < numSeries; i++) {
				datasetMultiLineChartByEconomy.addSeries(series[i]);
			}
			*/

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
				r.setSeriesVisibleInLegend(i+numSeries, Boolean.FALSE, true);
				datasetMultiLineChartByEconomy.addSeries(trend);
				/** Prints out slope of trend line for each country. */
				//System.out.println(countries[i] + ": " + m);
			}
			
			charts[8].setNotify(true);
		});
	}

}
