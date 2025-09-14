package mini_compiler;

import lexical.Scanner;
import lexical.Token;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner("programa.mc");
		Token tk;
		do {
			tk = sc.nextToken();
			System.out.println(tk);
		} while (tk!=null);
	}

}
