# Kimai Presenze Tool 📅

![Java](https://img.shields.io/badge/language-Java-007396?style=flat-square&logo=java)  ![Status](https://img.shields.io/badge/status-in%20development-orange?style=flat-square)  ![Platform](https://img.shields.io/badge/platform-cli-lightgrey?style=flat-square)

Strumento per la generazione di un report presenze mensile a partire da un file CSV esportato da Kimai.  
Produce un file ODS compilato automaticamente secondo un formato predefinito.

---

## ⚙️ Cosa fa

- Elabora i dati esportati da Kimai
- Genera automaticamente un **report delle presenze mensili** in formato `.ods`
- Riconosce le giornate con attività e le marca come **giorni lavorativi**
- Chiede all'utente:
  - il **nome** da inserire nel report
  - se compilare il **mese corrente o precedente**
- Verifica che il file di output non esista già (per evitare sovrascritture)
- Produce un file `presenzeCompilate.ods` basato su un template esistente

---

## ▶️ Come si usa

Compilare il progetto con Maven ed eseguire il JAR generato.  

Argomenti:

- INPUT_FILE (obbligatorio): CSV esportato da Kimai
- OUTPUT_FILE (opzionale): percorso del file di output (se omesso, sarà creato nella directory temporanea)
- CSV oppure ODS (opzionale): formato dell’output, predefinito: ODS

---

## ⛓️ Requisiti

- Java 17 o superiore
- jOpenDocument 1.3
- File `kimai-export.csv` esportato da Kimai

---

## 🚧 In sviluppo

- [ ] Calcolo automatico del rimborso chilometrico e compilazione documento
- [ ] Riconoscimento delle giornate in smart working
- [ ] Riconoscimento delle giornate festive e chiusura aziendale
- [ ] Collegamento diretto con API Kimai
- [ ] Generazione automatica di PDF
- [ ] Inserimento automatico firma
- [ ] Interfaccia grafica base
- [ ] Inserimento permessi / ferie / ROL / malattia
- [ ] Inserimento data compilazione automatica
- [ ] Calcolo automatico del costo auto ACI per rimborso chilometrico tramite modello

---

## 🤝 Contribuire

Pull request, issue e feedback sono benvenuti!

---

Realizzato tra una build fallita e l’altra, con la fondamentale supervisione di Alan.
