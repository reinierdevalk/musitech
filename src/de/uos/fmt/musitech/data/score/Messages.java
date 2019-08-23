/*
 * File Messages.java
 * Created on 18.12.2004 by Tillman Weyde.
 */

package de.uos.fmt.musitech.data.score;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO add class comment
 * @author Tillman Weyde
 */
public class Messages {

    private static final String BUNDLE_NAME = "de.uos.fmt.musitech.applications.course.messages";//$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
        // TODO Auto-generated method stub
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}