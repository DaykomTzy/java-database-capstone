package com.smartclinic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response format")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error type", example = "Bad Request")
    private String error;
    
    @Schema(description = "Detailed error message", example = "Invalid input parameters")
    private String message;
    
    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;
    
    @Schema(description = "API path where error occurred", example = "/api/users")
    private String path;
    
    @Schema(description = "List of validation errors")
    private List<ValidationError> validationErrors;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Validation error details")
    public static class ValidationError {
        
        @Schema(description = "Field name with error", example = "email")
        private String field;
        
        @Schema(description = "Error message", example = "Email must be valid")
        private String message;
        
        @Schema(description = "Rejected value", example = "invalid-email")
        private Object rejectedValue;
    }
    
    // Static factory methods
    public static ErrorResponse badRequest(String message, String path) {
        return new ErrorResponse(400, "Bad Request", message, LocalDateTime.now(), path, null);
    }
    
    public static ErrorResponse notFound(String message, String path) {
        return new ErrorResponse(404, "Not Found", message, LocalDateTime.now(), path, null);
    }
    
    public static ErrorResponse unauthorized(String message, String path) {
        return new ErrorResponse(401, "Unauthorized", message, LocalDateTime.now(), path, null);
    }
    
    public static ErrorResponse forbidden(String message, String path) {
        return new ErrorResponse(403, "Forbidden", message, LocalDateTime.now(), path, null);
    }
    
    public static ErrorResponse internalError(String message, String path) {
        return new ErrorResponse(500, "Internal Server Error", message, LocalDateTime.now(), path, null);
    }
    
    public static ErrorResponse validationError(String message, String path, List<ValidationError> validationErrors) {
        return new ErrorResponse(400, "Validation Error", message, LocalDateTime.now(), path, validationErrors);
    }
}