package com.cdac.statewide.hiscommon.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterResponse<T> {
    private boolean success; // Indicates whether the operation was successful
    private String message; // Descriptive message for the response
    private int statusCode; // HTTP status code (e.g., 200, 204, 500)
    private T data; // Response data (generic type)
}
