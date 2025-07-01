package org.myorg.wordcount;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import java.io.IOException;

public class SortReducer extends Reducer<IntWritable, Text, Text, IntWritable> {
    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        for (Text val : values) {
            context.write(val, key);
        }
    }
}

