package helper.upload_input;

import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import helper.DescriptionHandler;
import helper.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author spaethju
 */
@SuppressWarnings("serial")
public class UploaderInput
    implements Receiver, ProgressListener, FailedListener, FinishedListener {

  private ProgressBar progress = new ProgressBar(0.0f);
  private File tempFile;
  private DescriptionHandler dh = new DescriptionHandler();

  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.Receiver#receiveUpload(java.lang.String, java.lang.String)
   */
  public OutputStream receiveUpload(String filename, String mimeType) {
    progress.setIndeterminate(true);
    progress.setVisible(true);
    try {
      tempFile = File.createTempFile("temp", ".txt");
      return new FileOutputStream(tempFile);
    } catch (IOException e) {
      Utils.notification("Upload Failed!", dh.getUploadInputIOError(), "error");
      return null;
    }

  }

  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.ProgressListener#updateProgress(long, long)
   */
  @Override
  public void updateProgress(long readBytes, long contentLength) {
    progress.setVisible(true);
  }

  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.FailedListener#uploadFailed(com.vaadin.ui.Upload.FailedEvent)
   */
  @Override
  public void uploadFailed(FailedEvent event) {
    Utils.notification("Upload failed!", dh.getUploadInputFailedError(), "error");
    progress.setVisible(true);
  }

  /**
   * @return progress bar with the actual state of the upload
   */
  public ProgressBar getProgress() {
    return progress;
  }

  /**
   * @return uploaded file temporarly stored in
   */
  public File getTempFile() {
    return tempFile;
  }

  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.FinishedListener#uploadFinished(com.vaadin.ui.Upload.FinishedEvent)
   */
  @Override
  public void uploadFinished(FinishedEvent event) {
  }
}

