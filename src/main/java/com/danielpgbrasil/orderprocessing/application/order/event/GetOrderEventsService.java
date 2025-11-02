package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;

import java.util.List;

/* Este service poderia ser suprimido, pois não há nenhum processamento
    relevante sendo executado aqui. O controller poderia obter os eventos
    diretamente do repositório. Mesmo assim optei por criar este service
    porque fica mais fácil evoluir caso surja necessidade de algum
    processamento adicional, como validação de acesso, por exemplo.
 */
public class GetOrderEventsService {

    private final OrderEventRepository repository;

    public GetOrderEventsService(OrderEventRepository repository) {
        this.repository = repository;
    }

    public List<OrderEvent> getEvents(OrderId orderId) {
        return repository.findByOrderId(orderId);
    }
}
