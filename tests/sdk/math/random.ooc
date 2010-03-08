import math/Random
import os/Time
import structs/ArrayList

main: func {
    ("Simple Random-number: " + Random random() toString()) println()
    ("Random Integer between 0 and 20: " + Random randRange(0, 20) toString()) println()
    ("Random Integer bewteen 0 and 5 (including 5!!): " + Random randInt(0, 5) toString()) println()
    
    artists := ArrayList<String> new(5)
    artists add("Meshuggah").add("Die Aerzte").add("Kiemsa").add("Raised Fist").add("Rammstein")
    ("Today's favourite artist is.... " + Random choice(artists)) println()

    exclude := ArrayList<Int> new(3)
    exclude add(0).add(1).add(2)
    ("Random Integer between 0 and 10 unequal `0, 1, 2`: " + Random randRange(0, 10, exclude)) println()

    for (i in 0..1000000) {
        Random fastRandInt(0, 100)
    }
    printf("Created 1,000,000 random numbers with `fastRand`.\n")  
    printf("%d (between 0 and 100 as an example)\n", Random fastRandRange(0, 100))
}
