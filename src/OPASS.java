import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.*;
import org.semanticweb.owlapi.reasoner.*;

public class OPASS {  

	private static OWLObjectProperty _oproperty;
	
	public OPASS() {};

    public OPASS(OWLOntology tbox, OWLOntology abox,  OWLReasoner reasoner, OWLOntologyManager manager, OWLDataFactory dataFactory, Set<Set<OWLClass>> npair, int individuals, int nbopass) { 
		
		Set Properties = tbox.getObjectPropertiesInSignature();
		int sizeprop = Properties.size();
		int inc2 = nbopass;
		Random rand = new Random();
		PrefixManager pm = new DefaultPrefixManager();
		
		IRI irithing = IRI.create("http://www.w3.org/2002/07/owl#Thing");
        OWLClass thing = dataFactory.getOWLClass(irithing);
		IRI irinot = IRI.create("http://www.w3.org/2002/07/owl#Nothing");
		OWLClass nothing = dataFactory.getOWLClass(irinot);     
        
		while (inc2 > 0) {	

			boolean restart = true;
			int indiv1 = 0;
			int indiv2 = 0;
			
			OWLObjectProperty selectedproperty = null;
			while (restart) {

				restart = false;					
				int indiceprop =rand.nextInt(sizeprop);
				selectedproperty = (OWLObjectProperty)Properties.toArray()[indiceprop];
				
				DOMRAN domaran = new DOMRAN(tbox, reasoner, dataFactory, (OWLProperty)selectedproperty);

				int indicedomrange = rand.nextInt(domaran.getdamran().size());

				Vector propositiondomran = (Vector)domaran.getdamran().toArray()[indicedomrange];

				Set<OWLClass> setofdomain = reasoner.getObjectPropertyDomains(selectedproperty, true).getFlattened();
				OWLObject firstsetofdoamin = (OWLObject)setofdomain.toArray()[0];

				if (firstsetofdoamin.isTopEntity()) {
					OWLClass newdomain = (OWLClass)propositiondomran.firstElement();
					setofdomain.remove(thing);
					setofdomain.add(newdomain); 
				}		

				Set<OWLClass> setofrange = reasoner.getObjectPropertyRanges(selectedproperty, true).getFlattened();
										
				OWLObject firstsetofrange = (OWLObject)setofrange.toArray()[0];
				if (firstsetofrange.isTopEntity()) {
					OWLClass newrange = (OWLClass)propositiondomran.lastElement();
					setofrange.remove(thing);
					setofrange.add(newrange); 
				}

				boolean checkboolrange = false;
				boolean checkbooldomain = false;

				if (setofdomain.size() == 0) {
					do { indiv1 =rand.nextInt(individuals)+1;}
					while (EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),abox).isEmpty());
					checkbooldomain=true;
				}
				
				while (!checkbooldomain) {
					do { indiv1 =rand.nextInt(individuals)+1;}
					while (EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),abox).isEmpty());
					Iterator itdomain = setofdomain.iterator();
					while (itdomain.hasNext()) { 
						OWLClass selecteddomain = (OWLClass)itdomain.next();
						checkbooldomain = checkassertion(selecteddomain, EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),abox),npair);		
						if (checkbooldomain == false) {checkbooldomain=false;break;}
					}
				}
				
				Set<OWLClass> setofimperativeclassofrange = new HashSet<OWLClass>();
				for (OWLClassExpression i:EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),abox)) {
					for (Vector<Object> v:domaran.getdamran4all()) {
						Set<OWLClass> subclassofdom = reasoner.getSubClasses((OWLClass)v.firstElement(), false).getFlattened();
						subclassofdom.remove(nothing);
						subclassofdom.add((OWLClass)v.firstElement());				
						if (subclassofdom.contains((OWLClass)i)) {
							setofimperativeclassofrange.add((OWLClass)v.lastElement());
						}				
					}
				}
							
				Set<Vector<Object>> setofpairsmaxrange = new HashSet<Vector<Object>>();
				for (OWLClassExpression i:EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),abox)) {
					for (Vector<Object> v:domaran.getdamrancard()) {
						Set<OWLClass> subclassofdom = reasoner.getSubClasses((OWLClass)v.firstElement(), false).getFlattened();
						subclassofdom.remove(nothing);
						subclassofdom.add((OWLClass)v.firstElement());				
						if (subclassofdom.contains((OWLClass)i)) {
							Vector<Object> pair = new Vector<Object>(2);						
							pair.add(0,(OWLClass)v.toArray()[1]);
							pair.add(1,(Integer)v.lastElement());
							setofpairsmaxrange.add(pair);				
						}
					}
				}			
				
				if (setofrange.size() == 0) {
					do { indiv2 =rand.nextInt(individuals)+1;}
					while (EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv2, pm),abox).isEmpty());				
					checkboolrange=true;
				}				
				
				while (!checkboolrange) {														
					do { indiv2 =rand.nextInt(individuals)+1;}
					while (EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv2, pm),abox).isEmpty());

					Iterator itrange = setofrange.iterator();
					while (itrange.hasNext()) { 
						OWLClass selectedrange = (OWLClass)itrange.next();
						checkboolrange = checkassertion(selectedrange, EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv2, pm),abox),npair);		
						if (checkboolrange == false) {checkboolrange=false;break;}
					}
				
					for (OWLClass absoluterange: setofimperativeclassofrange) {
						Set<OWLClass> sub = reasoner.getSubClasses(absoluterange, false).getFlattened();
						sub.add(absoluterange);
						if (!EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv2, pm),abox).removeAll(sub)) {
							checkboolrange = false;			
							break;
						}	
					}

					if (checkboolrange) {
						for (Vector<Object> pair:setofpairsmaxrange) {
							int cardinality = 0;
							Set<OWLClass> sub = reasoner.getSubClasses((OWLClass)pair.firstElement(), false).getFlattened();
							sub.add((OWLClass)pair.firstElement());
							if (EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv2, pm),abox).removeAll(sub)) {
								if  (EntitySearcher.hasObjectPropertyValues(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),selectedproperty,abox)) {											
									Iterator itin =  EntitySearcher.getObjectPropertyValues(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),selectedproperty,abox).iterator();
									while (itin.hasNext()) {
										OWLIndividual indlinked = (OWLIndividual)itin.next();
										if (EntitySearcher.getTypes(indlinked,abox).removeAll(sub)) {
											cardinality++;
										}						
									}
								}					
							}

							if (cardinality == ((Integer)pair.lastElement())) {
								restart=true;break;
							}
						}			
					}			
				}
			}
					
			OWLNamedIndividual selectedindiv1 = dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm);
			OWLNamedIndividual selectedindiv2 = dataFactory.getOWLNamedIndividual("#ind"+indiv2, pm);
			OWLObjectPropertyAssertionAxiom propertyAssertion = dataFactory.getOWLObjectPropertyAssertionAxiom(selectedproperty, selectedindiv1, selectedindiv2);
			manager.addAxiom(abox, propertyAssertion);
			inc2--;
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
	
}

