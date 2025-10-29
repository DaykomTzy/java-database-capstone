package com.smartclinic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response format")
public class ApiResponse<T> {
    
    @Schema(description = "Response status", example = "success")
    private String status;
    
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
    
    @Schema(description = "Response data")
    private T data;
    
    @Schema(description = "Timestamp of the response")
    private LocalDateTime timestamp;
    
    @Schema(description = "API path", example = "/api/users")
    private String path;
    
    // Success static methods
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", "Operation completed successfully", data, LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data, LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data, String path) {
        return new ApiResponse<>("success", message, data, LocalDateTime.now(), path);
    }
    
    // Error static methods
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message, null, LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> error(String message, String path) {
        return new ApiResponse<>("error", message, null, LocalDateTime.now(), path);
    }
    
    // Validation error static method
    public static <T> ApiResponse<T> validationError(String message) {
        return new ApiResponse<>("validation_error", message, null, LocalDateTime.now(), null);
    }
}