package com.secres;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.*;

public class GraphCharts {
	
	private static DefaultCategoryDataset datasetBasicLineChart, datasetBasicLineChartByYear;
	
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
		
		/*
		CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		Crosshair crosshairDomain = new Crosshair(Double.NaN, Color.DARK_GRAY, new BasicStroke(0f));
		Crosshair crosshairRange = new Crosshair(Double.NaN, Color.DARK_GRAY, new BasicStroke(0f));
		crosshairOverlay.addDomainCrosshair(crosshairDomain);
		crosshairOverlay.addRangeCrosshair(crosshairRange);
		*/
		
		ChartPanel chartPanel = new ChartPanel(allLineChart);
		//chartPanel.setMouseWheelEnabled(true);
		chartPanel.setDomainZoomable(true);
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
		
		/*
		CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		Crosshair crosshairDomain = new Crosshair(Double.NaN, Color.DARK_GRAY, new BasicStroke(0f));
		Crosshair crosshairRange = new Crosshair(Double.NaN, Color.DARK_GRAY, new BasicStroke(0f));
		crosshairOverlay.addDomainCrosshair(crosshairDomain);
		crosshairOverlay.addRangeCrosshair(crosshairRange);
		*/
		
		ChartPanel chartPanel = new ChartPanel(allLineChart);
		chartPanel.setMouseWheelEnabled(true);
		return chartPanel;
	}
	
	static void updateDataBasicLineChartByYear() {
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
			datasetBasicLineChartByYear.addValue(average, "Average temperature per year", ((String)View.getTable().getModel().getValueAt(i, 0)).substring(0, 4));
			entries.clear();
			entriesList.clear();
		}
		Main.getSplash().dispose();
		View.getFrame().setVisible(true);
	}
	
}
