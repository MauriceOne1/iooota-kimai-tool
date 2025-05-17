package dev.iooota.kimai;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.spreadsheet.Sheet;

import dev.iooota.kimai.model.KimaiCsvModel;
import dev.iooota.kimai.utils.OdsTemplateLoader;

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
 * @author Alan
 */
public class KimaiOds {

	private static final String TAG_ORE_SMART = "smart";
	/**
	 * Tag usato per identificare le ore di ufficio nel CSV
	 */
	private static final String TAG_ORE_UFFICIO = "ufficio";
	private final Path outputFile;
	private final String nome;
	private final boolean mesePrecedente;
	private final List<KimaiCsvModel> csvModelList;

	/**
	 * Costruttore.
	 * 
	 * @param outputFile          percorso del file ODS da salvare
	 * @param nome                nome della persona da scrivere nella cella G6
	 * @param mesePrecedente      true per usare il mese precedente, false per usare il mese corrente
	 * @param csvModelList        lista entry dal CSV originario
	 */
	public KimaiOds(Path outputFile, String nome, boolean mesePrecedente, List<KimaiCsvModel> csvModelList) {
		this.outputFile = outputFile;
		this.nome = nome;
		this.mesePrecedente = mesePrecedente;
		this.csvModelList = csvModelList;
	}

	/**
	 * Esegue la compilazione del file ODS.
	 * Inserisce il nome nella cella G6, il mese in K6, e per ogni giorno con presenza scrive "8" nella colonna B.
	 * Salva il file compilato nella destinazione specificata.
	 */
	public void esegui() {
		try {
			// Apri il file ODS dal template
			OdsTemplateLoader loader = new OdsTemplateLoader("presenzeTemplate.ods");
			InputStream templateStream = loader.resolveTemplateFile();
			Sheet sheet = new ODPackage(templateStream).getSpreadSheet().getSheet(0);
			
			Map<Integer, List<Long>> oreUfficio = new HashMap<>();
			Map<Integer, List<Long>> oreSmart = new HashMap<>();

			for (KimaiCsvModel model : csvModelList) {
				Map<Integer, List<Long>> map;
				LocalDateTime startTime = model.getStarTime();
				LocalDateTime endTime = model.getEndTime();

				if (isOreUfficio(model)) {
					map = oreUfficio;
				}
				if (isOreSmart(model)) {
					map = oreSmart;
				}

				if (!map.containsKey(startTime)) {
					map.put(startTime.getDayOfMonth(), new ArrayList<>());
				}
				List<Long> list = oreUfficio.get(startTime);

				long hours = ChronoUnit.HOURS.between(startTime, endTime);
				list.add(hours);
			}

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
			for (Integer giorno : getDaysList(oreUfficio, oreSmart)) {
				int row = 9 + (giorno - 1); // Riga per giorno (es. giorno 1 → riga 9)
				if (oreUfficio.containsKey(giorno)) {
					List<Long> list = oreUfficio.get(giorno);
					int sum = 0;
					for (long l : list) {
						sum += l;
					}
					sheet.setValueAt(sum, 1, row); // Colonna B = indice 1
				}
				if (oreSmart.containsKey(giorno)) {
					List<Long> list = oreSmart.get(giorno);
					int sum = 0;
					for (long l : list) {
						sum += l;
					}
					sheet.setValueAt(8, 2, row); // Colonna C = indice 2
				}
				
			}

			// Salva il file ODS risultante
			sheet.getSpreadSheet().saveAs(outputFile.toFile());
			System.out.println("File ODS compilato salvato come: " + outputFile);

		} catch (Exception e) {
			System.out.println("Errore durante l’elaborazione:");
			e.printStackTrace();
		}
	}

	private Set<Integer> getDaysList(Map<Integer, List<Long>> oreUfficio, Map<Integer, List<Long>> oreSmart) {

		Set<Integer> result = new HashSet<>();

		result.addAll(oreUfficio.keySet());
		result.addAll(oreSmart.keySet());

		return result;
	}

	private boolean isOreSmart(KimaiCsvModel model) {
		return model.getTags().contains(TAG_ORE_SMART);
	}

	private boolean isOreUfficio(KimaiCsvModel model) {
		return model.getTags().contains(TAG_ORE_UFFICIO);
	}
}
