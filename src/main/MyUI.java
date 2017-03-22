package main;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import view.LayoutMain;

/**
 * This UI is the application entry point. A UI may either represent a browser window (or tab) or
 * some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SuppressWarnings("serial")
@Theme("vaccinedesignerportlet")
public class MyUI extends UI {

  private LayoutMain mainLayout;

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    mainLayout = new LayoutMain();
    setContent(mainLayout);
  }

  @WebServlet(value = "/*", asyncSupported = true)
  @VaadinServletConfiguration(ui = MyUI.class, productionMode = false, widgetset = "main.widgetset.VaccinedesignerportletWidgetset")
  public static class MyUIServlet extends VaadinServlet {
  }
  
}