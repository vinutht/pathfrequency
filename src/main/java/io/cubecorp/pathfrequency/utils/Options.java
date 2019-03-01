package io.cubecorp.pathfrequency.utils;

import io.cubecorp.pathfrequency.core.Context;

/**
 * This is the class to read the program arguments
 * The expected arguments to the program are
 * -k = Top K values to be shown
 * -r = Top json nodes to be shown whose count ratio is greater than the provided ratio
 * -i = input json filename needed as input for the program to compute the path frequency
 * **/
public class Options {


    private final Context context;

    private static Options instance = null;

    private Options(Context context) {
        this.context = context;
    }

    public static Options instance(Context context) {
        if (instance == null)
            instance = new Options(context);

        return instance;
    }

    public boolean parseArgs(String argv[]) throws Exception {

        if (argv != null) {
            int topK = 1;
            float occurrenceRatio = 0.3f;
            String inputFileName = "input.json";
            context.setInputFileName(inputFileName);

            for (int argc = 0; argc < argv.length; argc += 2) {
                if (argv[argc].equals("-k")) {
                    try {
                        topK = Integer.parseInt(argv[argc + 1]);
                    }
                    catch (Exception e) {
                        throw new Exception(context.getMessageString("topk.error"));
                    }

                }
                else if (argv[argc].equals("-r")) {
                    occurrenceRatio = Float.parseFloat(argv[argc + 1]);
                }
                else if(argv[argc].equals("-i")) {
                    inputFileName = argv[argc + 1];
                    context.setInputFileName(inputFileName);
                }
                else {
                    printUsage();
                    return false;
                }
            }

            if(topK <= 0) {
                throw new Exception(context.getMessageString("topk.error"));
            }

            if(occurrenceRatio > 1 || occurrenceRatio <= 0) {
                throw new Exception(context.getMessageString("path.occurrence.ratio.error"));
            }

            if(inputFileName == null || inputFileName.trim().length() == 0) {
                throw new Exception(context.getMessageString("input.file.mandatory"));
            }

            context.setTopK(topK);
            context.setOccurrenceRatio(occurrenceRatio);

            return true;
        }
        return false;
    }

    private void printUsage() {
        System.out.println("Usage: ");
        System.out.println("-k topK");
        System.out.println("-r occurenceRatio");
        System.out.println("-i input json filename");
    }
}
