import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

public class CASS {  
	private static int _size;

	private Set _classes;
	
	public CASS() {};

    public CASS(OWLOntology tbox, OWLOntology abox, OWLReasoner reasoner, OWLOntologyManager manager, OWLDataFactory dataFactory, Set<Set<OWLClass>> npair, int individuals, int nbcass) { 
				
		_classes = tbox.getClassesInSignature();
		_size = _classes.size();	
		
		Random rand = new Random();
		PrefixManager pm = new DefaultPrefixManager();

		int inc = nbcass;
		
		while (inc > 0) {

			int indiceclass = 0;
			int indiv =rand.nextInt(individuals)+1;
			Collection <OWLClassExpression> set = EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv, pm), abox);

			if (set.size() > 0) {
				boolean check = false;
				while (!check) {
					indiceclass = drawClass();		
					OWLClassExpression newclass = (OWLClass)_classes.toArray()[indiceclass];
					check = checkassertion(newclass, EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv, pm),abox), npair);		
				}
			}
						
			else {				
				indiceclass = drawClass();	
			}					

			OWLClass selectedclass = (OWLClass)_classes.toArray()[indiceclass];
			OWLNamedIndividual selectedindiv = dataFactory.getOWLNamedIndividual("#ind"+indiv, pm);
			OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(selectedclass, selectedindiv);        
			manager.addAxiom(abox, classAssertion);
			inc--;			
		}
    }
    
	public static boolean checkassertion(OWLClassExpression a, Collection<OWLClassExpression> b, Set<Set<OWLClass>> c) {
		Collection<OWLClassExpression> newset = b;
		newset.add(a);
		boolean bool = false;
		for (Set d:c) {
			if (b.containsAll(d)) {bool = true;}
		}
		return !bool;
	}
	
	private int drawClass() {
		int indiceclass = 0;	
		Random rand = new Random();
		indiceclass = rand.nextInt(_size);
		return indiceclass;
	}	
}
