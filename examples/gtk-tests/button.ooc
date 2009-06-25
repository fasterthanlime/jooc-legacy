import lang.String;

import gtk.Gtk;
import gtk.Window;
import gtk.Box;
import gtk.VBox;
import gtk.Button;

func main(Int argc, String[] argv) {

	Gtk.init(&argc, &argv);

	Window w = new("Gtk Button");

	Box b = new VBox(false, 20);
	b.setBorderWidth(20);
	w.add(b);

	b.add(Button.newTextButton("Useless button =)", @prout));
	b.add(Button.newFromStock("gtk-quit", Gtk.@mainQuit));

	w.showAll;
	w.connectNaked("destroy", Gtk.@mainQuit);

	if(argc >= 2 && argv[1].equals("--test")) {
		return 0;
	}
	
	Gtk.main;

}

func prout {

	printf("Whoops, sorry :/\n");

}
