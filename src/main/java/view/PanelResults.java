package view;

import java.util.ArrayList;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import model.ResultBean;

/**
 * 
 * The class {@link PanelResults} represents a panel for showing the results computed by the epitope
 * selection script.
 * 
 * @author spaethju
 * 
 */
@SuppressWarnings("serial")
public class PanelResults extends CustomComponent {

  private Panel panel;
  private VerticalLayout panelContent;
  private TabSheet resultsTab;
  private ArrayList<TabResult> tabs;
  private int resultCounter;

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
    panelContent.addComponent(resultsTab);

    panelContent.setMargin(true);
    panelContent.setSpacing(true);


    return panel;
  }

  /**
   * Adds a tab, showing the results saved in a result bean, to the tab sheet
   * 
   * @param resultBeans bean item container containing all solutions of a output of the epitope
   *        selection script
   * @param alleles alleles as an array
   */
  public void addResultTab(BeanItemContainer<ResultBean> resultBeans, String[] alleles) {
    TabResult tabResult = new TabResult(resultBeans, alleles);
    resultsTab.addTab(tabResult, "Result " + resultCounter);
    resultsTab.setStyleName(ValoTheme.TABSHEET_FRAMED);
    resultsTab.setSelectedTab(resultCounter - 1);
    resultCounter++;
    tabs.add(tabResult);
    resultsTab.addSelectedTabChangeListener(new SelectedTabChangeListener() {

      @Override
      public void selectedTabChange(SelectedTabChangeEvent event) {
        for (TabResult tr : tabs) {
          tr.getFilterable().removeAllContainerFilters();
        }
      }
    });
    tabResult.getOptionTab().addSelectedTabChangeListener(new SelectedTabChangeListener() {

      @Override
      public void selectedTabChange(SelectedTabChangeEvent event) {
        for (TabResult tr : tabs) {
          tr.getFilterable().removeAllContainerFilters();
        }
      }
    });
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
