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

}
