package view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataLabels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsBar;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import model.ResultBean;

/**
 * The class {@link TabResult} represents a layout which is used in a tab of the result tab sheet.
 * It presents the user the output data of the epitope selection script.
 * 
 * @author spaethju
 *
 */
@SuppressWarnings("serial")
public class TabResult extends VerticalLayout {

  private Grid resultGrid;
  private HorizontalLayout chartLayout, filterLayout;
  private HorizontalLayout infoLayout;
  private Filterable filterable;
  private String[] alleles;
  private TabSheet optionTab;

  /**
   * constructor, sets up a layout with a tab sheet if more than one solution was foung in the
   * result or without a tab sheet if just one solution was found.
   * 
   * @param resultBeans bean item container with the result or results if there are more than one
   *        solutions
   * @param alleles alleles stored in an array
   */
  public TabResult(BeanItemContainer<ResultBean> resultBeans, String[] alleles) {
    this.alleles = alleles;
    this.setSpacing(true);
    optionTab = new TabSheet();

    VerticalLayout oneResultLayout = new VerticalLayout();

    // if more than one result use a tab sheet
    if (resultBeans.size() > 1) {
      int counter = 1;
      optionTab.setStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
      for (Iterator<ResultBean> i = resultBeans.getItemIds().iterator(); i.hasNext();) {
        ResultBean bean = i.next();
        VerticalLayout moreResultsLayout = new VerticalLayout();
        moreResultsLayout.addComponents(createInfoLayout(bean), createFilterLayout(bean),
            createResultGrid(bean), createChartLayout(bean));
        optionTab.addTab(moreResultsLayout, counter + ". Solution");
        counter++;

      }
      this.addComponent(optionTab);

      // if just one result:
    } else {
      for (Iterator<ResultBean> i = resultBeans.getItemIds().iterator(); i.hasNext();) {
        ResultBean bean = i.next();
        oneResultLayout.addComponents(createInfoLayout(bean), createFilterLayout(bean),
            createResultGrid(bean), createChartLayout(bean));
      }
      this.addComponent(oneResultLayout);
    }
  }

