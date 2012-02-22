/**
 * @author Mike Ballard
 * @version Alpha
 */
package cs580.p1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

@SuppressWarnings("unused")
public class Main {
   public static void main(String[] arg) {
      ControlFlowGraph cfg = new ControlFlowGraph();
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      try {
         for (int lineNumber = 1; true; lineNumber++) {
            String line = in.readLine();
            cfg.parse(line, lineNumber);
         }
      } catch (IOException e) {
         /*
          * IOException should be thrown on the subsequent read after all of the
          * data has been consumed, so we can safely ignore it.
          */
      }
      System.out.println(cfg.toString());
   }
}

// TODO: single line control statements, 2+ predicates
