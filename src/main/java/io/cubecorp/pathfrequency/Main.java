package io.cubecorp.pathfrequency;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main {

    public static void main(String args[]) {

        ResourceBundle messages = ResourceBundle.getBundle("message", new Locale("en","US"));

        PathFrequency pathFrequency = PathFrequency.getInstance(new Context(messages));
    }
}
