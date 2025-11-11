# ⚙️ Order Processing

Este projeto demonstra de forma prática a implementação de um sistema de 
gerenciamento de entregas baseado em **arquitetura orientada a eventos**, 
simulando recepção, separação, transporte e entrega de pedidos.


## 🛠️ Tecnologias

- Java 21
- Spring Boot 3.5.7
- JUnit
- Mockito
- MockMvc
- JaCoCo
- Micrometer
- Liquibase
- H2Database (para testes)
- MySQL 8
- RabbitMQ 3.13.7


## 🏗️ Arquitetura e boas práticas

- Event-Driven Architecture (EDA)
- Domain-Driven Design (DDD)
- Clean Architecture
- Clean Code


## 🛡️ Consistência, resiliência e performance

O fluxo de processamento é dividido em duas etapas:

+ **Recepção do pedido**: síncrona e transacional.
+ **Separação, transporte e entrega**: assíncrona e eventual.


### Consistência

Todos os eventos são persistidos no banco **na mesma transação** que altera o estado do pedido, 
seguindo o padrão **Transactional Outbox**.


### Resiliência

- Os serviços verificam pré-condições para evitar o processamento duplicado de eventos.
- As mensagens com falha são enviadas para **Dead Letter Queue (DLQ)**.
- Os eventos podem ser republicados, garantindo consistência mesmo após falhas.

> Para manter o projeto simples, eventos fora de ordem são ignorados. Em cenários reais, 
> retries com delay devem recolocar o evento na fila, o que pode corrigir a ordem naturalmente.
> Se todas as tentativas falharem, o evento pode ser reprocessado a partir da fila de erro (DLQ).

### Baixa latência

- API retorna imediatamente após commit transacional, delegando processamento pesado ao fluxo assíncrono.  
- Publicação imediata de eventos garante disponibilidade quase instantânea na fila.  
- Processamento assíncrono via RabbitMQ mantém alta performance sem bloquear requisições.  


Essa abordagem integra **persistência confiável, consistência, resiliência e baixa latência**, 
servindo como referência didática para sistemas backend modernos.


## 🧩 Componentes principais

| Componente | Descrição |
|------------|-----------|
| Order / OrderEvent | Entidades de domínio (pedidos e seus eventos). |
| OrderRepository / JdbcOrderRepository | Interface e implementação do repositório de pedidos. |
| OrderEventRepository / JdbcOrderEventRepository | Interface e implementação do repositório de eventos. |
| PublishPendingOrderEventsService | Orquestra a publicação dos eventos pendentes. |
| AsyncPublishPendingOrderEventsService | Executa a publicação dos eventos em background. |
| PublishPendingOrderEventsScheduler | Agenda a publicação periódica de eventos pendentes. |
| OrderEventPublisher / RabbitMqOrderEventPublisher | Interface e implementação do publicador de eventos do pedido. |
| RabbitMqOrderEventConsumer | Consumidor de eventos do pedido. |


## 🔗 Endpoints da API

A API possui *endpoints* para criar e consultas pedidos e consultar seus dados, estado e eventos.
A documentação completa no formato OpenAPI (Swagger) é disponibilizada no
*endpoint* ```/swagger-ui/index.html```.

| Método | Endpoint            | Descrição                                              |
|--------|---------------------|--------------------------------------------------------|
| POST   | /orders             | Cria um novo pedido.                                   |
| GET    | /orders/{id}        | Consulta os dados de um pedido.                        |
| GET    | /orders/{id}/status | Consulta o status atual de um pedido.                  |
| GET    | /orders/{id}/events | Consulta o histórico completo de eventos de um pedido. |


## 💾 Banco de dados

A estrutura do banco de dados foi simplificada, visto que o foco do projeto é a
arquitetura orientada a eventos. As seguintes tabelas foram criadas:
+ **tb_order**: dados do pedido, com alguns dados persistidos como JSON.
+ **tb_order_event**: dados dos eventos dos pedidos.


## ➡️ Filas no RabbitMQ

As seguintes filas são criadas automaticamente no RabbitMQ para processamento
assíncrono dos eventos:
+ **order-events-queue**: fila principal, para processamento dos eventos.
+ **order-events-queue.error**: fila para backup dos eventos com falha no processamento.


## 👁️ Observabilidade

O sistema expõe métricas via **Spring Boot Actuator** e **Micrometer/Prometheus**.

**Métricas principais:**
- `failed_events_last_hour` → falhas de eventos na última hora.  
- `pending_events` → eventos aguardando publicação.  

**Endpoints relevantes:**
- `/actuator/health` → status da aplicação e componentes.  
- `/actuator/prometheus` → métricas para scraping pelo Prometheus.  
- `/actuator/metrics` → métricas detalhadas (JVM, DB, conexões).  

> ⚠️ Exposição de endpoints útil para desenvolvimento; deve ser restrita em produção.


## 🚀 Instruções para executar e testar localmente

### Pré-requisitos

É necessário ter um ambiente rodando os seguintes componentes:

| Componente     | Comando para verificar       | Exemplo de resultado esperado              |
|----------------|------------------------------|--------------------------------------------|
| Docker         | ```docker --version```       | ```Docker version 28.5.1, build e180ab8``` |
| Docker Compose | ```docker compose version``` | ```Docker Compose version v2.32.4```       |
| Git            | ```git --version```          | ```git version 2.34.1```                   |


### Executar os comandos

Execute os comandos abaixo para clonar o repositório, construir e executar
a pilha com MySQL, RabbitMQ e a aplicação. **Isto pode demorar alguns minutos!**

> ⚠️ **Atenção:** Antes de executar os comandos abaixo, garanta que nenhuma aplicação
> está rodando nas portas do RabbitMQ (**5672** e **15672**), MySQL (**3306**) e 
> da aplicação (**8080**).

```bash
git clone https://github.com/daniel-pereira-guimaraes/order-processing
cd order-processing
docker-compose up
```

Se tudo executar como esperado, estes endpoints estarão disponíveis:

+ Swagger da aplicação: http://localhost:8080/swagger-ui/index.html
+ RabbitMQ: http://localhost:15672/#/

Para acesso ao MySQL, use uma ferramenta cliente como DBeaver, MySQL Workbench
ou qualquer outra de sua preferência.


### Teste de retry e fila de erro

Depois que alguns pedidos forem processados, uma condição de evento com erro
pode ser simulada executando o comando SQL abaixo:

```sql
UPDATE order_processing.tb_order_event
SET published=0, created_at=0
WHERE type='CREATED'
ORDER BY id DESC LIMIT 1;
```

Este comando força a republicação de um evento com o timestamp de criação alterado para zero.
Para testes, adicionei uma validação deste atributo no consumer do evento, com este código
abaixo:

```java
private static void testRetry(OrderEvent event) {
    if (event.createdAt().value() == 0) {
        throw new IllegalStateException("Teste de retry");
    }
}
```

O lançamento da *exception* provoca o *retry* de processamento e, 
como a condição do erro não será resolvida, a mensagem será movida 
para a fila ```order-events-queue.error```.


## 💭 Considerações finais

Por se tratar de um projeto desenvolvido com propósito didático, o fluxo de processamento 
de pedidos é fictício, não representando a realidade: cada etapa executada apenas altera
o estado do pedido e publica um novo evento, até que o pedido chegue ao estado 
**DELIVERED** (entregue).

A intenção foi mostrar o processamento assíncrono, publicando e consumindo eventos através 
de um serviço de mensageria.


## 📜 Licença

Este projeto está licenciado sob a **licença MIT**, permitindo uso, cópia, modificação e distribuição.