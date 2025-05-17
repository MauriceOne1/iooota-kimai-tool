package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class KimaiDownloader {

    public static Path scaricaCsvDaUrl(String url, String fileNameLocale) throws IOException {
        Path destinazione = Paths.get(fileNameLocale);

        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, destinazione, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("CSV scaricato in: " + destinazione.toAbsolutePath());
            return destinazione;
        }
    }
}
