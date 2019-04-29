package helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.panel.PanelUpload;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EpitopePredictionFileHelper {

    private static final Logger LOG = LogManager.getLogger(EpitopePredictionFileHelper.class);

    public EpitopePredictionFileHelper() {

    }


    private static void isStringInHeader(String[] header, String column) throws Exception{
        if (!Arrays.asList(header).contains(column.trim()) && !column.trim().equals("")){
            LOG.error("Epitope prediction file has no column named " + column + ".");
            Utils.notification("Column error",
                    "The epitope prediction file has no column named " + column + ".",
                    "error");
            throw new Exception();
        }
    }


    private static void isAlleleInHeader(String[] header, String allele) throws Exception{
        List<String> headerList = new ArrayList<>();
        Arrays.asList(header).forEach((title) -> headerList.add(title.replace("HLA-", "")) );
        String allele_column_name = allele + " score";
        if (!allele.equals("")) {
            if (!headerList.contains(allele_column_name)) {
                LOG.error("Epitope prediction file does not contain allele " + allele + ".");
                Utils.notification("Allele error",
                        "The epitope prediction file does not contain allele " + allele + ".",
                        "error");
                throw new Exception();
            }
        }
    }


    public static void compareFileHeaderWithColumns(File file, PanelUpload uploadPanel) throws Exception {

        String[] header = readHeader(file);

        String methodColumn = uploadPanel.getMethodColTf().getValue();
        isStringInHeader(header, methodColumn);
        String expressionColumn = uploadPanel.getTranscriptExpressionColTf().getValue();
        isStringInHeader(header, expressionColumn);
        String taaColumn = uploadPanel.getTaaColTf().getValue();
        isStringInHeader(header, taaColumn);
        String distanceColumn = uploadPanel.getDistanceColTf().getValue();
        isStringInHeader(header, distanceColumn);
        String uncertaintyColumn = uploadPanel.getUncertaintyColTf().getValue();
        isStringInHeader(header, uncertaintyColumn);
        String immColumn = uploadPanel.getImmColTf().getValue();
        isStringInHeader(header, immColumn);

        if (uploadPanel.getHlaAsColumns()) {
            compareFileHeaderWithAlleles(file, uploadPanel);

        }
    }

    private static void compareFileHeaderWithAlleles(File file, PanelUpload uploadPanel) throws Exception {
        String[] header = readHeader(file);
        for (String allele : uploadPanel.getAlleles().values()) {
            isAlleleInHeader(header, allele.trim());
        }
    }

    private static String[] readHeader(File file){
        BufferedReader brReader = null;
        try {
            brReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            LOG.error("File was not found.");
            Utils.notification("File error", "The file was not found. Please try again", "error");
        }
        String line = null;
        try {
            line = brReader.readLine();
        } catch (IOException e) {
            LOG.error("File was not found.");
            Utils.notification("File error", "The file could not be read. Please try again", "error");
        }

        return line.split("\t");
    }

}
