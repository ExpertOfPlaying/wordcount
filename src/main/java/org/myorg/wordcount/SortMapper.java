package org.myorg.wordcount;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import java.io.IOException;

public class SortMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        String[] parts = value.toString().split(" – ");
        if (parts.length == 3) {
            String label = parts[0] + " – " + parts[1] + " – ";
            int length = Integer.parseInt(parts[2].trim());
            context.write(new IntWritable(length), new Text(label));
        }
    }
}