  /**
   * Creates the Layout which displays info text for the user.
   * 
   * @return info layout with information text
   */
  public HorizontalLayout createInfoLayout(ResultBean resultBean) {
    infoLayout = new HorizontalLayout();
    infoLayout.setSpacing(true);
    infoLayout.setMargin(true);
    infoLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    DecimalFormat df = new DecimalFormat("###.##");

    VerticalLayout nOfEpitopesLayout = new VerticalLayout();
    nOfEpitopesLayout.addStyleName("padded");
    Label nOfEpitopesTitleLa = new Label("Epitopes");
    Label nOfEpitopesLa = new Label(Integer.toString(resultBean.getNof_epitopes()));
    nOfEpitopesLa.setStyleName("parameter");
    nOfEpitopesLayout.addComponents(nOfEpitopesTitleLa, nOfEpitopesLa);
    nOfEpitopesLayout.addStyleName("center");

    VerticalLayout nOfTaaEpitopesLayout = new VerticalLayout();
    nOfTaaEpitopesLayout.addStyleName("padded");
    Label nOfTaaEpitopesTitleLa = new Label("TAAs");
    Label nOfTaaEpitopesLa = new Label(Integer.toString(resultBean.getNof_taa_epitopes()));
    nOfTaaEpitopesLa.setStyleName("parameter");
    nOfTaaEpitopesLayout.addComponents(nOfTaaEpitopesTitleLa, nOfTaaEpitopesLa);
    nOfTaaEpitopesLayout.addStyleName("center");

    VerticalLayout epThreshLayout = new VerticalLayout();
    epThreshLayout.addStyleName("padded");
    Label epThreshTitleLa = new Label("Epitope Threshold");
    Label epThreshLa = new Label(df.format(resultBean.getThreshold_epitope()));
    epThreshLa.setStyleName("parameter");
    epThreshLayout.addComponents(epThreshTitleLa, epThreshLa);
    epThreshLayout.addStyleName("center");

    VerticalLayout disThreshLayout = new VerticalLayout();
    disThreshLayout.addStyleName("padded");
    Label disThreshTitleLa = new Label("Distance Threshold");
    Label disThreshLa = new Label(df.format(resultBean.getThreshold_distance()));
    disThreshLa.setStyleName("parameter");
    disThreshLayout.addComponents(disThreshTitleLa, disThreshLa);
    disThreshLayout.addStyleName("center");

    VerticalLayout antConsLayout = new VerticalLayout();
    antConsLayout.addStyleName("padded");
    Label antConsTitleLa = new Label("Antigen Constraint");
    Label antConsLa = new Label(df.format(resultBean.getAntigen_const()));
    antConsLa.setStyleName("parameter");
    antConsLayout.addComponents(antConsTitleLa, antConsLa);
    antConsLayout.addStyleName("center");

    VerticalLayout hlaConsLayout = new VerticalLayout();
    hlaConsLayout.addStyleName("padded");
    Label hlaConsTitleLa = new Label("Allele Constraint");
    Label hlaConsLa = new Label(df.format(resultBean.getHla_const()));
    hlaConsLa.setStyleName("parameter");
    hlaConsLayout.addComponents(hlaConsTitleLa, hlaConsLa);
    hlaConsLayout.addStyleName("center");

    VerticalLayout ovConsLayout = new VerticalLayout();
    ovConsLayout.addStyleName("padded");
    Label ovConsTitleLa = new Label("Overlap Constraint");
    Label ovConsLa = new Label(df.format(resultBean.getOverlap_const()));
    ovConsLa.setStyleName("parameter");
    ovConsLayout.addComponents(ovConsTitleLa, ovConsLa);
    ovConsLayout.addStyleName("center");

    VerticalLayout distanceLayout = new VerticalLayout();
    distanceLayout.addStyleName("padded");
    Label distanceTitleLa = new Label("Distance2Self");
    Label distanceLa = new Label(df.format(resultBean.getDistance2self()));
    distanceLa.setStyleName("parameter");
    distanceLayout.addComponents(distanceTitleLa, distanceLa);
    distanceLayout.addStyleName("center");

    VerticalLayout uncLayout = new VerticalLayout();
    uncLayout.addStyleName("padded");
    Label uncTitleLa = new Label("Uncertainty");
    Label uncLa = new Label(df.format(resultBean.getUncertainty()));
    uncLa.setStyleName("parameter");
    uncLayout.addComponents(uncTitleLa, uncLa);
    uncLayout.addStyleName("center");

    VerticalLayout covHlasLayout = new VerticalLayout();
    covHlasLayout.addStyleName("padded");
    Label covHlasTitleLa = new Label("Covered HLAs");
    String covHlas = df.format(resultBean.getCovered_hlas() * 100);
    Label covHlasLa = new Label(covHlas + " %");
    covHlasLa.setStyleName("parameter");
    covHlasLayout.addComponents(covHlasTitleLa, covHlasLa);
    covHlasLayout.addStyleName("center");

    VerticalLayout covAntigenLayout = new VerticalLayout();
    covAntigenLayout.addStyleName("padded");
    Label covAntigenTitleLa = new Label("Covered Antigens");
    String covAntigen = df.format(resultBean.getCovered_antigens() * 100);
    Label covAntigenLa = new Label(covAntigen + " %");
    covAntigenLa.setStyleName("parameter");
    covAntigenLayout.addComponents(covAntigenTitleLa, covAntigenLa);
    covAntigenLayout.addStyleName("center");

    VerticalLayout immLayout = new VerticalLayout();
    immLayout.addStyleName("padded");
    Label immTitleLa = new Label("Immunogenicity");
    Label immLa = new Label(df.format(resultBean.getImmunogenicity()));
    immLa.setStyleName("parameter");
    immLayout.addComponents(immTitleLa, immLa);
    immLayout.addStyleName("center");

    VerticalLayout riskLayout = new VerticalLayout();
    riskLayout.addStyleName("padded");
    Label riskTitleLa = new Label("Risk");
    Label riskLa = new Label();
    if (resultBean.getRisk().equals("None")) {
      riskLa.setValue("0");
    } else {
      String risk = df.format((resultBean.getRisk()));
      riskLa.setValue(risk);
    }
    riskLa.setStyleName("parameter");
    riskLayout.addComponents(riskTitleLa, riskLa);
    riskLayout.addStyleName("center");

    infoLayout.addComponents(nOfEpitopesLayout, nOfTaaEpitopesLayout, epThreshLayout,
        disThreshLayout, antConsLayout, hlaConsLayout, ovConsLayout, distanceLayout, uncLayout,
        covHlasLayout, covAntigenLayout, immLayout, riskLayout);
    infoLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    return infoLayout;

  }


