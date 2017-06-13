import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.profiles.*;
import org.semanticweb.owlapi.model.OWLOntology;

public class PROFILE {
  
	public PROFILE() {};

    public PROFILE(OWLOntology ontology) { 


	// Check whether the profile is OWL2QL
	OWL2QLProfile profile = new OWL2QLProfile();
	OWLProfileReport report = profile.checkOntology(ontology);
	System.out.println("OWL2DL:" + report.isInProfile());
	System.getProperty("line.separator");

	for(OWLProfileViolation v:report.getViolations()) {
		System.out.println(v);
	}
   }
    

}
	
