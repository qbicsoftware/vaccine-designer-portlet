package helper.writer;

import com.vaadin.data.util.BeanItemContainer;
import helper.DescriptionHandler;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import model.EpitopeSelectionBean;

/**
 * The class {@link WriterScriptInput} writes the inpud files the epitope selection script expects
 * as input
 *
 * @author spaethju
 */
public class WriterScriptInput {

    private BufferedWriter inputWriter, includeWriter, excludeWriter, allelesWriter;
    private ArrayList<String> includedBeans, excludedBeans;
    private String type, unc, dist, inputPath, allelePath, includePath, excludePath;
    private Boolean hasTranscriptExpression, hasType, hasUnc, hasDist;
    private DescriptionHandler dh = new DescriptionHandler();

    /**
     * Constructor
     */
    public WriterScriptInput(String inputPath, String allelePath, String includePath,
                             String excludePath) {
        this.inputPath = inputPath;
        this.allelePath = allelePath;
        this.includePath = includePath;
        this.excludePath = excludePath;
    }

    /**
     * Writes the included and excluded beans of the bean item container in a file each. Writes the
     * allele file.
     *
     * @param container bean item container containing all neopeptides from the uploaded input file.
     */
    public void writeInputData(BeanItemContainer<EpitopeSelectionBean> container,
                               HashMap<String, String> alleles, HashMap<String, String> allele_expressions, String type,
                               String unc, String dist, Boolean hasTranscriptExpression, Boolean hasType, Boolean hasUnc,
                               Boolean hasDist) throws IOException {
        this.type = type;
        this.unc = unc;
        this.dist = dist;
        this.hasTranscriptExpression = hasTranscriptExpression;
        this.hasType = hasType;
        this.hasUnc = hasUnc;
        this.hasDist = hasDist;
        inputWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(inputPath), "utf-8"));
        allelesWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(allelePath), "utf-8"));

        includeWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(includePath), "utf-8"));

        excludeWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(excludePath), "utf-8"));

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

        String allelesHeadline = "A1\tA2\tB1\tB2\tC1\tC2\tA_expression\tB_expression\tC_expression";
        allelesWriter.write(allelesHeadline);
        allelesWriter.newLine();
        allelesWriter.write(setAlleleRow(alleles, allele_expressions));

        allelesWriter.close();
        includeWriter.close();
        excludeWriter.close();

        writeInputFile(container);

    }

    /**
     * Writes the input file for the epitope selection script.
     */
    public void writeInputFile(BeanItemContainer<EpitopeSelectionBean> container) throws IOException {

        // initialize buffered writer
        String header =
                "neopeptide" + "\t" + "length_of_neopeptide" + "\t" + "gene" + "\t" + "transcript" + "\t"
                        + "transcript_expression" + "\t" + "HLA" + "\t" + "score" + "\t" + "mutation";
        if (!type.equals("") && hasType) {
            header += ("\t" + type);
        }
        if (!unc.equals("") && hasUnc) {
            header += ("\t" + unc);
        }
        if (!dist.equals("") && hasDist) {
            header += ("\t" + dist);
        }
        inputWriter.write(header);
        inputWriter.newLine();
        for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext(); ) {
            EpitopeSelectionBean bean = i.next();
            for (String key : bean.getImm().keySet()) {
                if (key != null) {
                    String peptide =
                            bean.getNeopeptide() + "\t" + bean.getLength() + "\t" + bean.getGene() + "\t" + bean
                                    .getTranscript() + "\t" + bean.getTranscriptExpression() + "\t" + key + "\t" + bean
                                    .getImm().get(key) + "\t" + bean.getMutation();
                    if (!type.equals("") && hasType) {
                        peptide += ("\t" + bean.getType());
                    }
                    if (!unc.equals("") && hasUnc) {
                        peptide += ("\t" + bean.getUnc().get(key));
                    }
                    if (!dist.equals("") && hasDist) {
                        peptide += ("\t" + bean.getDist().get(key));
                    }
                    inputWriter.write(peptide);
                    inputWriter.newLine();
                }
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
        for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext(); ) {
            EpitopeSelectionBean bean = i.next();
            if (bean.getIncluded()) {
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
        for (Iterator<EpitopeSelectionBean> i = container.getItemIds().iterator(); i.hasNext(); ) {
            EpitopeSelectionBean bean = i.next();
            if (bean.getExcluded()) {
                excluded.add(bean.getNeopeptide());
            }
        }
        return excluded;
    }

    /**
     * @return string with all alleles seperated by tab
     */
    public String setAlleleRow(HashMap<String, String> alleles,
                               HashMap<String, String> allele_expressions) {
        String alleleString = alleles.get("A1") + "\t" +
                alleles.get("A2") + "\t" +
                alleles.get("B1") + "\t" +
                alleles.get("B2") + "\t" +
                alleles.get("C1") + "\t" +
                alleles.get("C2") + "\t" +
                allele_expressions.get("A") + "\t" +
                allele_expressions.get("B") + "\t" +
                allele_expressions.get("C") + "\t";
        return alleleString;
    }

}
