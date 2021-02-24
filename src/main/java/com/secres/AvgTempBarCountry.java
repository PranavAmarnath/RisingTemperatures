package com.secres;

import java.awt.BorderLayout;
import java.awt.Color;
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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
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
 * A class containing the Average Temperature by Country Bar Chart.
 * 
 * @author Pranav Amarnath
 *
 */
public class AvgTempBarCountry extends AbstractGraph {

	DefaultCategoryDataset dataset;
	JFreeChart chart;
	ChartPanel panel;
	
	/**
	 * Average temperature per country bar chart with scroll bar
	 * @return the <code>JPanel</code>
	 */
	@Override
	JPanel updateView() {
		final int NUM_COUNTRIES = 242;
		JScrollBar scroller = new JScrollBar(SwingConstants.HORIZONTAL, 0, 10, 0, NUM_COUNTRIES);
		
		dataset = new DefaultCategoryDataset();
		SlidingCategoryDataset dataset = new SlidingCategoryDataset(this.dataset, 0, 10);
		
		chart = ChartFactory.createBarChart("Countries with Highest Overall Temperatures", "Country", "Average Temperature \u00B0C", dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRangePannable(true);
		
		BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
		/** Reference: @see https://www.jfree.org/forum/viewtopic.php?t=30541 */
		barRenderer.setBarPainter(new StandardBarPainter()); // Flat look - RGB Red: (255, 85, 85)
		
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // Rotates axis labels to 45 degrees
        
		JPanel scrollPanel = new JPanel(new BorderLayout());
		//scroller.putClientProperty("JScrollBar.showButtons", true);
		scrollPanel.add(scroller);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 2, 5));
		scrollPanel.setBackground(Color.WHITE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPanel, BorderLayout.SOUTH);
		
		scroller.getModel().addChangeListener(e -> {
			dataset.setFirstCategoryIndex(scroller.getValue());
			scroller.repaint(); // removes scroll bar paint artifacts
		});
		
		CategoryItemRenderer renderer = plot.getRenderer();
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
		renderer.setDefaultItemLabelGenerator(generator);
		//renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 12)); // just font change optional
		renderer.setDefaultItemLabelsVisible(true);
		// Default item label is above the bar. Use below method for inside bar.
		//renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));

		panel = new ChartPanel(chart);
		panel.setMouseWheelEnabled(true);
		mainPanel.add(panel);

		return mainPanel;
	}
	
	/** Adds data to the average temperature by country bar chart */
	@Override
	void updateModel() {
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
				dataset.addValue(entriesAvgSorted.get((String)entriesAvgSorted.keySet().toArray()[i]), "Average temperature", ((String)entriesAvgSorted.keySet().toArray()[i]).substring(0, 9) + "...");
			}
			else {	
				dataset.addValue(entriesAvgSorted.get((String)entriesAvgSorted.keySet().toArray()[i]), "Average temperature", (String)entriesAvgSorted.keySet().toArray()[i]);
			}
		}
		chart.setNotify(true);
		panel.restoreAutoBounds(); // Auto-ranges axes
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
	
}
