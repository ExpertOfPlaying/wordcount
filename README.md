# Longest Word Per Language - Hadoop MapReduce

This project is a Hadoop MapReduce job that finds the longest word for each language.
The language is inferred from the name of the parent folder of each input file.

## 📁 Input Folder Structure
```
texts/
├── German/
│   └── file1.txt
├── English/
│   └── file2.txt
└── French/
    └── file3.txt
```

## 🧪 How It Works
- **Mapper** emits (`language`, `word`) pairs.
- **Reducer** filters valid alphabetic words and selects the longest one per language.

## ▶️ How to Run
```
hdfs dfs -mkdir /texts
hdfs dfs -put path/to/your/texts/* /texts

hadoop jar wordcount-1.0-SNAPSHOT.jar org.myorg.wordcount.Main /texts /output/longestwords

(Optionally if cmd doesn't support UTF-8)
chcp 65001

hdfs dfs -cat /output/longestwords/part-r-00000
```