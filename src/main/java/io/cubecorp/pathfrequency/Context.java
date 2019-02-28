package io.cubecorp.pathfrequency;

import java.util.ResourceBundle;

public class Context {

    private final ResourceBundle messageBundle;

    public Context(ResourceBundle messageBundle) {
        this.messageBundle = messageBundle;
    }

    public String getMessageString(String key, String ... args) {
        if(args != null) {
            return String.format(messageBundle.getString(key), args);
        }
        return messageBundle.getString(key);
    }
}
