package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import de.uni_tuebingen.qbic.main.LiferayAndVaadinUtils;
import helper.ConfigurationManager;
import helper.ConfigurationManagerFactory;
import helper.DBConfig;
import helper.DBManager;
import helper.Utils;
import life.qbic.openbis.openbisclient.OpenBisClient;
import logging.Log4j2Logger;
import mx4j.tools.config.DefaultConfigurationBuilder.New;
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

  @WebServlet(value = "/*", asyncSupported = true)
  @VaadinServletConfiguration(ui = MyUI.class, productionMode = false, widgetset = "main.widgetset.VaccinedesignerportletWidgetset")
  public static class MyUIServlet extends VaadinServlet {
  }
  
  private LayoutMain mainLayout;
  private OpenBisClient openbis;
  public static String tmpFolder;
  logging.Logger logger = new Log4j2Logger(MyUI.class);
  private ConfigurationManager config;
  public List<Project> projects; 

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    projects = new ArrayList<>();
    boolean success = true;
    String userID = "zxmqw74";
    config = ConfigurationManagerFactory.getInstance();
    logger.info(config.getDataSourceUser() + config.getDataSourcePassword() + config.getDataSourceUrl());
    tmpFolder = config.getTmpFolder();
    
    if (LiferayAndVaadinUtils.isLiferayPortlet()) {
      logger.info("Vaccine Designer is running on Liferay and user is logged in.");
      userID = LiferayAndVaadinUtils.getUser().getScreenName();
      
    } 
    // establish connection to the OpenBIS API
      try {
        logger.debug("trying to connect to openbis");
        openbis = new OpenBisClient(config.getDataSourceUser(), config.getDataSourcePassword(),
            config.getDataSourceUrl());
        openbis.login();
        Utils.notification("Connected to database", "You can use the database function", "success");
      } catch (Exception e) {
        success = false;
        logger.error(
            "User \"" + userID + "\" could not connect to openBIS and has been informed of this.");
      }
      
      if (success) {
        // stuff from openbis
        final List<String> spaces = openbis.getUserSpaces(userID);

        // stuff from mysql database
        DBConfig mysqlConfig = new DBConfig(config.getMysqlHost(), config.getMysqlPort(),
            config.getMysqlDB(), config.getMysqlUser(), config.getMysqlPass());
        @SuppressWarnings("unused")
        DBManager dbm = new DBManager(mysqlConfig);
        
        List<Project> projects = new ArrayList<>();
        for (String space : spaces) {
          for (Project project : openbis.getProjectsOfSpace(space)) {
            projects.add(project);
          }
        }
        
        // initialize the View with sample types, spaces and the dictionaries of tissues and species
        mainLayout = new LayoutMain(projects, openbis);
        setContent(mainLayout);

        logger.info("User \"" + userID + "\" connected to openBIS.");

      } else {
        mainLayout = new LayoutMain();
        setContent(mainLayout);
      }
        
      
    }
  
}