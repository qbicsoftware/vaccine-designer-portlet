package helper;

import com.vaadin.data.util.BeanItemContainer;
import life.qbic.MyPortletUI;
import model.EpitopeSelectionBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * The class {@link ParserInputAllelesAsRows} is responsible for the parsing of the uploaded epitope
 * prediction input data.
 *
 * @author spaethju
 */
public class ParserInputAllelesAsRows {

    private BeanItemContainer<EpitopeSelectionBean> epitopes;
    private String line, hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele, hlaC2allele;
    ;
    private int method, mutation, gene, transcript, transcriptExpression, neopeptide, hla,
            hla1BindingPrediction, uncertainty, distance, type, maxLength;
    private String methodCol, immCol, uncertaintyCol, distanceCol, typeCol;
    private HashMap<String, HashMap<String, HashMap<String, String>>> peptides;
    private BufferedReader brReader;
    private File file;
    private Boolean hasType, hasDist, hasImm, hasUnc, hasMethod, hasTranscriptExpression;
    private String[] alleles;
    private HashMap<String, String> alleleNames;

    public ParserInputAllelesAsRows() {
        hasTranscriptExpression = false;
    }


    /**
     * Reads in a epitope file, writes the input file for the epitope selection script and saves it as
     * a bean in a bean item container.
     *
     * @param file
     * @param immCol         name of the immunogenicity column
     * @param typeCol        name of the type column
     * @throws IOException
     */
    public void parse(File file, String methodCol, String immCol, String typeCol, HashMap<String, String> allelesNames) throws Exception {

        this.immCol = immCol;

        this.typeCol = typeCol;
        this.methodCol = methodCol;
        this.file = file;
        this.alleleNames = allelesNames;
        hlaA1allele = allelesNames.get("A1");
        hlaA2allele = allelesNames.get("A2");
        hlaB1allele = allelesNames.get("B1");
        hlaB2allele = allelesNames.get("B2");
        hlaC1allele = allelesNames.get("C1");
        hlaC2allele = allelesNames.get("C2");
        alleles = new String[]{hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele, hlaC2allele};

        // initialize bean item container for epitope selection beans
        epitopes = new BeanItemContainer<EpitopeSelectionBean>(EpitopeSelectionBean.class);

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

        // for each tab separated header set the corresponding field to the counters value and set
        // counter + 1

        for (String h : headers) {
            if (h.equalsIgnoreCase("mutation") || h.equalsIgnoreCase("POS")) {
                mutation = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("gene")) {
                gene = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("transcript") || h.equals("transcripts")) {
                transcript = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("transcript_expression")) {
                transcriptExpression = counter;
                counter = counter + 1;
                hasTranscriptExpression = true;
            } else if (h.equalsIgnoreCase("neopeptide") || h.equalsIgnoreCase("mut_pep") || h.equalsIgnoreCase("peptide")) {
                neopeptide = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("length_of_neopeptide") || h.equalsIgnoreCase("length")) {
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("HLA") || h.equals("ALLELE")) {
                hla = counter;
                counter = counter + 1;
            } else if (h.equals(immCol)) {
                hla1BindingPrediction = counter;
                counter = counter + 1;
                // just if a column name was given:
            } else if (!typeCol.equals("") && h.equals(typeCol)) {
                type = counter;
                counter = counter + 1;
            } else if (!methodCol.equals("") && h.equals(methodCol)) {
                method = counter;
                counter = counter + 1;
                hasMethod = true;
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

        peptides = new HashMap<>();
        int counter = 0;
        // read all lines of the file
        while ((line = brReader.readLine()) != null) {
            // get all columns
            String[] columns = line.split("\t");

            if (!(peptides.containsKey(columns[neopeptide]))) {
                peptides.put(columns[neopeptide], new HashMap<>());
                HashMap<String, String> valuesMap = new HashMap<>();
                valuesMap.put("mutation", columns[mutation]);
                valuesMap.put("gene", columns[gene]);
                valuesMap.put("transcript", columns[transcript]);
                valuesMap.put(columns[hla], columns[hla1BindingPrediction]);
                if (!(typeCol.equals("")) && hasType) {
                    valuesMap.put("type", columns[type]);
                }
                if (hasTranscriptExpression) {
                    valuesMap.put("transcriptExpression", columns[transcriptExpression]);
                }
                peptides.get(columns[neopeptide]).put(columns[method], valuesMap);
            }
            if (peptides.containsKey(columns[neopeptide]) && !peptides.get(columns[neopeptide]).containsKey(columns[method])) {
                HashMap<String, String> valuesAddMap = new HashMap<>();
                valuesAddMap.put("mutation", columns[mutation]);
                valuesAddMap.put("gene", columns[gene]);
                valuesAddMap.put("transcript", columns[transcript]);
                valuesAddMap.put(columns[hla], columns[hla1BindingPrediction]);
                if (!(typeCol.equals("")) && hasType) {
                    valuesAddMap.put("type", columns[type]);
                }
                if (hasTranscriptExpression) {
                    valuesAddMap.put("transcriptExpression", columns[transcriptExpression]);
                }
                peptides.get(columns[neopeptide]).put(columns[method], valuesAddMap);
            }
            if (peptides.containsKey(columns[neopeptide]) && peptides.get(columns[neopeptide]).containsKey(columns[method])) {
                HashMap<String, String> addAlleleMap = peptides.get(columns[neopeptide]).get(columns[method]);
                addAlleleMap.put(columns[hla].replace("HLA-", ""), columns[hla1BindingPrediction]);
                peptides.get(columns[neopeptide]).replace(columns[method], addAlleleMap);
            }

        }

        brReader.close();

        for (String peptide : peptides.keySet()){
            System.out.println(peptides.get(peptide));
        }
    }

    /**
     * Sets the bean with its parameters and adds it to a bean item container.
     */
    public void setBean() {
        MyPortletUI.logger.info(peptides.size() + " to parse...");
        for (String peptide : peptides.keySet()) {
            for (String method : peptides.get(peptide).keySet()) {
                    EpitopeSelectionBean newBean = new EpitopeSelectionBean();
                    // set parameters with key and values from the map
                    newBean.setIncluded(false);
                    newBean.setExcluded(false);
                    newBean.setNeopeptide(peptide);
                    HashMap<String, String> imm = new HashMap<>();
                    if (peptides.get(peptide).get(method).get(hlaA1allele) != null) {
                        imm.put(hlaA1allele, peptides.get(peptide).get(method).get(hlaA1allele));
                        newBean.setHlaA1(Float.parseFloat(peptides.get(peptide).get(method).get(hlaA1allele)));
                    }
                    if (peptides.get(peptide).get(method).get(hlaA2allele) != null) {
                        imm.put(hlaA2allele, peptides.get(peptide).get(method).get(hlaA2allele));
                        newBean.setHlaA2(Float.parseFloat(peptides.get(peptide).get(method).get(hlaA2allele)));
                    }
                    if (peptides.get(peptide).get(method).get(hlaB1allele) != null) {
                        imm.put(hlaB1allele, peptides.get(peptide).get(method).get(hlaB1allele));
                        newBean.setHlaB1(Float.parseFloat(peptides.get(peptide).get(method).get(hlaB1allele)));
                    }
                    if (peptides.get(peptide).get(method).get(hlaB2allele) != null) {
                        imm.put(hlaB2allele, peptides.get(peptide).get(method).get(hlaB2allele));
                        newBean.setHlaB2(Float.parseFloat(peptides.get(peptide).get(method).get(hlaB2allele)));
                    }
                    if (peptides.get(peptide).get(method).get(hlaC1allele) != null) {
                        imm.put(hlaC1allele, peptides.get(peptide).get(method).get(hlaC1allele));
                        newBean.setHlaC1(Float.parseFloat(peptides.get(peptide).get(method).get(hlaC1allele)));
                    }
                    if (peptides.get(peptide).get(method).get(hlaC2allele) != null) {
                        imm.put(hlaC2allele, peptides.get(peptide).get(method).get(hlaC2allele));
                        newBean.setHlaC2(Float.parseFloat(peptides.get(peptide).get(method).get(hlaC2allele)));
                    }
                    newBean.setImm(imm);
                alleles = new String[]{hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele, hlaC2allele};
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
                    if (hasTranscriptExpression) {
                        newBean.setTranscriptExpression(Float.parseFloat(peptides.get(peptide).get(method).get("transcriptExpression")));
                    } else {
                        newBean.setTranscriptExpression(1f);
                    }
                    if (!(typeCol.equals("")) && hasType) {
                        newBean.setType(peptides.get(peptide).get(method).get("type"));
                    }

                    epitopes.addBean(newBean);

                }
            }
        System.out.println("Number of epitopes: " + epitopes.size());
    }
//
//    public Boolean checkAlleles(HashMap<String, String> alleles) {
//        Boolean allelesCorrect = true;
//        for (String allele : alleleNames) {
//            System.out.println(allele);
//            if (!alleles.containsValue(allele))  {
//                allelesCorrect = false;
//            }
//        }
//        return allelesCorrect;
//    }

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

    public HashMap<String, String> getAlleleNames() {
        return alleleNames;
    }

    public String[] getAlleles() {
        return alleles;
    }


}
