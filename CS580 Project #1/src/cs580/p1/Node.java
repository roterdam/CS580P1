/**
 * @author Mike Ballard
 * @version Alpha
 */
package cs580.p1;

import java.util.LinkedList;

final class Node {
   protected static final boolean DEBUG=true;
   private static LinkedList<Node> visited=new LinkedList<Node>();
   private static long nodeNumber=1;
   protected long UUID=-1;
   //The various constants express the type the node could be.
   public static final int  
                      SIMPLE_NODE=0,
		   			    IF_NODE=1,
		   			    WHILE_NODE=2,
		   			    DO_NODE=3,
		   			    RETURN_NODE=4,
		   			    DUMMY_NODE=5,
		   			    ELSE_NODE=6;
   
   int                firstLineNumber     = -1,
                      lastLineNumber      = -1;
   Node               exitNode=null,
                      controlNode=null;
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
   
   boolean processElse=false; //Need for the if-else case.
   /**
    * Constructor.
    * 
    * @param lineNumber
    */
   public Node(int lineNumber) {
      firstLineNumber = lastLineNumber = lineNumber;
      UUID=nodeNumber;
      nodeNumber++;
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
   //TODO: Do I really need this?
   public boolean resetNode(int newNodeType){
      if(newNodeType>=Node.DUMMY_NODE) return false;
      this.edges.clear(); //Dangerous
      this.type=-1;
      Node replacement=ControlFlowGraph.generateStructure(newNodeType, 
            this.firstLineNumber,this.lastLineNumber);
      this.edges=replacement.edges;
      
      return false;
   }
   public void setExit(Node n){
      if(DEBUG) System.out.println("DEBUG: setExit was called. Setting Node: "+UUID+" exit to node: "+n.UUID);
      switch(this.type) {
         case Node.DO_NODE:
            /* Since this is a Do structure, we actually need to move to the 
             * next node in line to do the modifications as that is where the
             * actual control happens, the while();
             */
            controlNode.setExit(n);
            break;
         case Node.IF_NODE:
            if(DEBUG) System.out.println("NODE:"+this.UUID+" type is IF, edgeList.size()="+edges.size());
            if(edges.size()==2) {
               //This is an if-then-else node.
               Node left=edges.get(0);
               Node right=edges.get(1);
               if(DEBUG) System.out.println("left: "+left.UUID+" is relinking exit to node: "+n.UUID);
               left.setExit(n);
               if(DEBUG) System.out.println("right: "+right.UUID+" is relinking exit to node: "+n.UUID);
               right.setExit(n);
               if(DEBUG) System.out.println("setExit finished for IF type node: "+this.UUID);
            }
            else {//Just a regular if-then node.
               if(DEBUG) System.out.println("Setting "+this.UUID+" exit to "+n.UUID);
               if(DEBUG) System.out.println("Calling left node to set exit.");
               Node left=edges.get(0);
               left.setExit(n);
            }
            this.exitNode=n;
            break;
         case Node.WHILE_NODE:
            exitNode=n;
            break;
         default: 
            if(DEBUG) {
               System.out.println("Through the switch statement, node: "+UUID+" type:"+type);
            }
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
            if(DEBUG) System.out.println("Not a Dummy nor a return either, " +
            		"linking node: "+this.UUID+" to node: "+n.UUID);
            this.exitNode=n;
      }
   }

   public boolean nesting(Node nest) {
      if(DEBUG) System.out.println("DEBUG: Nesting Called. I am: "+type);
      boolean success=true;//Assume we will be successful until otherwise.
      switch(type){
         case IF_NODE:
            if(nest.type==Node.ELSE_NODE){
               this.processElse=true;
               edges.get(1).firstLineNumber=nest.firstLineNumber;
               edges.get(1).lastLineNumber=nest.lastLineNumber;
            } else if(this.processElse){ //Statements are being added to the else branch.
               //exitNode=null;
               edges.remove(1);
               edges.add(1,nest);
               nest.exitNode=this.exitNode; //TODO: FIXME
            }
            else {
               edges.remove(0); //Remove the true-branch.
               edges.add(0,nest);//Replace the true-branch.
               nest.setExit(this.exitNode);//Re-link the exit node.               
            }
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
            Node control=this.exitNode;
            if(DEBUG) {
               System.out.println("Linking this ("+UUID+","+this.firstLineNumber+","+this.lastLineNumber+")" +
               		"to ("+nest.UUID+","+nest.firstLineNumber+","+nest.lastLineNumber+") and that to " +
               				"("+control.UUID+","+control.firstLineNumber+","+control.lastLineNumber+")");
            }
            this.exitNode=nest;
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
   
   private StringBuilder toStringBuilder() {
      if(DEBUG) System.out.println("DEBUG: toStringBuilder() was called");
      if(visited.contains(this)) {
         if(DEBUG) System.out.println("Node: "+UUID+" was already visited, returning...");
         return new StringBuilder(""); //Already processed.
      }
      if(DEBUG) System.out.println("Adding Node: "+UUID+" to the visited list.");
      visited.add(this);
      StringBuilder buffer = new StringBuilder();
      
      buffer.append("\nNode: "+UUID+" lines: "+this.firstLineNumber+" to "+lastLineNumber+" type: "+type+"\n");
      
      if(DEBUG) System.out.println("\tSize of edges: "+edges.size()+" exit==null?"+" "+(exitNode==null)+" processElse=?"+processElse);
      for(Node n : edges) buffer.append("("+UUID+","+n.UUID+") ");
      
      if(exitNode!=null && processElse==false)buffer.append("E:("+UUID+","+exitNode.UUID+")\n");
      else if(DEBUG) buffer.append("E: (DEBUG) NULL\n");
      if(!edges.isEmpty()) {
         buffer.append(edges.get(0).toStringBuilder());
         if(processElse) buffer.append(edges.get(1).toStringBuilder());
      }
      if(exitNode!=null && processElse==false) {
         buffer.append(exitNode.toStringBuilder());
      }
      return buffer;
   }
   
   public String toString() {
      String toString =this.toStringBuilder().toString();
      visited.clear();
      return toString;
   }
}
