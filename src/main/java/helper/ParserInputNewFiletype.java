package helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.vaadin.data.util.BeanItemContainer;
import model.EpitopeSelectionBean;

/**
 * 
 * The class {@link ParserInputNewFiletype} is responsible for the parsing of the uploaded epitope
 * prediction input data.
 * 
 * @author spaethju
 * 
 * 
 */
public class ParserInputNewFiletype {

  private BeanItemContainer<EpitopeSelectionBean> epitopes;
  private String line;
  private int method, mutation, gene, transcript, transcriptExpression, neopeptide, hlaA1, hlaA2, hlaB1,
      hlaB2, hlaC1, hlaC2,type, maxLength;
  private String methodCol, typeCol, hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele, hlaC2allele;
  private HashMap<String, HashMap<String, String>> immMap, otherMap;
  private BufferedReader brReader;
  private File file;
  private Boolean hasType, hasMethod;
 

  public ParserInputNewFiletype() {

  }


  /**
   * Reads in a epitope file, writes the input file for the epitope selection script and saves it as
   * a bean in a bean item container.
   * 
   * @param file
   * @param typeCol name of the type column
   * @throws IOException
   */
  public void parse(File file, String methodCol, String typeCol) throws IOException  {

    this.typeCol = typeCol;
    this.methodCol = methodCol;
    this.file = file;

    // initialize bean item container for epitope selection beans
    epitopes = new BeanItemContainer<EpitopeSelectionBean>(EpitopeSelectionBean.class);

    // initialize buffered reader reading the file line by line
    correctInput();
      brReader = new BufferedReader(new FileReader(file));
      line = brReader.readLine();
    
      setHeaders();
      readInput();
      setBean();
    
  }
  
  public void correctInput() throws IOException {
    // splits the line tab seperarated
    brReader = new BufferedReader(new FileReader(file));
    line = brReader.readLine();
    String[] headers = line.split("\t");

    for (String h : headers) {
      if (!typeCol.equals("") && h.equals(typeCol)) {
        hasType = true;
      } else if (!typeCol.equals("") && !hasMethod.equals(typeCol)) {
        life.qbic.MyPortletUI.logger.error("Type column was not found in the uploaded file and ignored");
      }
      if (!methodCol.equals("") && h.equals(methodCol)) {
        hasMethod = true;
      } else if (!methodCol.equals("") && !h.equals(methodCol)) {
        life.qbic.MyPortletUI.logger.error("Method column was not found in the uploaded file and ignored");
      }
    }
    
  }

  /**
   * Searches the header of the uploaded file for needed columns and saves the column position in a
   * corresponding variable. Distancy, uncertainty and type column will just be included if a column
   * name was given for them.
   */
  public void setHeaders(){

    // splits the line tab seperarated
    String[] headers = line.split("\t");
    int counter = 0;
    int a = 0;
    int b = 0;
    int c = 0;

    // for each tab separated header set the corresponding field to the counters value and set
    // counter + 1
    for (String h : headers) {
      if (h.equalsIgnoreCase("pos")) {
        mutation = counter;
        counter = counter + 1;
      } else if (h.equals("gene")) {
        gene = counter;
        counter = counter + 1;
      } else if (h.equals("transcript") || h.equals("transcripts")) {
        transcript = counter;
        counter = counter + 1;
      } else if (h.equals("sequence")) {
        neopeptide = counter;
        counter = counter + 1;
      } else if (h.equals("length")) {
        counter = counter + 1;
      } else if (h.contains("A*") && h.contains(" score") && (a==0)) {
        hlaA1 = counter;
        hlaA1allele = h.replace("HLA-", "").split(" ")[0];
        counter = counter + 1;
        a++;
      } else if (h.contains("A*") && h.contains(" score") && (a==1)) {
        hlaA2 = counter;
        hlaA2allele = h.replace("HLA-", "").split(" ")[0];
        counter = counter + 1;
      } else if (h.contains("B*") && h.contains(" score") && (b==0)) {
        hlaB1 = counter;
        hlaB1allele = h.replace("HLA-", "").split(" ")[0];
        counter = counter + 1;
        b++;
      } else if (h.contains("B*") && h.contains(" score") && (a==1)) {
        hlaB2 = counter;
        hlaB2allele = h.replace("HLA-", "").split(" ")[0];
        counter = counter + 1;
      } else if (h.contains("C*") && h.contains(" score") && (c==0)) {
        hlaC1 = counter;
        hlaC1allele = h.replace("HLA-", "").split(" ")[0];
        counter = counter + 1;
        c++;
      } else if (h.contains("C*") && h.contains(" score") && (c==1)) {
        hlaC2 = counter;
        hlaC2allele = h.replace("HLA-", "").split(" ")[0];
        counter = counter + 1;
     // just if a column name was given:
      } else if (!typeCol.equals("") && h.equals(typeCol)) {
        type = counter;
        counter = counter + 1;
      } else if (!methodCol.equals("") && h.equals(methodCol)) {
          method = counter;
          counter = counter + 1;
        // if another header is found, ignore it and set counter + 1
      } else {
        counter = counter + 1;
      }
    }
  }

