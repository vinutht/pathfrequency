package io.cubecorp.pathfrequency.core;

import com.fasterxml.jackson.databind.JsonNode;
import io.cubecorp.pathfrequency.core.node.NameNode;
import io.cubecorp.pathfrequency.core.node.ValueNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class PathFrequency {

    private static PathFrequency instance;
    private final AtomicInteger numOfDocuments = new AtomicInteger();
    private final Map<String, NameNode> nameNodes = new HashMap<>();
    private final Context context;


    private PathFrequency(Context context) {
        Objects.requireNonNull(context, "Context is mandatory");
        this.context = context;
    }

    public static PathFrequency getInstance(Context context) {
        if(instance == null) {
            instance = new PathFrequency(context);
        }
        return instance;
    }

    private void iterateOverFields(String parent, Iterator<Map.Entry<String, JsonNode>> fields) {
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> eachField = fields.next();
            String key = eachField.getKey();
            JsonNode valueJsonNode = eachField.getValue();

            Iterator<Map.Entry<String, JsonNode>> childDocuments = valueJsonNode.fields();
            String path = String.format("%s/%s", parent, key);

            if(childDocuments.hasNext()) {
                //Intermediate Nodes
                NameNode nameNode = new NameNode(context, path, NameNode.NAME_NODE_TYPE.DOC);
                addNameNode(nameNode);
                iterateOverFields(path, childDocuments);
            }
            else {
                //Leaf
                if(valueJsonNode.isArray()) {
                    NameNode nameNode = new NameNode(context, path, NameNode.NAME_NODE_TYPE.ARRAY);
                    addNameNode(nameNode);

                    Iterator<JsonNode> arrValuesIter = valueJsonNode.elements();
                    int arrIndex = 0;
                    while(arrValuesIter.hasNext()) {
                        JsonNode arrElement = arrValuesIter.next();
                        if(!arrElement.isObject()) {

                            String newPath = String.format("%s/%s", path, arrIndex);
                            NameNode arrElemNameNode = new NameNode(context, newPath, NameNode.NAME_NODE_TYPE.LEAF);
                            addNameNode(arrElemNameNode);

                            ValueNode valueNode = new ValueNode(context, arrElement.toString());
                            addValueNode(newPath, valueNode);

                            arrIndex++;
                        }
                    }
                }
                else {
                    NameNode nameNode = new NameNode(context, path, NameNode.NAME_NODE_TYPE.LEAF);
                    addNameNode(nameNode);

                    ValueNode valueNode = new ValueNode(context, valueJsonNode.toString());
                    addValueNode(path, valueNode);
                }


            }
        }
    }

    public void addDocument(JsonNode jsonDocument) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonDocument.fields();
        iterateOverFields("", fields);
        numOfDocuments.incrementAndGet();
    }

    public String toString() {

        int topK = context.getTopK();
        float pathOccurrenceRatio = context.getOccurrenceRatio();

        context.setTotalNumberOfDocuments(numOfDocuments.get());

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        synchronized (nameNodes) {
            Iterator<String> keyIter = nameNodes.keySet().iterator();
            while(keyIter.hasNext()) {
                String key = keyIter.next();
                NameNode nameNode = nameNodes.get(key);

                int pf = nameNode.getPathFrequency();
                float ratio = ((float)pf/(float)numOfDocuments.get());

                if(ratio >= pathOccurrenceRatio) {
                    sb.append("\n\n");
                    sb.append(nameNode.toString());
                }
            }
        }

        sb.append("\n\n]");
        return sb.toString();
    }

    private void addNameNode(NameNode nameNode) {

        Objects.requireNonNull(nameNode, context.getMessageString("namenode.mandatory"));

        String path = nameNode.getPath();
        NameNode.NAME_NODE_TYPE nameNodeType = nameNode.getNodeType();

        Objects.requireNonNull(path, context.getMessageString("namenode.path.mandatory"));
        Objects.requireNonNull(nameNodeType, context.getMessageString("namenode.type.mandatory"));

        synchronized (nameNodes) {

            NameNode existingNameNode = nameNodes.get(path);
            if(existingNameNode == null) {
                existingNameNode = new NameNode(context, path, nameNodeType);
            }
            existingNameNode.incrementPathFrequency();
            nameNodes.put(path, existingNameNode);
        }
    }

    private void addValueNode(String path, ValueNode valueNode) {

        Objects.requireNonNull(path, context.getMessageString("namenode.path.mandatory"));
        Objects.requireNonNull(valueNode, context.getMessageString("valuenode.mandatory"));

        synchronized (nameNodes) {
            NameNode existingNameNode = nameNodes.get(path);
            Objects.requireNonNull(existingNameNode, context.getMessageString("namenode.notfound", path));
            existingNameNode.addValueNode(valueNode);
        }
    }


}
