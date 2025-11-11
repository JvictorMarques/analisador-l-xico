package mini_compiler;

import exceptions.SyntacticException;
import lexical.Scanner;
import lexical.Token;
import syntactic.Parser;

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
	public static void main(String[] args) {
		Scanner sc = new Scanner("programa.mc");
		try {
			Parser parser = new Parser(sc);
			parser.parse();
			System.out.println("Compilation successful");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
