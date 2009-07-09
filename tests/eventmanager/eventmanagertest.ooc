/**
 * Test suite for ooc's event manager.
 * Output and expected output must be the same.
 * @author Adrien Béraud <adrienberaud@gmail.com>
 * @version 0.3
 */

import event.EventDispatcher;
import MouseEvent;

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
	puts("Expected output :");
	puts("Event received from object1 : clic");
	puts("Event received from object2 : over");
	puts("Event received from object1 : over");
	puts("Event received from object2 : clic");
	puts("Output :");
	
	TestObject obj1 = new("object1");
	TestObject obj2 = new("object2");
	
	//test simple event dispatching
	obj1.addEventListener(cool, MouseEvent.CLIC);
	obj2.addEventListener(cool, MouseEvent.OVER);
	
	obj1.clic;
	obj1.over;
	
	obj2.clic;
	obj2.over;
	
	//test removing listeners
	obj1.removeEventListener(cool, MouseEvent.CLIC);
	obj2.removeEventListener(cool, MouseEvent.OVER);
	
	obj1.clic;
	obj1.over;
	
	obj2.clic;
	obj2.over;
	
	//test multiple similar listeners detection
	obj1.addEventListener(cool, MouseEvent.OVER);
	obj2.addEventListener(cool, MouseEvent.CLIC);
	obj1.addEventListener(cool, MouseEvent.OVER);
	obj2.addEventListener(cool, MouseEvent.CLIC);
	obj1.addEventListener(cool, MouseEvent.OVER);
	obj2.addEventListener(cool, MouseEvent.CLIC);
	
	obj1.clic;
	obj1.over;
	
	obj2.clic;
	obj2.over;
}

func cool(MouseEvent e) {
	TestObject target = e.target;
	String obj_name = target.obj_name;
	printf("Event received from %s : ", obj_name);
	
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
