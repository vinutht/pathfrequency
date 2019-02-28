package io.cubecorp.pathfrequency;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;


public class Main {

    public static void main(String args[]) {

        Context context = new Context();
        InputJson.Builder inputJsonBuilder = new InputJson.Builder();

        try {
            InputJson inputJson = inputJsonBuilder
                    .setInputFileName("input.json")
                    .setContext(context)
                    .build();

            PathFrequency pathFrequency = PathFrequency.getInstance(context);

            Iterator<JsonNode> inputJsonIter = inputJson.iterator();

            while(inputJsonIter.hasNext()) {
                JsonNode eachDocument = inputJsonIter.next();
                pathFrequency.addDocument(eachDocument);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
