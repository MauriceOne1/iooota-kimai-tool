# Iooota: Kimai export tool 📅

![Java](https://img.shields.io/badge/language-Java-007396?style=flat-square&logo=java)  ![Status](https://img.shields.io/badge/status-in%20development-orange?style=flat-square)  ![Platform](https://img.shields.io/badge/platform-cli-lightgrey?style=flat-square)

Strumento CLI per la generazione automatica di report presenze e rimborsi chilometrici a partire da un file CSV esportato da **Kimai**.  
Produce file ODS compilati in base a template predefiniti.

---

## ⚙️ Funzionalità

- ✅ Elabora i dati esportati da Kimai  
- ✅ Genera automaticamente un **report delle presenze mensili** in formato `.ods`  
- ✅ Compila automaticamente un **foglio di rimborso chilometrico**  
- ✅ Riconosce le giornate **con attività** e le marca come giorni lavorativi  
- ✅ Riconosce le giornate **con tag `ufficio`** per il rimborso  
- ✅ Chiede all’utente:
  - il **nome** da inserire
  - il **codice fiscale**
  - i **chilometri totali** (A/R)
  - il **coefficiente ACI**
  - il **tragitto** e la **descrizione**
  - se usare il **mese corrente o precedente** (per le presenze)  
- ✅ Inserisce **automaticamente la data di compilazione**  
- ✅ Verifica che il file di output non esista già   

---

## ▶️ Utilizzo

### Argomenti

- INPUT_FILE (obbligatorio): percorso del CSV esportato da Kimai  
- OUTPUT_FILE (opzionale): percorso file di output. Se omesso, viene generato automaticamente  
- MODALITÀ (opzionale): tipo di elaborazione da eseguire (`ODS` è il valore predefinito)  
  - `ODS`: genera un report presenze  
  - `RIMBORSO`: genera un foglio per rimborso chilometrico  
  - `CSV`: stampa i dati grezzi su console  
  - `API`: usa un CSV dimostrativo da remoto 

### Esempi

Generazione del foglio rimborso chilometrico a partire da un CSV esportato da Kimai:

```bash
mvn exec:java "-Dexec.args=src/main/resources/kimai-export.csv export.ods rimborso"
```

---

## ⛓️ Requisiti

- Java 17 o superiore  
- Maven  
- Libreria jOpenDocument 1.3  
- Un file `.csv` esportato da Kimai  

---

## 📦 Output

| Modalità   | Descrizione                                                                 |
|------------|------------------------------------------------------------------------------|
| ODS        | Compila un report mensile delle presenze                                     |
| RIMBORSO   | Compila automaticamente un foglio per rimborso chilometrico (richiede input) |
| CSV        | Mostra su console i dati letti dal file Kimai                                |
| API        | Scarica un CSV di esempio da URL esterno (per test/demo)                     |

---

## 🚧 Roadmap

- [x] Generazione report presenze mensili `.ods`  
- [x] Inserimento interattivo dati utente  
- [x] Compilazione rimborso chilometrico  
- [x] Gestione automatica del nome del file di output  
- [x] Inserimento data di compilazione  
- [x] Riconoscimento delle giornate in **smart working**  
- [ ] Riconoscimento automatico delle **giornate festive / chiusure aziendali**  
- [ ] Collegamento diretto alle **API Kimai**  
- [ ] Generazione **PDF** da ODS  
- [ ] Inserimento **firma automatica**  
- [ ] Interfaccia grafica minimale (GUI)  
- [ ] Inserimento permessi, ferie, ROL, malattia  
- [ ] Calcolo automatico del costo ACI in base a modello auto  

---

## 🤝 Contribuire

Pull request, issue e feedback sono benvenuti!  

---

*Realizzato tra una build fallita e l’altra, con la fondamentale supervisione di Alan.*
