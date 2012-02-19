/**
 * @author Mike Ballard
 * @version Alpha
 */
package cs580.p1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

@SuppressWarnings("unused")
public class Main {
	public static void main(String[] arg) {
	   //Testing.
	   ControlFlowGraph cfg = new ControlFlowGraph();
	   cfg.parse("Test", 0);
	}
}