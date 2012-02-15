/**
 * @author Mike Ballard
 * @version Alpha
 */
package cs580.p1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Main {
	public static void main(String[] arg) {
	   //Prompt the user for the file to be parsed.
	   Scanner stdin = new Scanner(System.in);
	   System.out.print("\nWhat is the file name (use full path)? ");
	   String fileName=stdin.nextLine();
	   
	   //Open up the file.
	   BufferedReader fin=null;
	   try {
	      fin=new BufferedReader(new FileReader(fileName));
	   }catch(IOException e){
	      System.out.println("\nIO Error.  Is the file name correct?");
	      e.printStackTrace();
	      System.exit(-1);
	   }
	   
	   //Declarations for the parsing.
	   //TODO: Double check that we are matching for all of the require controls statements.
	   String regex=".*(if|while|do|\\{|return|for).*",line=null;
	   int lineNumber=0;
	   //cfg will store all of the nodes generated from parsing.
	   LinkedList<Node> cfg = new LinkedList<Node>();
	   /*
	    * The stack will hold the last non-completed predicate node,
	    * with the hope that this can be used to make the correct connections
	    * for the various control flow objects.  This should hopefully simplify
	    * nesting situations.
       */
	   Stack<Node> lastControlStatement = new Stack<Node>();
	   
      try {
         while((line=fin.readLine())!=null) {
            //LineNumber != line number.  I know....
            //TODO: Make lineNumber variable clearer.
            lineNumber++;
            int totalNumberOfPredicates=line.split("(\\&\\&|\\|\\|)").length;
            if(line.matches(regex)) {
               //TODO: regex matches.
            } else {
               /*
                * Since we don't have a control statement we need to update
                * last node based off of a few rules.
                */
               if(lastControlStatement.isEmpty()) {
                  if(cfg.isEmpty()) {
                     //Empty list, time to populate it with the first statement
                     Node newNode= new Node(lineNumber);
                     newNode.type=Node.SIMPLE_NODE;
                  }
                  else { //CFG is empty
                     /*
                      * So the stack is empty and the list isn't. I will assume
                      * that the last entry in the list is a simple node.
                      */
                     cfg.getLast().lastLineNumber=lineNumber;
                  }
               }
               else {//lastControlStatement.isEmpty
                  /*
                   * So the stack isn't empty, and thus neither is the list.
                   * We need to see if the last node in the list is simple or
                   * not.
                   */
                  if(cfg.getLast().type==Node.SIMPLE_NODE){
                     //Update "P1" to include the currently read line.
                     cfg.getLast().lastLineNumber=lineNumber;
                  }
                  else {
                     /*
                      * Since the last node is for a control statement, we need
                      * to create a new "P1" node, link it to the last node in
                      * the list, and finally make it the last node in the
                      * list.
                      * 
                      * We don't link it directly to the top of the stack
                      * because that node may not actually be the last node in
                      * the list.  This would happen when the last control
                      * statement had multiple predicates to evaluate. If in 
                      * fact this is not the case, then the last node in the 
                      * list will equal to the top node in the stack.
                      */
                     Node newNode = new Node(lineNumber),
                          listNode = cfg.getLast();
                     newNode.type=Node.SIMPLE_NODE;
                     listNode.addEdge(newNode);
                     cfg.addLast(newNode);
                  }//End of if-else statement for simple last cfg node.
               }//End of the if-else statement for empty stack.
            }//End of the if-else statement that checks for regex matches
         }//End of the while loop
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
	}
}