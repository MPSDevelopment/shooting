package tech.shooting.commons.eventbus;

import java.util.Collection;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.subscription.Subscription;

@SuppressWarnings("rawtypes")
public class AvisionMbassador extends MBassador<Event> {

	public AvisionMbassador() {
		super();
	}

	public AvisionMbassador(IBusConfiguration configuration) {
		super(configuration);
	}

	public Collection<Subscription> getSubscriptions(Class messageType) {
		return getSubscriptionsByMessageType(messageType);
	}

	public int getSubscriptionsCount(Class messageType) {
		return getSubscriptionsByMessageType(messageType).size();
	}

}
