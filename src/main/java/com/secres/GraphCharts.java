package com.secres;

/**
 * A Utility class  (i.e. a class that is not meant to have instances)<br>
 * <P>
 * <code>GraphCharts</code> contains static methods for
 * returning the instances of each chart object.
 * 
 * @author Pranav Amarnath
 *
 */
public class GraphCharts {

	private static BasicLineChart basicLineChart = new BasicLineChart();
	private static AverageYearTempChart avgChart = new AverageYearTempChart();
	private static SeasonsChart seasonsCharts = new SeasonsChart();
	private static ThermometerAvgChart thermoChart = new ThermometerAvgChart();
	private static AvgTempBarCountry singleBarChart = new AvgTempBarCountry();
	private static AvgTempBarCountryChange doubleBarCharts = new AvgTempBarCountryChange();
	private static EconomyAvgLineChart economyChart = new EconomyAvgLineChart();
	
	static BasicLineChart getBasicLineChart() {
		return basicLineChart;
	}
	
	static AverageYearTempChart getAvgChart() {
		return avgChart;
	}
	
	static SeasonsChart getSeasonsCharts() {
		return seasonsCharts;
	}
	
	static ThermometerAvgChart getThermometerChart() {
		return thermoChart;
	}
	
	static AvgTempBarCountry getBarChart() {
		return singleBarChart;
	}
	
	static AvgTempBarCountryChange getBarChartChange() {
		return doubleBarCharts;
	}
	
	static EconomyAvgLineChart getEconomyChart() {
		return economyChart;
	}
	
}
