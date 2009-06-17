use gtk;

/** Typedefs */
typedef gchar GChar;
typedef gpointer GPointer;
typedef gulong GULong;

class SignalPacket {
	
	GObject object;
	GPointer data;
	Func callback;
	
	new(=object, =data, =callback) {
		//printf("new SignalPacket created with a %s and data '%s'\n", object.class->name, (String) data);
	}
	
	func call {
		//printf("[SignalPacket] Forwarding callback on a %s with data '%s'!\n", object.class->name, (String) data);
		callback(object, data);
	}
	
}

func signalWrapper(GtkObject* object, GPointer data) {
	
	//printf("[SignalPacket] signalWrapper, got called!\n");
	SignalPacket packet = (SignalPacket) data;
	packet.call();
	
}

/**
 * The base of the GObject hierarchy. Can send and receive signals
 */
abstract class GObject {

	/**
	 * must be implemented by any child class
	 */
	abstract func getObject -> GtkObject*;

	/**
	 * Connect a callback to a signal
	 * @param signalName the name of the signal to connect to
	 * @param callback a pointer to the Func to call whenever
	 * this signal is received
	 */
	func connect(String signalName, Func callback) {
		connect(signalName, callback, null);
	}

	/**
	 * Connect a callback to a signal
	 * @param signalName the name of the signal to connect to
	 * @param callback a pointer to the Func to call whenever
	 * this signal is received
	 * @param data data to be passed as an argument to the callback
	 */
	func connect(String signalName, Func callback, GPointer data) {
		GULong id = gtk_signal_connect(GTK_OBJECT(this.getObject()), signalName, GTK_SIGNAL_FUNC(signalWrapper), new SignalPacket(this, data, callback));
	}
	
	func connectNaked(String signalName, Func callback) {
		GULong id = gtk_signal_connect(GTK_OBJECT(this.getObject()), signalName, GTK_SIGNAL_FUNC(callback), null);
	}
	
	func connectNaked(String signalName, Func callback, GPointer data) {
		GULong id = gtk_signal_connect(GTK_OBJECT(this.getObject()), signalName, GTK_SIGNAL_FUNC(callback), data);
	}
	
	func emitByName(String signalName) {
		gtk_signal_emit_by_name(GTK_OBJECT(this.getObject()), signalName);
	}
	
	/**
	 * Add a reference to this object, thus preventing its destruction
	 */
	func ref {
		gtk_object_ref(getObject());
	}
	
	/**
	 * Release a reference from this object
	 */
	func unref {
		gtk_object_unref(getObject());
	}

	/**
	 * 'Take ownership' of this object, ie. remove its initial reference
	 */
	func sink {
		gtk_object_sink(getObject());
	}
	
	/**
	 * Sets a property on an object.
	 * @param propertyName the name of the property to set
	 * @param value the value 
	 */
	func setProperty(String name, GValue* value) {
		g_object_set_property(this.getObject(), name, value);
	}

}
