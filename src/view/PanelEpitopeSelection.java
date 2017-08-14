package view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import model.EpitopeSelectionBean;

/**
 * 
 * The class {@link PanelEpitopeSelection} represents a component for showing the uploaded epitope
 * selection data.
 * 
 * @author spaethju
 * 
 */
@SuppressWarnings({"serial"})
public class PanelEpitopeSelection extends CustomComponent {

  private Grid dataGrid;
  private Panel panel;
  private VerticalLayout infoLayout, panelContent;
  private BeanItemContainer<EpitopeSelectionBean> container;
  private String hlaA1, hlaA2, hlaB1, hlaB2, hlaC1, hlaC2;
  private HeaderRow header;
  private TextField geneTf, neopeptideTf, lengthTf, mutationTf, transcriptTf, typeTf;
  private Filterable filterable;
  private NativeSelect methodSelect;
  private String methodColumn;

  /**
   * Constructor
   */
  public PanelEpitopeSelection() {
    panel = new Panel();
    panelContent = new VerticalLayout();
    panelContent.setMargin(true);
    panelContent.setSpacing(true);

    dataGrid = new Grid();
    
    
    setCompositionRoot(panel);
  }

  /**
   * @return grid showing all the neopeptides
   */
  public Grid getDataGrid() {
    return dataGrid;
  }
  

