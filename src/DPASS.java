import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.*;
import org.semanticweb.owlapi.reasoner.*;

import de.beimax.janag.lists.*;
import de.beimax.janag.i18n.*;

public class DPASS {  
	private static OWLDataProperty _dproperty;
	
	public DPASS() {};

    public DPASS(OWLOntology tbox, OWLOntology abox,  OWLReasoner reasoner, OWLOntologyManager manager, OWLDataFactory dataFactory, Set<Set<OWLClass>> npair, int individuals, int nbdpass) { 

		Random r = new Random();
		
		Set Properties = tbox.getDataPropertiesInSignature();
		int sizeprop = Properties.size();
		int inc2 = nbdpass;
		Random rand = new Random();
		PrefixManager pm = new DefaultPrefixManager();

		IRI nameIRI = IRI.create("http://nemo.inf.ufes.br/obda/tboxes/akademos.owl#name");
		OWLDataProperty _name = dataFactory.getOWLDataProperty(nameIRI);
		IRI ageIRI = IRI.create("http://nemo.inf.ufes.br/linkedun.owl#age");
		OWLDataProperty _age = dataFactory.getOWLDataProperty(ageIRI);
		IRI citationsIRI = IRI.create("http://nemo.inf.ufes.br/linkedun.owl#citations");
		OWLDataProperty _citations = dataFactory.getOWLDataProperty(citationsIRI);
		IRI hindexIRI = IRI.create("http://nemo.inf.ufes.br/obda/tboxes/akademos.owl#hindex");
		OWLDataProperty _hindex = dataFactory.getOWLDataProperty(hindexIRI);
		IRI iriliteral = IRI.create("http://www.w3.org/2000/01/rdf-schema#Literal");
		OWLDatatype literal =  dataFactory.getOWLDatatype(iriliteral);   
							
		IRI irithing = IRI.create("http://www.w3.org/2002/07/owl#Thing");
        OWLClass thing = dataFactory.getOWLClass(irithing);
		IRI irinot = IRI.create("http://www.w3.org/2002/07/owl#Nothing");
		OWLClass nothing = dataFactory.getOWLClass(irinot);
		
        
		while (inc2 > 0) {

			boolean restart = true;
			int indiv1 = 0;
			OWLDatatype odt;
			OWLDataProperty selectedproperty = null;
			while (restart) {

			restart = false;		
			int indiceprop =rand.nextInt(sizeprop);
			selectedproperty = (OWLDataProperty)Properties.toArray()[indiceprop];
			
			DOMRAN domaran = new DOMRAN(tbox, reasoner, dataFactory, (OWLProperty)selectedproperty);
			int indicedomrange = rand.nextInt(domaran.getdamran4data().size());		
			Vector propositiondomran = (Vector)domaran.getdamran4data().toArray()[indicedomrange];
			
			Set<OWLClass> setofdomain = reasoner.getDataPropertyDomains(selectedproperty, true).getFlattened();
			OWLObject firstsetofdoamin = (OWLObject)setofdomain.toArray()[0];
						
			if (firstsetofdoamin.isTopEntity()) {
				OWLClass newdomain = (OWLClass)propositiondomran.firstElement();
				setofdomain.remove(thing);
				setofdomain.add(newdomain); 
			}

			Set<OWLDataRange> setofrange = new HashSet<OWLDataRange>();			
			setofrange.add((OWLDataRange)literal);
            Set <OWLDataPropertyRangeAxiom> sgdp = tbox.getDataPropertyRangeAxioms(selectedproperty);
            for (OWLDataPropertyRangeAxiom a : sgdp ) {
				setofrange.add(a.getRange());
			}

			OWLDatatype firstsetofrange = (OWLDatatype)setofrange.toArray()[0];
			if (firstsetofrange.isTopEntity()) {
				OWLDatatype newrange = (OWLDatatype)propositiondomran.lastElement();
				setofrange.remove(literal);
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
				
			Set<OWLDatatype> setofimperativeclassofrange = new HashSet<OWLDatatype>();
			for (OWLClassExpression i:EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),abox)) {
				for (Vector<Object> v:domaran.getdamran4all4data()) {
					Set<OWLClass> subclassofdom = reasoner.getSubClasses((OWLClass)v.firstElement(), false).getFlattened();
					subclassofdom.remove(nothing);
					subclassofdom.add((OWLClass)v.firstElement());				
					if (subclassofdom.contains((OWLClass)i)) {
						setofimperativeclassofrange.add((OWLDatatype)v.lastElement());
					}				
				}
			}

			Set<Vector<Object>> setofpairsmaxrange = new HashSet<Vector<Object>>();
			for (OWLClassExpression i:EntitySearcher.getTypes(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),abox)) {
				for (Vector<Object> v:domaran.getdamrancard4data()) {
					Set<OWLClass> subclassofdom = reasoner.getSubClasses((OWLClass)v.firstElement(), false).getFlattened();
					subclassofdom.remove(nothing);
					subclassofdom.add((OWLClass)v.firstElement());				
					if (subclassofdom.contains((OWLClass)i)) {
						Vector<Object> pair = new Vector<Object>(2);						
						pair.add(0,(OWLDatatype)v.toArray()[1]);
						pair.add(1,(Integer)v.lastElement());
						setofpairsmaxrange.add(pair);				
					}
				}
			}


			if (setofimperativeclassofrange.size() == 0) {setofimperativeclassofrange.add(literal);}
			OWLDatatype firstrange = (OWLDatatype)setofrange.toArray()[0];
			OWLDatatype firstimperativerange = (OWLDatatype)setofimperativeclassofrange.toArray()[0];

			if (firstrange.isTopEntity()) {odt = (OWLDatatype)setofimperativeclassofrange.toArray()[0];} else {odt = (OWLDatatype)setofrange.toArray()[0];}
															
				for (Vector<Object> pair:setofpairsmaxrange) {
					int cardinality = 0;
						if (odt == (OWLDatatype)pair.firstElement()) {
						if  (EntitySearcher.hasDataPropertyValues(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),selectedproperty,abox)) {											
							Iterator itin =  EntitySearcher.getDataPropertyValues(dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm),selectedproperty,abox).iterator();
							while (itin.hasNext()) {							
								OWLLiteral indlinked = (OWLLiteral)itin.next();
								if (indlinked.getDatatype().equals((OWLDatatype)pair.firstElement())) {
									cardinality++;
									OWLDatatype p = (OWLDatatype)pair.firstElement();
								}						
							}
						}					
					}
					if (cardinality == ((Integer)pair.lastElement())) {
						restart=true;break;
					}
				}
			}

			OWLNamedIndividual selectedindiv1 = dataFactory.getOWLNamedIndividual("#ind"+indiv1, pm);			

			if ((OWLDataProperty)selectedproperty ==  _citations) {
				int hindex = 0;								
				if  (EntitySearcher.hasDataPropertyValues(selectedindiv1,_hindex,abox)) {
					OWLLiteral owlit2 = (OWLLiteral)EntitySearcher.getDataPropertyValues(selectedindiv1, _hindex, abox).toArray()[0];
					hindex = owlit2.parseInteger();
				}
				
			int citations = (int) Math.round(r.nextGaussian()*300+ 600);
			do {		
				citations++;
			} while (citations <= hindex * hindex);
			
			OWLDataPropertyAssertionAxiom propertyAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(selectedproperty, selectedindiv1, dataFactory.getOWLLiteral(citations));
			manager.addAxiom(abox, propertyAssertion);			
			inc2--;				

		}
			
		if ((OWLDataProperty)selectedproperty ==  _hindex) {
			int citations = 0;								
			if  (EntitySearcher.hasDataPropertyValues(selectedindiv1,_citations,abox)) {
				OWLLiteral owlit2 = (OWLLiteral)EntitySearcher.getDataPropertyValues(selectedindiv1, _citations, abox).toArray()[0];
				citations = owlit2.parseInteger();
			}
			int hindex;
			do {
				hindex = (int) Math.round(r.nextGaussian()*12+ 6);
			} while (hindex <= 0);

			do {
				hindex--;
			} while ((citations < hindex * hindex && EntitySearcher.hasDataPropertyValues(selectedindiv1,_citations,abox)));
				
			OWLDataPropertyAssertionAxiom propertyAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(selectedproperty, selectedindiv1, dataFactory.getOWLLiteral(hindex));
			manager.addAxiom(abox, propertyAssertion);			
			inc2--;
		}
			
		if ((OWLDataProperty)selectedproperty ==  _age) {
			int age ;
			do {
				age = (int) Math.round(r.nextGaussian()*10+ 42);
				} while (age <= 20 || age >= 90);
				OWLDataPropertyAssertionAxiom propertyAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(selectedproperty, selectedindiv1, dataFactory.getOWLLiteral(age));
				manager.addAxiom(abox, propertyAssertion);			
				inc2--;
				}
															
			if ((OWLDataProperty)selectedproperty ==  _name) {
				boolean isintantiated = false;
				String name ="";
				while (!isintantiated) {	
					try {
						NameGenerator ng = new NameGenerator("languages.txt","semantics.txt");
						int i = rand.nextInt(ng.getPatterns().length);
						int j = rand.nextInt(ng.getGenders(ng.getPatterns()[i]).length);
						name = ng.getRandomName(ng.getPatterns()[i], ng.getGenders(ng.getPatterns()[i])[j], 1)[0];
					} catch (Exception e) {}
					if (!name.equals("")) {isintantiated = true;}
				}	
						
				OWLDataPropertyAssertionAxiom propertyAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(selectedproperty, selectedindiv1, dataFactory.getOWLLiteral(name));
				manager.addAxiom(abox, propertyAssertion);			
				inc2--;
			}				
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
