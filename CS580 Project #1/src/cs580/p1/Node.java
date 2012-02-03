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
   LinkedList<String> edges          = new LinkedList<String>();
   Node               previous       = null;

   public Node(int lineNumber) {
      this.lineNumber = lineNumber;
   }
}
