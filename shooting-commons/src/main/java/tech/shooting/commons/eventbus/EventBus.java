package tech.shooting.commons.eventbus;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.subscription.Subscription;

@Slf4j
public class EventBus {

	private static final String AVISION_MAIN_BUS = "Avision main eventbus";

	private static AvisionMbassador eventBus = null;

	private EventBus() {

	}

	static final IPublicationErrorHandler errorHandler = new IPublicationErrorHandler() {
		@Override
		public void handleError(PublicationError error) {
			log.error("Handled error while publishing event %s. ", error);
			log.error("Cause : ", error.getCause());
		}
	};

	private static AvisionMbassador getEventBus() {
		if (eventBus == null) {
			eventBus = new AvisionMbassador(new BusConfiguration().addFeature(Feature.SyncPubSub.Default()).addFeature(Feature.AsynchronousHandlerInvocation.Default()).addFeature(Feature.AsynchronousMessageDispatch.Default())
					.addPublicationErrorHandler(errorHandler).setProperty(IBusConfiguration.Properties.BusId, AVISION_MAIN_BUS));
		}
		return eventBus;
	}

	public static void publishEvent(Event event) {
		getEventBus().publish(event);
	}

	public static void publishEventAsync(Event event) {
		getEventBus().publishAsync(event);
	}

	public static void publishEventAsync(Event event, long timeout, TimeUnit timeunit) {
		getEventBus().publishAsync(event, timeout, timeunit);
	}

	public static void subscribe(Object subscriber) {
		getEventBus().subscribe(subscriber);
	}

	public static boolean unsubscribe(Object subscriber) {
		return getEventBus().unsubscribe(subscriber);
	}

	@SuppressWarnings("rawtypes")
	public static Collection<Subscription> getSubscribers(Class messageType) {
		return getEventBus().getSubscriptions(messageType);
	}
}
