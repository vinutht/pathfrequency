package io.cubecorp.pathfrequency;

import com.fasterxml.jackson.databind.JsonNode;
import io.cubecorp.pathfrequency.core.Context;
import io.cubecorp.pathfrequency.core.InputJson;
import io.cubecorp.pathfrequency.core.PathFrequency;

import java.io.IOException;
import java.util.Iterator;


public class Main {

    private static int processTopKArgs(Context context, String args[]) throws Exception {

        int topK = 1;
        if(args.length >= 1) {
            try {
                topK = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                throw new Exception(context.getMessageString("topk.error"));
            }

        }

        if(topK <= 0) {
            throw new Exception(context.getMessageString("topk.error"));
        }

        return topK;
    }

    private static float processPathOccurrenceRatioArgs(Context context, String args[]) throws Exception {

        float pathOccurenceRatio = 0.3f;

        if(args.length == 2) {
            pathOccurenceRatio = Float.parseFloat(args[1]);
        }

        if(pathOccurenceRatio > 1 || pathOccurenceRatio <= 0) {
            throw new Exception(context.getMessageString("path.occurrence.ratio.error"));
        }

        return pathOccurenceRatio;
    }

    public static void main(String args[]) throws Exception {

        Context context = new Context();
        InputJson.Builder inputJsonBuilder = new InputJson.Builder();

        int topK = processTopKArgs(context, args);
        float pathOccurenceRatio = processPathOccurrenceRatioArgs(context, args);

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

            System.out.println(pathFrequency.toString(topK));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
