package com.example;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class KimaiCsv {
	public static final String DEFAULT_CSV_SEPARATOR = ",";
	private List<KimaiCsvModel> entries;
	/**
	 * Cosa a caso
	 * @param inputFile
	 * @throws IOException
	 */
	public KimaiCsv(String inputFile) throws IOException {
		this(inputFile, DEFAULT_CSV_SEPARATOR);
	}

	public KimaiCsv(String inputFile, String separator) throws IOException {
		Path inputFilePath = Paths.get(inputFile);

		if (!Files.exists(inputFilePath)) {
			System.out.println("Errore: il file \"" + inputFile + "\" non è stato trovato.");
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(inputFilePath);) {
			reader.readLine();
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

			    // Salta se mancano i campi base
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
	
	public List<KimaiCsvModel> getEntries() {
		return entries;
	}

	public static void main(String[] args) throws IOException {
		new KimaiCsv("E:\\Progetti\\Eclipse Workspace\\kimai-tool\\src\\main\\resources\\kimai-export.csv");
	}
}

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

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
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
		return Objects.hash(activity, client, description, duration, endTime, exported, project, starTime, tags, user,
				username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KimaiCsvModel other = (KimaiCsvModel) obj;
		return Objects.equals(activity, other.activity) && Objects.equals(client, other.client)
				&& Objects.equals(description, other.description) && Objects.equals(duration, other.duration)
				&& Objects.equals(endTime, other.endTime) && Objects.equals(exported, other.exported)
				&& Objects.equals(project, other.project) && Objects.equals(starTime, other.starTime)
				&& Objects.equals(tags, other.tags) && Objects.equals(user, other.user)
				&& Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		return "KimaiCsvModel [starTime=" + starTime + ", endTime=" + endTime + ", duration=" + duration + ", user="
				+ user + ", username=" + username + ", project=" + project + ", activity=" + activity + ", client="
				+ client + ", description=" + description + ", exported=" + exported + ", tags=" + tags + "]";
	}

}
