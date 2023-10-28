package services.funkos;

import models.Funko;
import models.Notificacion;
import reactor.core.publisher.Flux;

public interface FunkosNotifications {
    Flux<Notificacion<Funko>> getNotificationAsFlux();

    void notify(Notificacion<Funko> notificacion);
}
