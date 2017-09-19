package helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * The class {@link WriterResults} is responsible for writing the final results in a file.
 * 
 * @author spaethju
 *
 */
public class WriterResults {

  private DescriptionHandler dh = new DescriptionHandler();

  /**
   * Constructor
   */
  public WriterResults() {

  }

  /**
   * Writes each computed result into one file
   * 
   * @param files files of all computed results
   */
  public void writeOutputData(ArrayList<File> files, String tmpResultPath) {
    int resultCounter = 1;

    try {
      BufferedWriter writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpResultPath)));
      for (File file : files) {
        BufferedReader br = new BufferedReader(new FileReader(file));
        writer.write("Result " + resultCounter + "\n" + "\n");
        resultCounter++;
        String line;
        while ((line = br.readLine()) != null) {
          writer.write(line);
          writer.newLine();
        }
        writer.write(
            "--------------------------------------------------------------------------" + "\n");
        br.close();
      }

      writer.close();
    } catch (IOException ex) {
      Utils.notification("Result Error", dh.getWriteResultOutputError(), "error");
      ex.printStackTrace();
    }

  }


}
