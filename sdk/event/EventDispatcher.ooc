import structs.SparseList;
import Event;

/**
 * Provides simple tools to manage and dispatch events.
 * @author		Adrien Béraud <adrienberaud@gmail.com>
 * @version		0.1
 * @see			Event
 * @since		0.3
 */
abstract class EventDispatcher {
	
	SparseList listeners;
	
	func new {
		listeners = new(1);
	}
	
	/**
	 * Add a listener to the listener list
	 * @param	listener a function reference (the "listener") called when the corresponding event is dispatched .
	 * @param	eventType the event to listen.
	 * @see		removeEventListener
	 */
	func addEventListener(Func listener, Int eventType) {
		for(EventListener eventListener: listeners) {
			//if(eventListener.@listener == @listener && eventListener.eventType == eventType) {
			if(eventListener->listener == listener && eventListener->eventType == eventType) {
				return;
			}
		}
		listeners.add(new EventListener(listener, eventType));
	}
	
	/**
	 * Remove a listener/eventType couple from the listener list
	 * @param		listener the function reference
	 * @param		eventType the event type.
	 * @return		true if the listener have been successfully removed, false if no corresponding listener/event couple have been found.
	 * @see			addEventListener
	 */
	func removeEventListener(Func listener, Int eventType) -> Bool{
		for(EventListener eventListener: listeners) {
			//if(eventListener.@listener == @listener && eventListener.eventType == eventType) {
			if(eventListener->listener == listener && eventListener->eventType == eventType) {
				listeners.removeElement(eventListener);
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * Dispatch a given event on the listener list.
	 * The target proprity of the event will be the object dispatching the event.
	 * This class ofer no warranty about the order of the function calls.
	 * @param		Event the event to dispatch
	 * @see			Event
	 */
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
