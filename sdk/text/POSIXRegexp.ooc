/**
* Regexp system to get matches, test patterns...
* @author Patrice Ferlet <metal3d@gmail.com>
*/

import structs.ArrayList;

include sys/types;
include regex;
//some ctype we need
ctype regmatch_t;
ctype regex_t;
typedef regmatch_t *PRegMatch;
typedef size_t SizeType;
typedef regex_t PRegExp;



class POSIXRegexp from RegexpInterface {

    String pattern;
    PRegExp preg;    
    func setPattern(=pattern) -> Bool{
        //compile regexp only one time per class !
        if (regcomp (&preg, pattern, REG_EXTENDED) != 0){
            return false;
        }
        return true;
    };

    /**
    * Try to find matches from pattern in haystack to return niddles (yes, niddle*s*)
    *
    * @param String pattern
    * @param String haystack
    * @return ArrayList (String) niddles
    */
    func match (String haystack) -> ArrayList {
        Int matches;
        SizeType nmatch=0;
        PRegMatch pmatch;
        ArrayList ret = new;
        
        //prepare matches
        nmatch = preg.re_nsub;
        pmatch = malloc (sizeof (pmatch) * nmatch);
            
        //now, we will parse
        Int start =0;
        Int end = 0;
        if (pmatch) {
            while (regexec (&preg, haystack+end, nmatch, pmatch, 0) == 0 ){
                //move pointer on sentence
                start = pmatch->rm_so+end;
                end   = pmatch->rm_eo+end;

                //To allocate memory
                SizeType size = end - start;
                String niddle = malloc (sizeof(niddle) * (size + 1));

                //if we have size, we have something to insert
                if (niddle) {
                    strncpy (niddle, &haystack[start], size);
                    niddle[size] = '\0';
                    //append resulut into List
                    ret.add(niddle);
                }
            }
            //we can return ArrayList
            return ret;
        }
        else {
            //ho my god...
            fprintf (stderr, "Out of memory error on Regexp matching process\n");
            return false;
        }
        //error...
        return false;
    }
}
