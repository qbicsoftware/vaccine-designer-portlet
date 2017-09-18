package helper;

import life.qbic.MyPortletUI;

import java.io.*;
import java.util.HashMap;

/**
 * 
 * The class {@link ParserAlleleFile} is responsible for the parsing the allele file
 * 
 * @author spaethju
 * 
 * 
 */
public class ParserAlleleFile {

  private File alleleFile;
  private int a1,a2,b1,b2,c1,c2;

  public ParserAlleleFile() {
    this.alleleFile = alleleFile;
  }

  public HashMap<String, String> parse(File alleleFile) throws IOException {
    HashMap<String, String> alleles = new HashMap<>();
    BufferedReader brReader = new BufferedReader(new FileReader(alleleFile));
    String line = brReader.readLine();
    String[] headers = line.split("\t");

    int counter = 0;

    for (String h : headers) {
      if (h.equalsIgnoreCase("a1")) {
        a1 = counter;
        counter = counter + 1;
      } else if (h.equalsIgnoreCase("a2")) {
        a2 = counter;
        counter = counter + 1;
      } else if (h.equalsIgnoreCase("b1")) {
        b1 = counter;
        counter = counter + 1;
      } else if (h.equalsIgnoreCase("b2")) {
        b2 = counter;
        counter = counter + 1;
      } else if (h.equalsIgnoreCase("c1")) {
        c1 = counter;
        counter = counter + 1;
      } else if (h.equalsIgnoreCase("c2")) {
        c2 = counter;
        counter = counter + 1;
      } else {
        counter = counter + 1;
      }
    }

    line = brReader.readLine();
    String[] columns = line.split("\t");

    alleles.put("A1", "HLA-" + columns[a1]);
    alleles.put("A1", "HLA-" + columns[a2]);
    alleles.put("A1", "HLA-" + columns[b1]);
    alleles.put("A1", "HLA-" + columns[b2]);
    alleles.put("A1", "HLA-" + columns[c1]);
    alleles.put("A1", "HLA-" + columns[c2]);

    brReader.close();

    return alleles;

  }
}