package com.vcs.vitalitygit.domain.dto.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    @JsonProperty("status")
    private HttpStatus status;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("message")
    private String message;

    @JsonProperty("debug_message")
    private String debugMessage;

    private ApiErrorResponse() {
        timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(HttpStatus status, Exception exception) {
        this();
        this.status = status;
        this.message = StringUtils.capitalize(exception.getMessage());
        this.debugMessage = exception.toString();
    }

    public ApiErrorResponse(HttpStatus status, Exception exception, String message) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = exception.toString();
    }
}
