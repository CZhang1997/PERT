# Program Evaluation and Review Technique
# Graph Algorithm
# Java

#input 
First number - number of nodes (tasks)
Second number - number of edges (task finish can go to the next task if no other task before that task need to be complete.

then add edges (from one to inifity) 
3 numbers 
first number is the task need to finish first
Second number is the task can start after previous one finish
third number was not use in this algorithm, just use 1 as place holder

then add duration of each task (from one to inifity) 
how many node is in the graph should have how many durations.

example: 
"10 13   1 2 1   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1";
This string shows 10 nodes, 13 edges, and 10 durations

#Output
u - the node from a graph. the task
Dur - the time it need to complete the task
EC - earliest time to complete this task
LC - Lastest time to complete this task
Slack - the time they can rest (LC - EC)
Critical - a task is Critical if the task have to be finish on the earliest time, otherwise the project will be delay.
