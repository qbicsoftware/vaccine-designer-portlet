package model;

import com.vaadin.data.util.BeanItemContainer;
import java.util.HashMap;

/**
 * The class {@link ResultBean} represents one solution of a result of the prediction by the epitope
 * selection script.
 *
 * @author spaethju
 */
public class ResultBean {

  private Integer nof_epitopes, nof_taa_epitopes, antigen_const, hla_const, overlap_const,
      distance2self, uncertainty;
  private Float threshold_epitope, threshold_distance, immunogenicity, covered_hlas,
      covered_antigens, risk;

  private BeanItemContainer<EpitopeResultBean> epitopeResultBeans;

  private HashMap<String, String> ps, as;

  public ResultBean() {

  }

  public int getNof_epitopes() {
    return nof_epitopes;
  }

  public void setNof_epitopes(int nof_epitopes) {
    this.nof_epitopes = nof_epitopes;
  }

  public int getNof_taa_epitopes() {
    return nof_taa_epitopes;
  }

  public void setNof_taa_epitopes(int nof_taa_epitopes) {
    this.nof_taa_epitopes = nof_taa_epitopes;
  }

  public int getAntigen_const() {
    return antigen_const;
  }

  public void setAntigen_const(int antigen_const) {
    this.antigen_const = antigen_const;
  }

  public int getHla_const() {
    return hla_const;
  }

  public void setHla_const(int hla_const) {
    this.hla_const = hla_const;
  }

  public int getOverlap_const() {
    return overlap_const;
  }

  public void setOverlap_const(int overlap_const) {
    this.overlap_const = overlap_const;
  }

  public int getDistance2self() {
    return distance2self;
  }

  public void setDistance2self(int distance2self) {
    this.distance2self = distance2self;
  }

  public int getUncertainty() {
    return uncertainty;
  }

  public void setUncertainty(int uncertainty) {
    this.uncertainty = uncertainty;
  }

  public Float getThreshold_epitope() {
    return threshold_epitope;
  }

  public void setThreshold_epitope(Float threshold_epitope) {
    this.threshold_epitope = threshold_epitope;
  }

  public Float getThreshold_distance() {
    return threshold_distance;
  }

  public void setThreshold_distance(Float threshold_distance) {
    this.threshold_distance = threshold_distance;
  }

  public Float getImmunogenicity() {
    return immunogenicity;
  }

  public void setImmunogenicity(Float immunogenicity) {
    this.immunogenicity = immunogenicity;
  }

  public Float getCovered_hlas() {
    return covered_hlas;
  }

  public void setCovered_hlas(Float covered_hlas) {
    this.covered_hlas = covered_hlas;
  }

  public Float getCovered_antigens() {
    return covered_antigens;
  }

  public void setCovered_antigens(Float covered_antigens) {
    this.covered_antigens = covered_antigens;
  }

  public BeanItemContainer<EpitopeResultBean> getEpitopeResultBeans() {
    return epitopeResultBeans;
  }

  public void setEpitopeResultBeans(BeanItemContainer<EpitopeResultBean> epitopeResultBeans) {
    this.epitopeResultBeans = epitopeResultBeans;
  }

  public Float getRisk() {
    return risk;
  }

  public void setRisk(Float risk) {
    this.risk = risk;
  }

  public HashMap<String, String> getPs() {
    return ps;
  }

  public void setPs(HashMap<String, String> ps) {
    this.ps = ps;
  }

  public HashMap<String, String> getAs() {
    return as;
  }

  public void setAs(HashMap<String, String> as) {
    this.as = as;
  }

}
