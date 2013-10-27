package edu.cmu.lti.f13.hw4_chens1.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;
import edu.cmu.lti.f13.hw4_chens1.typesystems.Document;
import edu.cmu.lti.f13.hw4_chens1.typesystems.Token;
import edu.cmu.lti.f13.hw4_chens1.utils.Utils;

public class RetrievalEvaluator extends CasConsumer_ImplBase {
	
	public class Node {
		private int rel;
		private double score;
		private int queryId;
		public int getQueryId() {
			return queryId;
		}
		public void setQueryId(int queryId) {
			this.queryId = queryId;
		}
		public int getRel() {
			return rel;
		}
		public void setRel(int rel) {
			this.rel = rel;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		
		
	}

	/** query id number **/
	public ArrayList<Integer> qIdList;

	/** query and text relevant values **/
	public ArrayList<Integer> relList;

	/** globalDictionary **/
	public HashMap<String, Integer> globalDictionary;
	
	public ArrayList<HashMap<String, Integer>> vectors;
	
	public HashMap<Integer, ArrayList<Node>> scores;
	
	ArrayList<Node>[] answers;

	public void initialize() throws ResourceInitializationException {

		qIdList = new ArrayList<Integer>();

		relList = new ArrayList<Integer>();

		globalDictionary = new HashMap<String, Integer>();
		
		vectors = new ArrayList<HashMap<String, Integer>>();
		
		scores = new HashMap<Integer, ArrayList<Node>>();
		
		
	}

