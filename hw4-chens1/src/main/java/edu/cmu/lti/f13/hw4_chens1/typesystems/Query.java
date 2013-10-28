

/* First created by JCasGen Thu Oct 24 18:20:46 EDT 2013 */
package edu.cmu.lti.f13.hw4_chens1.typesystems;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** This sub-type indicates that the doc is a type of query for the convenience of the cosine similarity.
 * Updated by JCasGen Mon Oct 28 15:54:05 EDT 2013
 * XML source: /Users/Jerry/git/hw4-chens1/hw4-chens1/src/main/resources/descriptors/typesystems/VectorSpaceTypes.xml
 * @generated */
public class Query extends Document {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Query.class);
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
  protected Query() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Query(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Query(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Query(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
}

    