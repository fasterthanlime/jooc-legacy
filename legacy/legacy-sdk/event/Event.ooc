/**
 * Provides a base class to extend to create personalized Events.
 * @author		Adrien Béraud <adrienberaud@gmail.com>
 * @version		0.1
 * @see			EventDispatcher
 * @since		0.3
 */
class Event {	
	Int type;
	Object target;
	
	func new(=type);
}
