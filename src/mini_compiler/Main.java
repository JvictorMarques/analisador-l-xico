package mini_compiler;

import lexical.Scanner;
import lexical.Token;

public class Main {
	/*
	* GRUPO:
	*
	* DIMAS LIMA DA COSTA
	* JOÃO VICTOR MARQUES DA SILVA
	* MARÍLIA SANTOS DO NASCIMENTO
	* NATHYANE OLIVEIRA
	* THIAGO VASCONCELOS
	* */
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner("programa.mc");
		Token tk;
		do {
			tk = sc.nextToken();
			System.out.println(tk);
		} while (tk!=null);
	}

}
