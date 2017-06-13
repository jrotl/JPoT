import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

public class DISJ {  

	private static Set<Set<OWLClass>> _setOfComplement;
	private static Set<Set<OWLClass>> _pairs;

	public DISJ() {};

    public DISJ(OWLOntology TBox, OWLReasoner reasoner, OWLDataFactory dataFactory) { 

		_setOfComplement = new HashSet<Set<OWLClass>>();
		OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(TBox));
	
		OWLOntologyWalkerVisitorEx<Object> visitor = new OWLOntologyWalkerVisitorEx<Object>(walker) {
			@Override
			public Object visit(OWLDisjointClassesAxiom desc) {
				_setOfComplement.add(getCurrentAxiom().getClassesInSignature());
				return null;
			}
			
			@Override
			public Object visit(OWLObjectComplementOf desc) {
				_setOfComplement.add(getCurrentAxiom().getClassesInSignature());
				return null;
			}
			

		};

		walker.walkStructure(visitor);

		_pairs = new HashSet<Set<OWLClass>>();
		IRI irinot = IRI.create("http://www.w3.org/2002/07/owl#Nothing");
		OWLClass _nothing = dataFactory.getOWLClass(irinot);
		
		
		for (Set i:_setOfComplement) {
			OWLClass a = (OWLClass)i.toArray()[0];
			OWLClass b = (OWLClass)i.toArray()[1];
			NodeSet<OWLClass> subClsesa = reasoner.getSubClasses(a, false);
			NodeSet<OWLClass> subClsesb = reasoner.getSubClasses(b, false);
			Set<OWLClass> subClsesOfa = subClsesa.getFlattened();
			subClsesOfa.remove(_nothing);
			subClsesOfa.add(a);
			Set<OWLClass> subClsesOfb = subClsesb.getFlattened();
			subClsesOfb.remove(_nothing);
			subClsesOfb.add(b);
								
			for (OWLClass j:subClsesOfa) {
				for (OWLClass k:subClsesOfb) {
					Set<OWLClass> pair = new HashSet<OWLClass>();
					pair.add(j);
					pair.add(k);
					_pairs.add(pair);
				}
			}
		}		
    }

    public Set<Set<OWLClass>> getDisj() {
		return _pairs;
	}    
}
