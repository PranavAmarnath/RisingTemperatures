package com.secres;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;

import javax.swing.SwingUtilities;

public class GraphCharts {

	private static DefaultCategoryDataset datasetBasicLineChart, datasetBasicLineChartByYear;
	private static XYSeriesCollection datasetBasicScatterPlotByYear, datasetBasicScatterPlotCoolingDec, datasetBasicScatterPlotCoolingJun;

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
		for(int i = 0; i < View.getTable().getModel().getRowCount(); i++) {
			if(!(((String)View.getTable().getModel().getValueAt(i, 1)).equals(""))) {
				datasetBasicLineChart.addValue(Double.parseDouble((String)View.getTable().getModel().getValueAt(i, 1)), "Temperature over time", (String)View.getTable().getModel().getValueAt(i, 0));
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
		for(int i = 0; i < View.getTable().getModel().getRowCount(); i+=12) {
			for(int j = 0; j < 12; j++) {
				if((String)View.getTable().getModel().getValueAt(i+j, 1) != null && !((String)View.getTable().getModel().getValueAt(i+j, 1)).trim().isEmpty()) {
					entries.put((String)View.getTable().getModel().getValueAt(i+j, 0), Double.parseDouble((String)View.getTable().getModel().getValueAt(i+j, 1)));
				}
				else {
					continue;
				}
			}
			//System.out.println(entries.size());
			int count = 0;
			for(int k = 0; k < entries.size(); k++) {
				if((String)View.getTable().getModel().getValueAt(i+k, 1) != null && !((String)View.getTable().getModel().getValueAt(i+k, 1)).trim().isEmpty()) {
					entriesList.add(entries.get((String)View.getTable().getModel().getValueAt(i+k, 0)));
					count++;
				}
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			for(int l = 0; l < entriesList.size(); l++) {
				average = average + entriesList.get(l);
			}
			average = average/count;
			//entriesAvg.add(average);
			datasetBasicLineChartByYear.addValue(average, "Average temperature per year", ((String)View.getTable().getModel().getValueAt(i, 0)).substring(0, 4));
			// Start Scatter Plot
			series.add(Integer.parseInt(((String)View.getTable().getModel().getValueAt(i, 0)).substring(0, 4)), average); // add elements
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
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in December per year"); // Init series
		// End Scatter Plot
		Map<String, Double> entries = new LinkedHashMap<>();
		List<Double> entriesList = new ArrayList<>();
		//double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/*
		 * Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets
		 */
		for(int i = 11; i < View.getTable().getModel().getRowCount(); i+=12) {
			if((String)View.getTable().getModel().getValueAt(i, 1) != null && !((String)View.getTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entries.put((String)View.getTable().getModel().getValueAt(i, 0), Double.parseDouble((String)View.getTable().getModel().getValueAt(i, 1)));
			}
			//System.out.println(entries.size());
			//int count = 0;
			if((String)View.getTable().getModel().getValueAt(i, 1) != null && !((String)View.getTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entriesList.add(entries.get((String)View.getTable().getModel().getValueAt(i, 0)));
				//count++;
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			//entriesAvg.add(average);
			// Start Scatter Plot
			for(int l = 0; l < entriesList.size(); l++) {
				if((String)View.getTable().getModel().getValueAt(i, 1) != null && !((String)View.getTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
					series.add(Double.parseDouble(((String) View.getTable().getModel().getValueAt(i, 0)).substring(0,4)), entriesList.get(l)); // add elements
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
		// Start Scatter Plot
		XYSeries series = new XYSeries("Temperature in June per year"); // Init series
		// End Scatter Plot
		Map<String, Double> entries = new LinkedHashMap<>();
		List<Double> entriesList = new ArrayList<>();
		//double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();

		/*
		 * Start read; take average of per-year data - save in LinkedHashMap, then ArrayList, then JFreeChart datasets
		 */
		for(int i = 5; i < View.getTable().getModel().getRowCount(); i+=12) {
			if((String)View.getTable().getModel().getValueAt(i, 1) != null && !((String)View.getTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entries.put((String)View.getTable().getModel().getValueAt(i, 0), Double.parseDouble((String)View.getTable().getModel().getValueAt(i, 1)));
			}
			//System.out.println(entries.size());
			//int count = 0;
			if((String)View.getTable().getModel().getValueAt(i, 1) != null && !((String)View.getTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
				entriesList.add(entries.get((String)View.getTable().getModel().getValueAt(i, 0)));
				//count++;
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			//entriesAvg.add(average);
			// Start Scatter Plot
			for(int l = 0; l < entriesList.size(); l++) {
				if((String)View.getTable().getModel().getValueAt(i, 1) != null && !((String)View.getTable().getModel().getValueAt(i, 1)).trim().isEmpty()) {
					series.add(Double.parseDouble(((String) View.getTable().getModel().getValueAt(i, 0)).substring(0,4)), entriesList.get(l)); // add elements
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

		Main.getSplash().dispose();
		View.getFrame().setVisible(true);
	}

	/*
	static void updateBasicScatterPlotByYear() {
		XYSeries series = new XYSeries("Average temperature per year");
		Map<String, Double> entries = new LinkedHashMap<>();
		List<Double> entriesList = new ArrayList<>();
		double average = 0;
		//List<Double> entriesAvg = new ArrayList<>();
		for(int i = 0; i < View.getTable().getModel().getRowCount(); i+=12) {
			for(int j = 0; j < 12; j++) {
				if((String)View.getTable().getModel().getValueAt(i+j, 1) != null && !((String)View.getTable().getModel().getValueAt(i+j, 1)).trim().isEmpty()) {
					entries.put((String)View.getTable().getModel().getValueAt(i+j, 0), Double.parseDouble((String)View.getTable().getModel().getValueAt(i+j, 1)));
				}
				else {
					continue;
				}
			}
			//System.out.println(entries.size());
			int count = 0;
			for(int k = 0; k < entries.size(); k++) {
				if((String)View.getTable().getModel().getValueAt(i+k, 1) != null && !((String)View.getTable().getModel().getValueAt(i+k, 1)).trim().isEmpty()) {
					entriesList.add(entries.get((String)View.getTable().getModel().getValueAt(i+k, 0)));
					count++;
				}
			}
			//System.out.println(Arrays.toString(entriesList.toArray()));
			for(int l = 0; l < entriesList.size(); l++) {
				average = average + entriesList.get(l);
			}
			average = average/count;
			//entriesAvg.add(average);
			series.add(Integer.parseInt(((String)View.getTable().getModel().getValueAt(i, 0)).substring(0, 4)), average);
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
