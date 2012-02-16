package cs580.p1;

import java.util.LinkedList;
import java.util.Stack;

public class ControlFlowGraph {
   //graph will store all of the nodes generated from parsing.
   LinkedList<Node> graph = new LinkedList<Node>();
   /*
    * The stack will hold the last non-completed predicate node,
    * with the hope that this can be used to make the correct connections
    * for the various control flow objects.  This should hopefully simplify
    * nesting situations.
    */
   Stack<Node> lastControlStatement = new Stack<Node>();
   
   /**
    * parse - Generates a control flow graph line by line.
    * @param line - String to be parsed.
    * @param lineNumber - lineNumber will be used when creating the nodes.
    * @return returns true if parsing the line was successful.  False otherwise.
    */
   public boolean parse(String line, int lineNumber) {
     return false;
   }
   /**
    * generateStructure creates a prime structure: D1,D2,D3,P1
    * @param nodeType - int that should correspond to the values available in the node class.
    * @return returns a graph structure of the passed prime number.  Invalid numbers return null.
    */
   public Node generateStructure(int nodeType) {
      Node head=new Node(nodeType);
      switch(nodeType){
         case Node.IF_NODE:
            Node thenChild=new Node(Node.SIMPLE_NODE);
            Node elseChild=new Node(Node.SIMPLE_NODE);
            Node ifExit=new Node(Node.SIMPLE_NODE);
            head.addEdge(thenChild); //Then
            head.addEdge(elseChild); //Else - Pruned if necessary
            thenChild.addEdge(ifExit);
            elseChild.addEdge(ifExit);
            break;
         case Node.WHILE_NODE:
            Node bodyChild=new Node(Node.SIMPLE_NODE);
            head.addEdge(bodyChild);
            bodyChild.addEdge(head);
            Node whileExit=new Node(Node.SIMPLE_NODE);
            head.addEdge(whileExit);
            break;
         case Node.DO_NODE:
            Node doExit=new Node(Node.SIMPLE_NODE);
            Node doBody=new Node(Node.SIMPLE_NODE);
            head.addEdge(doBody);
            doBody.addEdge(head);
            doBody.addEdge(doExit);
            break;
         case Node.SIMPLE_NODE:
         case Node.RETURN_NODE:
            //Don't need to do anything.
            break;
         default:
            head=null;
      }
      return head;
   }
}
