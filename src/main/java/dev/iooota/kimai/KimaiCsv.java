package dev.iooota.kimai;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import dev.iooota.kimai.model.KimaiCsvModel;

/**
 * Classe principale per l'importazione e l'elaborazione di un file CSV esportato da Kimai.
 * Ogni riga del file viene convertita in un oggetto {@link KimaiCsvModel}.
 * Supporta il parsing con separatori personalizzabili.
 * <p>Il file CSV deve avere almeno 12 colonne per essere considerato valido.</p>
 * 
 * @author Maurice
 * @author Alan
 */
public class KimaiCsv {

    /** Separatore di default per i file CSV. */
    public static final String DEFAULT_CSV_SEPARATOR = ",";

    private List<KimaiCsvModel> entries;

    /**
     * Crea una nuova istanza di {@link KimaiCsv} a partire da un file di input CSV.
     * Usa il separatore di default {@link #DEFAULT_CSV_SEPARATOR}.
     * 
     * @param inputFile percorso del file CSV come stringa
     * @throws IOException se il file non può essere letto
     */
    public KimaiCsv(String inputFile) throws IOException {
        this(Paths.get(inputFile), DEFAULT_CSV_SEPARATOR);
    }

    /**
     * Crea una nuova istanza di {@link KimaiCsv} a partire da un {@link Path}.
     * Usa il separatore di default {@link #DEFAULT_CSV_SEPARATOR}.
     * 
     * @param inputCsv percorso del file CSV
     * @throws IOException se il file non può essere letto
     */
    public KimaiCsv(Path inputCsv) throws IOException {
        this(inputCsv, DEFAULT_CSV_SEPARATOR);
    }

    /**
     * Crea una nuova istanza di {@link KimaiCsv} a partire da un {@link Path} e un separatore personalizzato.
     * Legge il file, scarta l'intestazione, e converte ogni riga in un {@link KimaiCsvModel} se valida.
     * 
     * @param inputFilePath percorso del file CSV
     * @param separator separatore usato nel CSV
     * @throws IOException se il file non può essere letto
     */
    public KimaiCsv(Path inputFilePath, String separator) throws IOException {

        if (!Files.exists(inputFilePath)) {
            System.out.println("Errore: il file \"" + inputFilePath + "\" non è stato trovato.");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(inputFilePath)) {
            reader.readLine(); // salta l’intestazione
            String line;
            this.entries = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] campi = line.split(separator, -1);
                if (campi.length < 12) {
                    System.out.println("Riga ignorata (colonne insufficienti): " + line);
                    continue;
                }

                String data = campi[0].replace("\"", "").trim();
                String da = campi[1].replace("\"", "").trim();
                String a = campi[2].replace("\"", "").trim();
                String duration = campi[3].replace("\"", "").trim();

                if (data.isEmpty() || da.isEmpty() || a.isEmpty() || duration.isEmpty()) {
                    System.out.println("Riga ignorata (data o orari vuoti): " + line);
                    continue;
                }

                try {
                    KimaiCsvModel model = new KimaiCsvModel(
                        LocalDateTime.of(LocalDate.parse(data), LocalTime.parse(da)),
                        LocalDateTime.of(LocalDate.parse(data), LocalTime.parse(a)),
                        duration,
                        campi[4].replace("\"", ""),
                        campi[5].replace("\"", ""),
                        campi[6].replace("\"", ""),
                        campi[7].replace("\"", ""),
                        campi[8].replace("\"", ""),
                        campi[9].replace("\"", ""),
                        campi[10].replace("\"", ""),
                        campi[11].replace("\"", "")
                    );
                    this.entries.add(model);
                } catch (Exception parseError) {
                    System.out.println("Riga ignorata per errore di parsing: " + line);
                }
            }
        }
    }

    /**
     * Restituisce tutte le entry caricate dal file CSV.
     * 
     * @return lista di {@link KimaiCsvModel}
     */
    public List<KimaiCsvModel> getEntries() {
        return entries;
    }

    /**
     * Metodo principale per testare la lettura del CSV.
     * 
     * @param args argomenti da linea di comando
     * @throws IOException se si verifica un errore durante la lettura del file
     */
    public static void main(String[] args) throws IOException {
        KimaiCsv parser = new KimaiCsv("E:\\Progetti\\Eclipse Workspace\\kimai-tool\\src\\main\\resources\\kimai-export.csv");
        parser.getEntries().forEach(System.out::println);
    }
}
