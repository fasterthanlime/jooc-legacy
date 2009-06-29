import Thread;

class MyThread from Thread {
	String name;
	func new(=name);

	func run{
		for (Int i: 0..10) {
			printf("Thread %s, iterator %d\n",name,i);
		}
	}
}


func main{
	ThreadManager tm = new;
	
	//append 2 threads
	MyThread t1 = new;
	MyThread t2=new;

	tm.add(t1);
	tm.add(t2);

	//start threads
	tm.start();

}