  /**
   * Creates a layout including pie charts for filtering the grid.
   * 
   * @param resultBean result to present
   * @return layout with charts
   */
  public HorizontalLayout createFilterLayout(ResultBean resultBean) {
    filterLayout = new HorizontalLayout();
    filterLayout.setSizeFull();
    filterLayout.setSpacing(true);
    filterLayout.setMargin(true);
    ChartEpitopeType typeChart = new ChartEpitopeType(resultBean);
    filterLayout.addComponent(typeChart);
    ChartGene geneChart = new ChartGene(resultBean);
    filterLayout.addComponent(geneChart);
    // ChartChromosome chromChart = new ChartChromosome(resultBean);
    // filterLayout.addComponent(chromChart);

    typeChart.addPointClickListener((PointClickListener) event -> {
      setFilter("type", typeChart.getDataSeriesObject(event));
      // chromChart.unselect();
      geneChart.unselect();
    });


    geneChart.addPointClickListener(new PointClickListener() {
      @Override
      public void onClick(com.vaadin.addon.charts.PointClickEvent event) {
        setFilter("genes", geneChart.getDataSeriesObject(event));
        typeChart.unselect();
        // chromChart.unselect();
      }
    });

    filterLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    return filterLayout;
  }

  /**
   * Creates a layout with bar charts to show the #ps and #as part of the output of the epitope
   * selection script
   * 
   * @param resultBean result to present
   * @return layout with bar charts
   */
  public HorizontalLayout createChartLayout(ResultBean resultBean) {
    chartLayout = new HorizontalLayout();
    chartLayout.setSizeFull();
    chartLayout.setMargin(true);
    chartLayout.setSpacing(true);
    Chart psChart = createBarChart(resultBean.getPs(), "Genes");
    Chart asChart = createBarChart(resultBean.getAs(), "Alleles");
    chartLayout.addComponent(asChart);
    chartLayout.addComponent(psChart);

    chartLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    return chartLayout;
  }


  /**
   * Creates a layout including a grid. The grid shows all epitopes which are in the result computed
   * by the epitope selection script
   * 
   * @param resultBean
   * @return layout with grid
   */
  public HorizontalLayout createResultGrid(ResultBean resultBean) {
    HorizontalLayout resultGridLayout = new HorizontalLayout();
    resultGridLayout.setSpacing(true);
    resultGridLayout.setMargin(true);

    // adjust grid
    resultGrid = new Grid();
    resultGrid.setSizeFull();
    resultGrid.setHeightMode(HeightMode.ROW);
    if (resultBean.getEpitopeResultBeans().size() > 10) {
      resultGrid.setHeightByRows(10);
    } else {

      resultGrid.setHeightByRows(resultBean.getEpitopeResultBeans().size());
    }

    resultGrid.setContainerDataSource(resultBean.getEpitopeResultBeans());
    filterable = (Filterable) resultGrid.getContainerDataSource();
    resultGrid.setColumnOrder("neoepitope", "type", "genes", "mutations", "immA1", "distA1",
        "immA2", "distA2", "immB1", "distB1", "immB2", "distB2", "immC1", "distC1", "immC2",
        "distC2");
    resultGrid.setEditorEnabled(false);
    resultGrid.setEditorBuffered(false);
    resultGrid.removeColumn("alleles");
    resultGrid.setSelectionMode(SelectionMode.NONE);
    joinHeader();
    resultGrid.setImmediate(true);
    resultGrid.setVisible(true);

    resultGridLayout.addComponent(resultGrid);
    resultGridLayout.setSizeFull();

    return resultGridLayout;
  }

