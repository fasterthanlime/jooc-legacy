StringTokenizer: class extends Iterable<String> {
	input: String
	delim: String
	
	index: Int
	
	init: func(=input, =delim) { 
		index = 0
	}
	
	iterator: func() -> Iterator<T> {
		return StringTokenizerIterator new(this)
	}
	
	hasMoreTokens: func() -> Bool {
		return index < input length()
	}
	
	nextToken: func() -> String {
		oldIndex := index
		index = input indexOf(delim)
		
		return input substring(oldIndex, index)
	}
}

StringTokenizerIterator: class<T> extends Iterator<T> {

	st: StringTokenizer
	index := 0
	
	init: func(=st) {}
	
	hasNext: func -> Bool {
		return st hasMoreTokens()
	}
	
	next: func -> T {
		return st nextToken()
	}
	
}