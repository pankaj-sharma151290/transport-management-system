package com.elemica.tms.model.resourceobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public @Data
class TMSExceptionResponse {

    private int    httpCode;
    private String httpStatus;
    private String message;
}
