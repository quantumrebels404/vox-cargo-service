package com.voxcargo.shipment.dto;

import java.util.Map;

public class ValidationResponse<T> {
    private boolean valid;
    private Map<String, String> errors;
    private T data;

    public ValidationResponse() {}

    public ValidationResponse(boolean valid, Map<String, String> errors, T data) {
        this.valid = valid;
        this.errors = errors;
        this.data = data;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}