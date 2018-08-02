package view.chart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import model.ResultBean;

/**
 * The class {@link ChartEpitopeType} represents a Chart component, showing the relation of type in
 * the result epitopes predicted by the epitope selection script.
 *
 * @author spaethju
 */
@SuppressWarnings("serial")
public class ChartEpitopeType extends Chart {

  private DataSeries series;

  private Configuration conf;

  private PlotOptionsPie plotOptions;

  /**
   * @param bean result bean to use for the chart
   */
  public ChartEpitopeType(ResultBean bean) {
    super(ChartType.PIE);
    conf = this.getConfiguration();
    conf.setTitle("Type");
    conf.setSubTitle("Filter by Type");

    series = new DataSeries();
    series.setName("Type");

    // add number of taa epitopes if exist
    if (bean.getNof_taa_epitopes() > 0) {
      series.add(new DataSeriesItem("TAA", bean.getNof_taa_epitopes()));
    }

    // add number of tsa epitopes
    if (bean.getNof_epitopes() - bean.getNof_taa_epitopes() > 0) {
      series.add(new DataSeriesItem("TSA", (bean.getNof_epitopes() - bean.getNof_taa_epitopes())));
    }

    conf.getLegend().setEnabled(false);

    plotOptions = new PlotOptionsPie();
    plotOptions.setSize("100px");
    plotOptions.setAllowPointSelect(true);
    plotOptions.setCursor(Cursor.POINTER);
    conf.setPlotOptions(plotOptions);

    this.setHeight("200px");

    series.setName("Epitopes");
    conf.setSeries(series);

    this.setImmediate(true);

    this.drawChart(conf);

  }

  /**
   * @param event point click event
   * @return name of the current data series object
   */
  public String getDataSeriesObject(PointClickEvent event) {
    return this.series.get(event.getPointIndex()).getName();
  }

  /**
   * sets back all sliced items of the chart
   */
  public void unselect() {
    for (int i = 0; i < series.size(); i++) {
      series.setItemSliced(i, false, false, true);
    }
  }

}
