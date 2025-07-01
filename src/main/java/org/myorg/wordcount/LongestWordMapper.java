package org.myorg.wordcount;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

/**
 * A Hadoop Mapper that emits each word in a line of text along with its associated language,
 * which is inferred from the parent directory name of the file.
 */
public class LongestWordMapper extends Mapper<Object, Text, Text, Text> {

    private String language;

    /**
     * Extracts the language name from the parent folder of the current file input split.
     *
     * @param context The context of the mapper.
     */
    @Override
    protected void setup(Context context) {
        // Get the parent folder name (e.g., "German", "English")
        Path filePath = ((FileSplit) context.getInputSplit()).getPath();
        language = filePath.getParent().getName();
    }

    /**
     * Emits each token in the text line as a (language, word) pair.
     * Tokens are filtered using Unicode letter pattern and only non-empty words are emitted.
     *
     * @param key     The byte offset of the current line in the file.
     * @param value   The actual line of text.
     * @param context The context to write the output key-value pairs.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the job is interrupted.
     */
    @Override
    protected void map(Object key, Text value, Context context)
            throws IOException, InterruptedException
    {
        String[] tokens = value.toString().split("\\P{L}+");

        for (String token : tokens) {
            if (!token.isEmpty()) {
                context.write(new Text(language), new Text(token));
            }
        }
    }
}
