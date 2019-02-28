package io.cubecorp.pathfrequency.core;

import java.util.Locale;
import java.util.ResourceBundle;

public class Context {

    private final ResourceBundle messageBundle;

    public Context() {
        this.messageBundle = ResourceBundle.getBundle("message", new Locale("en","US"));
    }

    public String getMessageString(String key, String ... args) {
        if(args != null) {
            return String.format(messageBundle.getString(key), args);
        }
        return messageBundle.getString(key);
    }
}
