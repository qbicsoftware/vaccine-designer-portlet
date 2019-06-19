package helper.parser;

import com.vaadin.data.util.BeanItemContainer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.EpitopeSelectionBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@link ParserInputAllelesAsRows} is responsible for the parsing of the uploaded epitope
 * prediction input data.
 *
 * @author spaethju
 */
public class ParserInputAllelesAsRows {

    private static final Logger LOG = LogManager.getLogger(ParserInputAllelesAsRows.class);
    private BeanItemContainer<EpitopeSelectionBean> epitopes;
    private String line, hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele, hlaC2allele;
    private int methodIdx, mutation, gene, transcript, transcriptExpression, neopeptide, hla,
            hla1BindingPrediction, uncertainty, distance, type, maxLength;
    private String methodCol, immCol, uncertaintyCol, distanceCol, typeCol, transcriptExpressionCol;
    private HashMap<String, HashMap<String, HashMap<String, String>>> peptides;
    private BufferedReader brReader;
    private Boolean hasType, hasDist, hasImm, hasUnc, hasMethod, hasTranscriptExpression;
    private ArrayList<String> alleleList;
    private String[] alleles;
    private String method;

    public ParserInputAllelesAsRows() {
        hasTranscriptExpression = false;
    }


    /**
     * Reads in a epitope file, writes the input file for the epitope selection script and saves it as
     * a bean in a bean item container.
     *
     * @param immCol name of the immunogenicity column
     * @param typeCol name of the type column
     */
    public void parse(File file, String methodCol, String immCol, String typeCol,
                      String uncertaintyCol, String distanceCol, String transcriptExpressionCol,
                      ArrayList<String> alleles) throws Exception {

        this.immCol = immCol;

        this.typeCol = typeCol;
        this.methodCol = methodCol;
        this.uncertaintyCol = uncertaintyCol;
        this.distanceCol = distanceCol;
        this.transcriptExpressionCol = transcriptExpressionCol;
        this.alleleList = alleles;

        hasMethod = false;

        int a = 0;
        int b = 0;
        int c = 0;

        for (String allele : alleles) {
            if (allele.startsWith("A")) {
                if (a == 0) {
                    a = a + 1;
                    hlaA1allele = allele;
                } else {
                    hlaA2allele = allele;
                }
            }
            else if (allele.startsWith("B")) {
                if (b == 0) {
                    b = b + 1;
                    hlaB1allele = allele;
                } else {
                    hlaB2allele = allele;
                }
            }
            else if (allele.startsWith("C")) {
                if (c == 0) {
                    c = c + 1;
                    hlaC1allele = allele;
                } else {
                    hlaC2allele = allele;
                }
            }
        }
        // initialize bean item container for epitope selection beans
        epitopes = new BeanItemContainer<>(EpitopeSelectionBean.class);

        // initialize buffered reader reading the file line by line
        brReader = new BufferedReader(new FileReader(file));
        line = brReader.readLine();
        setHeaders();
        readInput();
        setBean();
    }


