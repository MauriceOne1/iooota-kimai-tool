# Kimai Presenze Tool 📅

![Java](https://img.shields.io/badge/language-Java-007396?style=flat-square&logo=java)
![Status](https://img.shields.io/badge/status-in%20development-orange?style=flat-square)
![Platform](https://img.shields.io/badge/platform-cli-lightgrey?style=flat-square)

Uno strumento semplice ma potente per trasformare un file CSV esportato da **Kimai** in un **foglio presenze mensile** in formato `.ods`.

---

## ⚙️ Cosa fa

- Legge un **dump CSV** esportato da Kimai (`kimai-export.csv`)
- Genera automaticamente un **report delle presenze mensili** in formato `.ods`
- Riconosce le giornate con attività e le marca come **giorni lavorativi**
- Chiede all'utente:
  - il **nome** da inserire nel report
  - se compilare il **mese corrente o precedente**
- Produce un file `presenzeCompilate.ods` basato su un template esistente

---

## ▶️ Come si usa

Compilare il progetto con Maven o eseguire il JAR con uno dei seguenti comandi:

### `csv`  
Visualizza a console tutte le attività presenti nel file `kimai-export.csv`.

### `ods`  
Genera il file `.ods` compilato con presenze, nome e mese richiesto.

---

## ⛓️ Requisiti

- Java 17 o superiore
- jOpenDocument 1.3
- File `kimai-export.csv` esportato da Kimai
- File `presenzeTemplate.ods` come modello (deve già contenere le righe da 1 a 31)

---

## 🚧 In sviluppo

- [ ] Calcolo automatico del **rimborso chilometrico**
- [ ] Riconoscimento delle giornate in **smart working**
- [ ] Generazione automatica di **PDF**
- [ ] Interfaccia grafica base (facoltativa)

---

## 🤝 Contribuire

Pull request, issue e feedback sono benvenuti!

---

*Realizzato tra una build fallita e l’altra, con la fondamentale supervisione di Alan.*