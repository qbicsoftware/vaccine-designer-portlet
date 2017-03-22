package view;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The class {@link WindowLoading} represents a window showing the current state of computation. It
 * appears while the epitope selection script is started in a thread and shows if the computation is
 * still in progress, interrupted or finished successfully
 * 
 * @author spaethju
 *
 */
@SuppressWarnings("serial")
public class WindowLoading extends Window {

  private VerticalLayout loadingLayout;
  private ProgressBar loadingBar;
  private Label loadingLa;
  private Button cancelBu;

  /**
   *  Constructor, creates a window showing the computation in progress state
   */
  public WindowLoading() {
    this.setWidth(350.0f, Unit.PIXELS);
    this.setModal(true);
    this.center();
    this.setResizable(false);
    this.setDraggable(false);
    this.setClosable(false);
    this.setIcon(FontAwesome.ROCKET);

    loadingLayout = new VerticalLayout();

    loadingLa = new Label("Computation in progress... Please wait.");
    loadingBar = new ProgressBar();
    loadingBar.setIndeterminate(true);
    cancelBu = new Button("Cancel");
    cancelBu.setStyleName(ValoTheme.BUTTON_DANGER);

    loadingLayout.setMargin(true);
    loadingLayout.setSpacing(true);

    loadingLayout.addComponents(loadingLa, loadingBar, cancelBu);
    loadingLayout.setComponentAlignment(loadingLa, Alignment.MIDDLE_CENTER);
    loadingLayout.setComponentAlignment(loadingBar, Alignment.MIDDLE_CENTER);
    loadingLayout.setComponentAlignment(cancelBu, Alignment.MIDDLE_CENTER);

    this.setContent(loadingLayout);
    show();
  }

  
  /**
   * Changes the window information to show that the computation has finished successfully
   */
  public void success() {
    loadingLayout.removeAllComponents();
    Label successLabel = new Label("Computation finished successfully.");
    successLabel.addStyleName(ValoTheme.LABEL_SUCCESS);
    Button closeBu = new Button("OK");
    closeBu.setStyleName(ValoTheme.BUTTON_FRIENDLY);
    closeBu.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        close();
        UI.getCurrent().setScrollTop(100);
      }
    });
    loadingLayout.addComponents(successLabel, closeBu);
    loadingLayout.setComponentAlignment(closeBu, Alignment.MIDDLE_CENTER);
  }

  
  /**
   * changes the window information to show that the computation was not successfull 
   */
  public void failure() {
    loadingLayout.removeAllComponents();
    Label failureLabel = new Label(
        "Computation couln't finish successfully. Please try again using different parameters");
    failureLabel.addStyleName(ValoTheme.LABEL_FAILURE);
    Button closeBu = new Button("Back");
    closeBu.setStyleName(ValoTheme.BUTTON_DANGER);
    closeBu.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        close();
      }
    });
    loadingLayout.addComponents(failureLabel, closeBu);
    loadingLayout.setComponentAlignment(closeBu, Alignment.MIDDLE_CENTER);
  }


  /**
   * shows the window in the center of the current window.
   */
  public void show() {
    UI.getCurrent().addWindow(this);
  }

  public Button getCancelBu() {
    return cancelBu;
  }
}
