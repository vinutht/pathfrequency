package io.cubecorp.pathfrequency.core.node;

import io.cubecorp.pathfrequency.core.Context;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ValueNode stores the values of json-attribute and its statistics.
 * **/
public class ValueNode {

    private final Object value;
    private final Context context;

    //Number of times this value is present in the entire document set.
    private AtomicInteger valueFrequency = new AtomicInteger();

    public ValueNode(Context context, Object value) {

        Objects.requireNonNull(context, "Context is mandatory");
        Objects.requireNonNull(value, context.getMessageString("valuenode.value.mandatory"));

        this.context = context;
        this.value = value;
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


    public String toString(int pathOccurrence) {

        String value = getValue().toString();
        int vf = valueFrequency.get();
        float ratio = ((float)vf/(float)pathOccurrence);

        if(ratio == 1.0f) {
            return String.format("{%s, %s}, ", value, 1);
        }
        else {
            return String.format("{%s, %s/%s}, ", value, vf, pathOccurrence);
        }


    }


    public void print(int pathOccurrence) {

        String value = getValue().toString();
        int vf = valueFrequency.get();
        float ratio = ((float)vf/(float)pathOccurrence);

        if(ratio == 1.0f) {
            System.out.print(String.format("{%s, %s}, ", value, 1));
        }
        else {
            System.out.print(String.format("{%s, %s/%s}, ", value, vf, pathOccurrence));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueNode valueNode = (ValueNode) o;
        return Objects.equals(value, valueNode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
