package com.secres;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A class containing a line chart with the Top 5 World Economies Average Yearly Temperature from 1900 to 2012.
 * 
 * @author Pranav Amarnath
 *
 */
public class EconomyAvgLineChart extends AbstractGraph {

	XYSeriesCollection dataset;
	JFreeChart chart;
	ChartPanel panel;
	
	/**
	 * Multi-series line chart for Top 5 economies
	 * @return the <code>JPanel</code>
	 */
	@Override
	JPanel updateView() {
		dataset = new XYSeriesCollection();

		chart = ChartFactory.createXYLineChart("Average Temperature of Top 5 World Economies (1900-2012)", "Year", "Average Temperature \u00B0C", dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setNotify(false);
		/** Reference: @see https://stackoverflow.com/a/61398612/13772184 */
		XYPlot plot = chart.getXYPlot();
		//plot.setShadowGenerator(new DefaultShadowGenerator());
		//plot.setDomainGridlinesVisible(false);
		//plot.setRangeGridlinesVisible(false);
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesLinesVisible(1, true);
		r.setSeriesShapesVisible(1, false);
		r.setDefaultOutlineStroke(new BasicStroke(2.0f));
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		panel = new ChartPanel(chart);
		panel.setMouseWheelEnabled(true);

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

        panel.addOverlay(crosshairOverlay);
        
        panel.addChartMouseListener(new ChartMouseListener() {
			@Override
		    public void chartMouseClicked(ChartMouseEvent event) {
		        // ignore
		    }

		    @Override
		    public void chartMouseMoved(ChartMouseEvent event) {
		        Rectangle2D dataArea = panel.getScreenDataArea();
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
				panel.addOverlay(crosshairOverlay);
			}
			else {
				panel.removeOverlay(crosshairOverlay);
			}
        });
        JPanel crosshairCheckboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        crosshairCheckboxPanel.add(enableCrosshair);
        
        mainPanel.add(panel);
        mainPanel.add(crosshairCheckboxPanel, BorderLayout.SOUTH);
		
		return mainPanel;
	}
	
	/** Adds data to the multi-series line chart for Top 5 economies */
	@Override
	void updateModel() {
		XYPlot plot = chart.getXYPlot();
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
			dataset.addSeries(series[i]);
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
			double[] coefficients = Regression.getOLSRegression(dataset, i);
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
			dataset.addSeries(trend);
			// Prints out slope of trend line for each country:
			//System.out.println(countries[i] + ": " + m);
		}

		chart.setNotify(true);
	}
	
}
