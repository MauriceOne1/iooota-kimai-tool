package com.example;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * Classe che si occupa di compilare un file ODS a partire da un template.
 * Viene usata per generare un foglio presenze mensile indicando i giorni con presenza.
 * Utilizza la libreria JOpenDocument.
 * 
 * <p>Il file compilato inserisce:
 * <ul>
 *   <li>Il nome in G6</li>
 *   <li>Il mese in K6</li>
 *   <li>Il numero "8" nei giorni con presenza nella colonna B</li>
 * </ul>
 * </p>
 * 
 * @author Maurice
 */
public class KimaiOds {

	private final Path outputFile;
	private final String templateOds;
	private final String nome;
	private final boolean mesePrecedente;
	private final Set<Integer> giorniConPresenza;

	/**
	 * Costruttore.
	 * 
	 * @param outputFile          percorso del file ODS da salvare
	 * @param templateOds         percorso del file template ODS da usare
	 * @param nome                nome della persona da scrivere nella cella G6
	 * @param mesePrecedente      true per usare il mese precedente, false per usare il mese corrente
	 * @param giorniConPresenza   insieme dei giorni del mese in cui si è rilevata una presenza
	 */
	public KimaiOds(Path outputFile, String templateOds, String nome, boolean mesePrecedente, Set<Integer> giorniConPresenza) {
		this.outputFile = outputFile;
		this.templateOds = templateOds;
		this.nome = nome;
		this.mesePrecedente = mesePrecedente;
		this.giorniConPresenza = giorniConPresenza;
	}

	/**
	 * Esegue la compilazione del file ODS.
	 * Inserisce il nome nella cella G6, il mese in K6, e per ogni giorno con presenza scrive "8" nella colonna B.
	 * Salva il file compilato nella destinazione specificata.
	 */
	public void esegui() {
		try {
			// Apri il file ODS dal template
			File file = new File(templateOds);
			Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);

			// Scrivi il nome nella cella G6 (colonna 6, riga 5)
			sheet.setValueAt(nome, 6, 5);

			// Calcola il mese corrente o precedente
			LocalDate oggi = LocalDate.now();
			LocalDate mese = mesePrecedente ? oggi.minusMonths(1) : oggi;

			// Format "Marzo 2025"
			String nomeMese = mese.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
			String meseAnno = nomeMese.substring(0, 1).toUpperCase() + nomeMese.substring(1) + " " + mese.getYear();

			// Scrivi il mese nella cella K6 (colonna 10, riga 5)
			sheet.setValueAt(meseAnno, 10, 5);

			// Scrivi "8" nella colonna B per ogni giorno con presenza
			for (Integer giorno : giorniConPresenza) {
				int row = 9 + (giorno - 1); // Riga per giorno (es. giorno 1 → riga 9)
				sheet.setValueAt(8, 1, row); // Colonna B = indice 1
			}

			// Salva il file ODS risultante
			sheet.getSpreadSheet().saveAs(outputFile.toFile());
			System.out.println("File ODS compilato salvato come: " + outputFile);

		} catch (Exception e) {
			System.out.println("Errore durante l’elaborazione:");
			e.printStackTrace();
		}
	}
}
