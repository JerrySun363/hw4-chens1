

/* First created by JCasGen Thu Oct 24 18:20:46 EDT 2013 */
package edu.cmu.lti.f13.hw4_chens1.typesystems;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** This is an answer type to be used. Similar to Query, but there is a score stored for this type. It is different from the relevance value specified previously.
 * Updated by JCasGen Thu Oct 24 18:58:32 EDT 2013
 * XML source: /Users/Jerry/git/hw4-chens1/hw4-chens1/src/main/resources/descriptors/typesystems/VectorSpaceTypes.xml
 * @generated */
public class Answer extends Document {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Answer.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Answer() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Answer(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Answer(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Answer(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * Just use the document to generate a new Answer object.
    * <!-- end-user-doc -->
  @generated modifiable */

  public Answer(JCas jcas, Document doc){
	  super(jcas);
	  this.setText(doc.getText());
	  this.setTokenList(doc.getTokenList());
	  this.setQueryID(doc.getQueryID());
	  this.setScore(0.0);
  }
  
  
  
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: score

  /** getter for score - gets The score is indicated to show the score of the answers and also to rank the documents
   * @generated */
  public double getScore() {
    if (Answer_Type.featOkTst && ((Answer_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.f13.hw4_chens1.typesystems.Answer");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Answer_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets The score is indicated to show the score of the answers and also to rank the documents 
   * @generated */
  public void setScore(double v) {
    if (Answer_Type.featOkTst && ((Answer_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.f13.hw4_chens1.typesystems.Answer");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Answer_Type)jcasType).casFeatCode_score, v);}    
  }

    