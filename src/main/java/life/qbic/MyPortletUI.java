package life.qbic;

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedPortletSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import helper.database.DBConfig;
import helper.database.DBManager;
import helper.DescriptionHandler;
import helper.Utils;
import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManager;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManagerFactory;
import life.qbic.portal.liferayandvaadinhelpers.main.LiferayAndVaadinUtils;
import view.LayoutMain;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("life.qbic.AppWidgetSet")
public class MyPortletUI extends UI {

    public static Log logger = LogFactoryUtil.getLog(MyPortletUI.class);
    private OpenBisClient openbis;
    private ConfigurationManager config = new ConfigurationManagerFactory().getInstance();
    private DescriptionHandler dh = new DescriptionHandler();
    private Boolean success;
    private String url, pw, mysqlPW, mysqlUser, userID;

    @Override
    protected void init(VaadinRequest request) {
        if (LiferayAndVaadinUtils.isLiferayPortlet()) {
            final String portletContextName = getPortletContextName(request);
            final Integer numOfRegisteredUsers = getPortalCountOfRegisteredUsers();
        }

        //getCredentials();

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        success = true;
        config = ConfigurationManagerFactory.getInstance();

        if (LiferayAndVaadinUtils.isLiferayPortlet()) {
            logger.info("Vaccine Designer is running on Liferay and user is logged in.");
            userID = LiferayAndVaadinUtils.getUser().getScreenName();
            logger.info("UserID = " + userID);

        }
        // establish connection to the OpenBIS API
        try {
            logger.debug("trying to connect to openbis");
            if (LiferayAndVaadinUtils.isLiferayPortlet()) {
                openbis = new OpenBisClient(config.getDataSourceUser(), config.getDataSourcePassword(),
                        config.getDataSourceUrl());
            } else {
                openbis = new OpenBisClient(userID, pw, url);
            }

            openbis.login();
        } catch (Exception e) {
            success = false;
            logger.error(
                    "User \"" + userID + "\" could not connect to openBIS and has been informed of this.");
            Utils.notification("Error", dh.getDatabaseConnectionError(), "error");
            //e.printStackTrace();
        }

        LayoutMain mainLayout;
        if (success) {
            // stuff from openbis
            final List<String> spaces = openbis.getUserSpaces(userID);

            // stuff from mysql database
            DBConfig mysqlConfig = new DBConfig(config.getMsqlHost(), config.getMysqlPort(),
                    config.getMysqlDB(), config.getMysqlUser(), config.getMysqlPass());
            @SuppressWarnings("unused")
            DBManager dbm = new DBManager(mysqlConfig);

            List<Project> projects = new ArrayList<>();
            for (String space : spaces) {
                projects.addAll(openbis.getProjectsOfSpace(space));
            }

            // initialize the View with sample types, spaces and the dictionaries of tissues and species
            mainLayout = new LayoutMain(projects, openbis, success);
            setContent(mainLayout);

            logger.info("User \"" + userID + "\" connected to openBIS.");

        } else {
            mainLayout = new LayoutMain(success);
            setContent(mainLayout);
        }


    }

    private String getPortletContextName(VaadinRequest request) {
        WrappedPortletSession wrappedPortletSession = (WrappedPortletSession) request
                .getWrappedSession();
        PortletSession portletSession = wrappedPortletSession
                .getPortletSession();

        final PortletContext context = portletSession.getPortletContext();
        final String portletContextName = context.getPortletContextName();
        return portletContextName;
    }

    private Integer getPortalCountOfRegisteredUsers() {
        Integer result = null;

        try {
            result = UserLocalServiceUtil.getUsersCount();
        } catch (SystemException e) {
            logger.error(e);
        }

        return result;
    }

    public void getCredentials() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("/Users/spaethju/liferay/qbic-ext.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            url = prop.getProperty("datasource.url");
            pw = prop.getProperty("datasource.password");
            userID = prop.getProperty("datasource.user");
            mysqlPW = prop.getProperty("mysql.pass");
            mysqlUser = prop.getProperty("mysql.user");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
