/**
* PCRE Regular expression system to get matches, test patterns...
* @author Patrice Ferlet <metal3d@gmail.com>
*/

use pcre;
include pcre, string, stdio, stdlib;
import structs.ArrayList;
typedef pcre *Pcre_reg;

/**
 * PReg is PCRE Regular expression class
 */
class PReg {

    Pcre_reg reg;
    String error;
    Int errnum;
    

    func new(String pattern) {
        reg = pcre_compile (pattern, 0, &error, &errnum, null);
        //todo, check errors
    }

    func match (String haystack) -> ArrayList{
        Int start = 0;
        Int end   = 0;
        Int offsets[3];
        //offsets = malloc (30 * sizeof(offsets));
        Int len = strlen(haystack);
        Int size;
        String needle;
        ArrayList toReturn = new;

        /*Int ret = pcre_exec (reg, null, haystack, len, start, 0, offsets, 30 );
        printf("%d found\n", ret);

        for (Int i: 0..ret){
            printf("Found %d!\n",i);
            start = offsets[i];
            end = offsets[i+1];
            
            size = end - start;
            needle = malloc (sizeof(needle) * size +1 );
            strncpy(needle, &haystack[start],size);
            needle[size] = '\0';
            printf ("needle : %s\n", needle);
            toReturn.add(needle);
        }*/

        while (pcre_exec (reg, null, haystack, len, start, 0, offsets, 30 ) >= 0 ) {
            start = offsets[0];
            end = offsets[1];
            size = end - start;
            needle = malloc (sizeof(needle) * size +1 );
            strncpy(needle, &haystack[start],size);
            needle[size] = '\0';
            toReturn.add(needle);
            start += end;
        }
        return toReturn;
    }

}
