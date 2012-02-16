/**
 * @author Mike Ballard
 * @version Alpha
 */
package cs580.p1;

import java.util.LinkedList;

final class Node {
   //The various constants express the type the node could be.
   public static final int  
                      SIMPLE_NODE=0,
		   			    IF_NODE=1,
		   			    WHILE_NODE=2,
		   			    DO_NODE=3,
		   			    RETURN_NODE=4,
		   			    DUMMY_NODE=5;
   
   int                firstLineNumber     = -1,
                      lastLineNumber      = -1;
   /*
    * Simple nodes are statement nodes.
    * 
    * predicateCount denotes the total number of comparisons that are being made
    * in the control structure.
    */
   int		          type = 0;
   // Zero indexed.
   int                predicateCount = 0;
   /*
    * Unidirectional edges. We will assume that an edge goes from the current
    * node to all of the nodes in the edge list.
    */
   LinkedList<Node> edges          = new LinkedList<Node>();
   /**
    * Constructor.
    * 
    * @param lineNumber
    */
   public Node(int lineNumber) {
      firstLineNumber = lastLineNumber = lineNumber;
   }
   /**
    * Adds the passed Node object to the current objects edge list.
    * 
    * @param towards
    */
   public void addEdge(Node towards){
      edges.addLast(towards);
   }
}
