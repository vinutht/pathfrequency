# pathfrequency

The source code tries to solve the below problem.

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


# To run the program <br>
<br>

main-class: Main <br>
program-arguments: -k {topK} -r {occurrence-ratio} -i {input json filename} <br>
input-file: There is already an input.json file present along with the source-code in the resource directory. We can make use of it.<br>
If we need to add a new input file then we have to put it under resource folder.

# Example
io.cubecorp.pathfrequency.Main -k 2 -r 0.1 -i input.json

