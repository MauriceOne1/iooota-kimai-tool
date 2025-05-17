package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Classe di avvio per l'elaborazione di un file CSV esportato da Kimai e
 * generazione dell'output in formato ODS o CSV.
 *
 * <p>
 * Il programma accetta da uno a tre argomenti:
 * <ul>
 *   <li><strong>1 argomento</strong>: solo il file di input. L'output sarà generato in una directory temporanea, in formato ODS.</li>
 *   <li><strong>2 argomenti</strong>: file di input e percorso file di output. Il formato sarà ODS.</li>
 *   <li><strong>3 argomenti</strong>: file di input, output e tipo di formato (ODS o CSV).</li>
 * </ul>
 * </p>
 *
 * <p>
 * Se si sceglie il formato ODS, il programma chiederà all’utente di inserire il proprio nome e se si vuole generare
 * il file per il mese corrente o per quello precedente. L'output ODS sarà generato compilando un template con i giorni di presenza.
 * </p>
 *
 * <p>
 * Il file CSV di input deve essere un’esportazione da Kimai con almeno le colonne richieste dal parser {@link KimaiCsv}.
 * </p>
 * 
 * <p>
 * Se viene fornito un file di output già esistente, l'applicazione si interrompe per evitare sovrascritture.
 * </p>
 * 
 * <p>
 * Il formato predefinito di output è ODS.
 * </p>
 * 
 * @author Maurice
 */

public class Main {

	/**
	 * Il nome dell'applicazione per i comuni mortali che la invocheranno
	 */
	public static final String APPLICATION_NAME = "IoootaKimaiTool";

    /**
     * Stampa un breve messaggio di utilizzo del programma.
     */
    private static void printUsage() {
        System.out.println("Usage");
        System.out.println(APPLICATION_NAME + " INPUT_FILE [OUTPUT_FILE] [CSV|ODS]");
    }

    /**
     * Metodo principale.
     * Legge il file CSV di input e produce un file di output in formato ODS o CSV.
     * 
     * @param args argomenti da linea di comando:
     *             <ul>
     *                 <li>args[0] = file CSV di input</li>
     *                 <li>args[1] = file di output (opzionale)</li>
     *                 <li>args[2] = formato di output (CSV o ODS, opzionale)</li>
     *             </ul>
     * @throws IOException se si verifica un errore durante la lettura/scrittura dei file
     */
    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        Path inputFile = Paths.get(args[0]);
        Path outputFile = Paths.get(System.getProperty("java.io.tmpdir"), "Kimai.output");
        String outputFormat = "ODS";

        // Parsing degli argomenti
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
                throw new IllegalArgumentException("Numero di argomenti inatteso: " + args.length);
        }

        // Elaborazione in base al formato di output scelto
        switch (outputFormat.toLowerCase()) {
            case "ods": {
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

                    new KimaiOds(outputFile, nome, usaMesePrecedente, giorniConPresenza).esegui();
                }
                break;
            }
            case "csv": {
                KimaiCsv kimaiCsv = new KimaiCsv(inputFile);
                System.out.println(kimaiCsv.getEntries());
                break;
            }
            default: {
                System.err.println("Formato di output inatteso: " + outputFormat);
                System.exit(2);
            }
        }
    }
}
