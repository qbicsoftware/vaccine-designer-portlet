package model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;

/**
 * The class {@link EpitopeSelectionBean} represents the information of a neopeptide of the input
 * data.
 *
 * @author spaethju
 */

public class EpitopeSelectionBean {

  private String neopeptide, transcript, mutation, gene, type, method;
  private HashMap<String, String> imm, dist, unc;
  private Float hlaA1, hlaA2, hlaB1, hlaB2, hlaC1, hlaC2, distA1, distA2, distB1, distB2, distC1, distC2, uncA1, uncA2, uncB1, uncB2, uncC1, uncC2, transcriptExpression;
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

  public void setImm(HashMap<String, String> alleleImmunigenicities) {
    this.imm = alleleImmunigenicities;
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

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String[] prepareAlleleNames(String[] alleles) {
    String[] alleleNames = new String[6];

    alleleNames[0] = alleles[0];
    alleleNames[1] = alleles[1];
    alleleNames[2] = alleles[2];
    alleleNames[3] = alleles[3];
    alleleNames[4] = alleles[4];
    alleleNames[5] = alleles[5];
    return alleleNames;
  }

  public HashMap<String, String> getDist() {
    return dist;
  }

  public void setDist(HashMap<String, String> dist) {
    this.dist = dist;
  }

  public HashMap<String, String> getUnc() {
    return unc;
  }

  public void setUnc(HashMap<String, String> unc) {
    this.unc = unc;
  }

  public Float getDistA1() {
    return distA1;
  }

  public void setDistA1(Float distA1) {
    this.distA1 = distA1;
  }

  public Float getDistA2() {
    return distA2;
  }

  public void setDistA2(Float distA2) {
    this.distA2 = distA2;
  }

  public Float getDistB1() {
    return distB1;
  }

  public void setDistB1(Float distB1) {
    this.distB1 = distB1;
  }

  public Float getDistB2() {
    return distB2;
  }

  public void setDistB2(Float distB2) {
    this.distB2 = distB2;
  }

  public Float getDistC1() {
    return distC1;
  }

  public void setDistC1(Float distC1) {
    this.distC1 = distC1;
  }

  public Float getDistC2() {
    return distC2;
  }

  public void setDistC2(Float distC2) {
    this.distC2 = distC2;
  }

  public Float getUncA1() {
    return uncA1;
  }

  public void setUncA1(Float uncA1) {
    this.uncA1 = uncA1;
  }

  public Float getUncA2() {
    return uncA2;
  }

  public void setUncA2(Float uncA2) {
    this.uncA2 = uncA2;
  }

  public Float getUncB1() {
    return uncB1;
  }

  public void setUncB1(Float uncB1) {
    this.uncB1 = uncB1;
  }

  public Float getUncB2() {
    return uncB2;
  }

  public void setUncB2(Float uncB2) {
    this.uncB2 = uncB2;
  }

  public Float getUncC1() {
    return uncC1;
  }

  public void setUncC1(Float uncC1) {
    this.uncC1 = uncC1;
  }

  public Float getUncC2() {
    return uncC2;
  }

  public void setUncC2(Float uncC2) {
    this.uncC2 = uncC2;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}

