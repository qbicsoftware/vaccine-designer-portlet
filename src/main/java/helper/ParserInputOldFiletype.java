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
 * The class {@link ParserInputOldFiletype} is responsible for the parsing of the uploaded epitope
 * prediction input data.
 * 
 * @author spaethju
 * 
 * 
 */
public class ParserInputOldFiletype {

  private BeanItemContainer<EpitopeSelectionBean> epitopes;
  private String line;
  private int method, mutation, gene, transcript, transcriptExpression, neopeptide, hla,
      hla1BindingPrediction, uncertainty, distance, type, maxLength;
  private String methodCol, immCol, uncertaintyCol, distanceCol, typeCol;
  private HashMap<String, HashMap<String, String>> immMap, uncMap, distMap, otherMap;
  private BufferedReader brReader;
  private File file;
  private Boolean hasType, hasDist, hasImm, hasUnc, hasMethod;

  public ParserInputOldFiletype() {

  }


  /**
   * Reads in a epitope file, writes the input file for the epitope selection script and saves it as
   * a bean in a bean item container.
   * 
   * @param file
   * @param immCol name of the immunogenicity column
   * @param uncertaintyCol name of the uncertainty column
   * @param distanceCol name of the distance column
   * @param typeCol name of the type column
   * @throws Exception 
   */
  public void parse(File file, String methodCol, String immCol, String uncertaintyCol, String distanceCol,
      String typeCol) throws Exception {

    this.immCol = immCol;
    this.uncertaintyCol = uncertaintyCol;
    this.distanceCol = distanceCol;
    this.typeCol = typeCol;
    this.methodCol = methodCol;
    this.file = file;

    // initialize bean item container for epitope selection beans
    epitopes = new BeanItemContainer<EpitopeSelectionBean>(EpitopeSelectionBean.class);

    // initialize buffered reader reading the file line by line

      if(correctInput()) {
        brReader = new BufferedReader(new FileReader(file));
        line = brReader.readLine();
        setHeaders();
        readInput();
        setBean();
      } else {
        throw new Exception();
      }
  }
  
