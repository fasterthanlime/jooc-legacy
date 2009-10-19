import lang/[String, Exception]
include stdio

println: func ~withStr (str: String) {
	printf("%s\n", str)
}
println: func {
	printf("\n")
}

// input/output
printf: extern func (String, ...)
fprintf: extern func (FStream, String, ...)
sprintf: extern func (String, String, ...)
snprintf: extern func (String, Int, String, ...)

vprintf: extern func (String, VaList)
vfprintf: extern func (FStream, String, VaList)
vsprintf: extern func (String, String, VaList)
vsnprintf: extern func (String, Int, String, VaList)

fread: extern func (ptr: Pointer, size: SizeT, nmemb: SizeT, stream: FStream) -> SizeT
fwrite: extern func (ptr: Pointer, size: SizeT, nmemb: SizeT, stream: FStream) -> SizeT

fopen: extern func (String, String) -> FStream
fclose: extern func (FStream) -> Int

fputc: extern func (Char, FStream)
fputs: extern func (String, FStream)

scanf: extern func (format: String, ...)
fscanf: extern func (stream: FStream, format: String, ...)
sscanf: extern func (str: String, format: String, ...)

vscanf: extern func (format: String, ap: VaList)
vfscanf: extern func (stream: FStream, format: String, ap: VaList)
vsscanf: extern func (str: String, format: String, ap: VaList)

fgets: extern func (str: String, length: SizeT, stream: FStream)

FILE: extern cover
FStream: cover from FILE* {
	
	// TODO encodings
	readChar: func -> Char {
		c : Char
		fread(c&, 1, 1, this)
		return c
	}
	
	readLine: func -> String {
		// 128 is a reasonable default. Most terminals are 80x25
		chunk := 128
		length := chunk
		pos := 0
		str := gc_malloc(length) as String
		
		fgets(str, chunk, this)
		
		// while it's not '\n' it means not all the line has been read
		while(str last() != '\n') {
			// now insert the rest of the line in str
			pos += chunk - 1 // -1 cause we want to insert the rest before the '\0'
			
			// try to grow the string
			length += chunk
			tmp := gc_realloc(str, length)
			if(!tmp) Exception new(This, "Ran out of memory while reading a (apparently never-ending) line!")
			str = tmp

			// we cast as Char* to avoid operator overloading
			// TODO encodings
			fgets(str as Char* + pos, chunk, this)
		}
		
		return str
	}
	
	write: func ~chr (chr: Char) {
		fputc(chr, this)
	}
	
	write: func (str: String) {
		fputs(str, this)
	}
	
	write: func ~precise (str: Char*, offset: SizeT, length: SizeT) {
		// TODO encodings
		fwrite(str + offset, 1, length, this)
	}
	
}

stdout, stderr, stdin : extern FStream


fflush: extern func (stream: FStream)
