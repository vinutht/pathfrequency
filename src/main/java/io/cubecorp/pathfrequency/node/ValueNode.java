package io.cubecorp.pathfrequency.node;

import java.util.Objects;

public class ValueNode {

    private final Object value;
    private int valueFrequency;

    public enum VALUE_TYPE {
        INT, STRING
    }

    private final VALUE_TYPE type;

    public ValueNode(Object value, VALUE_TYPE type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public int getValueFrequency() {
        return valueFrequency;
    }

    public void setValueFrequency(int valueFrequency) {
        this.valueFrequency = valueFrequency;
    }

    public VALUE_TYPE getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueNode valueNode = (ValueNode) o;
        return Objects.equals(value, valueNode.value) &&
                type == valueNode.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
}
