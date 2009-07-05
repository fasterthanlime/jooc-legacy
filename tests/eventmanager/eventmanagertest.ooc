import MouseEvent, EventDispatcher;

class TestObject1 from EventDispatcher
{
	func new
	{
		super;
	}
	
	func d
	{
		dispatchEvent(new MouseEvent(MouseEvent.CLIC, 50, 30));
	}
}

func main
{
	TestObject1 obj = new;
	obj.addEventListener(cool, MouseEvent.CLIC);
	
}

func cool(MouseEvent e)
{
	printf("coucouc, evenement reçut !\n");
}
