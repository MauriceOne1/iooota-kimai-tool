package com.example;

import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.jopendocument.dom.spreadsheet.Sheet;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class KimaiOds {

	private final String inputCsv;
	private final String templateOds;
	private final String outputOds = "presenzeCompilate.ods";
	private final String nome;
	private final boolean mesePrecedente;

	public KimaiOds(String inputCsv, String templateOds, String nome, boolean mesePrecedente) {
		this.inputCsv = inputCsv;
		this.templateOds = templateOds;
		this.nome = nome;
		this.mesePrecedente = mesePrecedente;
	}

	public void esegui() {
		if (!Files.exists(Paths.get(inputCsv))) {
			System.out.println("File CSV non trovato: " + inputCsv);
			return;
		}

		try {
			KimaiCsv kimaiCsv = new KimaiCsv(inputCsv);

			// Ottieni tutti i giorni del mese presenti nel CSV (numeri da 1 a 31)
			Set<Integer> giorniConPresenza = kimaiCsv.getEntries().stream()
					.map(entry -> entry.getStarTime().getDayOfMonth()) // estrae il giorno (1-31)
					.collect(Collectors.toSet());

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
				sheet.setValueAt("8", 1, row); // colonna B = indice 1
			}

			// Salva il file ODS compilato
			File outputFile = new File(outputOds);
			sheet.getSpreadSheet().saveAs(outputFile);
			System.out.println("File ODS compilato salvato come: " + outputOds);

		} catch (Exception e) {
			System.out.println("Errore durante l’elaborazione:");
			e.printStackTrace();
		}
	}

}
