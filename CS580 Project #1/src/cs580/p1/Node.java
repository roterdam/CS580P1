package cs580.p1;

import java.util.LinkedList;

final class Node {
	   int lineNumber=-1;
	   /*
	    * Simple nodes are statement nodes.
	    * multiPredicate flag indicates that there is more than one predicate.
	    * TODO: add predicate count and replace multiPredicate flag?
	    */
	   boolean simple=true,ifNode=false,whileNode=false,doNode=false;
	   boolean multiPredicate=false;
	   LinkedList<String> edges = new LinkedList<String>();
	   Node previous=null;
	   
	   public Node(int lineNumber){
	      this.lineNumber=lineNumber;
	   }
	}
