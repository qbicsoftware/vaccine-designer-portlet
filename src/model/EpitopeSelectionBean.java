package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * The class {@link EpitopeSelectionBean} represents the information of a neopeptide of the input
 * data.
 * 
 * @author spaethju
 * 
 */

public class EpitopeSelectionBean {

  private String neopeptide, transcript, mutation, gene, type, method;
  private HashMap<String, String> imm, unc, dist;
  private Float hlaA1, hlaA2, hlaB1, hlaB2, hlaC1, hlaC2, transcriptExpression;
  private Float uncertaintyA1, uncertaintyA2, uncertaintyB1, uncertaintyB2, uncertaintyC1,
      uncertaintyC2, distanceA1, distanceA2, distanceB1, distanceB2, distanceC1, distanceC2;
  private int length;
  private Boolean included, excluded;

  public EpitopeSelectionBean() {

  }


  public String getNeopeptide() {
    return neopeptide;
  }

  public void setNeopeptide(String neopeptide) {
    this.neopeptide = neopeptide;
  }

  public Boolean getExcluded() {
    return excluded;
  }

  public void setExcluded(Boolean isExcluded) {
    this.excluded = isExcluded;
  }

  public Boolean getIncluded() {
    return included;
  }

  public void setIncluded(Boolean isIncluded) {
    this.included = isIncluded;
  }

  public int getLength() {
    return length;
  }


  public void setLength(int length) {
    this.length = length;
  }


  public HashMap<String, String> getImm() {
    return imm;
  }


  public void setImm(HashMap<String, String> imm) {
    this.imm = imm;
  }


  public Float getTranscriptExpression() {
    return transcriptExpression;
  }


  public void setTranscriptExpression(Float transcriptExpression) {
    this.transcriptExpression = transcriptExpression;
  }



  public String getTranscript() {
    return transcript;
  }


  public void setTranscript(String transcript) {
    this.transcript = transcript;
  }


  public String getGene() {
    return gene;
  }


  public void setGene(String gene) {
    this.gene = gene;
  }

  public String getMutation() {
    return mutation;
  }


  public void setMutation(String mutation) {
    this.mutation = mutation;
  }


  public Float getHlaA1() {
    return hlaA1;
  }


  public void setHlaA1(Float hlaA1) {
    this.hlaA1 = hlaA1;
  }

  public Float getHlaA2() {
    return hlaA2;
  }


  public void setHlaA2(Float hlaA2) {
    this.hlaA2 = hlaA2;
  }


  public Float getHlaB1() {
    return hlaB1;
  }


  public void setHlaB1(Float hlaB1) {
    this.hlaB1 = hlaB1;
  }


  public Float getHlaB2() {
    return hlaB2;
  }


  public void setHlaB2(Float hlaB2) {
    this.hlaB2 = hlaB2;
  }


  public Float getHlaC1() {
    return hlaC1;
  }


  public void setHlaC1(Float hlaC1) {
    this.hlaC1 = hlaC1;
  }


  public Float getHlaC2() {
    return hlaC2;
  }

