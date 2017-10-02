package helper;

import com.vaadin.data.util.BeanItemContainer;
import model.EpitopeResultBean;
import model.ResultBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * The class {@link ParserScriptResult} is responsible for parsing the output of the epitope selection script.
 *
 * @author spaethju
 */
public class ParserScriptResult {

    private BeanItemContainer<ResultBean> resultBeans;
    private String line;
    private String[] alleles;
    private BufferedReader br;

    /**
     * Constructor
     */
    public ParserScriptResult() {

    }

    /**
     * Reads in a file and saves all information in a result bean
     *
     * @param file output file of the epitope selection script
     */
    public void parse(File file) {
        resultBeans = new BeanItemContainer<>(ResultBean.class);

        try {

            br = new BufferedReader(new FileReader(file));
            while (null != (line = br.readLine())) {
                if (line.contains("CS-")) {
                    ResultBean resultBean = new ResultBean();
                    BeanItemContainer<EpitopeResultBean> epitopeResultBeans =
                            new BeanItemContainer<EpitopeResultBean>(EpitopeResultBean.class);
                    for (int i = 0; i <= 1; i++) {
                        line = br.readLine();
                    }
                    readCS(resultBean);
                    for (int i = 0; i <= 2; i++) {
                        line = br.readLine();
                    }
                    readES(resultBean, epitopeResultBeans);
                    for (int i = 0; i <= 2; i++) {
                        line = br.readLine();
                    }
                    readPS(resultBean);
                    for (int i = 0; i <= 2; i++) {
                        line = br.readLine();
                    }
                    readAs(resultBean);
                    resultBean.setEpitopeResultBeans(epitopeResultBeans);
                    resultBeans.addBean(resultBean);
                    line = br.readLine();
                }
                line = br.readLine();
            }

        } catch (IOException e) {
            Utils.notification("ERROR", "No results found, please try again.", "error");
        }
    }


    /**
     * Reads the #ps part of the output file and saves it as a map in the bean.
     *
     * @param resultBean result bean to save the information in
     * @throws IOException
     */
    public void readPS(ResultBean resultBean) throws IOException {
        HashMap<String, String> ps = new HashMap<>();
        while (!line.equals("")) {
            String[] gene = line.split("\t");
            ps.put(gene[0], gene[1]);
            line = br.readLine();
        }
        resultBean.setPs(ps);
    }

    /**
     * Reads the #as part of the file and saves it as a map in the bean.
     *
     * @param resultBean result bean to save the information in
     * @throws IOException
     */
    public void readAs(ResultBean resultBean) throws IOException {
        HashMap<String, String> as = new HashMap<>();
        while (!line.equals("")) {
            String[] hla = line.split("\t");
            as.put(hla[0], hla[1]);
            line = br.readLine();
        }
        resultBean.setAs(as);
    }

    /**
     * Reads the #cs part of the file and saves it in the bean. Splits the line tab seperated and
     * saves the column information in a bean.
     *
     * @param resultBean result bean to save the information in
     * @throws IOException
     */
    public void readCS(ResultBean resultBean) throws IOException {
        String[] cs = line.split("\t");
        resultBean.setNof_epitopes(Integer.parseInt(cs[0]));
        resultBean.setNof_taa_epitopes(Integer.parseInt(cs[1]));
        resultBean.setThreshold_epitope(Float.parseFloat(cs[2]));
        resultBean.setThreshold_distance(Float.parseFloat(cs[3]));
        resultBean.setAntigen_const(Integer.parseInt(cs[4]));
        resultBean.setHla_const(Integer.parseInt(cs[5]));
        resultBean.setOverlap_const(Integer.parseInt(cs[6]));
        resultBean.setDistance2self(Integer.parseInt(cs[7]));
        resultBean.setUncertainty(Integer.parseInt(cs[8]));
        resultBean.setImmunogenicity(Float.parseFloat(cs[9]));
        float risk;
        if (cs[10].equals("None")) {
            risk = 0;
        } else {
            risk = Float.parseFloat(cs[10]);
        }
        resultBean.setRisk(risk);
        resultBean.setCovered_hlas(Float.parseFloat(cs[11]));
        resultBean.setCovered_antigens(Float.parseFloat(cs[12]));
    }

    /**
     * Reads the #es part of the file. Each epitope is saved in a bean and are finally stored in a
     * bean item container. This bean item container is saved in the result bean.
     *
     * @param resultBean         result bean to save the information in
     * @param epitopeResultBeans bean item container to save the epitopes and its information.
     * @throws IOException
     */
    public void readES(ResultBean resultBean, BeanItemContainer<EpitopeResultBean> epitopeResultBeans)
            throws IOException {

        String[] columns = line.split("\t");

        // if no distance is in the result
        if (!columns[5].equals("distance")) {
            String[] a = {columns[5].replaceAll("_imm", ""), columns[6].replaceAll("_imm", ""),
                    columns[7].replaceAll("_imm", ""), columns[8].replaceAll("_imm", ""),
                    columns[9].replaceAll("_imm", ""), columns[10].replaceAll("_imm", "")};
            setAlleles(a);
            // if no distance is in the result
        } else {
            String[] b = {columns[6].replaceAll("_imm", ""), columns[7].replaceAll("_imm", ""),
                    columns[8].replaceAll("_imm", ""), columns[9].replaceAll("_imm", ""),
                    columns[10].replaceAll("_imm", ""), columns[11].replaceAll("_imm", "")};
            setAlleles(b);
        }
        line = br.readLine();
        for (int i = 0; i < resultBean.getNof_epitopes(); i++) {
            String[] es = line.split("\t");
            EpitopeResultBean newBean = new EpitopeResultBean();
            newBean.setNeoepitope(es[0]);
            if (es[1].equals("True")) {
                newBean.setType("TAA");
            } else {
                newBean.setType(es[1]);
            }
            newBean.setGenes(es[2]);
            newBean.setMutations(es[3]);
            if (!es[5].equals("nan")) {
                newBean.setImmA1(Float.parseFloat(es[5]));
            }
            if (!es[6].equals("nan")) {
                newBean.setImmB1(Float.parseFloat(es[6]));
            }
            if (!es[7].equals("nan")) {
                newBean.setImmC1(Float.parseFloat(es[7]));
            }
            if (!es[8].equals("nan")) {
                newBean.setImmA2(Float.parseFloat(es[8]));
            }
            if (!es[9].equals("nan")) {
                newBean.setImmB2(Float.parseFloat(es[9]));
            }
            if (!es[10].equals("nan")) {
                newBean.setImmC2(Float.parseFloat(es[10]));
            }
//            newBean.setDistA1(Float.parseFloat(es[11]));
//            newBean.setDistA2(Float.parseFloat(es[12]));
//            newBean.setDistB1(Float.parseFloat(es[13]));
//            newBean.setDistB2(Float.parseFloat(es[14]));
//            newBean.setDistC1(Float.parseFloat(es[15]));
//            newBean.setDistC2(Float.parseFloat(es[16]));

            epitopeResultBeans.addBean(newBean);
            line = br.readLine();
        }
    }


    /**
     * @return bean item container containing all results
     */
    public BeanItemContainer<ResultBean> getResultBeans() {
        return resultBeans;
    }


    /**
     * @return alleles shown in the file
     */
    public String[] getAlleles() {
        return alleles;
    }

    /**
     * @param alleles alleles found in the file
     */
    public void setAlleles(String[] alleles) {
        this.alleles = alleles;
    }

}