    /**
     * Searches the header of the uploaded file for needed columns and saves the column position in a
     * corresponding variable. Distancy, uncertainty and type column will just be included if a column
     * name was given for them.
     */
    public void setHeaders() throws NullPointerException {

        // splits the line tab seperarated
        String[] headers = line.split("\t");
        int counter = 0;
        hasType = false;
        hasDist = false;
        hasUnc = false;
        hasTranscriptExpression = false;
        // for each tab separated header set the corresponding field to the counters value and set
        // counter + 1

        for (String h : headers) {
            if (h.equalsIgnoreCase("mutation") || h.equalsIgnoreCase("POS")) {
                mutation = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("gene")) {
                gene = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("transcript") || h.equalsIgnoreCase("transcripts")) {
                transcript = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("neopeptide") || h.equalsIgnoreCase("mut_pep") || h
                    .equalsIgnoreCase("peptide")) {
                neopeptide = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("length_of_neopeptide") || h.equalsIgnoreCase("length")) {
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("HLA") || h.equalsIgnoreCase("ALLELE")) {
                hla = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase(immCol)) {
                hla1BindingPrediction = counter;
                counter = counter + 1;
                // just if a column name was given:
            } else if (!typeCol.equals("") && h.equalsIgnoreCase(typeCol)) {
                type = counter;
                counter = counter + 1;
                hasType = true;
            } else if (!methodCol.equals("") && h.equalsIgnoreCase(methodCol)) {
                methodIdx = counter;
                counter = counter + 1;
                hasMethod = true;
            } else if (!uncertaintyCol.equals("") && h.equalsIgnoreCase(uncertaintyCol)) {
                uncertainty = counter;
                counter = counter + 1;
                hasUnc = true;
            } else if (!distanceCol.equals("") && h.equalsIgnoreCase(distanceCol)) {
                distance = counter;
                counter = counter + 1;
                hasDist = true;
            } else if (!transcriptExpressionCol.equals("") && h
                    .equalsIgnoreCase(transcriptExpressionCol)) {
                transcriptExpression = counter;
                counter = counter + 1;
                hasTranscriptExpression = true;
                // if another header is found, ignore it at set counter + 1
            } else {
                counter = counter + 1;
            }

        }
    }

    /**
     * Reads the input of the file and saves it in different maps.
     */
    public void readInput() throws IOException {

        peptides = new HashMap<>();
        int counter = 0;
        // read all lines of the file
        while ((line = brReader.readLine()) != null) {
            // get all column
            String[] columns = line.split("\t");
            String allele = columns[hla].replace("HLA-", "").trim();
            if (!hasMethod){
                method = "unknown";
            } else {
                method = columns[methodIdx];
            }
            if (!alleleList.contains(allele)) {
                continue;
            }

            if (!(peptides.containsKey(columns[neopeptide]))) {
                peptides.put(columns[neopeptide], new HashMap<>());
                HashMap<String, String> valuesMap = new HashMap<>();
                valuesMap.put("mutation", columns[mutation]);
                valuesMap.put("gene", columns[gene]);
                valuesMap.put("transcript", columns[transcript]);
                valuesMap.put(allele + " Score", columns[hla1BindingPrediction]);
                if (!(distanceCol.equals("")) && hasDist) {
                    valuesMap.put(allele + " Dist", columns[distance]);
                }
                if (!(uncertaintyCol.equals("")) && hasUnc) {
                    valuesMap.put(allele + " Unc", columns[uncertainty]);
                }
                if (!(typeCol.equals("")) && hasType) {
                    valuesMap.put("type", columns[type]);
                }
                if (!(transcriptExpressionCol.equals("")) && hasTranscriptExpression) {
                    valuesMap.put("transcriptExpression", columns[transcriptExpression]);
                }
                peptides.get(columns[neopeptide]).put(method, valuesMap);
            }
            if (peptides.containsKey(columns[neopeptide]) && !peptides.get(columns[neopeptide])
                    .containsKey(method)) {
                HashMap<String, String> valuesAddMap = new HashMap<>();
                valuesAddMap.put("mutation", columns[mutation]);
                valuesAddMap.put("gene", columns[gene]);
                valuesAddMap.put("transcript", columns[transcript]);
                valuesAddMap.put(allele + " Score", columns[hla1BindingPrediction]);
                if (!(distanceCol.equals("")) && hasDist) {
                    valuesAddMap.put(allele + " Dist", columns[distance]);
                }
                if (!(uncertaintyCol.equals("")) && hasUnc) {
                    valuesAddMap.put(allele + " Unc", columns[uncertainty]);
                }
                if (!(typeCol.equals("")) && hasType) {
                    valuesAddMap.put("type", columns[type]);
                }
                if (!(transcriptExpressionCol.equals("")) && hasTranscriptExpression) {
                    valuesAddMap.put("transcriptExpression", columns[transcriptExpression]);
                }
                peptides.get(columns[neopeptide]).put(method, valuesAddMap);
            }
            if (peptides.containsKey(columns[neopeptide]) && peptides.get(columns[neopeptide])
                    .containsKey(method)) {
                HashMap<String, String> addAlleleMap = peptides.get(columns[neopeptide])
                        .get(method);
                addAlleleMap.put(allele + " Score", columns[hla1BindingPrediction]);
                if (!(distanceCol.equals("")) && hasDist) {
                    addAlleleMap.put(allele + " Dist", columns[distance]);
                }
                if (!(uncertaintyCol.equals("")) && hasUnc) {
                    addAlleleMap.put(allele + " Unc", columns[uncertainty]);
                }
                peptides.get(columns[neopeptide]).replace(method, addAlleleMap);
            }

        }

        brReader.close();
    }