  public void setHlaC2(Float hlaC2) {
    this.hlaC2 = hlaC2;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Float getUncertaintyA1() {
    return uncertaintyA1;
  }

  public void setUncertaintyA1(Float uncertaintyA1) {
    this.uncertaintyA1 = uncertaintyA1;
  }

  public Float getUncertaintyA2() {
    return uncertaintyA2;
  }

  public void setUncertaintyA2(Float uncertaintyA2) {
    this.uncertaintyA2 = uncertaintyA2;
  }

  public Float getUncertaintyB1() {
    return uncertaintyB1;
  }

  public void setUncertaintyB1(Float uncertaintyB1) {
    this.uncertaintyB1 = uncertaintyB1;
  }

  public Float getUncertaintyB2() {
    return uncertaintyB2;
  }

  public void setUncertaintyB2(Float uncertaintyB2) {
    this.uncertaintyB2 = uncertaintyB2;
  }

  public Float getUncertaintyC1() {
    return uncertaintyC1;
  }

  public void setUncertaintyC1(Float uncertaintyC1) {
    this.uncertaintyC1 = uncertaintyC1;
  }

  public Float getUncertaintyC2() {
    return uncertaintyC2;
  }

  public void setUncertaintyC2(Float uncertaintyC2) {
    this.uncertaintyC2 = uncertaintyC2;
  }

  public Float getDistanceA1() {
    return distanceA1;
  }

  public void setDistanceA1(Float distanceA1) {
    this.distanceA1 = distanceA1;
  }

  public Float getDistanceA2() {
    return distanceA2;
  }

  public void setDistanceA2(Float distanceA2) {
    this.distanceA2 = distanceA2;
  }

  public Float getDistanceB1() {
    return distanceB1;
  }

  public void setDistanceB1(Float distanceB1) {
    this.distanceB1 = distanceB1;
  }

  public Float getDistanceB2() {
    return distanceB2;
  }

  public void setDistanceB2(Float distanceB2) {
    this.distanceB2 = distanceB2;
  }

  public Float getDistanceC1() {
    return distanceC1;
  }

  public void setDistanceC1(Float distanceC1) {
    this.distanceC1 = distanceC1;
  }

  public Float getDistanceC2() {
    return distanceC2;
  }

  public void setDistanceC2(Float distanceC2) {
    this.distanceC2 = distanceC2;
  }

  public String getMethod() {
    return method;
  }


  public void setMethod(String method) {
    this.method = method;
  }


  public HashMap<String, String> getUnc() {
    return unc;
  }

  public void setUnc(HashMap<String, String> unc) {
    this.unc = unc;
  }

  public HashMap<String, String> getDist() {
    return dist;
  }

  public void setDist(HashMap<String, String> dist) {
    this.dist = dist;
  }

  public String[] prepareAlleleNames() {
    String[] alleleNames = new String[6];
    ArrayList<String> hlaA = new ArrayList<>();
    ArrayList<String> hlaB = new ArrayList<>();
    ArrayList<String> hlaC = new ArrayList<>();
    int aCounter = 0;
    int bCounter = 0;
    int cCounter = 0;

    for (String key : imm.keySet()) {
      if (key.contains("A*") && aCounter == 1) {
        hlaA.add(key);
      }
      if (key.contains("A*") && aCounter == 0) {
        hlaA.add(key);
        aCounter++;
      }
      if (key.contains("B*") && bCounter == 1) {
        hlaB.add(key);
      }
      if (key.contains("B*") && bCounter == 0) {
        hlaB.add(key);
        bCounter++;
      }
      if (key.contains("C*") && cCounter == 1) {
        hlaC.add(key);
      }
      if (key.contains("C*") && cCounter == 0) {
        hlaC.add(key);
        cCounter++;
      }
    }

    alleleNames[0] = hlaA.get(0);
    alleleNames[1] = hlaA.get(1);
    alleleNames[2] = hlaB.get(0);
    alleleNames[3] = hlaB.get(1);
    alleleNames[4] = hlaC.get(0);
    alleleNames[5] = hlaC.get(1);
    return alleleNames;
  }

  public void prepareImm(String[] alleleNames) {
    for (String key : imm.keySet()) {
      Float score = Float.parseFloat(imm.get(key).split(",")[0]);
      if (key.contains(alleleNames[0])) {
        setHlaA1(score);
      }
      if (key.contains(alleleNames[1])) {
        setHlaA2(score);
      }
      if (key.contains(alleleNames[2])) {
        setHlaB1(score);
      }
      if (key.contains(alleleNames[3])) {
        setHlaB2(score);
      }
      if (key.contains(alleleNames[4])) {
        setHlaC1(score);
      }
      if (key.contains(alleleNames[5])) {
        setHlaC2(score);
      }
    }
  }

  public void prepareUncertainty(String[] alleleNames) {
    for (String key : unc.keySet()) {
      Float uncertainty = Float.parseFloat(unc.get(key));
      if (key.contains(alleleNames[0])) {
        setUncertaintyA1(uncertainty);
      }
      if (key.contains(alleleNames[1])) {
        setUncertaintyA2(uncertainty);
      }
      if (key.contains(alleleNames[2])) {
        setUncertaintyB1(uncertainty);
      }
      if (key.contains(alleleNames[3])) {
        setUncertaintyB2(uncertainty);
      }
      if (key.contains(alleleNames[4])) {
        setUncertaintyC1(uncertainty);
      }
      if (key.contains(alleleNames[5])) {
        setUncertaintyC2(uncertainty);
      }
    }
  }

  public void prepareDistance(String[] alleleNames) {
    for (String key : dist.keySet()) {
      Float distance = Float.parseFloat(dist.get(key));
      if (key.contains(alleleNames[0])) {
        setDistanceA1(distance);
      }
      if (key.contains(alleleNames[1])) {
        setDistanceA2(distance);
      }
      if (key.contains(alleleNames[2])) {
        setDistanceB1(distance);
      }
      if (key.contains(alleleNames[3])) {
        setDistanceB2(distance);
      }
      if (key.contains(alleleNames[4])) {
        setDistanceC1(distance);
      }
      if (key.contains(alleleNames[5])) {
        setDistanceC2(distance);
      }
    }
  }

}

