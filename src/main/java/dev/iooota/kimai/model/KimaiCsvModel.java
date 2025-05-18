package dev.iooota.kimai.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Modello dati che rappresenta una riga del CSV esportato da Kimai.
 * @author Alan
 */
public class KimaiCsvModel {

    private LocalDateTime starTime;
    private LocalDateTime endTime;
    private Integer duration;
    private String user;
    private String username;
    private String project;
    private String activity;
    private String client;
    private String description;
    private String exported;
    private List<String> tags;

    public KimaiCsvModel(LocalDateTime from, LocalDateTime to, String duration, String utente, String nome,
                         String cliente, String progetto, String attivita, String desc, String esportate, String tags) {
        this.starTime = from;
        this.endTime = to;
        this.duration = Integer.valueOf(duration);
        this.user = utente;
        this.username = nome;
        this.client = cliente;
        this.project = progetto;
        this.activity = attivita;
        this.description = desc;
        this.exported = esportate;
        this.tags = Arrays.stream(tags.split(",")).filter(s -> !s.isBlank()).collect(Collectors.toList());
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
