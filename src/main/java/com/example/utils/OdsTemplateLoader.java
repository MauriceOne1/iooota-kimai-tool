package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.Main;

import dev.dirs.ProjectDirectories;

/**
 * Classe per gestire il caricamento dinamico di un file ODS template,
 * dato il nome del file.
 *
 * <p>
 * Il file di template viene cercato, in ordine di priorità, nelle seguenti posizioni:
 * <ol>
 *   <li>Directory corrente di esecuzione</li>
 *   <li>Cartella di configurazione dell'applicazione sotto la home dell'utente
 *       (es: ~/.config/com.example/IOOOTA/IoootaKimaiTool/)</li>
 *   <li>Classpath del progetto (es. risorse incluse nel JAR)</li>
 * </ol>
 * </p>
 *
 * <p>
 * Questo approccio consente di riutilizzare la stessa logica per caricare
 * template diversi come <code>presenzeTemplate.ods</code>,
 * <code>rimborsoTemplate.ods</code>, ecc.
 * </p>
 *
 * @see ProjectDirectories
 */
public class OdsTemplateLoader {

    /**
     * Directory sotto la home dell'utente in cui viene cercato il template.
     */
    private final Path homeConfigDir;

    /**
     * Directory corrente di esecuzione.
     */
    private final Path currentDir;

    /**
     * Nome del file di template da cercare.
     */
    private final String templateFileName;

    /**
     * Crea un nuovo loader per il file template specificato.
     *
     * @param templateFileName nome del file ODS (es: "presenzeTemplate.ods", "rimborsoTemplate.ods")
     */
    public OdsTemplateLoader(String templateFileName) {
        ProjectDirectories baseDirs = ProjectDirectories.from("com.example", "IOOOTA", Main.APPLICATION_NAME);
        this.homeConfigDir = Paths.get(baseDirs.configDir);
        this.currentDir = Paths.get("").toAbsolutePath();
        this.templateFileName = templateFileName;
    }

    /**
     * Cerca e apre uno stream del file ODS specificato.
     *
     * @return InputStream del template trovato
     * @throws IOException se il file non viene trovato in nessuna delle posizioni attese
     */
    public InputStream resolveTemplateFile() throws IOException {
        Path templateInHomeCfg = homeConfigDir.resolve(templateFileName);
        Path templateInCwd = currentDir.resolve(templateFileName);

        if (Files.isRegularFile(templateInCwd) && Files.isReadable(templateInCwd)) {
            return Files.newInputStream(templateInCwd);
        }

        if (Files.isRegularFile(templateInHomeCfg) && Files.isReadable(templateInHomeCfg)) {
            return Files.newInputStream(templateInHomeCfg);
        }

        InputStream resourceStream = ClassLoader.getSystemResourceAsStream(templateFileName);
        if (resourceStream != null) {
            return resourceStream;
        }

        throw new IOException("Template non trovato: " + templateFileName);
    }
}
