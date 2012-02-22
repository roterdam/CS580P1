/**
 * @author Mike Ballard
 * @version Alpha
 */
package cs580.p1;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
   public static void main(String[] arg) {
      ControlFlowGraph cfg = new ControlFlowGraph();
      Scanner in = new Scanner(System.in);
      try {
         for (int lineNumber = 1; true; lineNumber++) {
            String line = in.nextLine();
            cfg.parse(line, lineNumber);
         }
      } catch (NoSuchElementException e) {
         /*
          * IOException should be thrown on the subsequent read after all of the
          * data has been consumed, so we can safely ignore it.
          */
      }
      System.out.println(cfg.toString());
   }
}

// TODO: single line control statements, 2+ predicates