  /**
   * joins the headers and takes the allele as title
   */
  public void joinHeader() {
    String hlaA1 = alleles[0];
    String hlaA2 = alleles[3];
    String hlaB1 = alleles[1];
    String hlaB2 = alleles[4];
    String hlaC1 = alleles[2];
    String hlaC2 = alleles[5];
    HeaderRow hlaHeader = resultGrid.prependHeaderRow();
    HeaderCell hlaA1Cell = hlaHeader.join("immA1", "distA1");
    hlaA1Cell.setText(hlaA1);
    HeaderCell hlaA2Cell = hlaHeader.join("immA2", "distA2");
    hlaA2Cell.setText(hlaA2);
    HeaderCell hlaB1Cell = hlaHeader.join("immB1", "distB1");
    hlaB1Cell.setText(hlaB1);
    HeaderCell hlaB2Cell = hlaHeader.join("immB2", "distB2");
    hlaB2Cell.setText(hlaB2);
    HeaderCell hlaC1Cell = hlaHeader.join("immC1", "distC1");
    hlaC1Cell.setText(hlaC1);
    HeaderCell hlaC2Cell = hlaHeader.join("immC2", "distC2");
    hlaC2Cell.setText(hlaC2);

    resultGrid.getColumn("immA1").setHeaderCaption("Immunogenicity");
    resultGrid.getColumn("immA2").setHeaderCaption("Immunogenicity");
    resultGrid.getColumn("immB1").setHeaderCaption("Immunogenicity");
    resultGrid.getColumn("immB2").setHeaderCaption("Immunogenicity");
    resultGrid.getColumn("immC1").setHeaderCaption("Immunogenicity");
    resultGrid.getColumn("immC2").setHeaderCaption("Immunogenicity");
    resultGrid.getColumn("distA1").setHeaderCaption("Distance");
    resultGrid.getColumn("distA2").setHeaderCaption("Distance");
    resultGrid.getColumn("distB1").setHeaderCaption("Distance");
    resultGrid.getColumn("distB2").setHeaderCaption("Distance");
    resultGrid.getColumn("distC1").setHeaderCaption("Distance");
    resultGrid.getColumn("distC2").setHeaderCaption("Distance");
  }

  /**
   * Sets up the filter to a certain column filtering by a certain string
   * 
   * @param column to filter
   * @param filter string to filter the column
   */
  public void setFilter(String column, String filter) {
    Filter tmpFilter = new SimpleStringFilter(column, filter, true, false);
    if (!filterable.getContainerFilters().contains(tmpFilter)) {
      filterable.removeAllContainerFilters();
      filterable.addContainerFilter(tmpFilter);
    } else {
      filterable.removeContainerFilter(tmpFilter);
    }

  }


  /**
   * Creates a bar chart
   * 
   * @param map map containing the information for the chart
   * @param title title of the chart
   * @return chart with the information of the map
   */
  public Chart createBarChart(HashMap<String, String> map, String title) {
    Chart psChart = new Chart(ChartType.BAR);
    Configuration conf = psChart.getConfiguration();

    conf.setTitle(title);

    XAxis x = new XAxis();
    x.setTitle((String) null);
    for (String key : map.keySet()) {
      x.addCategory(key);
    }
    conf.addxAxis(x);

    YAxis y = new YAxis();
    y.setTitle(new AxisTitle(""));
    conf.addyAxis(y);

    PlotOptionsBar plot = new PlotOptionsBar();
    plot.setDataLabels(new DataLabels(true));
    conf.setPlotOptions(plot);

    conf.disableCredits();
    ListSeries listSeries = new ListSeries();
    listSeries.setName("log2(tumor_expression)");
    for (String key : map.keySet()) {
      listSeries.addData(Float.parseFloat(map.get(key)));
    }

    ArrayList<Series> series = new ArrayList<Series>();
    series.add(listSeries);
    conf.setSeries(series);

    psChart.setHeight("400px");
    psChart.setWidth("100%");
    psChart.drawChart(conf);

    return psChart;

  }

  public Filterable getFilterable() {
    return filterable;
  }

  public TabSheet getOptionTab() {
    return optionTab;
  }
}
