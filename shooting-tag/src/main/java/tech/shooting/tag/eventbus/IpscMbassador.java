package tech.shooting.tag.eventbus;

import java.util.Collection;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.subscription.Subscription;

@SuppressWarnings("rawtypes")
public class IpscMbassador extends MBassador<Event> {

	public IpscMbassador() {
		super();
	}

	public IpscMbassador(IBusConfiguration configuration) {
		super(configuration);
	}

	public Collection<Subscription> getSubscriptions(Class messageType) {
		return getSubscriptionsByMessageType(messageType);
	}

	public int getSubscriptionsCount(Class messageType) {
		return getSubscriptionsByMessageType(messageType).size();
	}

}
