import event.Event;

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