  public Boolean correctInput() throws IOException {
    // splits the line tab seperarated
    brReader = new BufferedReader(new FileReader(file));
    line = brReader.readLine();
    String[] headers = line.split("\t");

    for (String h : headers) {
      if (!typeCol.equals("") && h.equals(typeCol)) {
        hasType = true;
      }
      if (!distanceCol.equals("") && h.equals(distanceCol)) {
        hasDist = true;
      }
      if (!uncertaintyCol.equals("") && h.equals(uncertaintyCol)) {
        hasUnc = true;
      }
      if (!methodCol.equals("") && h.equals(methodCol)) {
        hasMethod = true;
      }
      if (!immCol.equals("") && h.equals(immCol)) {
        hasImm = true;
      }
    }
    
    return hasImm;
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

    // for each tab separated header set the corresponding field to the counters value and set
    // counter + 1
    for (String h : headers) {
       if (h.equals("GENE")) {
        gene = counter;
        counter = counter + 1;
       } else if (h.equals("POS")) {
         mutation = counter;
         counter = counter + 1;
      } else if (h.equals("TRANSCRIPT")) {
        transcript = counter;
        counter = counter + 1;
      } else if (h.equals("PEPTIDE")) {
        neopeptide = counter;
        counter = counter + 1;
      } else if (h.equals("LENGTH")) {
        counter = counter + 1;
      } else if (h.equals(("ALLELE"))) {
        hla = counter;
        counter = counter + 1;
      } else if (h.equals(immCol)) {
        hla1BindingPrediction = counter;
        counter = counter + 1;

        // just if a column name was given:
      } else if (!uncertaintyCol.equals("") && h.equals(uncertaintyCol)) {
        uncertainty = counter;
        counter = counter + 1;
      } else if (!distanceCol.equals("") && h.equals(distanceCol)) {
        distance = counter;
        counter = counter + 1;
      } else if (!typeCol.equals("") && h.equals(typeCol)) {
        type = counter;
        counter = counter + 1;
      } else if (!methodCol.equals("") && h.equals(methodCol)) {
        method = counter;
        counter = counter + 1;

        // if another header is found, ignore it at set counter + 1
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
    uncMap = new HashMap<>();
    distMap = new HashMap<>();
    otherMap = new HashMap<>();

    // read all lines of the file
    while ((line = brReader.readLine()) != null) {

      // get all columns
      String[] columns = line.split("\t");

      // initialize allele map
      HashMap<String, String> alleleImmMap = new HashMap<>();
      HashMap<String, String> alleleUncMap = new HashMap<>();
      HashMap<String, String> alleleDistMap = new HashMap<>();

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
        alleleImmMap.put(columns[hla].replace("HLA-", ""), columns[hla1BindingPrediction]);
        immMap.put(columns[neopeptide], alleleImmMap);
        if (!uncertaintyCol.equals("")) {
          alleleUncMap.put(columns[hla].replace("HLA-", ""), columns[uncertainty]);
          uncMap.put(columns[neopeptide], alleleUncMap);
        }
        if (!distanceCol.equals("")) {
          alleleDistMap.put(columns[hla].replace("HLA-", ""), columns[distance]);
          distMap.put(columns[neopeptide], alleleDistMap);
        }

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

        // add allele with its values to the map
        alleleImmMap.put(columns[hla].replace("HLA-", ""), columns[hla1BindingPrediction]);
        // alleleMap.put(columns[hla], columns[hla1BindingPrediction] + "," + columns[uncertainty] +
        // "," + columns[distance]);
        immMap.get(columns[neopeptide]).putAll(alleleImmMap);
        if (!uncertaintyCol.equals("") && hasUnc) {
          alleleUncMap.put(columns[hla].replace("HLA-", ""), columns[uncertainty]);
          uncMap.get(columns[neopeptide]).putAll(alleleUncMap);
        }
        if (!distanceCol.equals("") && hasDist) {
          alleleDistMap.put(columns[hla].replace("HLA-", ""), columns[distance]);
          distMap.get(columns[neopeptide]).putAll(alleleDistMap);
        }
     }
    }
    brReader.close();
  }

  /**
   * Sets the bean with its parameters and adds it to a bean item container.
   */
  public void setBean(){
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
      if (!uncertaintyCol.equals("") && hasUnc) {
        newBean.setUnc(uncMap.get(key));
        newBean.prepareUncertainty(alleleNames);
      }
      if (!distanceCol.equals("") && hasDist) {
        newBean.setDist(distMap.get(key));
        newBean.prepareDistance(alleleNames);
      }

      newBean.setLength(key.length());
      if (key.length() > maxLength) {
        maxLength = key.length();
      }
      //newBean.setMutation(otherMap.get(key).get("mutation"));
      newBean.setMutation(otherMap.get(key).get("mutation"));
      newBean.setGene(otherMap.get(key).get("gene"));
      newBean.setTranscript(otherMap.get(key).get("transcript"));
      newBean.setTranscriptExpression(1f);
      if (!typeCol.equals("") && hasType) {
        newBean.setType(otherMap.get(key).get("type"));
      }

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


  public Boolean getHasDist() {
    return hasDist;
  }


  public void setHasDist(Boolean hasDist) {
    this.hasDist = hasDist;
  }


  public Boolean getHasImm() {
    return hasImm;
  }


  public void setHasImm(Boolean hasImm) {
    this.hasImm = hasImm;
  }


  public Boolean getHasUnc() {
    return hasUnc;
  }


  public void setHasUnc(Boolean hasUnc) {
    this.hasUnc = hasUnc;
  }


  public Boolean getHasMethod() {
    return hasMethod;
  }


  public void setHasMethod(Boolean hasMethod) {
    this.hasMethod = hasMethod;
  }
}
