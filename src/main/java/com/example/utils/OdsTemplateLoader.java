package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.KimaiOds;
import com.example.Main;

import dev.dirs.ProjectDirectories;

/**
 * Classe per gestire il caricamento del template per l'output in formato ODS.
 * 
 * <p>
 * Il template da fornire è un file ODS che viene poi riempito
 * programmaticamente dalla classe {@link KimaiOds}.
 * 
 * <p>
 * Il file di template <code>{@value #TEMPLATE_FILE_NAME}</code> viene cercato
 * nelle seguenti posizioni, in ordine di priorità:
 * <ol>
 * <li>directory attuale
 * <li>sotto la directory {@value com.example.Main#APPLICATION_NAME} in un
 * percorso sotto la propria $HOME (OS dependent)
 * <li>classpath
 * </ol>
 * 
 * @see ProjectDirectories
 */
public class OdsTemplateLoader {

	/**
	 * Nome atteso per il file di template (FISSO)
	 */
	public static final String TEMPLATE_FILE_NAME = "presenzeTemplate.ods";

	/**
	 * Directory sotto la home dell'utente attuale nella quale verrà cercato il file
	 * di template, se presente.
	 * 
	 * @see ProjectDirectories#configDir
	 */
	private final Path homeConfigDir;

	/**
	 * Directory corrente, ovvero directory di lavoro attuale del processo JVM che
	 * richiama questa classe.
	 */
	private final Path currentDir;

	/**
	 * Crea una nuova istanza di {@link OdsTemplateLoader} inizializzando i path di
	 * ricerca interni.
	 */
	public OdsTemplateLoader() {
		ProjectDirectories baseDirs = ProjectDirectories.from("com.example", "IOOOTA", Main.APPLICATION_NAME);
		this.homeConfigDir = Paths.get(baseDirs.configDir);
		this.currentDir = Paths.get("").toAbsolutePath();
	}

	/**
	 * Ottiene un puntamento al file di template da utilizzare in base alle
	 * politiche di ricerca attuali.
	 * 
	 * @throws IOException in caso di errore di I/O in apertura dell'InputStream
	 */
	public InputStream resolveTemplateFile() throws IOException {
		Path templateInHomeCfg = homeConfigDir.resolve(TEMPLATE_FILE_NAME);
		Path templateInCwd = currentDir.resolve(TEMPLATE_FILE_NAME);

		if (Files.isRegularFile(templateInCwd) && Files.isReadable(templateInCwd)) {
			return Files.newInputStream(templateInCwd);
		}
		
		if (Files.isRegularFile(templateInHomeCfg) && Files.isReadable(templateInHomeCfg)) {
			return Files.newInputStream(templateInHomeCfg);
		}

		return ClassLoader.getSystemResourceAsStream(TEMPLATE_FILE_NAME);
	}

}
