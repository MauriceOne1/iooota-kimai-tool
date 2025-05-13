package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe principale per l'importazione e l'elaborazione di un file CSV esportato da Kimai.
 * Ogni riga del file viene convertita in un oggetto {@link KimaiCsvModel}.
 * Supporta il parsing con separatori personalizzabili.
 * <p>Il file CSV deve avere almeno 12 colonne per essere considerato valido.</p>
 * 
 * @author Maurice
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

/**
 * Modello dati per rappresentare una riga del file CSV esportato da Kimai.
 */
class KimaiCsvModel {

    private LocalDateTime starTime, endTime;
    private Integer duration;
    private String user;
    private String username;
    private String project;
    private String activity;
    private String client;
    private String description;
    private String exported;
    private List<String> tags;

    /**
     * Costruisce una nuova istanza di {@link KimaiCsvModel}.
     *
     * @param from        data e ora di inizio
     * @param to          data e ora di fine
     * @param duration    durata in minuti (in formato stringa intera)
     * @param utente      ID o nome utente
     * @param nome        nome completo dell'utente
     * @param cliente     nome del cliente
     * @param progetto    nome del progetto
     * @param attivita    attività svolta
     * @param desc        descrizione dell'attività
     * @param esportate   stato esportazione (es. "yes"/"no")
     * @param tags        tag separati da due punti ":"
     */
    public KimaiCsvModel(LocalDateTime from, LocalDateTime to, String duration, String utente, String nome, String cliente,
                         String progetto, String attivita, String desc, String esportate, String tags) {
        this.starTime = from;
        this.endTime = to;
        this.duration = Integer.parseInt(duration);
        this.user = utente;
        this.username = nome;
        this.client = cliente;
        this.project = progetto;
        this.activity = attivita;
        this.description = desc;
        this.exported = esportate;
        this.tags = Arrays.stream(tags.split(":")).collect(Collectors.toList());
    }

    public LocalDateTime getStarTime() {
        return starTime;
    }

    public void setStarTime(LocalDateTime starTime) {
        this.starTime = starTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExported() {
        return exported;
    }

    public void setExported(String exported) {
        this.exported = exported;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, client, description, duration, endTime, exported, project, starTime, tags, user, username);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KimaiCsvModel other = (KimaiCsvModel) obj;
        return Objects.equals(activity, other.activity) &&
               Objects.equals(client, other.client) &&
               Objects.equals(description, other.description) &&
               Objects.equals(duration, other.duration) &&
               Objects.equals(endTime, other.endTime) &&
               Objects.equals(exported, other.exported) &&
               Objects.equals(project, other.project) &&
               Objects.equals(starTime, other.starTime) &&
               Objects.equals(tags, other.tags) &&
               Objects.equals(user, other.user) &&
               Objects.equals(username, other.username);
    }

    @Override
    public String toString() {
        return "KimaiCsvModel [starTime=" + starTime + ", endTime=" + endTime + ", duration=" + duration +
               ", user=" + user + ", username=" + username + ", project=" + project + ", activity=" + activity +
               ", client=" + client + ", description=" + description + ", exported=" + exported + ", tags=" + tags + "]";
    }
}
