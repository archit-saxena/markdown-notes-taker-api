# 📓 Markdown Notes API (Spring Boot)

A simple **Markdown-based note-taking API** built with **Java Spring Boot**.  
Supports creating, updating, deleting, listing, grammar checking, and rendering notes from Markdown to HTML.  
Notes are stored as `.md` files on disk with metadata in `.json` files — **no database required**.

---

## ✨ Features

- 📄 Create notes from raw Markdown text or uploaded `.md` files
- 🛠 Update existing notes
- ❌ Delete notes
- 📜 List all saved notes
- 🔍 Grammar check for notes (using [LanguageTool](https://languagetool.org/))
- 🎨 Render Markdown to safe HTML (using [flexmark-java](https://github.com/vsch/flexmark-java))
- 💾 Stores notes **on disk** (markdown + metadata)

---

## 📂 Project Structure

``` html
project-root/
 ├── src/               # Java source code
 ├── pom.xml            # Maven dependencies
 ├── data/notes/        # Stored notes (.md + .json)
 └── README.md
```

---

## ⚙️ Tech Stack

- **Java 17+** (or compatible)
- **Spring Boot** (Web, Validation)
- **Flexmark** (Markdown → HTML)
- **LanguageTool** (Grammar checking)
- **Jsoup** (HTML sanitizing)

---

## 🚀 How to Run

### 1️⃣ Prerequisites

- Install [Java 17+](https://adoptium.net/)
- Install [Maven](https://maven.apache.org/)
- (Optional) Install Git

### 2️⃣ Clone the Repository

```bash
git clone https://github.com/archit-saxena/markdown-notes-taker-api
cd markdown-notes-taker-api
```

### 3️⃣ Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The API will start at:

`http://localhost:8080`

---

## 📌 Configuration

You can configure the storage path in `application.properties`:

```properties
notes.storage.path=./data/notes
```

By default, notes are stored in `./data/notes/`.

---

## 📡 API Endpoints

| Method | Endpoint                      | Description                       |
|--------|--------------------------------|-----------------------------------|
| POST   | `/api/notes`                   | Create note from JSON body Keys: {"title","markdown"}       |
| POST   | `/api/notes/upload`            | Upload `.md` file as note         |
| GET    | `/api/notes`                   | List all notes                    |
| GET    | `/api/notes/{id}/raw`          | Get raw Markdown text             |
| GET    | `/api/notes/{id}/render`       | Get HTML-rendered note            |
| PUT    | `/api/notes/{id}`              | Update existing note              |
| DELETE | `/api/notes/{id}`              | Delete note                       |
| POST   | `/api/notes/grammar`           | Grammar check from text           |
| POST   | `/api/notes/grammar-file`      | Grammar check from uploaded file  |
| GET    | `/api/notes/{id}/grammar`      | Grammar check from the note id    |
| GET    | `/api/notes/filenames`         | Lists all the notes present       |
---

## 🛡 Security Notes

- HTML output is sanitized using Jsoup `Safelist.basicWithImages()` to prevent XSS.
- All note IDs are UUIDs — no direct filename exposure.
- File uploads are limited in size via Spring Boot config.

---

## 📜 License

This project is open-source under the [MIT License](/LICENSE).

---

## 👨‍💻 Author

**Archit Saxena**  
GitHub: [@archit-saxena](https://github.com/archit-saxena)