	/**
	 *  :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 * 
	 * Here we have a basic assumption that each document are stored together. 
	 */
	/**
	 * @param aCas
	 * @throws ResourceProcessException
	 */
	/**
	 * @param aCas
	 * @throws ResourceProcessException
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {
		JCas jcas;
		try {
		jcas = aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator<Annotation> it = jcas.getAnnotationIndex(Document.type)
				.iterator();

		if (it.hasNext()) {
			Document doc = (Document) it.next();

			// Make sure that your previous annotators have populated this in
			// CAS
			FSList fsTokenList = doc.getTokenList();
			ArrayList<Token> tokenList = Utils.fromFSListToCollection(
					fsTokenList, Token.class);
			
            //System.out.println(fsTokenList == null);
			
            qIdList.add(doc.getQueryID());
			relList.add(doc.getRelevanceValue());
			// Construct the global dictionary here.
			for (int i = 0; i < tokenList.size(); i++) {
				String key = tokenList.get(i).getText();
				if (globalDictionary.containsKey(key)) {
					globalDictionary.put(key, globalDictionary.get(key)
							+ tokenList.get(i).getFrequency());
				} else {
					globalDictionary.put(key, tokenList.get(i).getFrequency());
				}
			}

			/* Do something useful here
			*/
			HashMap<String, Integer> docVec = this.fromListToHashMap(doc.getTokenList());
			vectors.add(docVec);
		}

	}

	/**
	 * 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);

		// compute the cosine similarity measure
		// record the score in the answer type.
		for(int i=0;i<relList.size();i++){
			int queryId = -1;
			if(relList.get(i) == 99){
				queryId =qIdList.get(i);
				HashMap<String, Integer> query = this.vectors.get(i);
				//int count = 0; 
				for(int j=0;j<qIdList.size();j++){
					  
					if(qIdList.get(j)==queryId && relList.get(j)!=99){
						//count++;
						//locate a potential answer
						//System.out.println(query.keySet());
						//System.out.println(this.vectors.get(j).keySet());
						double score = this.computeCosineSimilarity(query, this.vectors.get(j));
						//double score = this.computeDiceCoefficient(query, this.vectors.get(j));
						//double score = this.computeJaccardcoefficient(query, this.vectors.get(j));
						Node node = new Node();
						node.setRel(relList.get(j));
						node.setScore(score);
						node.setQueryId(queryId);
						if(scores.containsKey(queryId)){
							scores.get(queryId).add(node);
						}else{
							ArrayList<Node> newlist= new ArrayList<Node>();
							newlist.add(node);
							scores.put(queryId, newlist);
						}
					}
				}
				
			}
		}
		
		
		
		//:: compute the rank of retrieved sentences
		int i=0;
		answers = new ArrayList[scores.values().size()];
        for(ArrayList<Node> answer : scores.values()){
        	ArrayList<Node> newanswer = rankList(answer);
        	answers[i]= newanswer;
        	i++;
        }
		
		//:: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}

	/**
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity = 0.0;

		// compute cosine similarity between two sentences
		// Compute query Vector
		int queryCount = 0;
		int cosineCount = 0;
		int docCount = 0;

		Collection<String> queryKeys = queryVector.keySet();
		Iterator<String> ite = queryKeys.iterator();
		while (ite.hasNext()) {
			String term = ite.next();
			int query = queryVector.get(term);
			queryCount += query * query;
			if (docVector.containsKey(term)) {
				cosineCount += query * docVector.get(term);
			}
		}
		
		Collection<String> docKeys = docVector.keySet();
		Iterator<String> ite2 = docKeys.iterator();
		while (ite2.hasNext()) {
			String docK = ite2.next();
			docCount += docVector.get(docK) * docVector.get(docK);
		}
		// now we compute the cosine_similarity.
		cosine_similarity = cosineCount * 1.0
				/ (Math.sqrt(queryCount) * Math.sqrt(docCount));
		return cosine_similarity;
	}

	 /**
	  * 
	  * @return DiceCoefficient
	  */
	private double computeDiceCoefficient(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double DiceCoefficient = 0.0;
		int queryCount = 0;
		int cosineCount = 0;
		int docCount = 0;

		Collection<String> queryKeys = queryVector.keySet();
		Iterator<String> ite = queryKeys.iterator();
		while (ite.hasNext()) {
			String term = ite.next();
			if (docVector.containsKey(term)) {
				cosineCount ++;
			}
		}
		
	
		queryCount = queryVector.size();
		docCount = docVector.size();
		//now we compute the cosine_similarity.
		DiceCoefficient = cosineCount * 2.0
				/ (queryCount + docCount);
		//System.out.println(cosineCount +"  "+queryCount+" "+docCount);
		return DiceCoefficient;
		
	}

	
	/**
	 * @return jaccardCoefficient
	 * */
	private double computeJaccardcoefficient (Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		
		double jaccardCoefficient = 0.0;
		int queryCount = 0;
		int cosineCount = 0;
		int docCount = 0;

		Collection<String> queryKeys = queryVector.keySet();
		Iterator<String> ite = queryKeys.iterator();
		while (ite.hasNext()) {
			String term = ite.next();
			if (docVector.containsKey(term)) {
				cosineCount ++;
			}
		}
		
	
		queryCount = queryVector.size();
		docCount = docVector.size();
		//now we compute the cosine_similarity.
		jaccardCoefficient = cosineCount * 1.0
				/ (queryCount + docCount-cosineCount);
		//System.out.println(cosineCount +"  "+queryCount+" "+docCount);
		return jaccardCoefficient;
	}
	
	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr() {
		double metric_mrr = 0.0;
        
		// :: compute Mean Reciprocal Rank (MRR) of the text collection
		int sent = 1;
		for(ArrayList<Node> list: answers){
           int rank =0;
           while(rank < list.size()){
        	 boolean rel = (list.get(rank).getRel() == 1);
        	 if(rel){
        		 System.out.printf("Score:%.5f rel=1 qid=%d sent%d rank=%d\n",list.get(rank).getScore(),
        				 list.get(rank).getQueryId(), sent, rank+1);
        		 metric_mrr+= 1.0/(rank+1);
        		 break;
        	 }else{
        		 rank++;
        	 }
        	 
           }
           sent++;
        }
		metric_mrr/=(sent-1);
		return metric_mrr;
	}

	private HashMap<String, Integer> fromListToHashMap(FSList fsList) {
		ArrayList<Token> tokens = Utils.fromFSListToCollection(fsList, Token.class);
		HashMap<String, Integer> vector = new HashMap<String, Integer>();
		for (Token t : tokens) {
			vector.put(t.getText(), t.getFrequency());
		}
		return vector;

	}

	private ArrayList<Node> rankList(ArrayList<Node> answer){
		ArrayList<Node> newAnswer = new ArrayList<Node>();
		for(Node a: answer){
			int i=0;
			while(i<newAnswer.size()){
				if(a.getScore() < newAnswer.get(i).getScore())
					i++;
				else
					break;
			}
			newAnswer.add(i, a);
		}
		
		return newAnswer;
	}
	
}
