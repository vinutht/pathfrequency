package io.cubecorp.test;

import com.fasterxml.jackson.databind.JsonNode;
import io.cubecorp.pathfrequency.core.Context;
import io.cubecorp.pathfrequency.core.InputJson;
import io.cubecorp.pathfrequency.core.PathFrequency;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;


public class PathFrequencyTest {

    @Test
    public void addDocumentTest() {

        Context context =  new Context();

        PathFrequency.forceNullify();
        PathFrequency pathFrequency = PathFrequency.getInstance(context);
        InputJson inputJson = null;

        InputJson.Builder builder = new InputJson.Builder();
        try {
            inputJson = builder.setContext(context)
                    .setInputFileName("test.json")
                    .build();

            Iterator<JsonNode> jsonDocsIter = inputJson.iterator();
            int docCount = 0;
            while(jsonDocsIter.hasNext()) {
                pathFrequency.addDocument(jsonDocsIter.next());
                docCount++;
            }

            Assert.assertEquals(docCount, pathFrequency.getNumOfDocuments());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void pathFrequencyComputationTest1() {

        Context context =  new Context();
        context.setTopK(1);
        context.setOccurrenceRatio(0.3f);

        PathFrequency.forceNullify();
        PathFrequency pathFrequency = PathFrequency.getInstance(context);
        InputJson inputJson = null;

        InputJson.Builder builder = new InputJson.Builder();
        try {
            inputJson = builder.setContext(context)
                    .setInputFileName("test.json")
                    .build();

            Iterator<JsonNode> jsonDocsIter = inputJson.iterator();
            int docCount = 0;
            while(jsonDocsIter.hasNext()) {
                pathFrequency.addDocument(jsonDocsIter.next());

            }


            String str = "[\n" +
                    "\n" +
                    "/name, 1, [{\"Rony\", 1/6}, {\"Joe\", 3/6}, {\"Evan\", 2/6}, ]\n" +
                    "\n" +
                    "/address/city, 3/6, [{\"dublin\", 1/3}, {\"new york\", 1/3}, {\"sfo\", 1/3}, ]\n" +
                    "\n" +
                    "/address/street, 3/6, [{\"new hampshire ave\", 1/3}, {\"Santa Theresa st\", 1/3}, {\"montgomery st\", 1/3}, ]\n" +
                    "\n" +
                    "/address/state, 3/6, [{\"ca\", 2/3}, {\"ny\", 1/3}, ]\n" +
                    "\n" +
                    "/address/number, 3/6, [{101, 1/3}, {201, 1/3}, {301, 1/3}, ]\n" +
                    "\n" +
                    "/qualifications/0, 3/6, [{\"BE\", 1/3}, {\"BS\", 2/3}, ]\n" +
                    "\n" +
                    "/address, 3/6, []\n" +
                    "\n" +
                    "/qualifications/1, 3/6, [{\"MS\", 1}, ]\n" +
                    "\n" +
                    "/qualifications, 3/6, []\n" +
                    "\n" +
                    "]";

            Assert.assertEquals(str, pathFrequency.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    public void pathFrequencyComputationTest2() {

        Context context =  new Context();
        context.setTopK(2);
        context.setOccurrenceRatio(0.6f);

        PathFrequency.forceNullify();
        PathFrequency pathFrequency = PathFrequency.getInstance(context);
        InputJson inputJson = null;

        InputJson.Builder builder = new InputJson.Builder();
        try {
            inputJson = builder.setContext(context)
                    .setInputFileName("test.json")
                    .build();

            Iterator<JsonNode> jsonDocsIter = inputJson.iterator();
            int docCount = 0;
            while(jsonDocsIter.hasNext()) {
                pathFrequency.addDocument(jsonDocsIter.next());

            }

            String str = "[\n" +
                    "\n" +
                    "/name, 1, [{\"Joe\", 3/6}, {\"Evan\", 2/6}, ]\n" +
                    "\n" +
                    "]";

            Assert.assertEquals(str, pathFrequency.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    public void pathFrequencyComputationTest3() {

        Context context =  new Context();
        context.setTopK(3);
        context.setOccurrenceRatio(0.2f);

        PathFrequency.forceNullify();
        PathFrequency pathFrequency = PathFrequency.getInstance(context);
        InputJson inputJson = null;

        InputJson.Builder builder = new InputJson.Builder();
        try {
            inputJson = builder.setContext(context)
                    .setInputFileName("test.json")
                    .build();

            Iterator<JsonNode> jsonDocsIter = inputJson.iterator();
            int docCount = 0;
            while(jsonDocsIter.hasNext()) {
                pathFrequency.addDocument(jsonDocsIter.next());

            }

            String str = "[\n" +
                    "\n" +
                    "/name, 1, [{\"Joe\", 3/6}, ]\n" +
                    "\n" +
                    "/address/city, 3/6, []\n" +
                    "\n" +
                    "/address/street, 3/6, []\n" +
                    "\n" +
                    "/address/state, 3/6, []\n" +
                    "\n" +
                    "/address/number, 3/6, []\n" +
                    "\n" +
                    "/qualifications/0, 3/6, []\n" +
                    "\n" +
                    "/address, 3/6, []\n" +
                    "\n" +
                    "/qualifications/1, 3/6, [{\"MS\", 1}, ]\n" +
                    "\n" +
                    "/qualifications, 3/6, []\n" +
                    "\n" +
                    "]";

            Assert.assertEquals(str, pathFrequency.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
