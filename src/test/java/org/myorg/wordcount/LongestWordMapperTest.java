package org.myorg.wordcount;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class LongestWordMapperTest {

    private LongestWordMapper mapper;
    private Mapper<Object, Text, Text, Text>.Context context;

    @BeforeEach
    public void setUp() {
        mapper = new LongestWordMapper();
        context = mock(Mapper.Context.class);

        FileSplit split = mock(FileSplit.class);
        when(split.getPath()).thenReturn(new org.apache.hadoop.fs.Path("/some/path/English/file.txt"));
        when(context.getInputSplit()).thenReturn(split);

        mapper.setup(context);
    }

    @Test
    public void testMapFiltersAndEmitsWords() throws IOException, InterruptedException {
        Text line = new Text("Hello world 123 $$$ symbols");
        mapper.map(null, line, context);

        verify(context).write(new Text("English"), new Text("Hello"));
        verify(context).write(new Text("English"), new Text("world"));
        verify(context).write(new Text("English"), new Text("symbols"));

        verify(context, never()).write(eq(new Text("English")), eq(new Text("123")));
        verify(context, never()).write(eq(new Text("English")), eq(new Text("$$$")));
    }
}
