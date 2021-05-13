package uk.gov.companieshouse.efs.web.payment.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

enum SessionStatus {
    PAID("paid"), PENDING("pending"), CANCELLED("cancelled"), FAILED("failed");

    private String value;

    SessionStatus(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Returns the status or null if no match found
     *
     * @param text the status value
     * @return the status or null if no match found
     */
    @JsonCreator
    public static SessionStatus fromValue(String text) {
        for (SessionStatus b : SessionStatus.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
