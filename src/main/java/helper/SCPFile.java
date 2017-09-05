package helper;

import life.qbic.MyPortletUI;
import java.io.IOException;

public class SCPFile {

    public SCPFile(){

    }

    public void scp(String filepath, String sshurl){
        try {
            Runtime.getRuntime().exec("scp -i ~/.ssh/key_rsa " + filepath + " " + sshurl, null);
            MyPortletUI.logger.info(filepath + " copied to VM with NeoOptiTope");
        } catch (IOException e) {
            MyPortletUI.logger.error("VM with NeoOptitope not reachable");
            e.printStackTrace();
        }
    }
}
