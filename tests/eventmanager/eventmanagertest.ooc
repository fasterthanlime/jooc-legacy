import MouseEvent, EventDispatcher;

class TestObject1 from EventDispatcher 
{
	func new {
		super();
	}
	
	func d {
		this.dispatchEvent(new MouseEvent(MouseEvent.CLIC, 50, 30));
	}
}

func main {
	
	TestObject1 obj = new;
	obj.addEventListener(cool, MouseEvent.CLIC);
	obj.d;
	
}

func cool(MouseEvent e) {
	
	printf("Hi, we received an event!\n");
	
}
