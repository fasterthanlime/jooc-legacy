import iniparser/dictionary
include iniparser
use iniparser/iniparser

DictPtr: cover from dictionary*

iniparser_getnsec: extern func(d: DictPtr) -> Int
iniparser_getsecname: extern func(d: DictPtr, n: Int) -> String
iniparser_dump_ini: extern func(d: DictPtr, f: FILE*)
iniparser_dump: extern func(d: DictPtr, f: FILE*)
iniparser_getstring: extern func(d: DictPtr, key: const String, def: String) -> String
iniparser_getint: extern func(d: DictPtr, key: const String, notfound: Int) -> Int
iniparser_getdouble: extern func(d: DictPtr, key: String, notfound: Double) -> Double
iniparser_getboolean: extern func(d: DictPtr, key: const String, notfound: Int) -> Int
iniparser_setstring: extern func(ini: DictPtr, entry: String, val: Char) -> Int
iniparser_unset: extern func(ini: DictPtr, entry: String)
iniparser_find_entry: extern func(ini: DictPtr, entry: String) -> Int
iniparser_load: extern func(ininame: const String) -> DictPtr
iniparser_freedict: extern func(d: DictPtr)

