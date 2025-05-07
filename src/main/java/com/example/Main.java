package com.example;
import java.util.Scanner;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		
		if (args.length < 1) {
			System.err.println("Numero di argomenti errato!");
			System.exit(1);
		}
		
		String inputFile = "E:\\Progetti\\Eclipse Workspace\\kimai-tool\\src\\main\\resources\\kimai-export.csv";
		String presenzeTemplate = "E:\\Progetti\\Eclipse Workspace\\kimai-tool\\src\\main\\resources\\presenzeTemplate.ods";

		String arg0 = args[0];
		switch (arg0) {
		case "ods":
		case "ODS":
		    Scanner scanner = new Scanner(System.in);
		    System.out.print("Inserisci il tuo nome e cognome: ");
		    String nome = scanner.nextLine();
		    System.out.print("Vuoi inserire il mese precedente? (s/n): ");
		    String risposta = scanner.nextLine().trim().toLowerCase();
		    boolean usaMesePrecedente = risposta.equals("s") || risposta.equals("si");
		    new KimaiOds(inputFile, presenzeTemplate, nome, usaMesePrecedente).esegui();
		    break;
		case "csv":
		case "CSV":
			KimaiCsv kimaiCsv = new KimaiCsv(inputFile);
			System.out.println(kimaiCsv.getEntries());
			break;
		default:
			System.err.println("Argomento " + arg0 + " inatteso");
			System.exit(2);
		}
	}
}
