package io.cubecorp.pathfrequency.core.node;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.cubecorp.pathfrequency.core.Context;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the class which stores the json attribute-key and its statistics.
 * This is thread-safe with the intention to be accessed by multiple threads
 * **/
public class NameNode {

    public enum NAME_NODE_TYPE {
        LEAF, DOC, ARRAY
    }

    private final String path;
    private final NAME_NODE_TYPE nodeType;
    private final Context context;

    //Stores the frequency of the attribute-key
    private AtomicInteger pathFrequency = new AtomicInteger();

    //Stores all the values as ValueNode of this particular attribute-key
    //@GuardedBy(values)
    //This is thread-safe
    private Map<String, ValueNode> values = new HashMap<>();

    //Backing data-structure to store valueNodes in sorted order. The sort technique is Radix sort.
    //Key is the occurrence count and the value is the list of valuenodes with the same occurrence count.
    //I am using linked hashmap to get the keys in the order of insertion which will be used for topK.
    //@GuardedBy(values)
    private Multimap<String, ValueNode> occurrenceCountMap = LinkedHashMultimap.create();

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

    /**
     * This method adds valueNode at a particular path in the NameNode hierarchy.
     * It first finds the NameNode associated with the path and then attaches the ValueNode to that NameNode.
     *
     * It sorts the valueNode using Radix sort based on the occurrence count of the value node
     * */
    public void addValueNode(ValueNode valueNode) {

        if(nodeType == NAME_NODE_TYPE.LEAF) {

            Objects.requireNonNull(valueNode, context.getMessageString("valuenode.mandatory"));

            Object value = valueNode.getValue();

            Objects.requireNonNull(value, context.getMessageString("valuenode.value.mandatory"));

            String valString = value.toString();

            synchronized (values) {
                ValueNode existingValueNode = values.get(valString);
                if(existingValueNode == null) {
                    existingValueNode = new ValueNode(context, value);
                }
                existingValueNode.incrementValueFrequency();
                values.put(valString, existingValueNode);
                //Radix sorting technique. Please see printValueNodes or valueNodesToString method so that it make sense on how sorting is implemented in totality.
                occurrenceCountMap.put(existingValueNode.getValueFrequency()+"", existingValueNode);
            }

        }
    }

    /**
     * This method prints the details of the name node to the console.
     *
     * This should only be called from PathFrequency.print as the datastructures here are not guarded by any lock.
     *
     * **/
    public void print() {
        int numOfDocuments = context.getTotalNumberOfDocuments();
        int pf = pathFrequency.get();
        float ratio = ((float)pf/(float)numOfDocuments);
        if(ratio == 1.0f) {
            System.out.print(String.format("%s, %s, ", path, 1));
            printValueNodes();
            System.out.println();
        }
        else {
            System.out.print(String.format("%s, %s/%s, ", path, pf, numOfDocuments));
            printValueNodes();
            System.out.println();
        }
    }

    private void printValueNodes() {
        int topK = context.getTopK();

        List<String> rankList = new ArrayList<>();
        occurrenceCountMap.keySet().iterator().forEachRemaining(rankList::add);

        Set<ValueNode> topKValueNodes = new HashSet<>();

        /***
         * I am getting the topK valueNodes from the occurrenceCountMap.
         * Since occurrenceCountMap keys valueNode based on the occurrenceCount, I am getting all the keys (occurrenceCount)
         * and iterating it in the reverse order because higher the occurrencecount, it will be stored at the higher index.
         * Iterating for not more than topK
         *
         * Example
         * occurrencecountMap[1] = {evans, joe}
         * occurrenceCountMap[2] = {vinuth, rajesh}
         * occurrenceCountMap[5] = {susan, Ajay}
         *
         * Get the topK keys. If suppose the topK is 2 then we will get {5, 2}
         * Now add all the valueNodes pertaining to 5, 2 into set, so it becomes {susan, Ajay, vinuth, rajesh}
         *
         * Now further filter the above list based on the atleastKTimes attribute.
         * If the atleastKTimes attribute is 3 then only those value nodes with atleast 3 occurrences will be added.
         * In this case Vinuth, rajesh valueNodes will be omitted.
         * **/
        int reverseIndex = rankList.size()-1;
        int atleastKTimes = context.getAtleastKTimes();

        for(int rankIndex=1; rankIndex<=topK; rankIndex++) {
            if(reverseIndex >= 0) {
                String ocStr = rankList.get(reverseIndex);
                Integer occurrenceCount = Integer.parseInt(ocStr);
                if(occurrenceCount >= atleastKTimes) {
                    topKValueNodes.addAll(occurrenceCountMap.get(ocStr));
                }
                reverseIndex--;
            }
        }


        Iterator<ValueNode> valueNodes = topKValueNodes.iterator();
        System.out.print("[");
        while(valueNodes.hasNext()) {
            ValueNode valueNode = valueNodes.next();
            valueNode.print(pathFrequency.get());
        }
        System.out.print("]");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameNode nameNode = (NameNode) o;
        return Objects.equals(path, nameNode.path) &&
                nodeType == nameNode.nodeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, nodeType);
    }
}
