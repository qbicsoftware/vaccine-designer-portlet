package helper;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The class {@link Utils} provides useful methods.
 * 
 * @author spaethju
 *
 */
public class Utils {

  /**
   * Constructor
   */
  public Utils() {

  }

  /**
   * Shows a notification in the center of the window.
   * 
   * @param title title of the notification
   * @param description description of the notificiation
   * @param type type of the notification (error, success, else)
   */
  public static void notification(String title, String description, String type) {
    Notification notify = new Notification(title, description);
    if (type.equals("error")) {
      notify.setDelayMsec(5000);
      notify.setIcon(FontAwesome.FROWN_O);
      notify.setStyleName(ValoTheme.NOTIFICATION_ERROR + " " + ValoTheme.NOTIFICATION_CLOSABLE);
    } else if (type.equals("success")) {
      notify.setDelayMsec(5000);
      notify.setIcon(FontAwesome.SMILE_O);
      notify.setStyleName(ValoTheme.NOTIFICATION_SUCCESS + " " + ValoTheme.NOTIFICATION_CLOSABLE);
    } else {
      notify.setDelayMsec(5000);
      notify.setIcon(FontAwesome.COMMENT);
      notify.setStyleName(ValoTheme.NOTIFICATION_TRAY + " " + ValoTheme.NOTIFICATION_CLOSABLE);
    }
    notify.setPosition(Position.TOP_CENTER);
    notify.show(Page.getCurrent());
  }
}
