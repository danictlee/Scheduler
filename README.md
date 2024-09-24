# scheduling

This is a Scheduler implementation on Java.

It works based on a number of factors. Each process has its tempoTotalCPU (totalTimeCPU), which is how much time they need @ the CPU to finish.
Some have surtoCPU, which is the amount of time they can be executed @ the CPU before their I/O operations start and the Process becomes blocked.

To choose which Process is going in, the Scheduler chooses the one with the most credits, and if there is a tie, the Order atribute of the Procesess becomes the decider.
Everytime a Process finishes It's CPU execution, It becomes the last position in the order. 
