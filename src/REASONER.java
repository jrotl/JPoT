import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


public class REASONER {
	private static OWLReasoner _reasoner;

	public REASONER() {};

    public REASONER(OWLOntology ontology)  { 
      _reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );      
   }

    public OWLReasoner getReasoner() {
		return _reasoner;
	}     

    public boolean isConsistent() {
		return _reasoner.isConsistent();
	}  
}    
