import os/Process

main: func -> Int {
   
    b := SubProcess new(["/bin/ls",".", null])
    return b execute()
    
}
