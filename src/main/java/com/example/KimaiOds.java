package com.example;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

public class KimaiOds {

	private final Path outputFile;
	private final String templateOds;
	private final String nome;
	private final boolean mesePrecedente;
	private Set<Integer> giorniConPresenza;

	public KimaiOds(Path outputFile, String templateOds, String nome, boolean mesePrecedente, Set<Integer> giorniConPresenza) {
		this.outputFile = outputFile;
		this.templateOds = templateOds;
		this.nome = nome;
		this.mesePrecedente = mesePrecedente;
		this.giorniConPresenza = giorniConPresenza;
	}

	public void esegui() {
//		if (!Files.exists(outputFile)) {
//			System.out.println("File CSV non trovato: " + outputFile);
//			return;
//		}

		try {
			// Apri il file ODS
			File file = new File(templateOds);
			Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);

			// Imposta il nome in G6 (colonna 6, riga 5)
			sheet.setValueAt(nome, 6, 5);

			// Calcola il mese da scrivere
			LocalDate oggi = LocalDate.now();
			LocalDate mese = mesePrecedente ? oggi.minusMonths(1) : oggi;

			String nomeMese = mese.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
			String meseAnno = nomeMese.substring(0, 1).toUpperCase() + nomeMese.substring(1) + " " + mese.getYear();

			// Scrivi in K6 (colonna 10, riga 5)
			sheet.setValueAt(meseAnno, 10, 5);

			// Per ogni giorno con presenza, scrivi "8" nella cella B(9 + giorno - 1)
			for (Integer giorno : giorniConPresenza) {
				int row = 9 + (giorno - 1); // B10 è row 9, giorno 1 → row 9
				sheet.setValueAt(8, 1, row);// colonna B = indice 1
			}

			// Salva il file ODS compilato
			sheet.getSpreadSheet().saveAs(outputFile.toFile());
			System.out.println("File ODS compilato salvato come: " + outputFile);

		} catch (Exception e) {
			System.out.println("Errore durante l’elaborazione:");
			e.printStackTrace();
		}
	}

}
