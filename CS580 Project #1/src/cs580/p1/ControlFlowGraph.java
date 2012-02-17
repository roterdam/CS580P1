package cs580.p1;

import java.util.LinkedList;
import java.util.Stack;
/**
 * 
 * @author Michael J. Ballard
 * 
 * Purpose: TODO
 * 
 * Assumptions: TODO
 *
 */
public class ControlFlowGraph {
   //What we will be using to parse against.
   String regex = ".*(if|else|while|do|\\}|return|for).*";
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
      if(line.matches(regex)){
         //So we have a match, but which?
         if(line.contains("if")){
            /* The convention will be to assume that the first connection in
             * the if structure will be for the 'then' section, and the second
             * connection will be the else section.  It may be necessary to 
             * check lines ahead to make sure that the structure is correct.
             * TODO: Remove this assumption.
             */
            Node struct = generateStructure(Node.IF_NODE,lineNumber);
            lastControlStatement.push(struct);
            if(graph.isEmpty()) graph.add(struct);
            else {
               switch(graph.getLast().type){
               case Node.DUMMY_NODE:
                  Node last=graph.removeLast();
                  if(graph.isEmpty()) graph.add(struct);
                  else {
                     graph.getLast().removeEdge(last);
                     graph.getLast().addEdge(struct);
                     
                  }
                  break;
               case Node.SIMPLE_NODE:
                  graph.getLast().addEdge(struct);
                  graph.addLast(struct);
                  break;
               default:
                  System.out.println("\nERROR.\n\tWhen adding if-structure to"+
                  		" the graph.  The graph was not empty and the last" +
                  		"node was not a dummy node nor a procedure node." +
                  		"\nNode Type: "+graph.getLast().type+" line:"+
                  		graph.getLast().lastLineNumber);
                  System.exit(-1);
               }
            }
         } else if(line.contains("while")){
            
         } else if(line.contains("do")){
            
         } else if(line.contains("else")){
            
         }
            
      }
      else {
       /*
        * The passed string is a procedure and not a control statement, so we
        * need to either update the last node in the graph (if it is P1),  
        * create a P1 and link it properly, or if the last node is a dummy
        * then we should change the node to the P1 type.
        */
         if(graph.isEmpty()){
            Node top=this.generateStructure(Node.SIMPLE_NODE, lineNumber);
            graph.addLast(top);
         }
         else {
            Node last=graph.getLast();
            if(last.type==Node.SIMPLE_NODE){
               last.lastLineNumber=lineNumber;
            }
            else if(last.type==Node.DUMMY_NODE){
               last.type=Node.SIMPLE_NODE;
               last.firstLineNumber=last.lastLineNumber=lineNumber;
            }
            else {
               Node head = generateStructure(Node.SIMPLE_NODE,lineNumber);
               graph.getLast().addEdge(head);
               graph.addLast(head);
            }
         }
      }
      return false;
   }
   /**
    * generateStructure creates a prime structure: D1,D2,D3,P1
    * @param nodeType - int that should correspond to the values available in the node class.
    * @return returns a graph structure of the passed prime number.  Invalid numbers return null.
    */
   public Node generateStructure(int nodeType, int lineNumber) {
      Node head=new Node(nodeType);
      head.firstLineNumber=head.lastLineNumber=lineNumber;
      switch(nodeType){
         case Node.IF_NODE:
            Node thenChild=new Node(Node.SIMPLE_NODE);
            Node elseChild=new Node(Node.SIMPLE_NODE);
            Node ifExit=new Node(Node.DUMMY_NODE);
            head.addEdge(thenChild); //Then
            head.addEdge(elseChild); //Else - Pruned if necessary
            thenChild.addEdge(ifExit);
            elseChild.addEdge(ifExit);
            break;
         case Node.WHILE_NODE:
            Node bodyChild=new Node(Node.SIMPLE_NODE);
            head.addEdge(bodyChild);
            bodyChild.addEdge(head);
            Node whileExit=new Node(Node.DUMMY_NODE);
            head.addEdge(whileExit);
            break;
         case Node.DO_NODE:
            Node doExit=new Node(Node.DUMMY_NODE);
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
    //TODO: FIX ME PROPER!
   public String toString() {
      StringBuilder output = new StringBuilder();
      for(Node n : graph) {
         output.append(n.toString());
      }
      return output.toString();
   }
}
