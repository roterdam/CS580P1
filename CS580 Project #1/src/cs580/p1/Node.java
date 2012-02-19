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
		   			    DUMMY_NODE=5,
		   			    ELSE_NODE=6; //This is potentially bad coding, we'll see
   
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
   //TODO: Do I really need this?
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
      //TODO: BUGTEST
      switch(this.type) {
         case Node.DO_NODE:
            /* Since this is a Do structure, we actually need to move to the 
             * next node in line to do the modifications as that is where the
             * actual control happens, the while();
             */
            Node control = this.edges.get(0);
            control.exitNode=n;
            control.edges.remove(0);
            control.edges.add(0,n);
            break;
         case Node.IF_NODE:
            if(edges.size()>2) {
               //This is an if-then-else node.
               Node left=edges.get(1);
               Node right=edges.get(2);
               left.setExit(n);
               right.setExit(n);
            }
            else {//Just a regular if-then node.
               Node left=edges.get(1);
               left.setExit(n);
               edges.remove(0);
               edges.add(0,n);
            }
            break;
         case Node.WHILE_NODE:
            exitNode=n;
            break;
         default: 
               /*
                * The only things that should fall in here are: Simple, Dummy,
                * and Return.  Realistically, Dummy and return nodes should 
                * never be processed through here, as that means there has been
                * a mistake and this requires a error message and a rage quit.
                */
            if(this.type==Node.DUMMY_NODE||this.type==Node.RETURN_NODE){
               String err=(this.type==Node.DUMMY_NODE?"Dummy":"Return");
               System.out.println("Error: "+err+".setExit().\n\tYou shouldn't"+
               		" be calling setExit on these nodes.  Bailing out.");
               System.exit(-3);
            }
            //Now that we got that out of the way, lets handle the Simple.
            this.exitNode=n;
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
   
   //TODO: DEBUGGING
   public boolean nesting(Node nest) {
      boolean success=true;//Assume we will be successful until otherwise.
      switch(type){
         case IF_NODE:
            edges.remove(1); //Remove the true-branch.
            edges.add(1,nest);//Replace the true-branch.
            nest.setExit(this.exitNode);//Re-link the exit node.
            break;
         case WHILE_NODE:
            edges.remove(0);
            edges.add(0,nest);
            nest.setExit(this); //Gotta link it back to the control statement.
            break;
         case DO_NODE:
            /*
             * Kind of the awkward turtle of the bunch.  What needs to happen
             * here is we need to point the exit of this new structure to the
             * control node of the do loop. But we can't discard the head of 
             * the do-loop as things may be pointing to it. So, we are going
             * to link the head of the do (making it a dummy node if you will)
             * to this new structure.
             */
            Node control=this.edges.get(0);
            this.edges.remove(0);
            this.edges.add(0,nest);
            nest.setExit(control);
            break;
         case SIMPLE_NODE:
            System.err.println("Uhh...You can't nest Simple Nodes....");
            success=false;
            break;
         default:
            //Should actually never hit the default case.
            System.err.println("Error when trying to nest.\n\tCurrent node is"+
            		"Is: "+this.type+" and cannot be nested with a passed node" +
            				" of type: "+nest);
            System.exit(-2);
      }
      return success;
   }
   
   //TODO: toStringBuilder();
   public StringBuilder toStringBuilder() {
      return null;
   }
   
   public String toString() {
      return this.toStringBuilder().toString();
   }
}
