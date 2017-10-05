package view;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import helper.DescriptionHandler;
import model.EpitopeSelectionBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import static java.awt.Color.red;

/**
 * The class {@link PanelEpitopeSelection} represents a component for showing the uploaded epitope
 * selection data.
 *
 * @author spaethju
 */
@SuppressWarnings({"serial"})
public class PanelEpitopeSelection extends CustomComponent {

    private Grid dataGrid;
    private Panel panel;
    private VerticalLayout infoLayout, panelContent;
    private BeanItemContainer<EpitopeSelectionBean> container;
    private String hlaA1, hlaA2, hlaB1, hlaB2, hlaC1, hlaC2;
    private HeaderRow header;
    private TextField geneTf, neopeptideTf, lengthTf, mutationTf, transcriptTf, transcriptExpressionTf, typeTf;
    private Filterable filterable;
    private NativeSelect methodSelect;
    private String methodColumn;
    private DescriptionHandler dh = new DescriptionHandler();

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
     *                  neopeptide of the uploaded input data
     */
    public void setDataGrid(BeanItemContainer<EpitopeSelectionBean> container, String methodColumn, String[] alleleNames, boolean type, boolean transcriptExpression, boolean unc, boolean dist) {
        this.container = container;
        this.setMethodColumn(methodColumn);
        dataGrid.setSizeFull();

        // Set Allele Names for Headers
        hlaA1 = "HLA-" + alleleNames[0];
        hlaA2 = "HLA-" + alleleNames[1];
        hlaB1 = "HLA-" + alleleNames[2];
        hlaB2 = "HLA-" + alleleNames[3];
        hlaC1 = "HLA-" + alleleNames[4];
        hlaC2 = "HLA-" + alleleNames[5];


        // set epitope selection bean item container as container data source
        dataGrid.setContainerDataSource(container);

        // adjust grid
        dataGrid.setColumnOrder("included", "excluded", "neopeptide", "type", "length", "mutation", "gene", "transcript", "transcriptExpression", "hlaA1", "uncA1", "distA1", "hlaA2", "uncA2", "distA2", "hlaB1", "uncB1", "distB1", "hlaB2", "uncB2", "distB2", "hlaC1", "uncC1", "distC1", "hlaC2", "uncC2", "distC2");
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
        dataGrid.getColumn("transcriptExpression").setHeaderCaption("Expression");
        dataGrid.getColumn("type").setEditable(false);
        dataGrid.getColumn("hlaA1").setEditable(false);
        dataGrid.getColumn("hlaA1").setHeaderCaption(hlaA1 );
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
        dataGrid.getColumn("distA1").setEditable(false);
        dataGrid.getColumn("distA1").setHeaderCaption("Distance");
        dataGrid.getColumn("distA2").setEditable(false);
        dataGrid.getColumn("distA2").setHeaderCaption("Distance");
        dataGrid.getColumn("distB1").setEditable(false);
        dataGrid.getColumn("distB1").setHeaderCaption("Distance");
        dataGrid.getColumn("distB2").setEditable(false);
        dataGrid.getColumn("distB2").setHeaderCaption("Distance");
        dataGrid.getColumn("distC1").setEditable(false);
        dataGrid.getColumn("distC1").setHeaderCaption("Distance");
        dataGrid.getColumn("distC2").setEditable(false);
        dataGrid.getColumn("distC2").setHeaderCaption("Distance");
        dataGrid.getColumn("uncA1").setEditable(false);
        dataGrid.getColumn("uncA1").setHeaderCaption("Uncertainty");
        dataGrid.getColumn("uncA2").setEditable(false);
        dataGrid.getColumn("uncA2").setHeaderCaption("Uncertainty");
        dataGrid.getColumn("uncB1").setEditable(false);
        dataGrid.getColumn("uncB1").setHeaderCaption("Uncertainty");
        dataGrid.getColumn("uncB2").setEditable(false);
        dataGrid.getColumn("uncB2").setHeaderCaption("Uncertainty");
        dataGrid.getColumn("uncC1").setEditable(false);
        dataGrid.getColumn("uncC1").setHeaderCaption("Uncertainty");
        dataGrid.getColumn("uncC2").setEditable(false);
        dataGrid.getColumn("uncC2").setHeaderCaption("Uncertainty");
        dataGrid.getDefaultHeaderRow().getCell("included").setHtml("<font color='#2c972'>" + FontAwesome.CHECK_CIRCLE.getHtml() + "</font>");
        dataGrid.getDefaultHeaderRow().getCell("excluded").setHtml("<font color='#ed473b'>" +FontAwesome.TIMES_CIRCLE.getHtml()+ "</font>");
        dataGrid.getDefaultHeaderRow().setStyleName(ValoTheme.LABEL_BOLD);
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
        neopeptideTf.setSizeFull();
        lengthTf = createFieldFilter(dataGrid.getColumn("length"));
        lengthTf.setSizeFull();
        geneTf = createFieldFilter(dataGrid.getColumn("gene"));
        geneTf.setSizeFull();
        mutationTf = createFieldFilter(dataGrid.getColumn("mutation"));
        mutationTf.setSizeFull();
        transcriptTf = createFieldFilter(dataGrid.getColumn("transcript"));
        transcriptTf.setSizeFull();
        if (type) {
            typeTf = createFieldFilter(dataGrid.getColumn("type"));
            typeTf.setSizeFull();
            setFilter(dataGrid.getColumn("transcriptExpression"), header, typeTf);
        }
        if (transcriptExpression) {
            transcriptExpressionTf = createFieldFilter(dataGrid.getColumn("transcriptExpression"));
            transcriptExpressionTf.setSizeFull();
            setFilter(dataGrid.getColumn("transcriptExpression"), header, transcriptExpressionTf);
        }

        setFilter(dataGrid.getColumn("neopeptide"), header, neopeptideTf);
        setFilter(dataGrid.getColumn("length"), header, lengthTf);
        setFilter(dataGrid.getColumn("gene"), header, geneTf);
        setFilter(dataGrid.getColumn("mutation"), header, mutationTf);
        setFilter(dataGrid.getColumn("transcript"), header, transcriptTf);


        HeaderCell selectionCell = header.join("included", "excluded");
        selectionCell.setText("Selection");

        joinHeader(unc, dist);

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
    public void joinHeader(Boolean unc, Boolean dist) {
        if (dist && unc) {
            dataGrid.getColumn("hlaA1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaA2").setHeaderCaption("Score");
            dataGrid.getColumn("hlaB1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaB2").setHeaderCaption("Score");
            dataGrid.getColumn("hlaC1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaC2").setHeaderCaption("Score");
            HeaderCell hlaA1Cell = header.join("hlaA1", "distA1", "uncA1");
            hlaA1Cell.setText(hlaA1);
            HeaderCell hlaA2Cell = header.join("hlaA2", "distA2", "uncA2");
            hlaA2Cell.setText(hlaA2);
            HeaderCell hlaB1Cell = header.join("hlaB1", "distB1", "uncB1");
            hlaB1Cell.setText(hlaB1);
            HeaderCell hlaB2Cell = header.join("hlaB2", "distB2", "uncB2");
            hlaB2Cell.setText(hlaB2);
            HeaderCell hlaC1Cell = header.join("hlaC1", "distC1", "uncC1");
            hlaC1Cell.setText(hlaC1);
            HeaderCell hlaC2Cell = header.join("hlaC2", "distC2", "uncC2");
            hlaC2Cell.setText(hlaC2);
        } else if (!dist && unc) {
            dataGrid.getColumn("hlaA1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaA2").setHeaderCaption("Score");
            dataGrid.getColumn("hlaB1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaB2").setHeaderCaption("Score");
            dataGrid.getColumn("hlaC1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaC2").setHeaderCaption("Score");
            dataGrid.removeColumn("distA1");
            dataGrid.removeColumn("distA2");
            dataGrid.removeColumn("distB1");
            dataGrid.removeColumn("distB2");
            dataGrid.removeColumn("distC1");
            dataGrid.removeColumn("distC2");
            HeaderCell hlaA1Cell = header.join("hlaA1", "uncA1");
            hlaA1Cell.setText(hlaA1);
            HeaderCell hlaA2Cell = header.join("hlaA2", "uncA2");
            hlaA2Cell.setText(hlaA2);
            HeaderCell hlaB1Cell = header.join("hlaB1", "uncB1");
            hlaB1Cell.setText(hlaB1);
            HeaderCell hlaB2Cell = header.join("hlaB2", "uncB2");
            hlaB2Cell.setText(hlaB2);
            HeaderCell hlaC1Cell = header.join("hlaC1", "uncC1");
            hlaC1Cell.setText(hlaC1);
            HeaderCell hlaC2Cell = header.join("hlaC2", "uncC2");
            hlaC2Cell.setText(hlaC2);
        } else if (!unc && dist) {
            dataGrid.getColumn("hlaA1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaA2").setHeaderCaption("Score");
            dataGrid.getColumn("hlaB1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaB2").setHeaderCaption("Score");
            dataGrid.getColumn("hlaC1").setHeaderCaption("Score");
            dataGrid.getColumn("hlaC2").setHeaderCaption("Score");
            dataGrid.removeColumn("uncA1");
            dataGrid.removeColumn("uncA2");
            dataGrid.removeColumn("uncB1");
            dataGrid.removeColumn("uncB2");
            dataGrid.removeColumn("uncC1");
            dataGrid.removeColumn("uncC2");
            HeaderCell hlaA1Cell = header.join("hlaA1", "distA1");
            hlaA1Cell.setText(hlaA1);
            HeaderCell hlaA2Cell = header.join("hlaA2", "distA2");
            hlaA2Cell.setText(hlaA2);
            HeaderCell hlaB1Cell = header.join("hlaB1", "distB1");
            hlaB1Cell.setText(hlaB1);
            HeaderCell hlaB2Cell = header.join("hlaB2", "distB2");
            hlaB2Cell.setText(hlaB2);
            HeaderCell hlaC1Cell = header.join("hlaC1", "distC1");
            hlaC1Cell.setText(hlaC1);
            HeaderCell hlaC2Cell = header.join("hlaC2", "distC2");
            hlaC2Cell.setText(hlaC2);
        } else {
            dataGrid.removeColumn("uncA1");
            dataGrid.removeColumn("uncA2");
            dataGrid.removeColumn("uncB1");
            dataGrid.removeColumn("uncB2");
            dataGrid.removeColumn("uncC1");
            dataGrid.removeColumn("uncC2");
            dataGrid.removeColumn("distA1");
            dataGrid.removeColumn("distA2");
            dataGrid.removeColumn("distB1");
            dataGrid.removeColumn("distB2");
            dataGrid.removeColumn("distC1");
            dataGrid.removeColumn("distC2");
        }

        }

    /**
     * Sets up a filter for a certain column
     *
     * @param column column to filter
     * @param header header the filter text field is located
     * @param tf     text field for the filter input
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
        filter.addTextChangeListener((TextChangeListener) event -> {
            String newValue = event.getText();
            // Remove the previous filter
            container.removeContainerFilters(column.getPropertyId());
            if (newValue != null && !newValue.isEmpty()) {
                // Filter the information
                container.addContainerFilter(
                        new SimpleStringFilter(column.getPropertyId(), newValue, true, true));
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

        Label infoLa = createDescriptionLabel(dh.getEpitopeSelection());

        infoLayout.addComponent(infoLa);

        return infoLayout;
    }

    public Label createDescriptionLabel(String info) {
        Label descriptionLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml() + "    " + info, ContentMode.HTML);
        descriptionLabel.addStyleName("description");
        return descriptionLabel;
    }

    public HorizontalLayout createMethodSelection() {
        HorizontalLayout methodSelectionLayout = new HorizontalLayout();
        methodSelect = new NativeSelect("Select a Method");
        methodSelect.setNullSelectionAllowed(false);
        methodSelect.setRequired(true);
        methodSelect.setNullSelectionItemId("All");
        for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext(); ) {
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
     * Gets a list with all neopeptides marked as included.
     *
     * @return list with included neopeptides
     */
    public ArrayList<String> getIncludedBeans() {
        ArrayList<String> included = new ArrayList<>();
        for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext(); ) {
            EpitopeSelectionBean bean = i.next();
            if (bean.getIncluded()) {
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
        for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext(); ) {
            EpitopeSelectionBean bean = i.next();
            if (bean.getIncluded()) {
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
        for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext(); ) {
            EpitopeSelectionBean bean = i.next();
            if (bean.getType().equals("TAA")) {
                taas.add(bean.getNeopeptide());
            }
        }
        return taas;
    }

    /**
     * Adds a filter to the type column
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

    /**
     * Boolean converter to convert a boolean value to a checkbox HTML
     *
     * @author spaethju
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

}
