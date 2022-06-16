# RisingTemperatures
A Java application for analyzing climate change CSV data.<p>
Note: Fork of `Model` class architecture (PoC): [SecresCSV](https://github.com/PranavAmarnath/SecresCSV) - A GUI for opening/viewing, saving, printing, editing and/or refreshing multiple CSV files at a time in tabular format.

![image](https://user-images.githubusercontent.com/64337291/110227837-187ba680-7eb1-11eb-9aca-db3376051565.png)

## About the Data:
* Aims to analyze the temperature changes across 243 regions that cover approximately 197 countries of Planet Earth from 1750 to 2015
* Procured from Berkeley Earth, which is a California-based independent non-profit agency focused on land temperature data analysis for climate science
* Consists of five large datasets which have monthly average land and average ocean temperatures from 1750-2015 for 243 regions around the earth organized by state and major cities

## Methods/Design:
* Dependency Management/Build Automation:
  * Eclipse with Maven
  * Dependencies:
    * [JFreeChart](https://www.jfree.org/jfreechart/)
    * [FlatLaf](https://www.formdev.com/flatlaf/)
    * [OpenCSV](http://opencsv.sourceforge.net/)
  * Git to manage workflow
  * After every build, commit to git remote repo
* Wrapping Build with Installer:
  * `jpackage` --> wraps JAR file with installer to generate installer extension (i.e. exe/msi, dmg/pkg, deb/rpm) for platform dependence
  * Executable JAR file (created with Maven Shade Plugin) also used to maintain platform independence
* Reading Data:
  * OpenCSV parser in `Model` class
  * `View` class contains `JTable`; charts read from `JTable`'s `DefaultTableModel`s, not dataset files
* Optimization:
  * Optimizing Loading of Data
    * 3.5 hr --> 10 sec --> 3 sec
  * Optimizing Charts
    * 20 sec --> 2 sec
    * Used `HashMap` to iterate once through dataset for "change in temp. bar charts" to filter through data
* Visualization:
  * JFreeChart
  * Types of Visualizations:
    * `TimeSeries` line charts
    * Scatter plots with varying degree regression models
    * Line charts with regressions
    * Various types of bar charts to illustrate temperature and *change* in temperature
    * Thermometer Plot
* General Code:
  * Follows Modified-MVC Architecture
  * Model consists of CSV data and adds to `JTable`'s `DefaultTableModel`

## Discussion
* Visualizations progressed similarly at different intervals of time
  * Mid-Late 1700s - early 1800s --> Large uncertainty, data points scattered
  * Late 1800s - 1900s --> distinctive trends with discernible increase and decrease in temperature, adherence to regression models
  * Late 1900s - 2000s --> Unnatural increase in temperature, data points moving away from regression models, trends appear less distinct
  * All of the above --> Recent increase is unnatural, data has been moving away from regression line with positive slope, so temperature is increasing
* Other
  * Comparing linear and power regressions: global & regional temperature increasing linearly, not exponentially
  * Bar graphs identify temp. & change in temp. --> all regions have increased in temp. (1912-2012) except Kyrgyzstan
  * Effects: Rising temperatures, more droughts, intense hurricanes
  
## Future Applications/Research
* Fork of `Model` class architecture (PoC): [SecresCSV](https://github.com/PranavAmarnath/SecresCSV) - A GUI for opening/viewing, editing, printing, and/or saving (beta) multiple CSV files at a time in tabular format.
* Models calculated (linear & power) can be applied for future predictions - Ex. heatmap overlay from 2020-2030
* Future projects could fork and easily add new visualizations based on existing architecture (add new class that extends `AbstractGraph` with model and view methods, create instance in GraphCharts and coordinate with `View` class
* Applying sorting and filtering algorithms could prove useful in future charts/maps etc.
