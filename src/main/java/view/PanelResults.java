package view;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.themes.ValoTheme;
import helper.DescriptionHandler;
import model.ResultBean;

import java.util.ArrayList;

/**
 * The class {@link PanelResults} represents a panel for showing the results computed by the epitope
 * selection script.
 *
 * @author spaethju
 */
@SuppressWarnings("serial")
public class PanelResults extends CustomComponent {

    private Panel panel;
    private VerticalLayout panelContent;
    private TabSheet resultsTab;
    private ArrayList<TabResult> tabs;
    private int resultCounter;
    private DescriptionHandler dh = new DescriptionHandler();

    /**
     * Constructor
     */
    public PanelResults() {
        panel = new Panel();
        resultsTab = new TabSheet();
        tabs = new ArrayList<>();
        panelContent = new VerticalLayout();
        resultCounter = 1;
        setCompositionRoot(createPanel());
    }

    /**
     * Creates the main panel
     *
     * @return panel containing all other components
     */
    public Panel createPanel() {
        panel.setContent(panelContent);
        panelContent.addComponent(createInfo());
        panelContent.addComponent(resultsTab);

        panelContent.setMargin(true);
        panelContent.setSpacing(true);


        return panel;
    }

    /**
     * Adds a tab, showing the results saved in a result bean, to the tab sheet
     *
     * @param resultBeans bean item container containing all solutions of a output of the epitope
     *                    selection script
     * @param alleles     alleles as an array
     */
    public void addResultTab(BeanItemContainer<ResultBean> resultBeans, String[] alleles, Boolean hasDist) {
        TabResult tabResult = new TabResult(resultBeans, alleles, hasDist);
        resultsTab.addTab(tabResult, "Result " + resultCounter);
        resultsTab.setStyleName(ValoTheme.TABSHEET_FRAMED);
        resultsTab.setSelectedTab(resultCounter - 1);
        resultCounter++;
        tabs.add(tabResult);
        resultsTab.addSelectedTabChangeListener((SelectedTabChangeListener) event -> {
            for (TabResult tr : tabs) {
                tr.getFilterable().removeAllContainerFilters();
            }
        });
        tabResult.getOptionTab().addSelectedTabChangeListener((SelectedTabChangeListener) event -> {
            for (TabResult tr : tabs) {
                tr.getFilterable().removeAllContainerFilters();
            }
        });
    }


    public VerticalLayout createInfo() {
        VerticalLayout infoLayout = new VerticalLayout();
        Label infoLa = createDescriptionLabel(dh.getResults());
        infoLayout.addComponents(infoLa);

        return infoLayout;
    }

    public Label createDescriptionLabel(String info) {
        Label descriptionLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml() + "    " + info, ContentMode.HTML);
        descriptionLabel.addStyleName("description");
        return descriptionLabel;
    }

    /**
     * resets the whole panel
     */
    public void reset() {
        panelContent.removeAllComponents();
    }

    /**
     * @return all tabs of the tab sheet
     */
    public ArrayList<TabResult> getTabs() {
        return tabs;
    }

}
