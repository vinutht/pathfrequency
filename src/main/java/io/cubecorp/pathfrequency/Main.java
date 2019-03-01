package io.cubecorp.pathfrequency;

import com.fasterxml.jackson.databind.JsonNode;
import io.cubecorp.pathfrequency.core.Context;
import io.cubecorp.pathfrequency.core.InputJson;
import io.cubecorp.pathfrequency.core.PathFrequency;
import io.cubecorp.pathfrequency.utils.Options;

import java.io.IOException;
import java.util.Iterator;


public class Main {

    public static void main(String args[]) throws Exception {

        Context context = new Context();
        
        if(!Options.instance(context).parseArgs(args)) {
            return;
        }

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

            System.out.println(pathFrequency.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
