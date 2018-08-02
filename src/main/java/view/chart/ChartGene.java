package view.chart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import model.EpitopeResultBean;
import model.ResultBean;

/**
 * The class {@link ChartGene} represents a Chart component, showing the relation of genes in the
 * result epitopes, predicted by the epitope selection script.
 *
 * @author spaethju
 */
@SuppressWarnings("serial")
public class ChartGene extends Chart {

  private DataSeries series;

  private Configuration conf;

  private PlotOptionsPie plotOptions;


  /**
   * @param bean result bean to use for the chart
   */
  public ChartGene(ResultBean bean) {
    super(ChartType.PIE);
    conf = this.getConfiguration();
    conf.setTitle("Genes");
    conf.setSubTitle("Filter by Gene");

    series = new DataSeries();

    // add genes
    for (String key : getGeneData(bean).keySet()) {
      series.add(new DataSeriesItem(key, getGeneData(bean).get(key)));
    }

    plotOptions = new PlotOptionsPie();
    plotOptions.setSize("100px");
    plotOptions.setAllowPointSelect(true);
    plotOptions.setCursor(Cursor.POINTER);

    conf.setPlotOptions(plotOptions);

    this.setImmediate(true);

    this.setHeight("200px");

    series.setName("Epitopes");
    conf.setSeries(series);
    this.drawChart(conf);

  }

  /**
   * @param bean result bean to use for the chart
   * @return map with genes as keys and its number as value.
   */
  public Map<String, Integer> getGeneData(ResultBean bean) {
    Map<String, Integer> genes = new HashMap<String, Integer>();
    for (Iterator<EpitopeResultBean> i = bean.getEpitopeResultBeans().getItemIds().iterator(); i
        .hasNext(); ) {
      EpitopeResultBean b = i.next();
      String gene = b.getGenes();
      if (genes.containsKey(gene)) {
        genes.put(gene, ((int) genes.get(gene)) + 1);
      } else {
        genes.put(gene, 1);
      }
    }
    return genes;
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
