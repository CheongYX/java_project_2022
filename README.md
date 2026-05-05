# Protein Sequence Management System (Scientific Problem-Oriented)

## 1. Background & Vision
The success of **AlphaFold2** represents a significant step forward in unraveling the mysteries of biological evolution. However, the development of deep learning is inseparable from high-quality data; for instance, training AlphaFold2 required at least **2TB** of data. Efficiently collecting and constructing a database to store protein sequences—facilitating researcher uploads, updates, downloads, and browsing—is key to further deep learning research.

### Reference Links
*   **AlphaFold2 Project**: [https://github.com/deepmind/alphafold](https://github.com/deepmind/alphafold)
*   **UniProt Database (Reviewed)**: [https://www.uniprot.org/uniprot/?query=reviewed:yes](https://www.uniprot.org/uniprot/?query=reviewed:yes)

---

## 2. Project Objectives
This system is designed to handle real-world scientific data with the following capabilities:
*   **Robust Data Ingestion**: Parse and store files containing single or multiple sequence information, even when data is incomplete.
*   **Batch Processing**: Handle multiple files simultaneously to populate the local database.
*   **High-Efficiency Browsing**: View stored sequences through a paginated table interface or export specific pages to custom-format files.
*   **Multidimensional Search**: Retrieve sequences via single or multiple keyword matching with paginated results.
*   **Scientific Analysis**: Analyze收录 sequence information to provide frequency counts and statistical metrics (e.g., mean, median, and mode) based on sequence length.

---

## 3. Technical Implementation
*   **Language & Environment**: Java (J2SE-1.5).
*   **Database**: SQLite3—a lightweight, embedded engine chosen for its low resource consumption and high processing speed.
*   **Architecture**: Decoupled design using an **Interaction Module** for parsing user commands and a **Database Operation Module** for managing sequence data.

---

## 4. Logical Flowchart
The system operates through a structured four-stage pipeline:

1.  **Initialization**: Load J2SE-1.5 environment and SQLite JDBC driver; establish a persistent connection to the local `.db` file.
2.  **Command Dispatching**: Capture user input (file paths, keywords, or page numbers) and route them to the appropriate processing stream.
3.  **Core Processing**:
    *   **Append Stream**: Parse TSV/CSV files with **format guards** to intercept invalid extensions and handle missing information through automatic completion.
    *   **Search Stream**: Build dynamic SQL for matching and execute real-time statistics calculation.
    *   **Pagination Stream**: Utilize `LIMIT` and `OFFSET` for data slicing with boundary checks.
4.  **Persistence & Output**: Refresh the UI table and provide **safety-checked exports** to ensure researchers use valid scientific file formats.

---

## 5. Challenges & Innovations
### Engineering Challenges
*   **Stream API Operations**: Utilization of Stream API to handle data flows.
*   **High-Frequency Simulation**: Designed to withstand scenarios where researchers frequently upload or download data during specific periods.
*   **Recommendation System**: Implementation of non-precise matching where retrieved objects are ranked by keyword match accuracy.

### Innovations
*   **Sequence Similarity**: Ranking results based on sequence similarity rather than just metadata.
*   **Clustering Analysis**: Providing statistical reports that categorize收录 sequences into clusters.
*   **Machine Learning Integration**: Training models based on database sequences and labels to predict the types of unrecorded sequences.

---

## 6. How to Run
1.  **Environment**: Ensure **Java 8** is installed and added to your `PATH`.
2.  **Dependencies**: Place `sqlite-jdbc-3.36.0.3.jar` in the project directory.
3.  **Execution**: Open the source `Main.java` in an IDE (like Eclipse) or run the compiled JAR.
    ```bash
    java -jar rna.jar
    ```

--- 
**School**: School of Computer Science, Beijing Institute of Technology
```
