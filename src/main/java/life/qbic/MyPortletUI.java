package life.qbic;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;

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
import helper.DBConfig;
import helper.DBManager;
import helper.Utils;
import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManager;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManagerFactory;
import life.qbic.portal.liferayandvaadinhelpers.main.LiferayAndVaadinUtils;
import view.LayoutMain;

import java.util.ArrayList;
import java.util.List;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("life.qbic.AppWidgetSet")
public class MyPortletUI extends UI {

    public static Log logger = LogFactoryUtil.getLog(MyPortletUI.class);
    private LayoutMain mainLayout;
    private OpenBisClient openbis;
    public static String tmpFolder;
    ConfigurationManager config = new ConfigurationManagerFactory().getInstance();
    public List<Project> projects;

    @Override
    protected void init(VaadinRequest request) {
        final String portletContextName = getPortletContextName(request);
        final Integer numOfRegisteredUsers = getPortalCountOfRegisteredUsers();
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        String userID = "MISSING SCREENNAME";
        if (LiferayAndVaadinUtils.isLiferayPortlet()) {
            userID = LiferayAndVaadinUtils.getUser().getScreenName();
        }
        projects = new ArrayList<>();
        boolean success = true;
        config = ConfigurationManagerFactory.getInstance();
        tmpFolder = config.getTmpFolder();

        if (LiferayAndVaadinUtils.isLiferayPortlet()) {
            logger.info("Vaccine Designer is running on Liferay and user is logged in.");
            userID = LiferayAndVaadinUtils.getUser().getScreenName();
            logger.info("UserID = " + userID);

        }
        // establish connection to the OpenBIS API
        try {
            logger.debug("trying to connect to openbis");
            openbis = new OpenBisClient(config.getDataSourceUser(), config.getDataSourcePassword(),
                    config.getDataSourceUrl());
            openbis.login();
        } catch (Exception e) {
            success = false;
            logger.error(
                    "User \"" + userID + "\" could not connect to openBIS and has been informed of this.");
            Utils.notification("Error", "You can use the database function", "error");
        }

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
}
