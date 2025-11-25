# PII Redaction Tool (Text + PDF)

A lightweight, local, privacy-focused tool that detects and redacts Personally Identifiable Information (PII) from **text** and **PDF documents** using **Java + Spring Boot + PDFBox**, plus optional **LLM-based detection** using Google Gemini.

---

## 🚀 What I Built

This project provides a complete PII-redaction pipeline:

### ✔ Text Redaction  
Enter raw text → detect PII → output a fully redacted version.

### ✔ PDF Redaction (Apache PDFBox)  
Upload a PDF → extract text → detect PII → generate a new redacted PDF.

### ✔ Optional LLM-Powered Detection (Gemini)  
Used for complex PII such as:
- Names  
- Physical addresses  

Regex handles:
- Emails  
- Phone numbers  

### ✔ Simple Frontend (HTML/CSS/JS)  
Choose PII types → upload files or text → download cleaned results.

### ✔ Local-First  
Everything runs locally except LLM detection (optional).

---

## 🧠 Why This Approach?

### Spring Boot  
Fast, clean REST API and easy local development.

### PDFBox  
Best open-source library for reading, editing, and writing PDFs.

### Hybrid PII detection  
- Regex = fast & accurate for structured PII  
- LLM = excellent accuracy for ambiguous human-language PII  

Combining both gives:  
**speed + reliability + accuracy**

### Minimal Frontend  
Lightweight, fast to build, easy for evaluators to run locally.

## 🔧 Architecture

```
Frontend (HTML/JS)
|
v
Spring Boot Backend
|- TextRedactionService
|- PDFRedactionService (PDFBox)
|- PIIDetectionService
|- RegexEngine (emails, phones)
|- LLMService (names, addresses)
```

## 📌 Assumptions

- Users prefer a **local**, secure redaction tool.  
- Regex is enough for email/phone detection.  
- LLM required for names & addresses.  
- Input PDFs are **digital**, not scanned (no OCR).  
- User provides their own Gemini API key.

---

## ⚠ Limitations

- The app currently **fails to start** if Gemini API key is missing.  
- No support for **OCR** (scanned PDFs cannot be redacted).  
- PDF layout may not fully match original formatting.  
- LLM detection requires Internet and increases latency.

---

## 🔄 Trade-Offs

- Simple monolithic Spring Boot app instead of microservices.  
- Regex-only mode is fast but less accurate; LLM mode is accurate but slower.  
- Minimal frontend chosen to reduce dev overhead.

---

## 💡 What I Would Improve With More Time

1. **OCR Support** (Tesseract, Google Vision API)  
2. **Real PDF redaction annotations** instead of text replacement  
3. **Graceful fallback if LLM fails**  
4. **Better frontend (React SPA)** with file preview  
5. **Cloud deployment** with authentication  
6. **Caching + performance improvements**  
7. **Unit tests + PDF redaction tests**

---

## ▶ Running the Project

### Build & run
```bash
mvn clean package
java -jar target/PII-Redaction.jar
