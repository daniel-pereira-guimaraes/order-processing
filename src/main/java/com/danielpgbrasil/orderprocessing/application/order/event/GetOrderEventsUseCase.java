package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;

import java.util.List;

/* Este caso de uso poderia ser suprimido, pois não há nenhum processamento
    relevante sendo executado aqui. O controller poderia obter os eventos
    diretamente do repositório. Mesmo assim optei por criar este caso de
    uso porque ficaria mais simplesmente evoluir caso surja necessidade
    de algum processamento adicional, como validação de acesso, por exemplo.
 */
public class GetOrderEventsUseCase {

    private final OrderEventRepository repository;

    public GetOrderEventsUseCase(OrderEventRepository repository) {
        this.repository = repository;
    }

    public List<OrderEvent> getEvents(OrderId orderId) {
        return repository.findByOrderId(orderId);
    }
}
