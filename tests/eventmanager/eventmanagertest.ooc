import MouseEvent, event.EventDispatcher;

class TestObject from EventDispatcher 
{
	String obj_name;
	
	func new(=obj_name) {
		super();
	}
	
	func clic {
		this.dispatchEvent(new MouseEvent(MouseEvent.CLIC, 36, 30));
	}
	
	func over {
		this.dispatchEvent(new MouseEvent(MouseEvent.OVER, 50, 30));
	}
}

func main {
	
	TestObject obj1 = new("objet1");
	TestObject obj2 = new("objet2");
	
	obj1.addEventListener(cool, MouseEvent.CLIC);
	obj2.addEventListener(cool, MouseEvent.OVER);
	
	obj1.clic;
	obj1.over;
	
	obj2.clic;
	obj2.over;
}

func cool(MouseEvent e) {
	TestObject target = e.target;
	String obj_name = target.obj_name;
	printf("Hi, we received an event from %s : ", obj_name);
	
	/*switch(e.type)
	{
	case MouseEvent.CLIC:
		printf("clic\n");
		break;
	case MouseEvent.OVER:
		printf("over\n");
		break;
	}*/
	
	if(e.type == MouseEvent.CLIC) printf("clic\n");
	else if(e.type == MouseEvent.OVER) printf("over\n");
}
