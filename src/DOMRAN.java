import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.search.*;

public class DOMRAN {  

	
	private static Set<Vector<Object>> _domainrangecouples = new HashSet<Vector<Object>>();
	private static Set<Vector<Object>> _domainrangecouplesforall = new HashSet<Vector<Object>>();  
	private static Set<Vector<Object>> _domainrangecardinalitymax = new HashSet<Vector<Object>>();

	private static Set<Vector<Object>> _domainrangecouples4data = new HashSet<Vector<Object>>();
	private static Set<Vector<Object>> _domainrangecouplesforall4data = new HashSet<Vector<Object>>();  
	private static Set<Vector<Object>> _domainrangecardinalitymax4data = new HashSet<Vector<Object>>();
	
	public DOMRAN() {};

    public DOMRAN(OWLOntology tbox, OWLReasoner reasoner, OWLDataFactory dataFactory, OWLProperty oproperty) { 

		_domainrangecouples.clear();
		_domainrangecouplesforall.clear();
		_domainrangecardinalitymax.clear();
		_domainrangecouples4data.clear();

		Random rand = new Random();
        Iterator itpropertyaxioms = EntitySearcher.getReferencingAxioms(oproperty,tbox).iterator();

        // Class thing
		IRI irithing = IRI.create("http://www.w3.org/2002/07/owl#Thing");
        OWLClass thing = dataFactory.getOWLClass(irithing);
 
        // IRI thing
		IRI iriliteral = IRI.create("http://www.w3.org/2000/01/rdf-schema#Literal");
		OWLDatatype literal =  dataFactory.getOWLDatatype(iriliteral);  
		        
		while (itpropertyaxioms.hasNext()) {

			OWLAxiom ax = (OWLAxiom)itpropertyaxioms.next();

			if (ax.getAxiomType().getName()  == "SubClassOf") {

				OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom)ax;

				if (subax.getSuperClass().getClassExpressionType().toString()  == "ObjectSomeValuesFrom") {			
					Vector<Object> drc = new Vector <Object> ();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getClassesInSignature().toArray()[0]);				
					_domainrangecouples.add(drc);
				}

				if (subax.getSuperClass().getClassExpressionType().toString()  == "DataSomeValuesFrom") {			
					Vector<Object> drc = new Vector <Object> ();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getDatatypesInSignature().toArray()[0]);				
					_domainrangecouples4data.add(drc);
				}

