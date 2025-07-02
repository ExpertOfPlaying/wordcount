# Dockerfile for personalized Hadoop container (no SSH, auto-start, correct JAVA_HOME)
FROM openjdk:8-jdk

# Set environment variables
ENV JAVA_HOME=/usr/local/openjdk-8
ENV HADOOP_VERSION=3.4.1
ENV HADOOP_HOME=/opt/hadoop
ENV HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
ENV PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

# Install required packages and Hadoop
RUN apt-get update && \
    apt-get install -y wget curl ssh rsync sudo nano vim net-tools iputils-ping && \
    wget https://archive.apache.org/dist/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz && \
    tar -xvzf hadoop-${HADOOP_VERSION}.tar.gz -C /opt && \
    mv /opt/hadoop-${HADOOP_VERSION} $HADOOP_HOME && \
    rm hadoop-${HADOOP_VERSION}.tar.gz

# Generate SSH key (even though SSH is not used)
RUN ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa && \
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

# Configure Hadoop to run everything as root (no sudo)
RUN echo 'export HDFS_NAMENODE_USER=root' >> $HADOOP_CONF_DIR/hadoop-env.sh && \
    echo 'export HDFS_DATANODE_USER=root' >> $HADOOP_CONF_DIR/hadoop-env.sh && \
    echo 'export HDFS_SECONDARYNAMENODE_USER=root' >> $HADOOP_CONF_DIR/hadoop-env.sh && \
    echo 'export YARN_RESOURCEMANAGER_USER=root' >> $HADOOP_CONF_DIR/hadoop-env.sh && \
    echo 'export YARN_NODEMANAGER_USER=root' >> $HADOOP_CONF_DIR/hadoop-env.sh && \
    echo 'export HADOOP_SHELL_EXEC_IMPL=exec' >> $HADOOP_CONF_DIR/hadoop-env.sh && \
    echo 'export JAVA_HOME=${JAVA_HOME}' >> $HADOOP_CONF_DIR/hadoop-env.sh && \
    echo 'export HADOOP_SSH_OPTS="-o ConnectTimeout=1 -o StrictHostKeyChecking=no"' >> $HADOOP_CONF_DIR/hadoop-env.sh

# Fix SSH workers to avoid connection attempts
RUN echo "127.0.0.1" > $HADOOP_CONF_DIR/workers

RUN echo 'export HADOOP_NICENESS=0' >> $HADOOP_CONF_DIR/hadoop-env.sh

# Copy entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
CMD ["bash"]
