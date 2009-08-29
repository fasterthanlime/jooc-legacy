Class: abstract class {
	
	/// Number of bytes to allocate for a new instance of this class 
	size: SizeT

	/// Human readable representation of the name of this class
    name: String
	
	/// Pointer to instance of super-class
	super: const Class
	
	/// Initializer: set default values for a new instance of this class
	initialize: Func (Object)
	
	/// Finalizer: cleans up any objects belonging to this instance
    destroy: Func (Object)
	
	/// Create a new instance of the object of type defined by this class
	newInstance: final func -> Object {
		object := gc_malloc(size) as Object
		if(object) {
			object class = this
			initialize(object)
		}
		return object
	}
	
}

Object: class {

	class: const Class
	
}
