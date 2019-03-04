package io.cubecorp.pathfrequency;

import com.fasterxml.jackson.databind.JsonNode;
import io.cubecorp.pathfrequency.core.Context;
import io.cubecorp.pathfrequency.core.InputJson;
import io.cubecorp.pathfrequency.core.PathFrequency;
import io.cubecorp.pathfrequency.utils.Options;

import java.io.IOException;
import java.util.Iterator;

/**
 * This is the main entry of the Path Frequency computation.
 * The program expects two arguments
 * -k = The topK values of a given Json Node
 * -r = The occurrence ratio filters all the attributes whose occurrence is the list of json document is less than the ratio
 * -i = name of the input json
 *
 * The problem this program solves is as follows

 Given a set of json objects, output the set of (path, fraction of occurrences in the input set, top K
 most frequent values for leaf paths). For each path that is observed in the input set of JSON
 documents, we need to compute how often that path exists and when it exists what are the top
 K (say, 2) values along with their occurrence fractions – consider only values which occur at least
 K’ times (1 in the following example).
 Consider the following collection of JSON documents.
 {“name” : “Joe”, “address” : {“street” : “montgomery st”, “number”: 101, “city”: “new york”, “state”: “ny”}}
 {“name” : “Evan”, “address” : {“street” : “Santa Theresa st”, “number”: 201, “city”: “sfo”, “state”: “ca”}}
 {“name” : “Joe”, “address” : {“street” : “new hampshire ave”, “number”: 301, “city”: “dublin”, “state”: “ca”}}
 {“name” : “Joe”, “qualifications” : [“BS”, “MS”] }
 The paths, percentages, and top values are:
 [“name”, 1, [{Joe, 3⁄4}, {Evan, 1⁄4}]
 “address”, 3⁄4 [] (not a leaf path and hence we don’t consider the top K most frequent values)
 “qualifications”, 1⁄4, []
 “qualifications/0”, 1⁄4, [{“BS”, 1}]
 “qualifications/1”, 1⁄4, [{“MS”, 1}]
 “address/city”, 3⁄4, [{"new york", 1/3}, {"sfo", 1/3}]
 And, so on.

 *
 *
 *
 * **/
public class Main {

    public static void main(String args[]) throws Exception {

        //Application wide context
        Context context = new Context();

        /**
         * Parsing the arguments
         * Expected switch -k, -t, -r, -i
         * */
        Options options = Options.instance(context);
        if(!options.parseArgs(args)) {
            options.printUsage();
            return;
        }

        InputJson.Builder inputJsonBuilder = new InputJson.Builder();

        try {

            //Here I am reading the input json and parsing it into JSON Object Tree
            InputJson inputJson = inputJsonBuilder
                    .setInputFileName(context.getInputFileName())
                    .setContext(context)
                    .build();

            //This is the main class responsible for computing PathFrequency
            //This is a singleton class and is thread-safe
            PathFrequency pathFrequency = PathFrequency.getInstance(context);

            //Once the input json is parsed, start iterating through the elements
            Iterator<JsonNode> inputJsonIter = inputJson.iterator();

            while(inputJsonIter.hasNext()) {
                JsonNode eachDocument = inputJsonIter.next();
                //Add document will add each individual document which further is used to compute the path frequency
                //Add document is the main entry function for the path frequency computation.
                pathFrequency.addDocument(eachDocument);
            }

            //Once after all the documents are added and path frequency is computed, print the results
            //These are the two options to print


            //Option1: pathFrequency.print();
            //Option2 will print as it traverses the nodes and it doesnt construct large string objects in memory and hence it becomes more viable.


            //Option2: pathFrequency.print(Writer);
            //Option3 uses a writer object to write to it. It can be a filewriter so that the output is written to the file.

            //But I am using Option 2 here
            pathFrequency.print();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
