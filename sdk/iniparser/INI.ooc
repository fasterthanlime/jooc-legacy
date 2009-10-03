import iniparser/iniparser, iniparser/dictionary
import io/File

fclose: extern func(FILE*)
INI: class {

    fileName: String
    dict: DictPtr
    section = null: String

    init: func(=fileName) {
        dict = iniparser_load(fileName)
    }

    dumpINI: func ~explicitFile(file: String) {
        fptr := fopen(file, "w")
        iniparser_dump_ini(dict, fptr)
        fclose(fptr)
    }
    
    dumpINI: func() {
        fptr := fopen(fileName, "w")
        iniparser_dump_ini(dict, fptr)
        fclose(fptr)
    }

    dump: func ~explicitFile(fptr: FILE*) {
        iniparser_dump(dict, fptr)
    }

    dump: func() {
        fptr := fopen(fileName, "w")
        iniparser_dump(dict, fptr)
        fclose(fptr)
    }

    setCurrentSection: func(=section) {}

    getEntry: func<T> (key: String, def: T) -> T {
        result :T
        searchString: String
        if (section != null) {
            searchString = (section append(":")) append(key)

        } else {
            "ERROR: No section chosen" println() // TODO: add `verbose` mode
            searchString = key
        }
        
        if (T == String class) {
            result = iniparser_getstring(dict, searchString, def)
        } else if(T == Bool class) {
            tmp := iniparser_getboolean(dict, searchString, def)
            if (tmp) { // TRUE
                result = true
            } else {
                result = false
            }
        }
        return result

    }
        
            
}


 


