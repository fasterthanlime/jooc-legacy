import event.Event;

/**
 * A simple example class used by the test suite for ooc's event manager.
 * @author Adrien Béraud <adrienberaud@gmail.com>
 * @version 0.3
 */
class MouseEvent from Event {
	static const Int CLIC = 0;
	static const Int OVER = 1;
	
	Int x;
	Int y;
	
	func new(Int eventType, =x, =y)
	{
		super(eventType);
	}
}