  /**
   * Sets up the grid with the epitope data of the uploaded input.
   * 
   * @param container bean item container including all epitope selection beans representing a
   *        neopeptide of the uploaded input data
   */
  public void setDataGrid(BeanItemContainer<EpitopeSelectionBean> container, String methodColumn) {
    this.container = container;
    this.setMethodColumn(methodColumn);
    dataGrid.setSizeFull();

    // Set Allele Names for Headers
    String[] alleleNames =
        container.getItem(container.firstItemId()).getBean().prepareAlleleNames();
    hlaA1 = new String(alleleNames[0]);
    hlaA2 = new String(alleleNames[1]);
    hlaB1 = new String(alleleNames[2]);
    hlaB2 = new String(alleleNames[3]);
    hlaC1 = new String(alleleNames[4]);
    hlaC2 = new String(alleleNames[5]);


    // set epitope selection bean item container as container data source
    dataGrid.setContainerDataSource(container);

    // adjust grid
    dataGrid.setColumnOrder("included", "excluded", "neopeptide", "type", "length", "mutation",
        "gene", "transcript", "transcriptExpression", "hlaA1", "distanceA1", "uncertaintyA1",
        "hlaA2", "distanceA2", "uncertaintyA2", "hlaB1", "distanceB1", "uncertaintyB1", "hlaB2",
        "distanceB2", "uncertaintyB2", "hlaC1", "distanceC1", "uncertaintyC1", "hlaC2",
        "distanceC2", "uncertaintyC2");
    dataGrid.removeColumn("imm");
    dataGrid.removeColumn("dist");
    dataGrid.removeColumn("unc");
    dataGrid.setEditorEnabled(true);
    dataGrid.setEditorBuffered(false);
    dataGrid.setSelectionMode(SelectionMode.NONE);
    dataGrid.getColumn("included").setRenderer(new HtmlRenderer(), new BooleanConverter());
    dataGrid.getColumn("excluded").setRenderer(new HtmlRenderer(), new BooleanConverter());
    dataGrid.getColumn("neopeptide").setEditable(false);
    dataGrid.removeColumn("method");
    dataGrid.getColumn("length").setEditable(false);
    dataGrid.getColumn("gene").setEditable(false);
    dataGrid.getColumn("mutation").setEditable(false);
    dataGrid.getColumn("transcript").setEditable(false);
    dataGrid.getColumn("transcriptExpression").setEditable(false);
    dataGrid.getColumn("type").setEditable(false);
    dataGrid.getColumn("hlaA1").setEditable(false);
    dataGrid.getColumn("hlaA1").setHeaderCaption(hlaA1);
    dataGrid.getColumn("hlaA2").setEditable(false);
    dataGrid.getColumn("hlaA2").setHeaderCaption(hlaA2);
    dataGrid.getColumn("hlaB1").setEditable(false);
    dataGrid.getColumn("hlaB1").setHeaderCaption(hlaB1);
    dataGrid.getColumn("hlaB2").setEditable(false);
    dataGrid.getColumn("hlaB2").setHeaderCaption(hlaB2);
    dataGrid.getColumn("hlaC1").setEditable(false);
    dataGrid.getColumn("hlaC1").setHeaderCaption(hlaC1);
    dataGrid.getColumn("hlaC2").setEditable(false);
    dataGrid.getColumn("hlaC2").setHeaderCaption(hlaC2);
    dataGrid.getColumn("distanceA1").setEditable(false);
    dataGrid.getColumn("distanceA1").setHeaderCaption("Distance");
    dataGrid.getColumn("distanceA2").setEditable(false);
    dataGrid.getColumn("distanceA2").setHeaderCaption("Distance");
    dataGrid.getColumn("distanceB1").setEditable(false);
    dataGrid.getColumn("distanceB1").setHeaderCaption("Distance");
    dataGrid.getColumn("distanceB2").setEditable(false);
    dataGrid.getColumn("distanceB2").setHeaderCaption("Distance");
    dataGrid.getColumn("distanceC1").setEditable(false);
    dataGrid.getColumn("distanceC1").setHeaderCaption("Distance");
    dataGrid.getColumn("distanceC2").setEditable(false);
    dataGrid.getColumn("distanceC2").setHeaderCaption("Distance");
    dataGrid.getColumn("uncertaintyA1").setEditable(false);
    dataGrid.getColumn("uncertaintyA1").setHeaderCaption("Uncertainty");
    dataGrid.getColumn("uncertaintyA2").setEditable(false);
    dataGrid.getColumn("uncertaintyA2").setHeaderCaption("Uncertainty");
    dataGrid.getColumn("uncertaintyB1").setEditable(false);
    dataGrid.getColumn("uncertaintyB1").setHeaderCaption("Uncertainty");
    dataGrid.getColumn("uncertaintyB2").setEditable(false);
    dataGrid.getColumn("uncertaintyB2").setHeaderCaption("Uncertainty");
    dataGrid.getColumn("uncertaintyC1").setEditable(false);
    dataGrid.getColumn("uncertaintyC1").setHeaderCaption("Uncertainty");
    dataGrid.getColumn("uncertaintyC2").setEditable(false);
    dataGrid.getColumn("uncertaintyC2").setHeaderCaption("Uncertainty");
    dataGrid.getColumn("included").setHeaderCaption("In");
    dataGrid.getColumn("excluded").setHeaderCaption("Out");

    dataGrid.setHeightMode(HeightMode.ROW);
    dataGrid.setHeightByRows(10);
    dataGrid.setVisible(true);

    // set row style for include and exclude
    dataGrid.setRowStyleGenerator(rowRef -> {// Java 8
      if (((Boolean) rowRef.getItem().getItemProperty("excluded").getValue()).booleanValue())
        return "excluded";
      if (((Boolean) rowRef.getItem().getItemProperty("included").getValue()).booleanValue())
        return "included";
      else
        return null;
    });
    

    // set up filter
    header = dataGrid.prependHeaderRow();
    neopeptideTf = createFieldFilter(dataGrid.getColumn("neopeptide"));
    neopeptideTf.setWidth("120px");
    lengthTf = createFieldFilter(dataGrid.getColumn("length"));
    lengthTf.setWidth("50px");
    geneTf = createFieldFilter(dataGrid.getColumn("gene"));
    geneTf.setWidth("100px");
    mutationTf = createFieldFilter(dataGrid.getColumn("mutation"));
    mutationTf.setWidth("150px");
    transcriptTf = createFieldFilter(dataGrid.getColumn("transcript"));
    transcriptTf.setWidth("120px");
    typeTf = new TextField();
    setFilter(dataGrid.getColumn("neopeptide"), header, neopeptideTf);
    setFilter(dataGrid.getColumn("length"), header, lengthTf);
    setFilter(dataGrid.getColumn("gene"), header, geneTf);
    setFilter(dataGrid.getColumn("mutation"), header, mutationTf);
    setFilter(dataGrid.getColumn("transcript"), header, transcriptTf);

    HeaderCell selectionCell = header.join("included", "excluded");
    selectionCell.setText("Selection");
    
    filterable = (Filterable) dataGrid.getContainerDataSource();

    panelContent.addComponent(createInfo());
    
    if (!methodColumn.equals("")) {
      panelContent.addComponent(createMethodSelection());
      dataGrid.setEnabled(false);
    }
    
    panelContent.addComponent(dataGrid);

    panel.setContent(panelContent);
  }
  

