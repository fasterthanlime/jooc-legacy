import structs.SparseList;
import Event;

/**
 * 
 */
abstract class EventDispatcher {
	
	SparseList listeners;
	
	func new {
		listeners = new(1);
	}
	
	func addEventListener(Func listener, Int event) {
		listeners.add(new EventListener(listener, event));
	}
	
	func removeEventListener(Func listener, Int eventType) -> Bool{
		for(EventListener eventListener: listeners) {
			
			if(eventListener.listener == listener && eventListener.eventType == eventType) {
				listeners.removeElement(eventListener);
				return true;
			}
			
		}
	}
	
	func dispatchEvent(Event event){
		event.target = this;
		for(EventListener listener : listeners) {
			if(listener.eventType == event.type)
				listener.listener(event);
		}
	}
}

class EventListener {
	
	Func listener;
	Int eventType;
	func new(=listener, =eventType);
}
