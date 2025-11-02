package com.danielpgbrasil.orderprocessing.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        tags = {
                @Tag(name = "Pedidos", description = "Cadastro de pedidos."),
                @Tag(name = "Monitoramento", description = "Monitoramento do estado do serviço.")
        },
        info = @Info(
                title = "Order Processing API",
                version = "1.0.0",
                description = """
                        <p><strong>Descrição:</strong>
                            API para processamento assíncrono da logística de pedidos, contemplando
                            os eventos de recepção do pedido, separação, transporte e entrega,
                            garantindo persistência e histórico completo de eventos para cada pedido.
                            A arquitetura é orientada a eventos, resiliente a falhas e escalável,
                            com mínima latência e consistência total dos dados.
                        </p>
                        <p><strong>Objetivo:</strong>
                            Desenvolvida para demonstrar proficiência técnica, esta API é uma
                            simplificação que não incorpora a totalidade das regras de negócio nem
                            a complexidade de infraestrutura e segurança inerentes a um ambiente
                            de produção real.
                        </p>
                        <p><strong>Tecnologias utilizadas:</strong>
                            Java 21, Spring Boot 3.5.7, JDBC, JUnit, Mockito, MySQL 8, RabbitMQ, Swagger.
                        </p>
                        <p><strong>Boas práticas aplicadas:</strong>
                            TDD, DDD, Clean Code, Clean Architecture, documentação padronizada,
                            publicação confiável de eventos persistidos, processamento assíncrono com
                            prevenção de duplicidade e logging estruturado para rastreabilidade.
                        </p>
                        """,
                contact = @Contact(
                        name = "Daniel Pereira Guimarães",
                        url = "https://www.linkedin.com/in/daniel-pereira-guimaraes/"
                )
        )


)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addResponses("AppErrorResponse",
                                new ApiResponse()
                                        .description("Erro genérico")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new Schema<>()
                                                                .$ref("#/components/schemas/AppErrorResponse")
                                                        )
                                                )
                                        )
                        )
                );
    }
}