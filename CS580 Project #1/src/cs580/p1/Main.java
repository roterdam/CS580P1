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
	   System.out.print("\nWhat is the file name (user full path)? ");
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
	   String regex=".*(if|while|do|\\{|\\}).*",line=null;
	   int lineNumber=0;
	   //cfg will store all of the nodes generated from parsing.
	   LinkedList<Node> cfg = new LinkedList<Node>();
	   /*
	    * The stack will hold the last non-completed predicate node,
	    * with the hope that this can be used to make the correct connections
	    * for the various control flow objects.  This should hopefully simplify
	    * nesting situations.
       */
	   Stack<Node> action = new Stack<Node>();
	   
	   try {
	      while((line=fin.readLine())!=null) {
	         //LineNumber != line number.  I know....
	         //TODO: Make lineNumber variable clearer.
	         lineNumber++;
	         //Check to see if there are multiple predicates.
	         boolean complexPredicate=false;
	         //Check to see if the current line has a control command.
	         //TODO: Deal with {,}
	         if(line.matches(regex)) {
	            /*
	             * If there isn't a match then split will return the whole
	             * string in an array of a single element.
	             * This makes the assumption that the testing statement doesn't
	             * break to multiple lines.
	             * TODO: Allow multi-line testing statements (within reason).
	             */
	            int totalNumberOfPredicates=line.split("(\\&\\&|\\|\\|)").length;
	            
	            //There is a match with the regular expression.  But which?
	            //TODO: Squash this down to a single case since node differentiation is the only difference between the cases.
	            if(line.contains("if")) {
	                  /*
	                   * Generate as many nodes as needed to fulfill the number
	                   * of comparisons in the encountered predicate.
	                   * TODO: Collapse this into just a single case.
	                   * TODO: update else to deal with complex predicates
	                   * TODO: changing to numbers should be easy and allow for more than 2 comparisons.
	                   */
	               Node last = cfg.getLast();
                  Node newNode = new Node(lineNumber);
                  newNode.simple=false;
                  newNode.ifNode=true;
                  
                  //Add a link going from the past node to the new node.
                  last.addEdge(newNode);

                  cfg.addLast(newNode);
                  action.push(newNode);
                  
	               for(int nodeNumber=1; 
	                     nodeNumber<totalNumberOfPredicates; ++nodeNumber) {
	                  lineNumber++;
	                  Node n = new Node(lineNumber);
	                  last.addEdge(n);
	               }
	            } else if(line.contains("while")) {
	               if(complexPredicate) {
	                  
	               } else {
	                  
	               }
	            } else if(line.contains("do")){
	               if(complexPredicate) {
                     
                  } else {
                     
                  }
	            } else if(line.contains("else")){
	               if(!action.peek().ifNode) {
	                  /*
	                   * We have encountered an else node but the last action
	                   * wasn't 'if'.  Time to bail out.  This code shouldn't
	                   * be necessary and might be removed at a later time.
	                   * UNNECESSARY_EDGE_CASE 
	                  */
	                  throw new TokenMismatchException("Line Number:"+lineNumber
	                        +"\nread in else, found not if\n");
	               }
	               /*
	                * Since we have a matching if statement, we can process the
	                * else clause
	                */
	               Node lastAction=action.pop();
	               Node elseAction=new Node(lineNumber);
	               //Create an edge from the if to this else.
	               lastAction.edges.add(lastAction.lineNumber+","+lineNumber);
	               elseAction.simple=true;
	               /*
	                * Add the else node to the list of nodes.  This might
	                * create an excessive amount of nodes.  If it does then
	                * I will add rules to take care of that at a later time.
	                */
	               cfg.addLast(elseAction); 
	            }
	         } else {
	            /*
	             * If the node list is empty, populate it, otherwise form
	             * proper links between the last element in the node list and 
	             * the current line number.
	             * Note: if the previous element is a predicate statement then
	             * we need to generate a new node, otherwise we will just lump
	             * the statement nodes together.
	            */
	            if(cfg.size()==0) {
	               Node n = new Node(lineNumber);
	               n.simple=true;
	               cfg.add(n);
	            }
	            else {
	               //Non-empty list, but we have an element in the list already
	               if(cfg.getLast().simple) {
	                  //non-predicate last node, update the line number
	                 cfg.getLast().lineNumber=lineNumber; 
	               } else {
	                  //predicate last node
	                  if(action.isEmpty()){
	                     //Last item on the stack is 
	                  }
	               }
	            }
	         }
	      }
	   }
	   catch(TokenMismatchException e){
	      e.printStackTrace();
	   }
	   catch(Exception e) {
	      e.printStackTrace();
	      System.exit(-1);
	   }
	   
	}
}