  /**
   * groups the header with the allele as title
   */
  public void joinHeader() {

    HeaderCell hlaA1Cell = header.join("hlaA1", "uncertaintyA1", "distanceA1");
    hlaA1Cell.setText(hlaA1);
    HeaderCell hlaA2Cell = header.join("hlaA2", "uncertaintyA2", "distanceA2");
    hlaA2Cell.setText(hlaA2);
    HeaderCell hlaB1Cell = header.join("hlaB1", "uncertaintyB1", "distanceB1");
    hlaB1Cell.setText(hlaB1);
    HeaderCell hlaB2Cell = header.join("hlaB2", "uncertaintyB2", "distanceB2");
    hlaB2Cell.setText(hlaB2);
    HeaderCell hlaC1Cell = header.join("hlaC1", "uncertaintyC1", "distanceC1");
    hlaC1Cell.setText(hlaC1);
    HeaderCell hlaC2Cell = header.join("hlaC2", "uncertaintyC2", "distanceC2");
    hlaC2Cell.setText(hlaC2);

    dataGrid.getColumn("hlaA1").setHeaderCaption("Score");
    dataGrid.getColumn("hlaA2").setHeaderCaption("Score");
    dataGrid.getColumn("hlaB1").setHeaderCaption("Score");
    dataGrid.getColumn("hlaB2").setHeaderCaption("Score");
    dataGrid.getColumn("hlaC1").setHeaderCaption("Score");
    dataGrid.getColumn("hlaC2").setHeaderCaption("Score");
  }
  

  /**
   * Sets up a filter for a certain column
   * 
   * @param column column to filter
   * @param header header the filter text field is located
   * @param tf text field for the filter input
   */
  public void setFilter(Column column, HeaderRow header, TextField tf) {
    // This create a HeaderRow to add filter fields
    // For each column from the grid
    HeaderCell cellFilter = header.getCell(column.getPropertyId());
    // Add a textfield
    cellFilter.setComponent(tf);
  }


  /**
   * Creates a text field with filter function for a certain column
   * 
   * @param column column to filter
   * @return text field with filter function
   */
  private TextField createFieldFilter(final Column column) {
    TextField filter = new TextField();
    filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
    filter.setImmediate(true);
    filter.addTextChangeListener(new TextChangeListener() {
      @Override
      public void textChange(TextChangeEvent event) {
        String newValue = event.getText();
        // Remove the previous filter
        container.removeContainerFilters(column.getPropertyId());
        if (newValue != null && !newValue.isEmpty()) {
          // Filter the information
          container.addContainerFilter(
              new SimpleStringFilter(column.getPropertyId(), newValue, true, true));
        }
      }
    });
    return filter;
  }
  
  /**
   * Sets up the filter to a certain column filtering by a certain string
   * 
   * @param column to filter
   * @param filter string to filter the column
   */
  public void filter(String column, String filter) {
    Filter tmpFilter = new SimpleStringFilter(column, filter, false, false);
    if (!filterable.getContainerFilters().contains(tmpFilter)) {
      filterable.removeAllContainerFilters();
      filterable.addContainerFilter(tmpFilter);
    } else {
      filterable.removeContainerFilter(tmpFilter);
    }

  }
  

