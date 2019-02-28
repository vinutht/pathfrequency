package io.cubecorp.pathfrequency.node;

import io.cubecorp.pathfrequency.Context;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ValueNode {

    private final Object value;
    private final Context context;

    private AtomicInteger valueFrequency = new AtomicInteger();

    public enum VALUE_TYPE {
        INT, STRING
    }

    private final VALUE_TYPE valueType;

    public ValueNode(Context context, Object value, VALUE_TYPE type) {

        Objects.requireNonNull(context, "Context is mandatory");
        Objects.requireNonNull(value, context.getMessageString("valuenode.value.mandatory"));
        Objects.requireNonNull(type, context.getMessageString("valuenode.type.mandatory"));

        this.context = context;
        this.value = value;
        this.valueType = type;
    }

    public Object getValue() {
        return value;
    }

    public int getValueFrequency() {
        return valueFrequency.get();
    }

    public void incrementValueFrequency() {
        this.valueFrequency.incrementAndGet();
    }

    public VALUE_TYPE getValueType() {
        return valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueNode valueNode = (ValueNode) o;
        return Objects.equals(value, valueNode.value) &&
                valueType == valueNode.valueType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, valueType);
    }
}
