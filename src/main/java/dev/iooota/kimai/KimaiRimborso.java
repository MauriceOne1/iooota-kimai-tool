package dev.iooota.kimai;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.spreadsheet.Sheet;

import dev.iooota.kimai.model.KimaiCsvModel;
import dev.iooota.kimai.utils.OdsTemplateLoader;

/**
 * Classe che genera un file ODS di rimborso chilometrico partendo dai dati
 * esportati da Kimai.
 * <p>
 * Il file viene compilato usando un template ODS predefinito, in cui vengono
 * inseriti dati statici (nome, codice fiscale, data, coefficiente) e righe
 * dinamiche per ogni giorno con presenza taggata "ufficio".
 * </p>
 * 
 * @author Maurice
 */
public class KimaiRimborso {

    private final Path outputFile;
    private final String nome;
    private final String codiceFiscale;
    private final double km;
    private final double coefficienteAci;
    private final List<KimaiCsvModel> entries;
    private final String descrizione;
    private final String tragitto;

    /**
     * Costruttore della classe KimaiRimborso.
     *
     * @param outputFile      Percorso del file ODS da generare
     * @param nome            Nome e cognome del dipendente
     * @param codiceFiscale   Codice fiscale del dipendente
     * @param km              Chilometri percorsi (andata e ritorno)
     * @param coefficienteAci Coefficiente chilometrico ACI
     * @param entries         Lista di attività esportate da Kimai
     * @param descrizione     Testo da inserire nella colonna "Descrizione"
     * @param tragitto        Testo da inserire nella colonna "Percorso effettuato"
     */
    public KimaiRimborso(
            Path outputFile,
            String nome,
            String codiceFiscale,
            double km,
            double coefficienteAci,
            List<KimaiCsvModel> entries,
            String descrizione,
            String tragitto
    ) {
        this.outputFile = outputFile;
        this.nome = nome;
        this.codiceFiscale = codiceFiscale;
        this.km = km;
        this.coefficienteAci = coefficienteAci;
        this.entries = entries;
        this.descrizione = descrizione;
        this.tragitto = tragitto;
    }

    /**
     * Compila il template ODS inserendo:
     * <ul>
     *   <li>Nome e codice fiscale nei campi intestazione</li>
     *   <li>Data di generazione e coefficiente ACI nei campi dedicati</li>
     *   <li>Una riga per ogni giorno con presenza "ufficio", con percorso, descrizione e km</li>
     * </ul>
     * Il file viene salvato nel percorso specificato in outputFile.
     */
    public void esegui() {
        try {
            // Caricamento template
            OdsTemplateLoader loader = new OdsTemplateLoader("rimborsoTemplate.ods");
            InputStream templateStream = loader.resolveTemplateFile();
            Sheet sheet = new ODPackage(templateStream).getSpreadSheet().getSheet(0);

            // Dati statici
            sheet.setValueAt(nome, 3, 5);                        // D6
            sheet.setValueAt(codiceFiscale, 3, 6);               // D7
            sheet.setValueAt(coefficienteAci, 3, 31);            // D32
            sheet.setValueAt(LocalDate.now().toString(), 2, 46); // C47

            // Filtra giorni con tag "ufficio"
            Set<LocalDate> giorniUfficio = new HashSet<>();
            for (KimaiCsvModel model : entries) {
                if (model.getTags().stream().anyMatch(tag -> tag.equalsIgnoreCase("ufficio"))) {
                    giorniUfficio.add(model.getStarTime().toLocalDate());
                }
            }

            List<LocalDate> dateOrdinate = giorniUfficio.stream().sorted().toList();

            // Scrittura righe dinamiche da B17 (riga 16, zero-based)
            int rigaCorrente = 16;
            for (LocalDate data : dateOrdinate) {
                sheet.setValueAt(data.toString(), 1, rigaCorrente);    // B: Data
                sheet.setValueAt(tragitto, 2, rigaCorrente);           // C: Percorso
                sheet.setValueAt(descrizione, 3, rigaCorrente);        // D: Descrizione
                sheet.setValueAt(km, 4, rigaCorrente);                 // E: Km percorsi
                rigaCorrente++;
            }

            // Salvataggio file
            sheet.getSpreadSheet().saveAs(outputFile.toFile());
            System.out.println("File ODS rimborso salvato in: " + outputFile);

        } catch (Exception e) {
            System.out.println("Errore durante la generazione del rimborso:");
            e.printStackTrace();
        }
    }
}
