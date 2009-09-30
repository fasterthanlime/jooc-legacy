import structs/HashMap

Value: class <T> {
	
	value: T
	type: Class
	
	init: func (type: Class, value: T) {
		this value = value
		this type = type
	}
	
}

ValueMap: class extends HashMap {
	
	init: func ~value {
		T = Value
		super()
	}
	
	get: func ~casted <V> (key: String, V: Class) -> V {
		
		v1 := get(key)
		v2 := v1 as Value
		v3 := v2 value
		v4 := v3 as V
		return v4
		
		//return get(key) as Value value as V
		
	}
	
}

main: func {
	
	vm := ValueMap new()
	vm put("duh", Value new(Int, 42))
	
	answer := vm get("duh", Int)
	println("The answer is " + answer)
	
}
