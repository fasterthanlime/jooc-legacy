Class: abstract class {
	
	/// Number of octets to allocate for a new instance of this class 
	instanceSize: SizeT
	
	/// Number of octets to allocate to hold an instance of this class
	/// it's different because for classes, instanceSize may greatly
	/// vary, but size will always be equal to the size of a Pointer.
	/// for basic types (e.g. Int, Char, Pointer), size == instanceSize
	size: SizeT

	/// Human readable representation of the name of this class
	name: String
	
	/// Pointer to instance of super-class
	super: const Class
	
	/// Create a new instance of the object of type defined by this class
	alloc: final func -> Object {
		object := gc_malloc(instanceSize) as Object
		if(object) {
			object class = this
			object __defaults__()
		}
		return object
	}
	
	instanceof: final func (T: Class) -> Bool {
		if(this == T) return true
		if(super != null) return super class instanceof(T)
		return false
	}
	
	// workaround needed to avoid C circular dependency with _ObjectClass
	__defaults__: static Func (Class)
	__destroy__: static Func (Class)
	__load__: static Func
	
}

Object: abstract class {

	class: Class
	
	/// Instance initializer: set default values for a new instance of this class
	__defaults__: func {}
	
	/// Finalizer: cleans up any objects belonging to this instance
	__destroy__: func {}
	
}
