import structs.SparseList;
import Event;

abstract class EventDispatcher {
	
	SparseList listeners;
	
	func new {
		listeners = new(5);
	}
	
	func addEventListener(Func listener, Int eventType){
		listeners.add(new EventListener(listener, eventType));
		/*EventListener el = new EventListener();
		el.listener = listener;
		el.event = eventType;
		listeners.add(el);*/
	}
	
	func removeEventListener(Func listener, Int eventType){
		for(EventListener eventListener: listeners) {
			
			if(eventListener.listener == listener && eventListener.event == eventType) {
				listeners.removeElement(eventListener);
			}
			
		}
	}
	
	func dispatchEvent(Event event){
		event.target = this;
		for(EventListener listener : listeners) {
			listener.listener(event);
		}
	}
}

class EventListener {
	
	Func listener;
	Int event;
	func new(=listener, =event);
	
}
