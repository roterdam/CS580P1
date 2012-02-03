/**
 * @author Mike Ballard
 * @version Alpha
 */
package cs580.p1;

import java.util.LinkedList;

final class Node {
   int                lineNumber     = -1;
   /*
    * Simple nodes are statement nodes.
    * 
    * predicateCount denotes the total number of comparisons that are being made
    * in the control structure.
    */
   boolean            simple         = true, 
                      ifNode         = false, 
                      whileNode      = false,
                      doNode         = false;
   // Zero indexed.
   int                predicateCount = 0;
   /*
    * Unidirectional edges. We will assume that an edge goes from the current
    * node to all of the nodes in the edge list.
    */
   LinkedList<Node> edges          = new LinkedList<Node>();
   Node               previous       = null;
   /**
    * Constructor.
    * 
    * @param lineNumber
    */
   public Node(int lineNumber) {
      this.lineNumber = lineNumber;
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