  /**
   * Creates the Layout which displays info text for the user.
   * 
   * @return info layout with information text
   */
  public VerticalLayout createInfo() {
    infoLayout = new VerticalLayout();

    Label infoLa = new Label("Select your epitopes:");

    Label outLa = new Label("'Out' labeled epitopes will be excluded from the set of epitopes.");
    outLa.addStyleName("out");
    Label inLa =
        new Label("'In' labeled epitopes will be definitely included in the set of epitopes.");
    inLa.addStyleName("in");
    infoLa.addStyleName(ValoTheme.LABEL_BOLD);

    infoLayout.addComponents(infoLa, inLa, outLa);

    return infoLayout;
  }
  
  public HorizontalLayout createMethodSelection() {
    HorizontalLayout methodSelectionLayout = new HorizontalLayout();
    methodSelect = new NativeSelect("Select a Method");
    methodSelect.setNullSelectionAllowed(false);
    methodSelect.setRequired(true);
    methodSelect.setNullSelectionItemId("All");
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      methodSelect.addItem(bean.getMethod());
    }
    
    methodSelectionLayout.addComponent(methodSelect);
    return methodSelectionLayout;
    
  }
  
  public void applyMethodFilter() {
      filter("method", methodSelect.getValue().toString());
  }

  /**
   * Resets the grid and panel
   */
  public void reset() {
    dataGrid.removeAllColumns();
    panelContent.removeAllComponents();
  }
  

  public BeanItemContainer<EpitopeSelectionBean> getContainer() {
    return container;
  }


  /**
   * Boolean converter to convert a boolean value to a checkbox HTML
   * 
   * @author spaethju
   *
   */
  public class BooleanConverter implements Converter<String, Boolean> {
    @Override
    public Boolean convertToModel(String value, Class<? extends Boolean> targetType, Locale locale)
        throws ConversionException {
      return null;
    }

    @Override
    public String convertToPresentation(Boolean value, Class<? extends String> targetType,
        Locale locale) throws ConversionException {
      return "<input type='checkbox' disabled='disabled'" + (value.booleanValue() ? "checked" : "")
          + " />";
    }

    @Override
    public Class<Boolean> getModelType() {
      return Boolean.class;
    }

    @Override
    public Class<String> getPresentationType() {
      return String.class;
    }

  }
  

  /**
   * Gets a list with all neopeptides marked as included.
   * 
   * @return list with included neopeptides
   */
  public ArrayList<String> getIncludedBeans() {
    ArrayList<String> included = new ArrayList<>();
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      if (bean.getIncluded() == true) {
        included.add(bean.getNeopeptide());
      }
    }
    return included;
  }
  

  /**
   * Gets a list with all neopeptides marked as excluded.
   * 
   * @return list with excluded neopeptides
   */
  public ArrayList<String> getExcludedBeans() {
    ArrayList<String> excluded = new ArrayList<>();
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      if (bean.getIncluded() == true) {
        excluded.add(bean.getNeopeptide());
      }
    }
    return excluded;
  }
  

  /**
   * Gets a list with all taa neopeptides
   * 
   * @return list with taa neopeptides
   */
  public ArrayList<String> getTAA() {
    ArrayList<String> taas = new ArrayList<>();
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      if (bean.getType().equals("TAA")) {
        taas.add(bean.getNeopeptide());
      }
    }
    return taas;
  }
  
  /**
   * Adds a filter to the type column
   * 
   */
  public void addTypeFilter() {
    typeTf = createFieldFilter(dataGrid.getColumn("type"));
    typeTf.setWidth("50px");
    setFilter(dataGrid.getColumn("type"), header, typeTf);
  }
  

  public TextField getGeneTf() {
    return geneTf;
  }
  

  public TextField getNeopeptideTf() {
    return neopeptideTf;
  }


  public TextField getLengthTf() {
    return lengthTf;
  }


  public TextField getMutationTf() {
    return mutationTf;
  }
  
  public NativeSelect getMethodSelect() {
    return methodSelect;
  }

  public void setMethodSelect(NativeSelect methodSelect) {
    this.methodSelect = methodSelect;
  }

  public String getMethodColumn() {
    return methodColumn;
  }

  public void setMethodColumn(String methodColumn) {
    this.methodColumn = methodColumn;
  }

}
