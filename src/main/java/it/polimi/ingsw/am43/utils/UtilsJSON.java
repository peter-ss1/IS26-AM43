package it.polimi.ingsw.am43.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class providing a globally shared Jackson object mapper instance
 * for JSON serialization and deserialization processing.
 */
public class UtilsJSON {
    public static final ObjectMapper mapper = new ObjectMapper();
    private UtilsJSON(){}
}
