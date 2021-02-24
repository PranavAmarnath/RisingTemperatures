package com.secres;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

/**
 * A class containing the Average Change in Temperature for Countries Chart sorted.
 * 
 * @author Pranav Amarnath
 *
 */
public class AvgTempBarCountryChange extends AbstractGraph {

	DefaultCategoryDataset datasetGreatest, datasetLeast;
	JFreeChart chartGreatest, chartLeast;
	ChartPanel panelGreatest;
	ChartPanel panelLeast;
	
	/**
	 * Double bar chart organized by greatest difference in temperature from 1912-2012
	 * @return the <code>ChartPanel</code>
	 */
	JPanel updateViewGreatest() {
		final int NUM_COUNTRIES = 240;
		JScrollBar scroller = new JScrollBar(SwingConstants.VERTICAL, 0, 10, 0, NUM_COUNTRIES);
		
		datasetGreatest = new DefaultCategoryDataset();
		SlidingCategoryDataset dataset = new SlidingCategoryDataset(datasetGreatest, 0, 10);
		
		JPanel scrollPanel = new JPanel(new BorderLayout());
		//scroller.putClientProperty("JScrollBar.showButtons", true);
		scrollPanel.add(scroller);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(60, 2, 60, 2));
		scrollPanel.setBackground(Color.WHITE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPanel, BorderLayout.EAST);
		
		scroller.getModel().addChangeListener(e -> {
			dataset.setFirstCategoryIndex(scroller.getValue());
			scroller.repaint(); // removes scroll bar paint artifacts
		});

		chartGreatest = ChartFactory.createBarChart("Countries with Highest Net Change In Temperature (1912-2012)", "Country", "Average Temperature \u00B0C", dataset, PlotOrientation.HORIZONTAL, true, true, false);
		chartGreatest.setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = chartGreatest.getCategoryPlot();
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

		panelGreatest = new ChartPanel(chartGreatest);
		panelGreatest.setMouseWheelEnabled(true);
		mainPanel.add(panelGreatest);

		return mainPanel;
	}
	
	/**
	 * Double bar chart organized by least difference in temperature from 1912-2012
	 * @return the <code>ChartPanel</code>
	 */
	JPanel updateViewLeast() {
		final int NUM_COUNTRIES = 240;
		JScrollBar scroller = new JScrollBar(SwingConstants.VERTICAL, 0, 10, 0, NUM_COUNTRIES);
		
		datasetLeast = new DefaultCategoryDataset();
		SlidingCategoryDataset dataset = new SlidingCategoryDataset(datasetLeast, 0, 10);
		
		JPanel scrollPanel = new JPanel(new BorderLayout());
		//scroller.putClientProperty("JScrollBar.showButtons", true);
		scrollPanel.add(scroller);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(60, 2, 60, 2));
		scrollPanel.setBackground(Color.WHITE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPanel, BorderLayout.EAST);
		
		scroller.getModel().addChangeListener(e -> {
			dataset.setFirstCategoryIndex(scroller.getValue());
			scroller.repaint(); // removes scroll bar paint artifacts
		});

		chartLeast = ChartFactory.createBarChart("Countries with Least Net Change In Temperature (1912-2012)", "Country", "Average Temperature \u00B0C", dataset, PlotOrientation.HORIZONTAL, true, true, false);
		chartLeast.setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = chartLeast.getCategoryPlot();
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

		panelLeast = new ChartPanel(chartLeast);
		panelLeast.setMouseWheelEnabled(true);
		mainPanel.add(panelLeast);

		return mainPanel;
	}
	
	/** Adds data to both of the double bar charts by country organized by difference */
	@Override
	void updateModel() {
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
				datasetGreatest.addValue(entriesAvgSorted.get((String)entriesDifferences.keySet().toArray()[i]), "1912", ((String)entriesDifferences.keySet().toArray()[i]).substring(0, 9) + "...");
				datasetGreatest.addValue(entriesAvgSecondSorted.get((String)entriesDifferences.keySet().toArray()[i]), "2012", ((String)entriesDifferences.keySet().toArray()[i]).substring(0, 9) + "...");
			}
			else {
				datasetGreatest.addValue(entriesAvgSorted.get((String)entriesDifferences.keySet().toArray()[i]), "1912", (String)entriesDifferences.keySet().toArray()[i]);
				datasetGreatest.addValue(entriesAvgSecondSorted.get((String)entriesDifferences.keySet().toArray()[i]), "2012", (String)entriesDifferences.keySet().toArray()[i]);
			}
		}
		for(int i = 0; i < NUM_COUNTRIES; i++) {
			if(((String)entriesDifferencesLeast.keySet().toArray()[i]).length() > 10) {
				datasetLeast.addValue(entriesAvgLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "1912", ((String)entriesDifferencesLeast.keySet().toArray()[i]).substring(0, 9) + "...");
				datasetLeast.addValue(entriesAvgSecondLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "2012", ((String)entriesDifferencesLeast.keySet().toArray()[i]).substring(0, 9) + "...");
			}
			else {
				datasetLeast.addValue(entriesAvgLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "1912", (String)entriesDifferencesLeast.keySet().toArray()[i]);
				datasetLeast.addValue(entriesAvgSecondLeast.get((String)entriesDifferencesLeast.keySet().toArray()[i]), "2012", (String)entriesDifferencesLeast.keySet().toArray()[i]);
			}
		}
		chartGreatest.setNotify(true);
		chartLeast.setNotify(true);
		panelGreatest.restoreAutoBounds(); // Auto-ranges axes
		panelLeast.restoreAutoBounds(); // Auto-ranges axes
	}
	
	/** 
	 * Reference: @see https://stackoverflow.com/a/23846961/13772184
	 * @param map  the map
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
		map = map.entrySet().stream()
			       .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(
			    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return map;
    }
	
	/** 
	 * Reference: @see https://stackoverflow.com/a/23846961/13772184
	 * @param map  the map
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		map = map.entrySet().stream()
			       .sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(
			    	          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return map;
    }
	
	/**
	 * Returns the panel containing bar chart of countries ordered by greatest temperature
	 * @return JPanel
	 */
	public JPanel getPanelGreatest() {
		return panelGreatest;
	}
	
	/**
	 * Returns the panel containing bar chart of countries ordered by least temperature
	 * @return JPanel
	 */
	public JPanel getPanelLeast() {
		return panelLeast;
	}
	
}
