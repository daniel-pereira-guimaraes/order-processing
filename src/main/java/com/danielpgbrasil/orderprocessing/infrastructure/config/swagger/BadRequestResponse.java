package com.danielpgbrasil.orderprocessing.infrastructure.config.swagger;

import com.danielpgbrasil.orderprocessing.infrastructure.shared.AppErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "400",
        description = "Requisição inválida",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                        implementation = AppErrorResponse.class,
                        example = "Mensagem de erro"
                )
        )
)
public @interface BadRequestResponse {}