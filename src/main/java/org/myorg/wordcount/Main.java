package org.myorg.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Main entry point for the MapReduce job that finds the longest word per language.
 * Accepts two command-line arguments: the input path and the output path.
 */
public class Main {

    /**
     * Configures and executes two MapReduce jobs.
     * 1. Find longest word per language
     * 2. Sort these words by length descending
     *
     * @param args Command-line arguments: input path and output path.
     * @throws Exception if any error occurs during job setup or execution.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: Main <input_path> <output_path>");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String intermediatePath = "intermediate_output";

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        Job job_longest_word = Job.getInstance(conf, "Longest Word Per Language");

        job_longest_word.setJarByClass(Main.class);
        job_longest_word.setMapperClass(LongestWordMapper.class);
        job_longest_word.setReducerClass(LongestWordReducer.class);

        job_longest_word.setMapOutputKeyClass(Text.class);
        job_longest_word.setMapOutputValueClass(Text.class);
        job_longest_word.setOutputKeyClass(Text.class);
        job_longest_word.setOutputValueClass(IntWritable.class);

        FileInputFormat.setInputDirRecursive(job_longest_word, true);
        FileInputFormat.addInputPath(job_longest_word, new Path(inputPath));

        Path intermediate = new Path(intermediatePath);
        if (fs.exists(intermediate)) {
            fs.delete(intermediate, true);
        }
        FileOutputFormat.setOutputPath(job_longest_word, intermediate);

        if(!job_longest_word.waitForCompletion(true)) {
            System.exit(1);
        }

        Job job_longest_word_sort =  Job.getInstance(conf, "Longest Word Per Language Sort");
        job_longest_word_sort.setJarByClass(Main.class);
        job_longest_word_sort.setMapperClass(SortMapper.class);
        job_longest_word_sort.setReducerClass(SortReducer.class);

        job_longest_word_sort.setSortComparatorClass(DescendingIntComparator.class);

        job_longest_word_sort.setMapOutputKeyClass(IntWritable.class);
        job_longest_word_sort.setMapOutputValueClass(Text.class);
        job_longest_word_sort.setOutputKeyClass(Text.class);
        job_longest_word_sort.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job_longest_word_sort, intermediate);

        Path outDir = new Path(outputPath);
        if (fs.exists(outDir)) {
            fs.delete(outDir, true);
        }
        FileOutputFormat.setOutputPath(job_longest_word_sort, outDir);

        job_longest_word_sort.getConfiguration().set("mapreduce.output.textoutputformat.encoding", "UTF-8");

        System.exit(job_longest_word_sort.waitForCompletion(true) ? 0 : 1);
    }
}