package edu.cmu.lti.f13.hw4_chens1.annotators;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.internal.util.TextTokenizer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f13.hw4_chens1.VectorSpaceRetrieval;
import edu.cmu.lti.f13.hw4_chens1.typesystems.Document;
import edu.cmu.lti.f13.hw4_chens1.typesystems.Token;
import edu.cmu.lti.f13.hw4_chens1.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {
	HashSet<String> stopwords = new HashSet<String>();
	
	@Override
	public void initialize(UimaContext aContext){
		 URL docUrl2 = VectorSpaceRetrieval.class.getResource("/stopwords.txt");
		   
		    if (docUrl2 == null) {
		       throw new IllegalArgumentException("Error opening stopwords.txt");
		    }
		    //System.out.println(docUrl2.toString());
		try {
			Scanner scan = new Scanner(new File(docUrl2.getPath()));
			while(scan.hasNext()){
				stopwords.add(scan.next());
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}
	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		//TO DO: construct a vector of tokens and update the tokenList in CAS
		FSList tokenList = doc.getTokenList();
		if(tokenList == null)
		  tokenList = new FSList(jcas);
	  
		Collection<Token> tokens = Utils.fromFSListToCollection(tokenList, Token.class);
		Iterator<Token> ite = tokens.iterator();
		HashMap<String, Token> tokenMap = new HashMap<String, Token>();
		
		while(ite.hasNext()){
		     Token toAdd = ite.next();
		     tokenMap.put(toAdd.getCoveredText(), toAdd);
		}
		
		TextTokenizer t = new TextTokenizer(docText);
    t.setSeparators(".,!?");
    t.setShowWhitespace(false);
    t.setShowSeparators(false);
    
    while (t.hasNext()) {
      //Actually don't care about position,
      //we only record the 
      String next = t.nextToken().toLowerCase();
      //String next = t.nextToken();
        
    	if(stopwords.contains(next)){
    	  //System.out.println("ignore this:"+ next);
    	  continue;
      }
      
      if(tokenMap.containsKey(next)){
         Token token = tokenMap.get(next);
         token.setFrequency(token.getFrequency()+1);
         tokenMap.put(next, token);
      }else{
         Token token = new Token(jcas);
         token.setText(next);
         token.setFrequency(1);
         tokenMap.put(next, token);
      }
        
    }
    //we should update the
    tokenList = Utils.fromCollectionToFSList(jcas, tokenMap.values());
    doc.setTokenList(tokenList);

	}

}
