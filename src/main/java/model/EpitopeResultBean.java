package model;

import java.util.HashMap;

/**
 * The class {@link EpitopeResultBean} represents the information of one epitope from the optimal
 * set of epitopes predicted by the epitope selection script.
 *
 * @author spaethju
 */
public class EpitopeResultBean {

  private String neoepitope, type, genes, mutations;
  private HashMap<String, String> alleles;
  private Float immA1, immA2, immB1, immB2, immC1, immC2, distA1, distA2, distB1, distB2, distC1,
      distC2;

  public EpitopeResultBean() {

  }


  public String getNeoepitope() {
    return neoepitope;
  }

  public void setNeoepitope(String neoepitope) {
    this.neoepitope = neoepitope;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getGenes() {
    return genes;
  }

  public void setGenes(String genes) {
    this.genes = genes;
  }

  public String getMutations() {
    return mutations;
  }

  public void setMutations(String mutations) {
    this.mutations = mutations;
  }

  public Float getImmA1() {
    return immA1;
  }

  public void setImmA1(Float immA1) {
    this.immA1 = immA1;
  }

  public Float getImmA2() {
    return immA2;
  }

  public void setImmA2(Float immA2) {
    this.immA2 = immA2;
  }

  public Float getImmB1() {
    return immB1;
  }

  public void setImmB1(Float immB1) {
    this.immB1 = immB1;
  }

  public Float getImmB2() {
    return immB2;
  }

  public void setImmB2(Float immB2) {
    this.immB2 = immB2;
  }

  public Float getImmC1() {
    return immC1;
  }

  public void setImmC1(Float immC1) {
    this.immC1 = immC1;
  }

  public Float getImmC2() {
    return immC2;
  }

  public void setImmC2(Float immC2) {
    this.immC2 = immC2;
  }

  public HashMap<String, String> getAlleles() {
    return alleles;
  }

  public void setAlleles(HashMap<String, String> alleles) {
    this.alleles = alleles;
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
}
