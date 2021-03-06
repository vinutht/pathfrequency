package io.cubecorp.pathfrequency.core;

import java.util.Locale;
import java.util.ResourceBundle;

public class Context {

    private final ResourceBundle messageBundle;

    private int atleastKTimes;
    private int topK;
    private float occurrenceRatio;
    private String inputFileName;
    private int totalNumberOfDocuments;

    public Context() {
        this.messageBundle = ResourceBundle.getBundle("message", new Locale("en","US"));
    }

    public String getMessageString(String key, String ... args) {
        if(args != null) {
            return String.format(messageBundle.getString(key), args);
        }
        return messageBundle.getString(key);
    }

    public int getAtleastKTimes() {
        return atleastKTimes;
    }

    public void setAtleastKTimes(int atleastKTimes) {
        this.atleastKTimes = atleastKTimes;
    }

    public float getOccurrenceRatio() {
        return occurrenceRatio;
    }

    public void setOccurrenceRatio(float occurrenceRatio) {
        this.occurrenceRatio = occurrenceRatio;
    }

    public int getTotalNumberOfDocuments() {
        return totalNumberOfDocuments;
    }

    public void setTotalNumberOfDocuments(int totalNumberOfDocuments) {
        this.totalNumberOfDocuments = totalNumberOfDocuments;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }
}
