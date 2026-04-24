/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.chart.impl;

import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Mario
 */
public class CategoryChartData {

    private String titleOfChart = "";
    private String xAxisName = "";
    private String yAxisName = "";
    private PlotOrientation orientation = PlotOrientation.HORIZONTAL;
    private DefaultCategoryDataset dataset = null;
    private IntervalMarker intervalMarker = null;

    public CategoryChartData(String titleOfChart, String xAxisName, String yAxisName,
            PlotOrientation orientation, DefaultCategoryDataset dataset,
            IntervalMarker intervalMarker) {
        this.titleOfChart = titleOfChart;
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.orientation = orientation;
        this.dataset = dataset;
        this.intervalMarker = intervalMarker;

    }

    public CategoryChartData(String titleOfChart, DefaultCategoryDataset dataset) {
        this.titleOfChart = titleOfChart;
        this.dataset = dataset;
    }




    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    public void setDataset(DefaultCategoryDataset dataset) {
        this.dataset = dataset;
    }

    public IntervalMarker getIntervalMarker() {
        return intervalMarker;
    }

    public void setIntervalMarker(IntervalMarker intervalMarker) {
        this.intervalMarker = intervalMarker;
    }

    public PlotOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(PlotOrientation orientation) {
        this.orientation = orientation;
    }

    public String getTitleOfChart() {
        return titleOfChart;
    }

    public void setTitleOfChart(String titleOfChart) {
        this.titleOfChart = titleOfChart;
    }

    public String getxAxisName() {
        return xAxisName;
    }

    public void setxAxisName(String xAxisName) {
        this.xAxisName = xAxisName;
    }

    public String getyAxisName() {
        return yAxisName;
    }

    public void setyAxisName(String yAxisName) {
        this.yAxisName = yAxisName;
    }


}
