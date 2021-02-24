package com.secres;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.general.DefaultValueDataset;

/**
 * A class creating the thermometer chart's view with the "AverageYearTempChart" scatter plot model.
 * 
 * @author Pranav Amarnath
 *
 */
public class ThermometerAvgChart extends AbstractGraph {

	DefaultValueDataset dataset = new DefaultValueDataset();
	JFreeChart chart;
	ChartPanel panel;
	
	/**
	 * View for thermometer chart; model retrieved from yearly average temp.
	 * @return <code>JPanel</code>
	 */
	@Override
	JPanel updateView() {
		dataset = new DefaultValueDataset();
		
		ThermometerPlot plot = new ThermometerPlot(dataset);
        chart = new JFreeChart("Thermometer Plot", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        plot.setInsets(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setPadding(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
        plot.setThermometerStroke(new BasicStroke(2.0f));
        plot.setThermometerPaint(Color.LIGHT_GRAY);
        plot.setUnits(ThermometerPlot.UNITS_CELCIUS);
        plot.setGap(3);
        plot.setRange(0.0, 10.0);
        plot.setSubrange(0, 0.0, 9.0);
        plot.setSubrangePaint(0, Color.GREEN);
        plot.setSubrange(2, 9.0, 10.0);
        plot.setSubrangePaint(2, Color.RED);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        panel = new ChartPanel(chart);
        mainPanel.add(panel);

        JSlider slider = new JSlider(1750, 2015, 1750);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(25);
        dataset.setValue(8.719); // avg. temp. in datasetBasicScatterPlotByYear at year 1750
        slider.addChangeListener(e -> {
        	dataset.setValue(GraphCharts.getAvgChart().getDatasetScatter().getYValue(0, slider.getValue()-1750));
        });
        mainPanel.add(slider, BorderLayout.SOUTH);
        
        return mainPanel;
	}
	
	/**
	 * Returns the dataset
	 * @return DefaultValueDataset
	 */
	@Override
	public DefaultValueDataset getDataset() {
		return dataset;
	}
	
	/**
	 * Returns the chart
	 * @return JFreeChart
	 */
	@Override
	public JFreeChart getChart() {
		return chart;
	}
	
	/**
	 * Returns the panel
	 * @return JPanel
	 */
	@Override
	public JPanel getPanel() {
		return panel;
	}
	
}
