package services.funkos;

import models.Funko;
import models.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class FunkosNotificationsImpl implements FunkosNotifications {

    private static FunkosNotificationsImpl instance = new FunkosNotificationsImpl();

    private final Flux<Notificacion<Funko>> funkosNotificationFlux;
    private FluxSink<Notificacion<Funko>> funkosNotification;

    private FunkosNotificationsImpl() {
        this.funkosNotificationFlux = Flux.<Notificacion<Funko>>create(emitter -> this.funkosNotification = emitter).share();
    }

    public static synchronized FunkosNotificationsImpl getInstance() {
        if (instance == null) {
            instance = new FunkosNotificationsImpl();
        }
        return instance;
    }

    @Override
    public Flux<Notificacion<Funko>> getNotificationAsFlux() {
        return funkosNotificationFlux;
    }

    @Override
    public void notify(Notificacion<Funko> notificacion) {
        funkosNotification.next(notificacion);
    }
}