// FILE: src/main/java/com/edugenius/views/ParameterReceiver.java
package com.edugenius.views;

import java.util.Map;

/**
 * Interface for panels that can receive parameters during navigation
 * Used to pass data between screens (e.g., course data to dashboard)
 */
public interface ParameterReceiver {
    /**
     * Receive parameters when navigating to this screen
     * @param params Map of parameter names to values
     */
    void receiveParameters(Map<String, Object> params);
}