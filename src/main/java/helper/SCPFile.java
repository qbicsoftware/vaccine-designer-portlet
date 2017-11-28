package helper;

import life.qbic.MyPortletUI;

import java.io.IOException;

public class SCPFile {

    public SCPFile() {

    }

    public void scpToRemote(String homePath, String filepath, String sshurl, String sshKey) {
        try {
            String command = "scp -i " + homePath + ".ssh/" + sshKey + " " + filepath + " " + sshurl;
            MyPortletUI.logger.info(command);
            Process scpTo = Runtime.getRuntime().exec(command, null);
            scpTo.waitFor();
        } catch (IOException e) {
            MyPortletUI.logger.error("VM with NeoOptitope not reachable");
            e.printStackTrace();
        } catch (InterruptedException e) {
            MyPortletUI.logger.error("scp was interrputed");
            e.printStackTrace();
        }
    }

    public void scpFromRemote(String homePath, String remoteurl, String sshurl, String filepath, String sshKey) {
        try {
            String command = "scp -i " + homePath + ".ssh/" + sshKey + " " + remoteurl+":" + sshurl + " " + filepath;
            MyPortletUI.logger.info(command);
            Process scpFrom = Runtime.getRuntime().exec(command, null);
            scpFrom.waitFor();
        } catch (IOException e) {
            MyPortletUI.logger.error("VM with NeoOptitope not reachable");
            e.printStackTrace();
        } catch (InterruptedException e) {
            MyPortletUI.logger.error("scp was interrupted");
            e.printStackTrace();
        }
    }
}
