package com.opexos.imageuploader.commondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO that contains error message
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    private String errorMessage;
}
