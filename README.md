# JPoT
Just another Populator of TBoxes
- OVERVIEW:
   
	JPOT uses the following libraries:

	- OWLAPI DISTRIBUTION 4.1.0 or above, released under Open Source licenses (LGPL and Apache). 
	The OWL API is a Java API for creating, manipulating and serialising OWL Ontologies.
	
	- PELLET REASONER (for OWLAPI 4.0.* released by @ignazio1977). 
	Pellet is an OWL 2 reasoner in Java released under Open source (AGPL) and commercially licensed.
	
	- JaNaG (Java Name Generator released by @mkalus) released under GNU Library or Lesser General Public License version 3.0 (LGPLv3)
	JaNaG is a random name generator based on a name fragment database that creates relatively reasonably sounding names from different cultures/influences.  


- USAGE:

	The executables of JPOT (bundled shared libraries) are located in the './bin ' directory. 
	The sources of JPOT are located in the './src' directory. To use the library make sure that Java finds 
	owlapi-distribution-4.1.0.jar,pellet-2.4.0-ignazio1977/lib/pellet-owlapi-ignazio1977-2.4.0-ignazio1977.jar,lib/,pellet-2.4.0-ignazio1977/lib/ and JaNaG.jar in the class path.
	JPOT populates Tboxes using 4 parameters:
	
	-i: a number of (potential) individuals
	
	-x: a number assertions
	
	-c: a ratio of the number of concept assertions on the total number of assertions
	
	-d: a ratio of the number of role assertions on the number of data assertions

	For example:
	
		java -classpath .:owlapi-distribution-4.1.0.jar:pellet-2.4.0-ignazio1977/lib/pellet-owlapi-ignazio1977-2.4.0-ignazio1977.jar:lib/*:pellet-2.4.0-ignazio1977/lib/*:JaNaG.jar:. JPOT
		-i 1000000
		-x 1000000
		-c 0.5
		-d 0.5
		
- REQUIREMENTS and INSTALLATION:

	JPOT requires OWLAPI DISTRIBUTION 4.1.0 [1] and PELLET 2.4.0 [2] and JANAG [3]. If they are not included in the release, then you should install them manually on your system. 
		

- CHANGELOG:

	JPOT version Spring 2017:

[1] http://owlapi.sourceforge.net/
[1] http://clarkparsia.com/pellet
[3] https://www.beimax.de/janag/
