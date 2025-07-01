package org.myorg.wordcount;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import java.io.IOException;

/**
 * A Hadoop Reducer that determines the longest valid word per language.
 * Words containing only letters are considered valid.
 */
public class LongestWordReducer extends Reducer<Text, Text, Text, IntWritable> {

    /**
     * Processes all words for a given language and determines the longest one.
     * Invalid words (non-alphabetic) are skipped.
     *
     * @param language The language name (from the folder structure).
     * @param words    An iterable of candidate words.
     * @param context  The context used to emit the longest word and its length.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the job is interrupted.
     */
    @Override
    protected void reduce(Text language, Iterable<Text> words, Context context)
            throws IOException, InterruptedException {

        String longestWord = "";
        for (Text word : words) {
            String w = word.toString();

            if (w != null && !w.isEmpty()) {
                // Erlaube nur Wörter mit Buchstaben, optional mit - oder '
                if (!w.matches("^[\\p{L}\\-']+$")) continue;

                int wLen = w.codePointCount(0, w.length());
                int currLen = longestWord.codePointCount(0, longestWord.length());

                if (wLen > currLen) {
                    longestWord = w;
                }
            }
        }

        if (!longestWord.isEmpty()) {
            context.write(
                    new Text(language.toString() + " \u2013 " + longestWord + " \u2013 "),
                    new IntWritable(longestWord.codePointCount(0, longestWord.length()))
            );
        }
    }
}
