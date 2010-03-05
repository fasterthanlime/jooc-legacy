import math/Random
import structs/ArrayList

main: func {
    ("Simple Random-number: " + Random random() toString()) println()
    ("Random Integer between 0 and 20: " + Random randRange(0, 20) toString()) println()
    ("Random Integer bewteen 0 and 5 (including 5!!): " + Random randInt(0, 5) toString()) println()
    artists := ArrayList<String> new(5)
    artists add("Meshuggah").add("Die Aerzte").add("Kiemsa").add("Raised Fist").add("Rammstein")
    ("Todays favourite artist is.... " + Random choice(artists)) println()
}