    /**
     * Sets the bean with its parameters and adds it to a bean item container.
     */
    public void setBean() {
        LOG.info(peptides.size() + " to parse...");
        for (String peptide : peptides.keySet()) {
            for (String method : peptides.get(peptide).keySet()) {
                EpitopeSelectionBean newBean = new EpitopeSelectionBean();
                // set parameters with key and values from the map
                newBean.setIncluded(false);
                newBean.setExcluded(false);
                newBean.setNeopeptide(peptide);
                HashMap<String, String> imm = new HashMap<>();
                HashMap<String, String> dist = new HashMap<>();
                HashMap<String, String> unc = new HashMap<>();
                if (peptides.get(peptide).get(method).get(hlaA1allele + " Score") != null) {
                    imm.put(hlaA1allele, peptides.get(peptide).get(method).get(hlaA1allele + " Score"));
                    newBean.setHlaA1(
                            Float.parseFloat(peptides.get(peptide).get(method).get(hlaA1allele + " Score")));
                    if (!(distanceCol.equals("")) && hasDist) {
                        dist.put(hlaA1allele, peptides.get(peptide).get(method).get(hlaA1allele + " Dist"));
                        newBean.setDistA1(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaA1allele + " Dist")));
                    }
                    if (!(uncertaintyCol.equals("")) && hasUnc) {
                        unc.put(hlaA1allele, peptides.get(peptide).get(method).get(hlaA1allele + " Unc"));
                        newBean.setUncA1(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaA1allele + " Unc")));
                    }

                }
                if (peptides.get(peptide).get(method).get(hlaA2allele + " Score") != null) {
                    imm.put(hlaA2allele, peptides.get(peptide).get(method).get(hlaA2allele + " Score"));
                    newBean.setHlaA2(
                            Float.parseFloat(peptides.get(peptide).get(method).get(hlaA2allele + " Score")));
                    if (!(distanceCol.equals("")) && hasDist) {
                        dist.put(hlaA2allele, peptides.get(peptide).get(method).get(hlaA2allele + " Dist"));
                        newBean.setDistA2(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaA2allele + " Dist")));
                    }
                    if (!(uncertaintyCol.equals("")) && hasUnc) {
                        unc.put(hlaA2allele, peptides.get(peptide).get(method).get(hlaA2allele + " Unc"));
                        newBean.setUncA2(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaA2allele + " Unc")));
                    }

                }
                if (peptides.get(peptide).get(method).get(hlaB1allele + " Score") != null) {
                    imm.put(hlaB1allele, peptides.get(peptide).get(method).get(hlaB1allele + " Score"));
                    newBean.setHlaB1(
                            Float.parseFloat(peptides.get(peptide).get(method).get(hlaB1allele + " Score")));
                    if (!(distanceCol.equals("")) && hasDist) {
                        dist.put(hlaB1allele, peptides.get(peptide).get(method).get(hlaB1allele + " Dist"));
                        newBean.setDistB1(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaB1allele + " Dist")));
                    }
                    if (!(uncertaintyCol.equals("")) && hasUnc) {
                        unc.put(hlaB1allele, peptides.get(peptide).get(method).get(hlaB1allele + " Unc"));
                        newBean.setUncB1(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaB1allele + " Unc")));
                    }

                }
                if (peptides.get(peptide).get(method).get(hlaB2allele + " Score") != null) {
                    imm.put(hlaB2allele, peptides.get(peptide).get(method).get(hlaB2allele + " Score"));
                    newBean.setHlaB2(
                            Float.parseFloat(peptides.get(peptide).get(method).get(hlaB2allele + " Score")));
                    if (!(distanceCol.equals("")) && hasDist) {
                        dist.put(hlaB2allele, peptides.get(peptide).get(method).get(hlaB2allele + " Dist"));
                        newBean.setDistB2(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaB2allele + " Dist")));
                    }
                    if (!(uncertaintyCol.equals("")) && hasUnc) {
                        unc.put(hlaB2allele, peptides.get(peptide).get(method).get(hlaB2allele + " Unc"));
                        newBean.setUncB2(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaB2allele + " Unc")));
                    }

                }
                if (peptides.get(peptide).get(method).get(hlaC1allele + " Score") != null) {
                    imm.put(hlaC1allele, peptides.get(peptide).get(method).get(hlaC1allele + " Score"));
                    newBean.setHlaC1(
                            Float.parseFloat(peptides.get(peptide).get(method).get(hlaC1allele + " Score")));
                    if (!(distanceCol.equals("")) && hasDist) {
                        dist.put(hlaC1allele, peptides.get(peptide).get(method).get(hlaC1allele + " Dist"));
                        newBean.setDistC1(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaC1allele + " Dist")));
                    }
                    if (!(uncertaintyCol.equals("")) && hasUnc) {
                        unc.put(hlaC1allele, peptides.get(peptide).get(method).get(hlaC1allele + " Unc"));
                        newBean.setUncC1(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaC1allele + " Unc")));
                    }

                }
                if (peptides.get(peptide).get(method).get(hlaC2allele + " Score") != null) {
                    imm.put(hlaC2allele, peptides.get(peptide).get(method).get(hlaC2allele + " Score"));
                    newBean.setHlaC2(
                            Float.parseFloat(peptides.get(peptide).get(method).get(hlaC2allele + " Score")));
                    if (!(distanceCol.equals("")) && hasDist) {
                        dist.put(hlaC2allele, peptides.get(peptide).get(method).get(hlaC2allele + " Dist"));
                        newBean.setDistC2(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaC2allele + " Dist")));
                    }
                    if (!(uncertaintyCol.equals("")) && hasUnc) {
                        unc.put(hlaC2allele, peptides.get(peptide).get(method).get(hlaC2allele + " Unc"));
                        newBean.setUncC2(
                                Float.parseFloat(peptides.get(peptide).get(method).get(hlaC2allele + " Unc")));
                    }

                }


                newBean.setImm(imm);
                newBean.setDist(dist);
                newBean.setUnc(unc);

                alleles = new String[]{hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele,
                        hlaC2allele};
                alleles = newBean.prepareAlleleNames(alleles);

                if (!(methodCol.equals("")) && hasMethod) {
                    newBean.setMethod(method);
                }
                newBean.setLength(peptide.length());
                if (peptide.length() > maxLength) {
                    maxLength = peptide.length();
                }
                newBean.setMutation(peptides.get(peptide).get(method).get("mutation"));
                newBean.setGene(peptides.get(peptide).get(method).get("gene"));
                newBean.setTranscript(peptides.get(peptide).get(method).get("transcript"));
                if (!(transcriptExpressionCol.equals("")) && hasTranscriptExpression) {
                    newBean.setTranscriptExpression(
                            Float.parseFloat(peptides.get(peptide).get(method).get("transcriptExpression")));
                } else {
                    newBean.setTranscriptExpression(1f);
                }
                if (!(typeCol.equals("")) && hasType) {
                    newBean.setType(peptides.get(peptide).get(method).get("type"));
                }

                epitopes.addBean(newBean);

            }
        }
    }

    /**
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

    public String[] getAlleles() {
        return alleles;
    }

    public Boolean getHasTranscriptExpression() {
        return hasTranscriptExpression;
    }
}
