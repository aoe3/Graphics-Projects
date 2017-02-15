Andrew Eccles (aoe3)
Daisy Zheng (dhz9)

To run:
go to src directory
in command line, run
	javac meshgen/MeshGen.java
	java meshgen.MeshGen -g <sphere|cylinder|torus> [-n <divisionsU>] [-m <divisionsV>] [-r <minorRadius>] -o <outfile.obj>
for usage 1 and
	java meshgen.MeshGen -i <infile.obj> -o <outfile.obj>
for usage 2.

Using 2 slip days.