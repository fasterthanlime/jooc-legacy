Class: abstract class {
	
	/// Number of bytes to allocate for a new instance of this class 
	size: SizeT

	/// Human readable representation of the name of this class
	name: String
	
	/// Pointer to instance of super-class
	super: const Class
	
	/// Create a new instance of the object of type defined by this class
	alloc: final func -> Object {
		object := gc_malloc(this size) as Object
		if(object) {
			object class = this
			object __defaults__()
		}
		return object
	}
	
	instanceof: final func (T: Class) -> Bool {
		if(this == T) return true
		if(super != null) return super instanceof(T)
		return false
	}
	
	// workaround needed to avoid C circular dependency with _ObjectClass
	__defaults__: static Func (Class)
	__destroy__: static Func (Class)
	
}

Object: abstract class {

	class: Class
	
	/// Instance initializer: set default values for a new instance of this class
	__defaults__: func {}
	
	/// Finalizer: cleans up any objects belonging to this instance
	__destroy__: func {}
	
}
