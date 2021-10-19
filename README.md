# Traffic-Analysis

### Introduction:
This is a piece of software designed to accompany the paper "Intersection Based Traffic Simulation using Quantum Random Number Generation" produced by Dean Preece.

### Requirements:
For usage the following requirements must be obtained:
- Java 11 (version 11.0.11 or greater) ( <i><b>https://www.oracle.com/uk/java/technologies/javase/jdk11-archive-downloads.html </b></i>)
- A 64-bit Linux or Windows OS device

### Obtaining the files:

The files for the project can be obtained in 2 ways:
- Navigate to a folder and run the following command via git: <i><b> git clone https://github.com/craziii/Traffic-Analysis.git </b></i>
- Download the latest release or example package at <b><i> https://github.com/craziii/Traffic-Analysis/releases </i></b>

### How to use:
Using this software should be performed from the command line or via batch or shell files. The parameters of the software should be entered as shown below.

Once the software has been set to run it should be left without interruption until the simulation is completed, and the outputs have been logged.

### Parameters:
Required values in <b>bold</b>

Argument format: java -jar traffic-analysis.jar --option="value"

(-i),(--intersection), the chance between 0 and 1 for intersections to use. values: 0 - 1 inclusive

(-c),(--car), the chance between 0 and 1 for a car to spawn on each road. values: 0 - 1 inclusive

<b>(-m),(--map), the filename / path to the project mapfile. values: filename.extension</b>

(-l),(--log), whether to log all console outputs to the log.txt file or not. values: TRUE/FALSE

(-h),(--help), prints a helpful dialogue informing users of the available arguments and required values. values: N/A

<b>(-s),(--steps), Steps to be simulated by the program. values: Any whole number > 0</b>

(-o),(--output), output map to file in a rudimentary format once mapping has been completed. values: TRUE/FALSE

(-u),(--update), number of steps per information update. values: Any whole number > 0

(-v),(--verbose), enable verbose logging. values: TRUE/FALSE

(-a),(--assessment), enable the new method of traffic light assessment utilising the traffic pressure system. values: TRUE/FALSE

<b>(-x),(--max), set the maximum number of cycles each intersection must wait before changing lights. values: Any whole number > 0</b>

(-f),(--filename), set the output folder name for all system outputs. values: foldername

### Example usage:

example included in the example package:

java -jar Traffic-Analysis.jar -i="0.1" -c="0.2" -m="maps/ExampleMap.csv" -l=TRUE -s=20000 -o=TRUE -u=1000 -v=FALSE -a=TRUE -x=5 -f="ExampleTest-s20000-a1"

This will create a folder "/output/ExampleTest-s20000-a1" with the outputs of the simulation once completed.

### Mapfile Format:

Mapfiles are the necessary file that must be given to the simulator for it to be able to build a grid mapping successfully.

Each mapfile is a comma seperated value (.csv) file that is formatted as follows:

- Each comma (,) seperates intersections in a row
- Each new line (\n) seperates intersections in a column
- Each colon (:) seperates intersection outputs from each other
- Each dash (-) seperates an intersection output from the number of nodes in its associated road

Example:

n-25:e-15:s-10:w-25,n-10:e-10:s-10:w-15<br>
n-10:e-25:s-5,n-10:w-25

This will produce the following map:
![example mapping](https://raw.githubusercontent.com/craziii/Traffic-Analysis/master/example%20intersection.png)

### Testers:
- Nicolas Garron
- Joshua Gannon