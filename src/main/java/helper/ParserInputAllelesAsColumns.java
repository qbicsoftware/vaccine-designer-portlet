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
 * The class {@link ParserInputAllelesAsColumns} is responsible for the parsing of the uploaded epitope
 * prediction input data.
 *
 * @author spaethju
 */
public class ParserInputAllelesAsColumns {

    private BeanItemContainer<EpitopeSelectionBean> epitopes;
    private String line;
    private int method, mutation, gene, transcript, transcriptExpression, neopeptide, hlaA1, hlaA2, hlaB1,
            hlaB2, hlaC1, hlaC2, type, maxLength;
    private String methodCol, typeCol, transcriptExpressionCol, hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele, hlaC2allele;
    private HashMap<String, HashMap<String, HashMap<String, String>>> peptides;
    private String[] alleleNames;
    private BufferedReader brReader;
    private File file;
    private Boolean hasType, hasMethod, hasTranscriptExpression;
    private String[] alleles;


    public ParserInputAllelesAsColumns() {
        hasTranscriptExpression = false;
    }


    /**
     * Reads in a epitope file, writes the input file for the epitope selection script and saves it as
     * a bean in a bean item container.
     *
     * @param file
     * @param typeCol name of the type column
     * @throws IOException
     */
    public void parse(File file, String methodCol, String typeCol, String transcriptExpressionCol) throws IOException {

        this.typeCol = typeCol;
        this.methodCol = methodCol;
        this.transcriptExpressionCol = transcriptExpressionCol;
        this.file = file;

        // initialize bean item container for epitope selection beans
        epitopes = new BeanItemContainer<EpitopeSelectionBean>(EpitopeSelectionBean.class);

        // initialize buffered reader reading the file line by line
        correctInput();
        brReader = new BufferedReader(new FileReader(file));
        line = brReader.readLine();

        try {
            setHeaders();
            readInput();
            setBean();
        } catch (NullPointerException e) {
            MyPortletUI.logger.error("Error while parsing the data: Maybe some columns/entries in the tsv file are missing or are named falsly.");
            e.printStackTrace();
        }

    }

    public void correctInput() throws IOException {
        // splits the line tab seperarated
        brReader = new BufferedReader(new FileReader(file));
        line = brReader.readLine();
        String[] headers = line.split("\t");
        hasType = false;
        hasMethod = false;
        hasTranscriptExpression = false;
        for (String h : headers) {
            if (!(typeCol.equals("")) && h.equals(typeCol)) {
                hasType = true;
            }
            if (!(methodCol.equals("")) && h.equals(methodCol)) {
                hasMethod = true;
            }
            if (!(transcriptExpressionCol.equals("") && h.equals(transcriptExpressionCol))) {
                hasTranscriptExpression = true;
            }
        }

    }

