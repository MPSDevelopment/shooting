package tech.shooting.commons.eventbus;

public class EventHandler implements Subscribable {

	
	public EventHandler(){
		subscribe();
	}
	
	@Override
	public void subscribe() {
		EventBus.subscribe(this);
		
	}

	@Override
	public void unsubscribe() {
		EventBus.unsubscribe(this);
		
	}

}
