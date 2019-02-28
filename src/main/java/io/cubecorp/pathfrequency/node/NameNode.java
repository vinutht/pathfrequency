package io.cubecorp.pathfrequency.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NameNode {

    public enum NAME_NODE_TYPE {
        LEAF, DOC, ARRAY
    }

    private final String path;
    private final NAME_NODE_TYPE type;

    private AtomicInteger pathFrequency = new AtomicInteger();

    private List<ValueNode> values = new ArrayList<>();

    public NameNode(String path, NAME_NODE_TYPE type) {
        Objects.requireNonNull(path);

        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public NAME_NODE_TYPE getType() {
        return type;
    }

    public int getPathFrequency() {
        return pathFrequency.get();
    }

    public void incrementPathFrequency() {
        pathFrequency.incrementAndGet();
    }

    public void setValueNode(ValueNode valueNode) {
        if(type == NAME_NODE_TYPE.LEAF) {

        }
    }

}
