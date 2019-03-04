package io.cubecorp.pathfrequency.core;

import com.fasterxml.jackson.databind.JsonNode;
import io.cubecorp.pathfrequency.core.node.NameNode;
import io.cubecorp.pathfrequency.core.node.ValueNode;

import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the main class which computes the Path Frequency.
 *
 * This class is thread-safe and it can be used by multiple threads to parallelly compute the path frequency of the independent document.
 *
 * This is also a singleton class
 *
 * **/
public final class PathFrequency {

    private static PathFrequency instance;

    //Total number of documents used to compute the path frequency
    //It is atomic
    private final AtomicInteger numOfDocuments = new AtomicInteger();

    //This is the field which stores all the Attributes of the JSON document
    //@GuardedBy(nameNodes) and is thread safe
    private final Map<String, NameNode> nameNodes = new HashMap<>();

    //Application wide context.
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

    public int getNumOfDocuments() {
        return numOfDocuments.get();
    }

    /**
     * This is the main entry point for computing the path frequency of the given document.
     * This api can be called with in a task and run by threads to improve the performance of the computation.
     * It traverses the jsonDocument provided and creates NameNodes and ValueNodes from each attribute in the json document.
     *
     * NameNode is the json key attribute and ValueNode is the value attribute
     * Both of these objects store and compute the statistics needed for pathfrequency computation.
     *
     * @param jsonDocument The json document object representing individual json record.
     *
     * */
    public void addDocument(JsonNode jsonDocument) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonDocument.fields();
        iterateOverFields("", fields);
        numOfDocuments.incrementAndGet();
    }

    /***
     * This method will print the results as it traverses the jsonobjecttree
     *
     * Make use of this to print to the console.
     * **/
    public void print() {

        float pathOccurrenceRatio = context.getOccurrenceRatio();

        context.setTotalNumberOfDocuments(numOfDocuments.get());

        System.out.println("[");

        //Even though we could have avoided using synchronizer here but keeping it for safety.
        //print will generally be called when all the threads are done with adding their documents for path frequency computation, so we neednot use synchronized here.
        synchronized (nameNodes) {
            Iterator<String> keyIter = nameNodes.keySet().iterator();
            while(keyIter.hasNext()) {
                String key = keyIter.next();
                NameNode nameNode = nameNodes.get(key);

                int pf = nameNode.getPathFrequency();
                float ratio = ((float)pf/(float)numOfDocuments.get());

                if(ratio >= pathOccurrenceRatio) {
                    System.out.println("\n\n");
                    nameNode.print();
                }
            }
        }

        System.out.println("\n\n]");
    }


    /***
     * The idea here is to write the output to the file.
     * Right now it is an empty function
     *
     * @param writer This is the writer object that will be used to write the output to. It can be bufferedfilewriter.
     * */
    public void print(Writer writer) {

    }

    public static void forceNullify() {
        instance = null;
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
