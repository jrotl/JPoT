import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.owlapi.explanation.*;

import org.semanticweb.owlapi.reasoner.*;
public class JPOT {
	protected static String[] _tboxes;
	protected static String _abox;
	protected static int _individuals, _axioms, _nbcass, _nbopass, _nbdpass;
	protected static float  _rcp, _rod;
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException  { 
	
		if (!parseArgs(args)) {System.exit(1);}
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();   
		OWLDataFactory dataFactory = manager.getOWLDataFactory();    
		for (String tbox : _tboxes){
			File file = new File(tbox);
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);		
		}
		OWLOntologyMerger merger = new OWLOntologyMerger(manager);
	
		IRI IRIAbox = IRI.create("http://www.jpot.br/abox");
		OWLOntology Abox = merger.createMergedOntology(manager, IRIAbox);
		IRI IRITbox = IRI.create("http://www.jpot.br/tbox");
		OWLOntology Tbox = merger.createMergedOntology(manager, IRITbox);

		System.out.println("Loaded ontology: " + Abox);
		
		System.out.println("Population...");
		REASONER reasoner = new REASONER(Tbox);
		DISJ disj = new DISJ(Tbox,reasoner.getReasoner(),dataFactory);

		_nbcass = Math.round(_axioms * _rcp);
		CASS start = new CASS(Tbox, Abox, reasoner.getReasoner(), manager, dataFactory, disj.getDisj(),_individuals,_nbcass);
		
		_nbopass = Math.round((_axioms - _nbcass) * _rod);
		OPASS opass = new OPASS(Tbox, Abox, reasoner.getReasoner(),  manager, dataFactory, disj.getDisj(),_individuals,_nbopass);
	
		_nbdpass =  Math.round((_axioms - _nbcass) * (1 - _rod));
		DPASS dpass = new DPASS(Tbox, Abox, reasoner.getReasoner(),  manager, dataFactory, disj.getDisj(),_individuals,_nbdpass);
		

        File newfile = new File(_abox);
        manager.saveOntology(Abox, IRI.create(newfile.toURI()));
		
		REASONER reasoner2 = new REASONER(Abox);							
		//OWLReasoner pellet = PelletReasonerFactory.getInstance().createReasoner(Abox);
		boolean consistent = reasoner2.isConsistent();
		System.out.println("Tbox + ABox Consistent ?: " + consistent);					

	}

	public static boolean parseArgs(String[] args) {
        int i = 0;
        if (args.length == 0) {return false;}
        while (i < args.length) {

			if (args[i].equals("-tboxes") || args[i].equals("-t") || args[i].equals("-tbox")) {
				_tboxes = args[i + 1].split(",");;
                i += 2;
            }

            else if (args[i].equals("-abox") || args[i].equals("-a")) {
				_abox = args[i + 1];
                i += 2;
            }

            else if (args[i].equals("-individual") || args[i].equals("-i")) {
				_individuals = Integer.parseInt(args[i + 1]);
                i += 2;
            }
            
            else if (args[i].equals("-axioms") || args[i].equals("-x")) {
				_axioms = Integer.parseInt(args[i + 1]);
				i += 2;
            }
            
            else if (args[i].equals("-rcp") || args[i].equals("-c")) {
				_rcp = Float.parseFloat(args[i + 1]);
				i += 2;
            }                       
            else if (args[i].equals("-rod") || args[i].equals("-o")) {
				_rod = Float.parseFloat(args[i + 1]);
				i += 2;
            }
            
            else {
				System.out.println("There is one mistake in the command line.");
				return false;
            }
		}
				
        return true;
	}
	
}
