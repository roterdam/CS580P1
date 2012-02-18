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
 *TODO: document all of the structures for magic numbers
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
    * @return returns true if parsing the line was successful. False otherwise.
    */
   public boolean parse(String line, int lineNumber) {
      boolean correctParse=true;
      String str = line.toLowerCase();
      //TODO: Parsing stuff.
      if(!lastControlStatement.isEmpty()) {
         //Got something on the stack, MDMAZING!
         Node stack=lastControlStatement.peek();
         if(stack.type==Node.SIMPLE_NODE||stack.type==Node.DUMMY_NODE) {
            //TODO: Finish
         }
         else {
            Node struct = generateStructure(whichCtrlStmt(str),lineNumber);
            stack.nest(struct);
            lastControlStatement.push(struct);
            //TODO: finish
         }
      }
      return correctParse;
   }
   
   private int whichCtrlStmt(String line){
      int returnVal=Node.SIMPLE_NODE;
      if(line.contains("if")) returnVal=Node.IF_NODE;
      else if(line.contains("do")) returnVal=Node.DO_NODE;
      else if(line.contains("while")) returnVal=Node.WHILE_NODE;
      else if(line.contains("return")) returnVal=Node.RETURN_NODE;
      return returnVal;
   }
   
   /**
    * generateStructure creates a prime structure: D1,D2,D3,P1
    * @param nodeType - int that should correspond to the values available in the node class.
    * @return returns a graph structure of the passed prime number.  Invalid numbers return null.
    */
   public static Node generateStructure(int nodeType, int lineNumber) {
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
   /**
    * Same as the previous generateStructure.  Allows for the first and last
    * lines to be different.
    * @param nodeType
    * @param firstLine
    * @param lastLine
    * @return
    */
   public static Node generateStructure(int nodeType, int firstLine, int lastLine){
      Node n=generateStructure(nodeType,lastLine);
      n.firstLineNumber=firstLine;
      return n;
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
