package io.cubecorp.pathfrequency;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InputJson {

    private final String fileName;
    private final Context context;

    private JsonNode rootNode;

    private InputJson(Context context, String fileName) throws IOException {
        this.fileName = fileName;
        this.context = context;

        parse();
    }

    public Iterator<JsonNode> iterator() throws Exception {
        if(rootNode.isArray()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode returnNode = objectMapper.readTree(rootNode.toString());
            return returnNode.iterator();
        }
        throw new Exception(context.getMessageString("input.json.array.expected"));
    }

    private void parse() throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        rootNode = jsonMapper.readTree(file);

    }



    public static class Builder {

        private String fileName;
        private Context context;

        public Builder setInputFileName(String fn) {
            Objects.requireNonNull(fn, "Filename is mandatory");

            this.fileName = fn;
            return this;
        }

        public Builder setContext(Context context) {
            Objects.requireNonNull(context, "Context is mandatory");

            this.context = context;
            return this;
        }

        public InputJson build() throws IOException {
            return new InputJson(context, fileName);
        }
    }

}
