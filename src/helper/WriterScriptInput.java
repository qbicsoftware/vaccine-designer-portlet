package helper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;

import model.EpitopeSelectionBean;

/**
 * The class {@link WriterScriptInput} writes the inpud files the epitope selection script expects
 * as input
 * 
 * @author spaethju
 *
 */
public class WriterScriptInput {

  private BufferedWriter inputWriter, includeWriter, excludeWriter, allelesWriter;
  private ArrayList<String> includedBeans, excludedBeans;
  private String method, type, uncertainty, distance, imm;

  /**
   * Constructor
   */
  public WriterScriptInput() {

  }

  /**
   * Writes the included and excluded beans of the bean item container in a file each. Writes the
   * allele file.
   * 
   * @param container bean item container containing all neopeptides from the uploaded input file.
   * @throws IOException
   */
  public void writeInputData(BeanItemContainer<EpitopeSelectionBean> container, String imm) throws IOException {
    this.imm = imm;
    this.method = method;
    this.type = type;
    this.uncertainty = uncertainty;
    this.distance = distance;
    inputWriter = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("/Users/spaethju/Desktop/input.txt"), "utf-8"));
    allelesWriter = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("/Users/spaethju/Desktop/alleles.txt"), "utf-8"));

    includeWriter = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("/Users/spaethju/Desktop/include.txt"), "utf-8"));

    excludeWriter = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("/Users/spaethju/Desktop/exclude.txt"), "utf-8"));

    includedBeans = getIncludedBeans(container);
    excludedBeans = getExcludedBeans(container);
    for (String includedNeopeptide : includedBeans) {
      includeWriter.write(includedNeopeptide);
      includeWriter.newLine();
    }

    for (String excludedNeopeptide : excludedBeans) {
      excludeWriter.write(excludedNeopeptide);
      excludeWriter.newLine();
    }

    String allelesHeadline =
        new String("A1\tA2\tB1\tB2\tC1\tC2\tA_expression\tB_expression\tC_expression");
    allelesWriter.write(allelesHeadline);
    allelesWriter.newLine();
    allelesWriter.write(getAlleles(container));

    allelesWriter.close();
    includeWriter.close();
    excludeWriter.close();
    
    writeInputFile(container);
  }
  
  /**
   * Writes the input file for the epitope selection script.
   * 
   * @throws IOException
   */
  public void writeInputFile(BeanItemContainer<EpitopeSelectionBean> container) throws IOException {

    // initialize buffered writer
    inputWriter.write("neopeptide" + "\t" + "length_of_neopeptide" + "\t" + "gene" + "\t" + "transcript" + "\t" + "transcript_expression" + "\t" + "HLA" + "\t" + imm + "\t" + "mutation");
    inputWriter.newLine();
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      for (String key : bean.getImm().keySet()) {
        inputWriter.write(bean.getNeopeptide() + "\t" + bean.getLength() + "\t" + bean.getGene() + "\t" + bean.getTranscript() + "\t" + bean.getTranscriptExpression() + "\t" + key + "\t" + bean.getImm().get(key) + "\t" + bean.getMutation());
        inputWriter.newLine();
      }
    }

    inputWriter.close();
  }

  /**
   * @param container bean item container containing all neopeptides from the uploaded input file.
   * @return list of all included beans
   */
  public ArrayList<String> getIncludedBeans(BeanItemContainer<EpitopeSelectionBean> container) {
    ArrayList<String> included = new ArrayList<String>();
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      if (bean.getIncluded() == true) {
        included.add(bean.getNeopeptide());
      }
    }
    return included;
  }

  /**
   * @param container bean item container containing all neopeptides from the uploaded input file.
   * @return list of all excluded beans
   */
  public ArrayList<String> getExcludedBeans(BeanItemContainer<EpitopeSelectionBean> container) {
    ArrayList<String> excluded = new ArrayList<String>();
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      if (bean.getExcluded() == true) {
        excluded.add(bean.getNeopeptide());
      }
    }
    return excluded;
  }

  /**
   * @param container bean item container containing all neopeptides from the uploaded input file.
   * @return string with all alleles seperated by tab
   */
  public String getAlleles(BeanItemContainer<EpitopeSelectionBean> container) {
    Set<String> alleles = new HashSet<String>();
    for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext();) {
      EpitopeSelectionBean bean = i.next();
      for (String key : bean.getImm().keySet()) {
        alleles.add(key);
      }

    }
    ArrayList<String> sortedList = new ArrayList<String>(alleles);
    Collections.sort(sortedList);
    Collections.reverse(sortedList);
    String alleleString = new String("10\t10\t10");
    for (String allele : sortedList) {
      alleleString = allele + "\t" + alleleString;
    }

    return alleleString;
  }

  /**
   * @param container bean item container containing all neopeptides from the uploaded input file.
   * @return number of all included beans
   */
  public int getNumberOfIncludedBeans(BeanItemContainer<EpitopeSelectionBean> container) {
    return getIncludedBeans(container).size();
  }

}
