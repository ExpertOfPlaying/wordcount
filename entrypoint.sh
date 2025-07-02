#!/bin/bash

# Format HDFS only if not already formatted
if [ ! -f /tmp/hdfs-formatted ]; then
  "$HADOOP_HOME"/bin/hdfs namenode -format -force -nonInteractive
  touch /tmp/hdfs-formatted
fi

# Start Hadoop daemons manually (no SSH)
"$HADOOP_HOME"/bin/hdfs --daemon start namenode
"$HADOOP_HOME"/bin/hdfs --daemon start datanode
"$HADOOP_HOME"/bin/hdfs --daemon start secondarynamenode
"$HADOOP_HOME"/bin/yarn --daemon start resourcemanager
"$HADOOP_HOME"/bin/yarn --daemon start nodemanager

# Keep container running
exec bash
