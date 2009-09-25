import structs/Array
import text/StringBuffer

TypeInfo: cover {
	
	clazz: Class
	params: Array<This>
	
	new: static func (.clazz) -> This {
		this : This
		this clazz = clazz
		this params = null
		return this
	}
	
	toString: func -> String {
		sb := StringBuffer new()
		sb append(clazz name)
		if(params) {
			sb append('<')
			isFirst := true
			for(param : This in params) {
				sb append(param toString())
				if(!isFirst) {
					sb append(", ")
				} else {
					isFirst = false
				}
			}
			sb append('>')
		}
		return sb toString()
	}
	
}
