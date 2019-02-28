package io.cubecorp.pathfrequency.core.node;

import io.cubecorp.pathfrequency.core.Context;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NameNode {

    public enum NAME_NODE_TYPE {
        LEAF, DOC, ARRAY
    }

    private final String path;
    private final NAME_NODE_TYPE nodeType;
    private final Context context;

    private AtomicInteger pathFrequency = new AtomicInteger();

    private Map<String, ValueNode> values = new HashMap<>();

    public NameNode(Context context, String path, NAME_NODE_TYPE nodeType) {

        Objects.requireNonNull(context, "Context is mandatory");
        Objects.requireNonNull(path, context.getMessageString("namenode.path.mandatory"));
        Objects.requireNonNull(nodeType, context.getMessageString("namenode.type.mandatory"));

        this.context = context;
        this.path = path;
        this.nodeType = nodeType;
    }

    public String getPath() {
        return path;
    }

    public NAME_NODE_TYPE getNodeType() {
        return nodeType;
    }

    public int getPathFrequency() {
        return pathFrequency.get();
    }

    public void incrementPathFrequency() {
        pathFrequency.incrementAndGet();
    }

    public void addValueNode(ValueNode valueNode) {

        if(nodeType == NAME_NODE_TYPE.LEAF) {

            Objects.requireNonNull(valueNode, context.getMessageString("valuenode.mandatory"));

            Object value = valueNode.getValue();
            ValueNode.VALUE_TYPE valueType = valueNode.getValueType();

            Objects.requireNonNull(value, context.getMessageString("valuenode.value.mandatory"));
            Objects.requireNonNull(valueType, context.getMessageString("valuenode.type.mandatory"));

            String valString = value.toString();

            synchronized (values) {
                ValueNode existingValueNode = values.get(valString);
                if(existingValueNode == null) {
                    existingValueNode = new ValueNode(context, value, valueType);
                }
                existingValueNode.incrementValueFrequency();
                values.put(valString, existingValueNode);
            }

        }
    }

    public String toString(int topK, int numOfDocuments) {
        int pf = pathFrequency.get();
        float ratio = ((float)pf/(float)numOfDocuments);
        return String.format("%s, %s/%s, %s", path, pf, numOfDocuments, valueNodesToString(topK));
    }

    private String valueNodesToString(int topK) {
        Iterator<String> keys = values.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while(keys.hasNext()) {
            ValueNode valueNode = values.get(keys.next());
            if(valueNode.getValueFrequency() >= topK) {
                sb.append(valueNode.toString(pathFrequency.get()));
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
