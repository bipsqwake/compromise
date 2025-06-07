package com.bipsqwake.compromise_ws.exception;

import com.bipsqwake.compromise_ws.message.ErrorResponse;

public class WebException extends Exception {

    private static final String EXTRACTION_FAILED = "extraction_failed";
    private static final String COLLECTION_IS_TOO_SMALL = "small_collection";
    private static final String INVALID_INPUT = "invalid_input";
    
    private String error;
    private String errorDescription;

    public static WebException extractionFailed(String description) {
        return new WebException(EXTRACTION_FAILED, description);
    }

    public static WebException collectionTooSmall(String description) {
        return new WebException(COLLECTION_IS_TOO_SMALL, description);
    }

    public static WebException invalidIput(String description) {
        return new WebException(INVALID_INPUT, description);
    }

    private WebException(String error, String errorDescription) {
        super(errorDescription);
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(error, errorDescription);
    }
}
