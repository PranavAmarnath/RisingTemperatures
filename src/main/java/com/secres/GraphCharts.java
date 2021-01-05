package com.secres;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
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

public class GraphCharts {

	private static DefaultCategoryDataset datasetBasicLineChart, datasetBasicLineChartByYear;
	private static XYSeriesCollection datasetBasicScatterPlotByYear, datasetBasicScatterPlotCoolingDec, datasetBasicScatterPlotCoolingJun;
	private static DefaultCategoryDataset datasetBasicBarChartByCountry, datasetDoubleBarChartByCountry;
	
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
		// Start Scatter Plot
		XYSeries series = new XYSeries("Average temperature per year"); // Init series
		// End Scatter Plot
		Map<String, Double> entries = new LinkedHashMap<>();
		List<Double> entriesList = new ArrayList<>();
		double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/*
		 * Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets
		 */
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
					//entriesList.add(entries.get((String)View.getGlobalTable().getModel().getValueAt(i+k, 0)));
					count++;
					average = average + entries.get((String)View.getGlobalTable().getModel().getValueAt(i+k, 0));
				}
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			/*
			for(int l = 0; l < entriesList.size(); l++) {
				average = average + entriesList.get(l);
			}
			*/
			average = average/count;
			//entriesAvg.add(average);
			datasetBasicLineChartByYear.addValue(average, "Average temperature per year", ((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4));
			// Start Scatter Plot
			series.add(Integer.parseInt(((String)View.getGlobalTable().getModel().getValueAt(i, 0)).substring(0, 4)), average); // add elements
			// End Scatter Plot
			entries.clear();
			entriesList.clear();
		}

		// Start Scatter Plot
		((XYSeriesCollection) datasetBasicScatterPlotByYear).addSeries(series); // add series to dataset

		// Begin trend display
		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
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
		// End trend display
		// End Scatter Plot
	}

	static ChartPanel basicScatterPlotByYear() {
		datasetBasicScatterPlotByYear = new XYSeriesCollection();

		JFreeChart scatterPlot = ChartFactory.createScatterPlot("Average Temperatures 1750-2015", "Year", "Average Temperature \u00B0C", datasetBasicScatterPlotByYear, PlotOrientation.VERTICAL, true, true, false);

		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
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

		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
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
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in December per year"); // Init series
		// End Scatter Plot
		Map<String, Double> entries = Collections.synchronizedMap(new LinkedHashMap<>());
		List<Double> entriesList = new ArrayList<>();
		//double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/*
		 * Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets
		 */
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
		((XYSeriesCollection) datasetBasicScatterPlotCoolingDec).addSeries(series); // add series to dataset

		// Begin trend display
		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
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
		// End trend display
		// End Scatter Plot
	}
	
	static ChartPanel basicScatterPlotCoolingJun() {
		datasetBasicScatterPlotCoolingJun = new XYSeriesCollection();

		JFreeChart scatterPlot = ChartFactory.createScatterPlot("June Temperatures 1750-2015", "Year", "Temperature \u00B0C", datasetBasicScatterPlotCoolingJun, PlotOrientation.VERTICAL, true, true, false);

		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
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

		/*
		 * Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets
		 */
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
		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
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

		JFreeChart barChart = ChartFactory.createBarChart("Countries with Highest Overall Temperatures", "Country", "Average Temperature \u00B0C", datasetBasicBarChartByCountry, PlotOrientation.VERTICAL, false, true, false);

		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setRangePannable(true);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		//renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		/*
		 * Default item label is above the bar. Use below method for inside bar.
		 */
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
				datasetBasicBarChartByCountry.addValue(entriesAvg.get((String)entriesAvg.keySet().toArray()[i]), "Average temperature per year", (String)entriesAvg.keySet().toArray()[i]);
			}
		}
		/*
		entriesAvg = sortByValue(entriesAvg);
		for(int i = 0; i < 10; i++) { // for sortByValue()
			datasetBasicBarChartByCountry.addValue(entriesAvg.get((String)entriesAvg.keySet().toArray()[i]), (String)entriesAvg.keySet().toArray()[i], (String)entriesAvg.keySet().toArray()[i]);
		}
		*/
		SwingUtilities.invokeLater(() -> {
			Main.getPB().setValue(Main.getPB().getValue() + 20);
		});
	}
	
	/*
	 * @see https://stackoverflow.com/a/23846961/13772184
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
		map = map.entrySet().stream()
			       .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(
			    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));;

        return map;
    }
	
	/*
	 * @see https://stackoverflow.com/a/2581754/13772184
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	static ChartPanel doubleBarChartByCountry() {
		datasetDoubleBarChartByCountry = new DefaultCategoryDataset();

		JFreeChart barChart = ChartFactory.createBarChart("Countries with Highest Change In Temperature Over a Century (1912-2012)", "Country", "Average Temperature \u00B0C", datasetDoubleBarChartByCountry, PlotOrientation.HORIZONTAL, true, true, false);

		/*
		 * @see https://stackoverflow.com/a/61398612/13772184
		 */
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setRangePannable(true);
		//Font font = new Font("SansSerif", Font.BOLD, 15);
		//plot.getDomainAxis().setTickLabelFont(font);
		
		CategoryItemRenderer renderer = plot.getRenderer(); 
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()); 

		renderer.setDefaultItemLabelGenerator(generator);
		renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		/*
		 * Default item label is above the bar. Use below method for inside bar.
		 */
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
		entriesDifferences = sortByValueDescending(entriesDifferences);
		entriesAvg = sortByValueDescending(entriesAvg);
		entriesAvgSecond = sortByValueDescending(entriesAvgSecond);
		for(int i = 0; i < 20; i++) {
			if((entriesAvgSecond.get((String)entriesAvgSecond.keySet().toArray()[i])) < 100) {
				if(((String)entriesDifferences.keySet().toArray()[i]).equals("Canada")) {
					continue;
				}
				else {
					datasetDoubleBarChartByCountry.addValue(entriesAvg.get((String)entriesDifferences.keySet().toArray()[i]), "1912", (String)entriesDifferences.keySet().toArray()[i]);
					datasetDoubleBarChartByCountry.addValue(entriesAvgSecond.get((String)entriesDifferences.keySet().toArray()[i]), "2012", (String)entriesDifferences.keySet().toArray()[i]);
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
		
		//System.out.println("Reached view visible.");
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
