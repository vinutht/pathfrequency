package io.cubecorp.pathfrequency.utils;

import io.cubecorp.pathfrequency.core.Context;

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
    }
}
