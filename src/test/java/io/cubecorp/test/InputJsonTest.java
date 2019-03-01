package io.cubecorp.test;


import io.cubecorp.pathfrequency.core.Context;
import io.cubecorp.pathfrequency.core.InputJson;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class InputJsonTest {

    @Test
    public void invalidFile() {

        Context context = new Context();

        InputJson.Builder builder = new InputJson.Builder();
        InputJson inputJson = null;

        try {
            inputJson = builder.setContext(context)
                    .setInputFileName("wrongfile.json")
                    .build();
        } catch (Exception e) {
        }

        Assert.assertNull(inputJson);

    }

    @Test
    public void validFile() throws IOException {

        Context context = new Context();

        InputJson.Builder builder = new InputJson.Builder();
        InputJson inputJson = builder.setContext(context)
                .setInputFileName("input.json")
                .build();

        Assert.assertNotNull(inputJson);
    }
}
