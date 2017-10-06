package helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * The class {@link ParserAlleleFile} is responsible for the parsing the allele file
 *
 * @author spaethju
 */
public class ParserAlleleFile {

    private File alleleFile;
    private int a1, a2, b1, b2, c1, c2;

    public ParserAlleleFile() {
        this.alleleFile = alleleFile;
    }

    public HashMap<String, String> parse(File alleleFile) throws IOException {
        HashMap<String, String> alleles = new HashMap<>();
        BufferedReader brReader = new BufferedReader(new FileReader(alleleFile));
        String line = brReader.readLine();
        String[] headers = line.split("\t");
        if (headers.length >= 6) {
            int counter = 0;

            for (String h : headers) {
                if (h.equalsIgnoreCase("a1")) {
                    a1 = counter;
                    counter = counter + 1;
                } else if (h.equalsIgnoreCase("a2")) {
                    a2 = counter;
                    counter = counter + 1;
                } else if (h.equalsIgnoreCase("b1")) {
                    b1 = counter;
                    counter = counter + 1;
                } else if (h.equalsIgnoreCase("b2")) {
                    b2 = counter;
                    counter = counter + 1;
                } else if (h.equalsIgnoreCase("c1")) {
                    c1 = counter;
                    counter = counter + 1;
                } else if (h.equalsIgnoreCase("c2")) {
                    c2 = counter;
                    counter = counter + 1;
                } else {
                    counter = counter + 1;
                }
            }

            line = brReader.readLine();
            String[] columns = line.split("\t");
            alleles.put("A1", columns[a1].trim());
            alleles.put("A2", columns[a2].trim());
            alleles.put("B1", columns[b1].trim());
            alleles.put("B2", columns[b2].trim());
            alleles.put("C1", columns[c1].trim());
            alleles.put("C2", columns[c2].trim());
        } else {
            int a = 0;
            int b = 0;
            int c = 0;
            while ((line = brReader.readLine()) != null) {
                if (line.contains("A") && a == 0) {
                    alleles.put("A1", line.trim());
                    a = 1;
                }
                if (line.contains("A") && a == 1) {
                    alleles.put("A2", line.trim());
                }
                if (line.contains("B") && b == 0) {
                    alleles.put("B1", line.trim());
                    b = 1;
                }
                if (line.contains("B") && b == 1) {
                    alleles.put("B2", line.trim());
                }
                if (line.contains("C") && c == 0) {
                    alleles.put("C1", line.trim());
                    c = 1;
                }
                if (line.contains("C") && c == 1) {
                    alleles.put("C2", line.trim());
                }
            }
        }

        brReader.close();

        return alleles;

    }
}