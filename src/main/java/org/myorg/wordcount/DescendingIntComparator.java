package org.myorg.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class DescendingIntComparator extends WritableComparator {
    protected DescendingIntComparator() {
        super(IntWritable.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        IntWritable i1 = (IntWritable) a;
        IntWritable i2 = (IntWritable) b;
        return -1 * i1.compareTo(i2);
    }
}
