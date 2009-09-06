File: class {
	path: String
	separator = getSystemSeparator() : static const Char
	
	getSystemSeparator: static func -> Char {
		/*version(unix) {
			return '/'
		}
		version(windows) {
			return '\\'
		}*/
		
		return '/'
	}

	init: func(=path) { }
	
	isDir: func -> Bool {
		return false
	}
	
	isFile: func -> Bool {
		return false
	}
	
	isLink: func -> Bool {
		return false
	}
	
	stSize: func -> Int {
		return 0
	}
	
	exists: func -> Bool {
		return false
	}
	
	ownerPerm: func -> Int {
		return 0
	}
	
	groupPerm: func -> Int {
		return 0
	}
	
	otherPerm: func -> Int {
		return 0
	}
	
	name: func -> String {
		return ""
	}
}