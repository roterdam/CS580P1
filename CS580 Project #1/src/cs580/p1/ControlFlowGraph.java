package cs580.p1;

import java.util.Stack;
/**
 * 
 * @author Michael J. Ballard
 * 
 * Purpose: TODO
 * 
 * Assumptions: TODO
 *    1. The code to be processed should only have a single return statement
 *    2. The code should be easily compiled by a C compiler, assuming said
 *       compiler didn't have a preprocessor.
 * From above, it should be clearly understood that this code has not been
 * written to handle ill-structured code.
 * 
 *TODO: document all of the structures for magic numbers
 */
public class ControlFlowGraph {
   private static final boolean DEBUG=true;
   //What we will be using to parse against.
   String regex = ".*(if|else|while|do|return|for).*";
   String endControlToken="}";
   //Just a pointer to the graph.
   Node graph = null;
   /*
    * The stack will hold the last non-completed predicate node,
    * with the hope that this can be used to make the correct connections
    * for the various control flow objects.  This should hopefully simplify
    * nesting situations.
    */
   Stack<Node> lastControlStatement = new Stack<Node>();
   boolean keepParsing=true;
   /**
    * parse - Generates a control flow graph line by line.
    * @param line - String to be parsed.
    * @param lineNumber - lineNumber will be used when creating the nodes.
    * @return returns true if parsing the line was successful. False otherwise.
    */
   //TODO: Handle multiple predicates (at least 2)
   //TODO: Fix if to handle else
   //TODO: Allow for single (on the next line) if,else,while (currently assuming {} are being used
   public boolean parse(String line, int lineNumber) {
      if(DEBUG) {
         System.out.println("\nDEBUG: parse(\""+line+"\","+lineNumber+") was called.");
         System.out.println("keepParsing="+keepParsing);
      }
      if(!keepParsing) return false; //Abort parsing as per assumption.
      boolean correctParse=true;
      String str = line.toLowerCase();
      //TODO: Parsing stuff.
      int parsedStatement=whichCtrlStmt(str);
      //TODO: Maybe turn this switch statement into nested if/else?
      switch(parsedStatement) {
         case Node.DO_NODE:
         case Node.IF_NODE:
         case Node.WHILE_NODE:
            if(DEBUG) {
               System.out.println("Passed through the switchs statement, and determined it to be a control.");
               System.out.println("calling process("+parsedStatement+","+lineNumber+","+str+")");
            }
            process(parsedStatement,lineNumber,str);
            break;
         case Node.RETURN_NODE:
            /*
             * Since we have hit the return statement, and by the assumptions,
             * it is finally time to end this 
             */
            graph.setExit(generateStructure(Node.DUMMY_NODE,lineNumber));
            keepParsing=false;
            break;
         default:
            if(DEBUG) 
               System.out.println("default case.");
            /*
             * So we need to check to see if the line contains an end of
             * statement token, ie '}'.  If it does then the control structure
             * is done and can be popped off, otherwise we just assume that the
             * line is just a procedure.
             */
            if(str.contains(endControlToken)) {
               if(DEBUG) System.out.println("Statement was an endControlToken");
               /* Whatever it was, should now be done.  The only case that this
                * would be false is if there is nothing on the stack, which
                * would mean that we went through a block and it contained only
                * procedure statements.
                */
               if(lastControlStatement.isEmpty()) break; //Okay to discard.
               else {
                  if(DEBUG) System.out.println("performing closure.");
                  /* Since the stack has elements, we know that the top has
                   * finished processing.  However, it could be the case that
                   * the we have a sequence of elements.  So What we are going
                   * to do is create a dummy node that points to this node
                   * finished node. During processing if we find that
                   * there is a dummy node on the stack, we can link to it thus
                   * creating the sequence. 
                   * 
                   * The only time this isn't (shouldn't) be the case is when 
                   * we are processing the first element.  At which point the 
                   * stack would be empty and the graph reference would also be
                   * empty.
                   */
                  Node dummy=generateStructure(Node.DUMMY_NODE,-1);
                  dummy.exitNode=lastControlStatement.pop();
                  lastControlStatement.push(dummy);
                  if(lastControlStatement.isEmpty() && graph==null) {
                     graph=dummy.exitNode;
                  }
                  else { //TODO: Prove and remove.
                     //This should never ever happen.
                     System.out.println("ERROR: empty stack && graph!=null");
                     System.exit(-5);
                  }
               }
            } else {
                //The line passed is a procedure node
               if(DEBUG) System.out.println("Determined it to be a procedure node, calling process("+Node.SIMPLE_NODE+","+lineNumber+","+str+")");
               process(Node.SIMPLE_NODE,lineNumber,str);
            }
      }//End of Switch
      return correctParse;
   }
   
   private void process(int nodeType, int lineNumber, String line) {
      if(DEBUG){
         System.out.println("DEBUG: process("+nodeType+","+lineNumber+","+line+") was called.");
      }
      Node struct=generateStructure(nodeType,lineNumber);
      if(!lastControlStatement.isEmpty()){
         if(DEBUG) System.out.println("lastControlStatement.isEmpty()==false");
         Node lastAct=lastControlStatement.peek();
         if(DEBUG) System.out.println("switching on: "+lastAct.type);
         switch(lastAct.type){
            case Node.IF_NODE:
               //TODO: Finish if stack==if, fix for else
               if(nodeType==Node.ELSE_NODE){
                  //Redefine struct since it is NULL.
               }else {
                  lastAct.setExit(struct);
               }
               lastControlStatement.push(struct);
               break;
            case Node.SIMPLE_NODE:
               //TODO: DEBUG
               Node P1=lastControlStatement.pop();
               P1.setExit(struct);
               if(graph==null) graph=P1;
               lastControlStatement.push(struct);
               break;
            case Node.DUMMY_NODE:
               //Perform sequencing.
               lastAct=lastControlStatement.pop();
               lastAct.exitNode.setExit(struct);
               lastControlStatement.push(struct);
               break;
            case Node.DO_NODE: //Commit: Merged Do/While
            case Node.WHILE_NODE:
               //TODO: DEBUG
               lastAct.nesting(struct);
               lastControlStatement.push(struct);
               break;
            default:
               //The program should never flow into here.
               System.out.println("ERROR: Processing.\n\tProcess(" +nodeType+
                     ","+lineNumber+","+line+")\ntNode Type didn't match " +
                     		"a specific type(IF/DO/WHILE/SIMPLE). Bailing out.");
               System.exit(-4);
         }//End Switch
      }//End If
      else {
         if(DEBUG) System.out.println("lastControlAction.isEmpty()==true.\nPushing structure on the stack, and setting graph.");
         graph=struct;
         lastControlStatement.push(struct);
      }//End Else
   }//End Funct
   
   private int whichCtrlStmt(String line){
      int returnVal=-1;
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
            Node ifExit=null;
            head.addEdge(thenChild); //Then
            head.addEdge(elseChild); //Else - Pruned if necessary
            thenChild.addEdge(ifExit);
            elseChild.addEdge(ifExit);
            break;
         case Node.WHILE_NODE:
            Node bodyChild=new Node(Node.SIMPLE_NODE);
            head.addEdge(bodyChild);
            bodyChild.addEdge(head);
            head.setExit(null);
            break;
         case Node.DO_NODE:
            Node doBody=new Node(Node.SIMPLE_NODE);
            head.addEdge(doBody);
            doBody.addEdge(head);
            doBody.addEdge(null);
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
   //TODO: DEBUG:Make sure this is the correct info.
   public String toString() {
      return graph.toString();
   }
}