				if (subax.getSuperClass().getClassExpressionType().toString()  == "ObjectAllValuesFrom") {			
					Vector<Object> drc = new Vector <Object> ();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getClassesInSignature().toArray()[0]);
					_domainrangecouples.add(drc);
					_domainrangecouplesforall.add(drc);
				}

				if (subax.getSuperClass().getClassExpressionType().toString()  == "DataAllValuesFrom") {			
					Vector<Object> drc = new Vector <Object> ();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getDatatypesInSignature().toArray()[0]);
					_domainrangecouples4data.add(drc);
					_domainrangecouplesforall4data.add(drc);
				}
				
				if (subax.getSuperClass().getClassExpressionType().toString()  == "ObjectMaxCardinality") {			
					Vector<Object> drc = new Vector <Object> ();				
					OWLObjectMaxCardinality maxcar =  (OWLObjectMaxCardinality)subax.getSuperClass();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getClassesInSignature().toArray()[0]);
					drc.add(maxcar.getCardinality());
					_domainrangecardinalitymax.add(drc);
				}

				if (subax.getSuperClass().getClassExpressionType().toString()  == "DataMaxCardinality") {			
					Vector<Object> drc = new Vector <Object> ();				
					OWLDataMaxCardinality maxcar =  (OWLDataMaxCardinality)subax.getSuperClass();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getDatatypesInSignature().toArray()[0]);
					drc.add(maxcar.getCardinality());
					_domainrangecardinalitymax4data.add(drc);
				}
						
				if (subax.getSuperClass().getClassExpressionType().toString()  == "ObjectExactCardinality") {			
					Vector<Object> drc = new Vector <Object> ();				
					OWLObjectExactCardinality excar =  (OWLObjectExactCardinality)subax.getSuperClass();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getClassesInSignature().toArray()[0]);
					drc.add(excar.getCardinality());
					_domainrangecardinalitymax.add(drc);
				}	

				if (subax.getSuperClass().getClassExpressionType().toString()  == "DataExactCardinality") {			
					Vector<Object> drc = new Vector <Object> ();					
					OWLDataExactCardinality excar =  (OWLDataExactCardinality)subax.getSuperClass();
					drc.add(subax.getSubClass());
					drc.add(subax.getSuperClass().getDatatypesInSignature().toArray()[0]);
					drc.add(excar.getCardinality());
					_domainrangecardinalitymax4data.add(drc);
				}
			
				if ((subax.getSuperClass().getClassExpressionType().toString()  == "ObjectIntersectionOf") || (subax.getSuperClass().getClassExpressionType().toString()  == "ObjectUnionOf")) {					
					Iterator itsome;
					if (subax.getSuperClass().getClassExpressionType().toString()  == "ObjectIntersectionOf") {itsome = subax.getSuperClass().asConjunctSet().iterator();} 
					else {itsome = subax.getSuperClass().asDisjunctSet().iterator();}
					
					while (itsome.hasNext()) { 
						OWLClassExpression axi2 = (OWLClassExpression)itsome.next();
						if (axi2.getClassExpressionType().toString()  == "ObjectSomeValuesFrom") {
							if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {									
								Vector<Object> drc = new Vector <Object> ();
								drc.add(subax.getSubClass());
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								_domainrangecouples.add(drc);
							}
						}

						if (axi2.getClassExpressionType().toString()  == "DataSomeValuesFrom") {
							if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {									
								Vector<Object> drc = new Vector <Object> ();
								drc.add(subax.getSubClass());
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								_domainrangecouples4data.add(drc);
							}
						}
											
						if (axi2.getClassExpressionType().toString()  == "ObjectAllValuesFrom") {
							if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {
								Vector<Object> drc = new Vector <Object> ();
								drc.add(subax.getSubClass());
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								_domainrangecouples.add(drc);
								_domainrangecouplesforall.add(drc);
							}
						}
						
						if (axi2.getClassExpressionType().toString()  == "DataAllValuesFrom") {
							if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {		
								Vector<Object> drc = new Vector <Object> ();
								drc.add(subax.getSubClass());
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								_domainrangecouples4data.add(drc);
								_domainrangecouplesforall4data.add(drc);										
							}
						}								
											
						if (axi2.getClassExpressionType().toString()  == "ObjectMaxCardinality") {	
							if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {		
								Vector<Object> drc = new Vector <Object> ();
								OWLObjectMaxCardinality maxcar =  (OWLObjectMaxCardinality)axi2;
								drc.add(subax.getSubClass());
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								drc.add(maxcar.getCardinality());
								_domainrangecardinalitymax.add(drc);
							}
						}
			
						if (axi2.getClassExpressionType().toString()  == "DataMaxCardinality") {	
							if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {		
								Vector<Object> drc = new Vector <Object> ();					
								OWLDataMaxCardinality maxcar =  (OWLDataMaxCardinality)axi2;
								drc.add(subax.getSubClass());
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								drc.add(maxcar.getCardinality());
								_domainrangecardinalitymax4data.add(drc);
							}
						}
																																																													
						//Exact cardinality restiction (NEW)			
						if (axi2.getClassExpressionType().toString()  == "ObjectExactCardinality") {	
							if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {		
								Vector<Object> drc = new Vector <Object> ();
								OWLObjectExactCardinality excar =  (OWLObjectExactCardinality)axi2;
								drc.add(subax.getSubClass());
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								drc.add(excar.getCardinality());
								_domainrangecardinalitymax.add(drc);
							}
						}


						//Exact cardinality restiction (NEW)			
						if (axi2.getClassExpressionType().toString()  == "DataExactCardinality") {	
							if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {		
								Vector<Object> drc = new Vector <Object> ();					
								OWLDataExactCardinality excar =  (OWLDataExactCardinality)axi2;
								drc.add(subax.getSubClass());
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								drc.add(excar.getCardinality());
								_domainrangecardinalitymax4data.add(drc);
							}
						}									
					}
				}
			}

			// Equivalence 
			if (ax.getAxiomType().getName()  == "EquivalentClasses") {

				OWLEquivalentClassesAxiom eqax = (OWLEquivalentClassesAxiom)ax;
				Iterator itinter = eqax.getClassExpressionsAsList().iterator();
				
				while (itinter.hasNext()) { 
				
					OWLClassExpression axi = (OWLClassExpression)itinter.next();
										
					// Only Existential quantification
					if (axi.getClassExpressionType().toString()  == "ObjectSomeValuesFrom") {			
						Vector<Object> drc = new Vector <Object> ();					
						drc.add(eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getClassesInSignature().toArray()[0]);				
						_domainrangecouples.add(drc);
					}

					// Only Existential quantification
					if (axi.getClassExpressionType().toString()  == "DataSomeValuesFrom") {			
						Vector<Object> drc = new Vector <Object> ();					
						drc.add(eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getDatatypesInSignature().toArray()[0]);				
						_domainrangecouples4data.add(drc);
					}

					// Only Universal quantification				
					if (axi.getClassExpressionType().toString()  == "ObjectAllValuesFrom") {			
						Vector<Object> drc = new Vector <Object> ();
						drc.add( eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getClassesInSignature().toArray()[0]);
						_domainrangecouples.add(drc);
						_domainrangecouplesforall.add(drc);
					}
					
					// Only Universal quantification				
					if (axi.getClassExpressionType().toString()  == "DataAllValuesFrom") {			
						Vector<Object> drc = new Vector <Object> ();
						drc.add( eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getDatatypesInSignature().toArray()[0]);
						_domainrangecouples.add(drc);
						_domainrangecouplesforall4data.add(drc);
					}					
					
					//Maximal cardinality restiction				
					if (axi.getClassExpressionType().toString()  == "ObjectMaxCardinality") {			
						Vector<Object> drc = new Vector <Object> ();						
						OWLObjectMaxCardinality maxcar =  (OWLObjectMaxCardinality)axi;
						drc.add(eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getClassesInSignature().toArray()[0]);
						drc.add(maxcar.getCardinality());
						_domainrangecardinalitymax.add(drc);
					}

					//Maximal cardinality restiction				
					if (axi.getClassExpressionType().toString()  == "DataMaxCardinality") {			
						Vector<Object> drc = new Vector <Object> ();						
						OWLDataMaxCardinality maxcar =  (OWLDataMaxCardinality)axi;
						drc.add(eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getDatatypesInSignature().toArray()[0]);
						drc.add(maxcar.getCardinality());
						_domainrangecardinalitymax4data.add(drc);
					}
					
					//Maximal cardinality restiction				
					if (axi.getClassExpressionType().toString()  == "ObjectExactCardinality") {			
						Vector<Object> drc = new Vector <Object> ();						
						OWLObjectExactCardinality excar =  (OWLObjectExactCardinality)axi;
						drc.add(eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getClassesInSignature().toArray()[0]);
						drc.add(excar.getCardinality());
						_domainrangecardinalitymax.add(drc);
					}	

					//Maximal cardinality restiction				
					if (axi.getClassExpressionType().toString()  == "DataExactCardinality") {			
						Vector<Object> drc = new Vector <Object> ();						
						OWLDataExactCardinality excar =  (OWLDataExactCardinality)axi;
						drc.add(eqax.getNamedClasses().toArray()[0]);
						drc.add(axi.getDatatypesInSignature().toArray()[0]);
						drc.add(excar.getCardinality());
						_domainrangecardinalitymax4data.add(drc);
					}	
										
				// Intersection 
					
				if ((axi.getClassExpressionType().toString()  == "ObjectIntersectionOf") || (axi.getClassExpressionType().toString()  == "ObjectUnionOf")) {	
					Iterator itsome;
					if (axi.getClassExpressionType().toString()  == "ObjectIntersectionOf") {itsome = axi.asConjunctSet().iterator();} 
					else {itsome = axi.asDisjunctSet().iterator();}
						while (itsome.hasNext()) { 
							OWLClassExpression axi2 = (OWLClassExpression)itsome.next();
							if (axi2.getClassExpressionType().toString()  == "ObjectSomeValuesFrom") {
								if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {
								Vector<Object> drc = new Vector <Object> ();
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								_domainrangecouples.add(drc);
								}
							}

							if (axi2.getClassExpressionType().toString()  == "DataSomeValuesFrom") {
								if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {
								Vector<Object> drc = new Vector <Object> ();
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								_domainrangecouples4data.add(drc);
								}
							}
					
							if (axi2.getClassExpressionType().toString()  == "ObjectAllValuesFrom") {								
								if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {
								Vector<Object> drc = new Vector <Object> ();
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								_domainrangecouples.add(drc);
								_domainrangecouplesforall.add(drc);
								}
							}

							if (axi2.getClassExpressionType().toString()  == "DataAllValuesFrom") {	
								if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {
								Vector<Object> drc = new Vector <Object> ();
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								_domainrangecouples4data.add(drc);
								_domainrangecouplesforall4data.add(drc);
								}
							}
														
							//Maximal cardinality restiction				
							if (axi2.getClassExpressionType().toString()  == "ObjectMaxCardinality") {			
								if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {								
								Vector<Object> drc = new Vector <Object> ();
								OWLObjectMaxCardinality maxcar =  (OWLObjectMaxCardinality)axi2;
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								drc.add(maxcar.getCardinality());
								_domainrangecardinalitymax.add(drc);
								}
							}

							//Maximal cardinality restiction				
							if (axi2.getClassExpressionType().toString()  == "DataMaxCardinality") {			
								if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {								
								Vector<Object> drc = new Vector <Object> ();
								OWLDataMaxCardinality maxcar =  (OWLDataMaxCardinality)axi2;
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								drc.add(maxcar.getCardinality());
								_domainrangecardinalitymax4data.add(drc);
								}
							}
							
							//Exact cardinality restiction				
							if (axi2.getClassExpressionType().toString()  == "ObjectExactCardinality") {			
								if (oproperty.equals(axi2.getObjectPropertiesInSignature().toArray()[0])) {								
								Vector<Object> drc = new Vector <Object> ();
								OWLObjectExactCardinality excar =  (OWLObjectExactCardinality)axi2;
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getClassesInSignature().toArray()[0]);
								drc.add(excar.getCardinality());
								_domainrangecardinalitymax.add(drc);
								}
							}
							
							//Exact cardinality restiction				
							if (axi2.getClassExpressionType().toString()  == "DataExactCardinality") {			
								if (oproperty.equals(axi2.getDataPropertiesInSignature().toArray()[0])) {								
								Vector<Object> drc = new Vector <Object> ();
								OWLDataExactCardinality excar =  (OWLDataExactCardinality)axi2;
								drc.add(eqax.getNamedClasses().toArray()[0]);
								drc.add(axi2.getDatatypesInSignature().toArray()[0]);
								drc.add(excar.getCardinality());
								_domainrangecardinalitymax4data.add(drc);
								}
							}							
						}	
					}	
				}
			}
		}

		if (_domainrangecouples.size() == 0) {
	        Vector<Object> drcinit = new Vector <Object> ();
			drcinit.add(thing);
			drcinit.add(thing);
			_domainrangecouples.add(drcinit);
		}

		if (_domainrangecouples4data.size() == 0) {
	        Vector<Object> drcinit = new Vector <Object> ();
			drcinit.add(thing);
			drcinit.add(literal);
			_domainrangecouples4data.add(drcinit);
		}
				
    }

    public Set<Vector<Object>> getdamran() {
		return _domainrangecouples;
	}    

    public Set<Vector<Object>> getdamran4all() {
		return _domainrangecouplesforall;
	}
	
	public Set<Vector<Object>> getdamrancard() {
		return _domainrangecardinalitymax;
	} 

    public Set<Vector<Object>> getdamran4data() {
		return _domainrangecouples4data;
	} 

    public Set<Vector<Object>> getdamran4all4data() {
		return _domainrangecouplesforall4data;
	}
	
	public Set<Vector<Object>> getdamrancard4data() {
		return _domainrangecardinalitymax4data;
	} 
}

