# Longest Word Per Language - Hadoop MapReduce

This project finds the longest word for each language in a text corpus using Hadoop MapReduce, with all components containerized via Docker.  
**Languages** are inferred from the folder names in your input directory.

---

## Input Format

The input directory **must** group files by language in subfolders (the folder names will be used as the language key):

Example:
- `shared/input/texts/English/file1.txt`
- `shared/input/texts/German/file1.txt`
- `shared/input/texts/French/file1.txt`

---

## Quick Start

### 1. Requirements

- [Docker](https://www.docker.com/)
- [Java 8 (1.8.x)](https://adoptium.net/temurin/releases/?version=8)
- [Maven 3.x](https://maven.apache.org/)
- At least 4GB RAM free (for Hadoop in Docker)

### 2. Build the Project

Build on your host (outside Docker):
```sh
mvn clean package
```

This creates target/wordcount-1.0-SNAPSHOT.jar

### 3. Prepare Input Data

Place your text files in `shared/input/texts/`, grouped by language as above.

### 4. Start Hadoop Cluster

From the project root:
```sh
docker-compose up -d
```

### 5. Upload Data to HDFS

Open a shell in the namenode container:
```sh
docker exec -it namenode bash
```

Inside the container:
```sh
hdfs dfs -mkdir -p /input/texts
hdfs dfs -put /shared/input/texts/* /input/texts/
```

### 6. Run the MapReduce Job

Inside the namenode container:
```sh
hadoop jar /target/wordcount-1.0-SNAPSHOT.jar org.myorg.wordcount.Main /input/texts /output/longestwords
```

### 7. View Results

Inside the container:
```sh
hdfs dfs -cat /output/longestwords/part-r-00000
```

Or copy output to your host:
```sh
hdfs dfs -get /output/longestwords /shared/output/
```

View on host: `shared/output/longestwords/part-r-00000`

## Running Unit Tests

Run on your host:
```sh
mvn test
```

- `LongestWordMapperTest.java`: Mapper test.
- `LongestWordReducerTest.java`: Reducer test.

## How the Code Works

Mapper: Emits (language, word) pairs, where language comes from the parent folder.
Reducer: Selects the longest alphabetic word for each language.
Main.java: Configures and runs the MapReduce job.

## Developer Notes

Add a language: Add a new subfolder under shared/input/texts/ (e.g., Italian/).
Customize paths: You can use any HDFS input/output paths.

## Encoding / UTF-8 Notes

If your terminal (especially on Windows) shows encoding issues:

Change to UTF-8:
```sh
chcp 65001
```

Or use a UTF-8 compatible terminal (Windows Terminal, Git Bash, etc.)

## Shutting Down
When finished:

```sh
docker-compose down
```

## Troubleshooting

HDFS command not found?
Make sure you are inside the namenode container.

Job not found?
Make sure `/target/wordcount-1.0-SNAPSHOT.jar` exists.

## Example Output

Format: Language – Word – Length :

English – Mekkamuselmannenmassenmenchenmoerdermohrenmuttermarmormonumentenmacher – 	70
German – eindusendsöbenhunnertuneiunsösstig – 	34
Italian – quattrocentoquarantatremila – 	27
Russian – niezaviershiennoienabroski – 	26
Dutch – landbouwgereedschappen – 	22
French – constitutionnellement – 	21
Ukrainian – благочестивомудренно – 	20
Spanish – circunstanciadamente – 	20

## Cleaning Up

To remove everything including HDFS data:

```sh
docker-compose down -v
```