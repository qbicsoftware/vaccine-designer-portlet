package helper.ssh;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SCPFile {

  private static final Logger LOG = LogManager.getLogger(SCPFile.class);

  public SCPFile() {

  }

  public void scpToRemote(String homePath, String filepath, String sshurl, String sshKey) {
    try {
      String command = "scp -i " + homePath + ".ssh/" + sshKey + " " + filepath + " " + sshurl;
      LOG.info(command);
      Process scpTo = Runtime.getRuntime().exec(command, null);
      scpTo.waitFor();
    } catch (IOException e) {
      LOG.error("VM with NeoOptitope not reachable");
      e.printStackTrace();
    } catch (InterruptedException e) {
      LOG.error("scp was interrputed");
      e.printStackTrace();
    }
  }

  public void scpFromRemote(String homePath, String remoteurl, String sshurl, String filepath,
      String sshKey) {
    try {
      String command =
          "scp -i " + homePath + ".ssh/" + sshKey + " " + remoteurl + ":" + sshurl + " " + filepath;
      LOG.info(command);
      Process scpFrom = Runtime.getRuntime().exec(command, null);
      scpFrom.waitFor();
    } catch (IOException e) {
      LOG.error("VM with NeoOptitope not reachable");
      e.printStackTrace();
    } catch (InterruptedException e) {
      LOG.error("scp was interrupted");
      e.printStackTrace();
    }
  }
}
