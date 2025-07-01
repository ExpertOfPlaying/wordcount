package org.myorg.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class LongestWordReducerTest {

    @Test
    public void testReduceFindsLongestWord() throws IOException, InterruptedException {
        LongestWordReducer reducer = new LongestWordReducer();
        Reducer<Text, Text, Text, IntWritable>.Context context = mock(Reducer.Context.class);

        List<Text> words = Arrays.asList(
                new Text("apple"),
                new Text("banana"),
                new Text("watermelon")
        );

        reducer.reduce(new Text("English"), words, context);

        verify(context).write(
                new Text("English – watermelon – "),
                new IntWritable("watermelon".length())
        );
    }
}