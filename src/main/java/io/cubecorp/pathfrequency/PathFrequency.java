package io.cubecorp.pathfrequency;

import io.cubecorp.pathfrequency.node.NameNode;
import io.cubecorp.pathfrequency.node.ValueNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

final class PathFrequency {

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

    public void addNameNode(NameNode nameNode) {

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

    public void addValueNode(String path, ValueNode valueNode) {

        Objects.requireNonNull(path, context.getMessageString("namenode.path.mandatory"));
        Objects.requireNonNull(valueNode, context.getMessageString("valuenode.mandatory"));

        synchronized (nameNodes) {
            NameNode existingNameNode = nameNodes.get(path);
            Objects.requireNonNull(existingNameNode, context.getMessageString("namenode.notfound", path));
            existingNameNode.addValueNode(valueNode);
        }
    }
}
