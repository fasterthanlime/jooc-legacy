/**
* Regexp system to get matches, test patterns... and cover String type to add methods like "match", "split" or "replace"
* @author Patrice Ferlet <metal3d@gmail.com>
*/

import structs.ArrayList;
import text.POSIXRegexp;
import text.PCRE;

abstract class RegexpInterface {
    abstract func setPattern(String pattern)-> Bool;
    abstract func match(String subject) -> ArrayList;
}


class Regexp {

    static const Int PCRE = 0;
    static const Int POSIX = 1;

    static Int DEFAULT_TYPE = 1;

    String pattern;
    PRegExp preg;

    /**
    * Create Regexp by engine type
    * @param
    */
    static func get() -> RegexpInterface{
        Int type = Regexp.DEFAULT_TYPE;
        //for now, const Int are not supported... 
        if(type == Regexp.PCRE )
                return new PCRE;
        //default...
        return new POSIXRegexp;
    }

    /**
    * set regexp engine
    * @param Int Engine
    */
    static func setEngine (Int engine){
        Regexp.DEFAULT_TYPE = engine;
    }

}


/**
* Append "match" "replace" and "split" to String type
*/
cover String {
    func match(String pattern) -> ArrayList {
        RegexpInterface re = Regexp.get();
        re.setPattern(pattern);

        return re.match(this);
    }

}
