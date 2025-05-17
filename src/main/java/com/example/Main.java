package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;

import com.example.utils.KimaiDownloader;

/**
 * Classe di avvio per l'elaborazione di un file CSV esportato da Kimai e
 * generazione dell'output in formato ODS o CSV.
 *
 * <p>
 * Il programma accetta da uno a tre argomenti:
 * <ul>
 * <li><strong>1 argomento</strong>: solo il file di input. L'output sarà
 * generato automaticamente in formato ODS con un nome basato sul mese
 * corrente.</li>
 * <li><strong>2 argomenti</strong>: file di input e percorso file di output. Il
 * formato sarà ODS.</li>
 * <li><strong>3 argomenti</strong>: file di input, output e tipo di formato
 * (ODS, CSV, RIMBORSO o API).</li>
 * </ul>
 * </p>
 *
 * <p>
 * Se si sceglie il formato ODS o RIMBORSO, il programma chiederà all’utente di
 * inserire ulteriori informazioni da salvare nel file, come nome e altri
 * parametri.
 * </p>
 *
 * <p>
 * Il file CSV di input deve essere un’esportazione da Kimai con almeno le
 * colonne richieste dal parser {@link KimaiCsv}.
 * </p>
 *
 * <p>
 * Se viene fornito un file di output già esistente, l'applicazione si
 * interrompe per evitare sovrascritture.
 * </p>
 *
 * <p>
 * Il formato predefinito di output è ODS.
 * </p>
 *
 * @author Maurice
 * @author Alan
 */
public class Main {

    /**
     * Il nome dell'applicazione per i comuni mortali che la invocheranno
     */
    public static final String APPLICATION_NAME = "IoootaKimaiTool";

    /**
     * Stampa un breve messaggio di utilizzo del programma.
     */
    /**
     * Stampa un messaggio di aiuto sull'uso del programma.
     */
    private static void printUsage() {
        System.out.println("\n=== " + APPLICATION_NAME + " ===");
        System.out.println("Strumento per generare report in formato ODS o CSV da un file esportato da Kimai.");
        System.out.println();
        System.out.println("PARAMETRI:");
        System.out.println("  INPUT_FILE           Percorso del file CSV esportato da Kimai (obbligatorio)");
        System.out.println("  OUTPUT_FILE          Percorso del file ODS/CSV da generare (opzionale)");
        System.out.println("  FORMATO              Tipo di output: ODS (default), CSV, RIMBORSO, API");
        System.out.println();
        System.out.println("NOTE:");
        System.out.println("  - Se OUTPUT_FILE non è specificato, verrà generato un nome automatico");
        System.out.println("    basato sul mese corrente (es: exportRimborsoMaggio2025.ods).");
        System.out.println("  - Alcuni formati richiedono input interattivo (es. nome utente, km, ecc.).");
        System.out.println("  - L'applicazione si interrompe se il file di output esiste già.");
        System.out.println();
    }

    /**
     * Metodo principale. Legge il file CSV di input e produce un file di output
     * in formato ODS o CSV.
     *
     * @param args argomenti da linea di comando:
     * <ul>
     * <li>args[0] = file CSV di input</li>
     * <li>args[1] = file di output (opzionale)</li>
     * <li>args[2] = formato di output (CSV o ODS, opzionale)</li>
     * </ul>
     * @throws IOException se si verifica un errore durante la lettura/scrittura
     * dei file
     */
    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        Path inputFile = Paths.get(args[0]);
        Path outputFile;

        if (args.length >= 2) {
            outputFile = Paths.get(args[1]);
            if (Files.exists(outputFile)) {
                System.err.println("File di output già esistente: " + outputFile);
                System.exit(1);
            }
        } else {
            // Genera nome automatico tipo "exportRimborsoMaggio2025.ods"
            LocalDate oggi = LocalDate.now();
            String nomeMese = oggi.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            nomeMese = nomeMese.substring(0, 1).toUpperCase() + nomeMese.substring(1).replace(" ", "");

            String fileName = "exportRimborso" + nomeMese + oggi.getYear() + ".ods";

            // Percorso in ~/Documents oppure usa la dir temporanea
            outputFile = Paths.get(System.getProperty("user.home"), "Documents", fileName);
            System.out.println("Nessun file di output specificato. Salvataggio automatico in: " + outputFile);

            if (Files.exists(outputFile)) {
                System.err.println("File già esistente: " + outputFile);
                System.exit(1);
            }
        }
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
                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.print("Inserisci il tuo nome e cognome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Vuoi inserire il mese precedente? (s/n): ");
                    String risposta = scanner.nextLine().trim().toLowerCase();
                    boolean usaMesePrecedente = risposta.equals("s") || risposta.equals("si");

                    new KimaiOds(outputFile, nome, usaMesePrecedente, kimaiCsv.getEntries()).esegui();
                }
                break;
            }
            case "rimborso": {
                KimaiCsv kimaiCsv = new KimaiCsv(inputFile);

                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.print("Inserisci il tuo nome e cognome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Inserisci il tuo codice fiscale: ");
                    String codiceFiscale = scanner.nextLine();
                    System.out.print("Inserisci i chilometri del tratto lavoro-ufficio + ufficio-lavoro (es. 28.4): ");
                    double km = Double.parseDouble(scanner.nextLine());
                    System.out.println("ATTENZIONE! verrà creata una singola riga per giornata! (comprensiva di andata e ritorno)");
                    System.out.print("Inserisci il coefficiente ACI (es. 0.39): ");
                    double coefficiente = Double.parseDouble(scanner.nextLine());
                    System.out.print("Inserisci il percorso effettuato (es: casa - lavoro / lavoro - casa): ");
                    String tragitto = scanner.nextLine();
                    System.out.print("Inserisci la descrizione: ");
                    String descrizione = scanner.nextLine();
                    new KimaiRimborso(outputFile, nome, codiceFiscale, km, coefficiente, kimaiCsv.getEntries(), descrizione, tragitto).esegui();
                }
                break;
            }

            case "csv": {
                KimaiCsv kimaiCsv = new KimaiCsv(inputFile);
                System.out.println(kimaiCsv.getEntries());
                break;
            }
            case "api": {
                String url = "https://raw.githubusercontent.com/plotly/datasets/master/2014_usa_states.csv";
                String fileLocale = "export.csv";
                Path inputFileAPI = KimaiDownloader.scaricaCsvDaUrl(url, fileLocale);
                System.out.println("Contenuto del CSV:");
                Files.lines(inputFileAPI).forEach(System.out::println);
            }
            default: {
                System.err.println("Formato di output inatteso: " + outputFormat);
                System.exit(2);
            }
        }
    }
}
