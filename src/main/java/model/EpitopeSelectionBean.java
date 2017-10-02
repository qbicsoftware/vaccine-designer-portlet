package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The class {@link EpitopeSelectionBean} represents the information of a neopeptide of the input
 * data.
 *
 * @author spaethju
 */

public class EpitopeSelectionBean {

    private String neopeptide, transcript, mutation, gene, type, method;
    private HashMap<String, String> imm;
    private Float hlaA1, hlaA2, hlaB1, hlaB2, hlaC1, hlaC2, transcriptExpression;
    private int length;
    private Boolean included, excluded;

    public EpitopeSelectionBean() {

    }


    public String getNeopeptide() {
        return neopeptide;
    }

    public void setNeopeptide(String neopeptide) {
        this.neopeptide = neopeptide;
    }

    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean isExcluded) {
        this.excluded = isExcluded;
    }

    public Boolean getIncluded() {
        return included;
    }

    public void setIncluded(Boolean isIncluded) {
        this.included = isIncluded;
    }

    public int getLength() {
        return length;
    }


    public void setLength(int length) {
        this.length = length;
    }


    public HashMap<String, String> getImm() {
        return imm;
    }

    public Float getTranscriptExpression() {
        return transcriptExpression;
    }


    public void setTranscriptExpression(Float transcriptExpression) {
        this.transcriptExpression = transcriptExpression;
    }


    public String getTranscript() {
        return transcript;
    }


    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }


    public String getGene() {
        return gene;
    }


    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getMutation() {
        return mutation;
    }


    public void setMutation(String mutation) {
        this.mutation = mutation;
    }


    public Float getHlaA1() {
        return hlaA1;
    }


    public void setHlaA1(Float hlaA1) {
        this.hlaA1 = hlaA1;
    }

    public Float getHlaA2() {
        return hlaA2;
    }


    public void setHlaA2(Float hlaA2) {
        this.hlaA2 = hlaA2;
    }


    public Float getHlaB1() {
        return hlaB1;
    }


    public void setHlaB1(Float hlaB1) {
        this.hlaB1 = hlaB1;
    }


    public Float getHlaB2() {
        return hlaB2;
    }


    public void setHlaB2(Float hlaB2) {
        this.hlaB2 = hlaB2;
    }


    public Float getHlaC1() {
        return hlaC1;
    }


    public void setHlaC1(Float hlaC1) {
        this.hlaC1 = hlaC1;
    }


    public Float getHlaC2() {
        return hlaC2;
    }

    public void setHlaC2(Float hlaC2) {
        this.hlaC2 = hlaC2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] prepareAlleleNames(String[] alleles) {
        String[] alleleNames = new String[6];

        alleleNames[0] = alleles[0];
        alleleNames[1] = alleles[1];
        alleleNames[2] = alleles[2];
        alleleNames[3] = alleles[3];
        alleleNames[4] = alleles[4];
        alleleNames[5] = alleles[5];
        return alleleNames;
    }

    public void setImm(HashMap<String, String> alleleImmunigenicities) {
        this.imm = alleleImmunigenicities;
    }



}

