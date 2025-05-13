package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
	private static void printUsage() {
		System.out.println("Usage");
		System.out.println("IoootaKimaiTool INPUT_FILE [OUTPUT_FILE] [CSV|ODS]");
	}

	public static void main(String[] args) throws IOException {

		if (args.length < 1) {
			printUsage();
			System.exit(1);
		}

		Path inputFile = Paths.get(args[0]);
		Path outputFile = Paths.get(System.getProperty("java.io.tmpdir"), "Kimai.output");
		String outputFormat = "ODS";
		String presenzeTemplate = "E:\\Progetti\\Eclipse Workspace\\kimai-tool\\src\\main\\resources\\presenzeTemplate.ods";

		switch (args.length) {
		case 3:
			outputFormat = args[2];
		case 2:
			outputFile = Paths.get(args[1]);
			if (Files.exists(outputFile)) {
				System.err.println("File di output già esistente: " + outputFile);
				System.exit(1);
			}
			break;
		case 1:
			if (!Files.exists(inputFile) || !Files.isRegularFile(inputFile)) {
				System.err.println("File di input non trovato: " + inputFile);
				System.exit(1);
			}
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + args.length);
		}

		switch (outputFormat) {
		case "ods":
		case "ODS": {
			KimaiCsv kimaiCsv = new KimaiCsv(inputFile);
			Set<Integer> giorniConPresenza = new HashSet<>();
			for (KimaiCsvModel model : kimaiCsv.getEntries()) {
				giorniConPresenza.add(model.getStarTime().getDayOfMonth());
			}

			try (Scanner scanner = new Scanner(System.in)) {
				System.out.print("Inserisci il tuo nome e cognome: ");
				String nome = scanner.nextLine();
				System.out.print("Vuoi inserire il mese precedente? (s/n): ");
				String risposta = scanner.nextLine().trim().toLowerCase();
				boolean usaMesePrecedente = risposta.equals("s") || risposta.equals("si");
				new KimaiOds(outputFile, presenzeTemplate, nome, usaMesePrecedente, giorniConPresenza).esegui();
			}
			break;
		}
		case "csv":
		case "CSV": {
			KimaiCsv kimaiCsv = new KimaiCsv(inputFile);
			System.out.println(kimaiCsv.getEntries());
			break;
		}
		default: {
			System.err.println("Argomento " + outputFormat + " inatteso");
			System.exit(2);
		}
		}
	}
}