  /**
   * Reads the input of the file and saves it in different maps.
   * 
   * @throws IOException
   */
  public void readInput() throws IOException {

    // initialize maps
    immMap = new HashMap<>();
    otherMap = new HashMap<>();

    // read all lines of the file
    while ((line = brReader.readLine()) != null) {

      // get all columns
      String[] columns = line.split("\t");

      // initialize allele map
      HashMap<String, String> alleleImmMap = new HashMap<>();
    
      // if neopeptide not yet readed
      if (!immMap.containsKey(columns[neopeptide])) {

        // initialize others map
        HashMap<String, String> others = new HashMap<>();

        // save mutation, gene, transcript, transcriptExpression
        others.put("mutation", columns[mutation]);
        others.put("gene", columns[gene]);
        others.put("transcript", columns[transcript]);
        others.put("transcriptExpression", columns[transcriptExpression]);
        if (!methodCol.equals("") && hasMethod){
          others.put("method", columns[method]);
        }
        // if type column exists also read type
        if (!typeCol.equals("") && hasType) {
          others.put("type", columns[type]);
        }

        // save all in a map with its neopeptide as key
        otherMap.put(columns[neopeptide], others);

        // save allele and its immunogenicity, uncertainty and distance in a map each and together
        // with
        // map this map with its neopeptide
        alleleImmMap.put(hlaA1allele, columns[hlaA1]);
        alleleImmMap.put(hlaA2allele, columns[hlaA2]);
        alleleImmMap.put(hlaB1allele, columns[hlaB1]);
        alleleImmMap.put(hlaB2allele, columns[hlaB2]);
        alleleImmMap.put(hlaC1allele, columns[hlaC1]);
        alleleImmMap.put(hlaC2allele, columns[hlaC2]);
        immMap.put(columns[neopeptide], alleleImmMap);

        // if neopeptide already exists in the map
      } else {

        // if different mutation, concat that mutation
        if (!otherMap.get(columns[neopeptide]).get("mutation").contains(columns[mutation])) {
          otherMap.get(columns[neopeptide]).put("mutation",
              otherMap.get(columns[neopeptide]).get("mutation").concat(", " + columns[mutation]));
        }
        // if different gene, concat that gene
        if (!otherMap.get(columns[neopeptide]).get("gene").contains(columns[gene])) {
          otherMap.get(columns[neopeptide]).put("gene",
              otherMap.get(columns[neopeptide]).get("gene").concat(", " + columns[gene]));
        }
        // if different transcript, concat that transcript
        if (!otherMap.get(columns[neopeptide]).get("transcript").contains(columns[transcript])) {
          otherMap.get(columns[neopeptide]).put("transcript", otherMap.get(columns[neopeptide])
              .get("transcript").concat(", " + columns[transcript]));
        }
        // if different transcript expression, add that transcript expression
        if (!otherMap.get(columns[neopeptide]).get("transcriptExpression")
            .contains(columns[transcriptExpression])) {
          otherMap.get(columns[neopeptide]).put("transcriptExpression",
              otherMap.get(columns[neopeptide]).get("transcriptExpression")
                  .concat(", " + columns[transcriptExpression]));
        } 
      }
    }
    brReader.close();
  }

  /**
   * Sets the bean with its parameters and adds it to a bean item container.
   */
  public void setBean() {
    for (String key : immMap.keySet()) {

      // initialize new epitope selection bean
      EpitopeSelectionBean newBean = new EpitopeSelectionBean();

      // set parameters with key and values from the map
      newBean.setIncluded(false);
      newBean.setExcluded(false);
      newBean.setNeopeptide(key);
      newBean.setImm(immMap.get(key));
      String[] alleleNames = newBean.prepareAlleleNames();
      newBean.prepareImm(alleleNames);
      if (!methodCol.equals("") && hasMethod) {
        newBean.setMethod(otherMap.get(key).get("method"));
      }
      newBean.setLength(key.length());
      if (key.length() > maxLength) {
        maxLength = key.length();
      }
      newBean.setMutation(otherMap.get(key).get("mutation"));
      newBean.setGene(otherMap.get(key).get("gene"));
      newBean.setTranscript(otherMap.get(key).get("transcript"));
      if (!typeCol.equals("") && hasType) {
        newBean.setType(otherMap.get(key).get("type"));
      }
      newBean.setTranscriptExpression(1f);

      epitopes.addBean(newBean);

    }
  }

  /**
   * 
   * @return maximum length of a neopeptide in the file
   */
  public int getMaxLength() {
    return maxLength;
  }

  /**
   * @return epitope selection bean item container
   */
  public BeanItemContainer<EpitopeSelectionBean> getEpitopes() {
    return epitopes;
  }


  public Boolean getHasType() {
    return hasType;
  }

  public void setHasType(Boolean hasType) {
    this.hasType = hasType;
  }


  public Boolean getHasMethod() {
    return hasMethod;
  }

  public void setHasMethod(Boolean hasMethod) {
    this.hasMethod = hasMethod;
  }



}
