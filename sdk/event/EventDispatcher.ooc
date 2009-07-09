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
	
	func addEventListener(Func listener, Int eventType) {
		for(EventListener eventListener: listeners) {
			//WTF ?
			//if(eventListener.listener == listener && eventListener.eventType == eventType) {
			if(eventListener->listener == listener && eventListener->eventType == eventType) {
				return;
			}
		}
		listeners.add(new EventListener(listener, eventType));
	}
	
	func removeEventListener(Func listener, Int eventType) -> Bool{
		for(EventListener eventListener: listeners) {
			//WTF ?
			//if(eventListener.listener == listener && eventListener.eventType == eventType) {
			if(eventListener->listener == listener && eventListener->eventType == eventType) {
				listeners.removeElement(eventListener);
				return true;
			}
			
		}
		return false;
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
