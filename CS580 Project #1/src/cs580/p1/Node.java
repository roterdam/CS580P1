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
   Node               exitNode=null;
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
   public boolean addEdge(Node towards){
      /* Current behavior is to add it to the end of the list, however, if this
       * implementation is later changed, then it will be necessary to make
       * sure that nodes are being added to the end of the list.
       */
      return edges.add(towards);
   }
   public boolean removeEdge(Node nodeToBeRemoved){
      //There really shouldn't be multiple links going to the same node.
      return edges.removeLastOccurrence(nodeToBeRemoved);
   }
   public boolean resetNode(int newNodeType){
      if(newNodeType>=Node.DUMMY_NODE) return false;
      this.edges.clear(); //Dangerous
      this.type=-5;
      Node replacement=ControlFlowGraph.generateStructure(newNodeType, 
            this.firstLineNumber,this.lastLineNumber);
      this.edges=replacement.edges;
      
      return false;
   }
   public void setExit(Node n){
      //TODO: FINISH
      if(this.type!=Node.IF_NODE && this.type!=Node.SIMPLE_NODE){
         if(!edges.isEmpty()) edges.remove(0);
         edges.add(0,n);
         exitNode=n;
      }
      else if(this.type==Node.IF_NODE) {
         
      }else if(this.type==Node.SIMPLE_NODE) {
         
      }
   }
   //TODO: Broken concept
   public boolean replaceEdge(int edgeLocation, Node replacementNode){
      boolean success=true;
      if(edges.isEmpty() || edgeLocation>edges.size()) success=false;
      else {
         edges.remove(edgeLocation);
         edges.add(edgeLocation,replacementNode);
      }
      return success;
   }
   //TODO: public boolean sequence(Node seq) {
   
   //TODO: Finish
   public boolean nest(Node nest) {
      boolean success=true;
      switch(type){
         case IF_NODE:
            edges.remove(1); //Remove the true-branch.
            edges.add(1,nest);//Replace the true-branch.
            nest.setExit(this.exitNode);//Re-link the exit node.
            break;
         case WHILE_NODE:
            edges.remove(1);
            edges.add(1,nest);
            nest.setExit(this); //Gotta look it back to the control statement.
            break;
         case DO_NODE:
            //I don't like do-loops.
            break;
         case SIMPLE_NODE:
            System.err.println("Uhh...You can't nest Simple Nodes....");
            success=false;
            break;
         default:
            //OH..uhh..how did I..er?
            //TODO: Handle default case.
      }
      return success;
   }
}