    /**
     * Searches the header of the uploaded file for needed columns and saves the column position in a
     * corresponding variable. Distancy, uncertainty and type column will just be included if a column
     * name was given for them.
     */
    public void setHeaders() {

        // splits the line tab seperarated
        String[] headers = line.split("\t");
        int counter = 0;
        int a = 0;
        int b = 0;
        int c = 0;

        // for each tab separated header set the corresponding field to the counters value and set
        // counter + 1
        for (String h : headers) {
            h.trim();
            h.replace("HLA-", "");
            if (h.equalsIgnoreCase("pos")) {
                mutation = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("gene")) {
                gene = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("transcript") || h.equalsIgnoreCase("transcripts")) {
                transcript = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("sequence")) {
                neopeptide = counter;
                counter = counter + 1;
            } else if (h.equalsIgnoreCase("length")) {
                counter = counter + 1;
            } else if (h.startsWith("A*") && h.endsWith("score") && (a == 0)) {
                hlaA1 = counter;
                hlaA1allele = h.split(" ")[0];
                counter = counter + 1;
                a++;
            } else if (h.startsWith("A*") && h.endsWith("score") && (a == 1)) {
                hlaA2 = counter;
                hlaA2allele = h.split(" ")[0];
                counter = counter + 1;
            } else if (h.startsWith("B*") && h.endsWith("score") && (b == 0)) {
                hlaB1 = counter;
                hlaB1allele = h.split(" ")[0];
                counter = counter + 1;
                b++;
            } else if (h.startsWith("B*") && h.endsWith("score") && (b == 1)) {
                hlaB2 = counter;
                hlaB2allele = h.split(" ")[0];
                counter = counter + 1;
            } else if (h.startsWith("C*") && h.endsWith("score") && (c == 0)) {
                hlaC1 = counter;
                hlaC1allele = h.split(" ")[0];
                counter = counter + 1;
                c++;
            } else if (h.startsWith("C*") && h.endsWith("score") && (c == 1)) {
                hlaC2 = counter;
                hlaC2allele = h.split(" ")[0];
                counter = counter + 1;
                // just if a column name was given:
            } else if (!(typeCol.equals("")) && h.equals(typeCol) && hasType) {
                type = counter;
                counter = counter + 1;
            } else if (!(methodCol.equals("")) && h.equals(methodCol) && hasMethod) {
                method = counter;
                counter = counter + 1;
                // if another header is found, ignore it and set counter + 1
            } else if (!(transcriptExpressionCol.equals("")) && h.equals(transcriptExpressionCol) && hasTranscriptExpression) {
                transcriptExpression = counter;
                counter = counter + 1;
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
        peptides = new HashMap<>();

        // read all lines of the file
        while ((line = brReader.readLine()) != null) {
            // get all columns
            String[] columns = line.split("\t");

            // if neopeptide not yet readed
            if (!(peptides.containsKey(columns[neopeptide]))) {
                // initialize others map
                addCompletePeptide(columns);
            } else {
                peptides.get(columns[neopeptide]).put(columns[method], addValues(columns));
            }

        }
        brReader.close();
    }

    /**
     * Sets the bean with its parameters and adds it to a bean item container.
     */
    public void setBean() {
        for (String peptide : peptides.keySet()) {
            for (String method : peptides.get(peptide).keySet()) {
                EpitopeSelectionBean newBean = new EpitopeSelectionBean();
                // set parameters with key and values from the map
                newBean.setIncluded(false);
                newBean.setExcluded(false);
                newBean.setNeopeptide(peptide);
                try {
                    HashMap<String, String> imm = new HashMap<>();
                    imm.put(hlaA1allele, peptides.get(peptide).get(method).get(hlaA1allele));
                    imm.put(hlaA2allele, peptides.get(peptide).get(method).get(hlaA2allele));
                    imm.put(hlaB1allele, peptides.get(peptide).get(method).get(hlaB1allele));
                    imm.put(hlaB2allele, peptides.get(peptide).get(method).get(hlaB2allele));
                    imm.put(hlaC1allele, peptides.get(peptide).get(method).get(hlaC1allele));
                    imm.put(hlaC2allele, peptides.get(peptide).get(method).get(hlaC2allele));
                    newBean.setHlaA1(Float.parseFloat(peptides.get(peptide).get(method).get(hlaA1allele)));
                    newBean.setHlaA2(Float.parseFloat(peptides.get(peptide).get(method).get(hlaA2allele)));
                    newBean.setHlaB1(Float.parseFloat(peptides.get(peptide).get(method).get(hlaB1allele)));
                    newBean.setHlaB2(Float.parseFloat(peptides.get(peptide).get(method).get(hlaB2allele)));
                    newBean.setHlaC1(Float.parseFloat(peptides.get(peptide).get(method).get(hlaC1allele)));
                    newBean.setHlaC2(Float.parseFloat(peptides.get(peptide).get(method).get(hlaC2allele)));
                    newBean.setImm(imm);
                } catch (NumberFormatException e) {

                }
                alleles = new String[]{hlaA1allele, hlaA2allele, hlaB1allele, hlaB2allele, hlaC1allele, hlaC2allele};
                alleleNames = newBean.prepareAlleleNames(alleles);
                //newBean.prepareImm(alleleNames);
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

    public Boolean getHasMethod() {
        return hasMethod;
    }

    public void setHasMethod(Boolean hasMethod) {
        this.hasMethod = hasMethod;
    }

    public HashMap<String, String> addValues(String[] columns) {
        HashMap<String, String> values = new HashMap<>();
        values.put("mutation", columns[mutation]);
        values.put("gene", columns[gene]);
        values.put("transcript", columns[transcript]);
        values.put(hlaA1allele, columns[hlaA1]);
        values.put(hlaA2allele, columns[hlaA2]);
        values.put(hlaB1allele, columns[hlaB1]);
        values.put(hlaB2allele, columns[hlaB2]);
        values.put(hlaC1allele, columns[hlaC1]);
        values.put(hlaC2allele, columns[hlaC2]);
        // if type column exists also read type
        if (!(typeCol.equals("")) && hasType) {
            values.put("type", columns[type]);
        }
        if (!(transcriptExpressionCol.equals("") && hasTranscriptExpression)) {
            values.put("transcriptExpression", columns[transcriptExpression]);
        }

        return values;
    }

    public HashMap<String, HashMap<String, String>> addMethodWithValues(String[] columns) {
        HashMap<String, HashMap<String, String>> methodWithValues = new HashMap<>();
        if (!(methodCol.equals("")) && hasMethod) {
            methodWithValues.put(columns[method], addValues(columns));
        } else {
            methodWithValues.put("method", addValues(columns));
        }

        return methodWithValues;
    }

    public void addCompletePeptide(String[] columns) {
        peptides.put(columns[neopeptide], addMethodWithValues(columns));
    }

    public String[] getAlleles() {
        return alleles;
    }

    public Boolean getHasTranscriptExpression() {
        return hasTranscriptExpression;
    }
}

