package edu.cmu.lti.f13.hw4_chens1.annotators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.internal.util.TextTokenizer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f13.hw4_chens1.typesystems.Document;
import edu.cmu.lti.f13.hw4_chens1.typesystems.Token;
import edu.cmu.lti.f13.hw4_chens1.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

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
      String next = t.nextToken();
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
