import gtk.Gtk;
import gtk.Window;
import gtk.Label;

func main(Int argc, String[] argv) {

	Gtk.init(&argc, &argv);

	Window w = new("Gtk Basic");
	w.setUSize(150, 150);
	w.add(new Label("Hey, you =)"));
	w.showAll;
	w.connectNaked("destroy", Gtk.@mainQuit);
	
	Gtk.main;

}
