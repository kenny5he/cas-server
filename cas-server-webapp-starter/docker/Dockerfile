FROM openjdk:11
MAINTAINER docker_user 923662064@qq.com

# 设置时区
RUN echo "Asia/Shanghai" > /etc/timezone && \
	dpkg-reconfigure -f noninteractive tzdata

RUN mkdir -p /app/files/
RUN mkdir -p /app/logs/

# 默认挂载日志目录，临时文件目录
VOLUME ["/app/logs/","/app/files/"]

# Wget 网络下载应用包
# RUN wget https://app.microfoolish.com/download/cas-server-0.0.1-SNAPSHOT.tar.gz \
#    tar -xzvf cas-server-0.0.1-SNAPSHOT.tar.gz -C /app/

# 本地获取应用包
ADD cas-server-0.0.1-SNAPSHOT.tar.gz cas-server-0.0.1-SNAPSHOT.tar.gz \
    tar -xzvf cas-server-0.0.1-SNAPSHOT.tar.gz -C /app/

RUN chmod +x /app/bin/*.sh
RUN chmod +x /app/libs/*.jar

EXPOSE 8003
CMD ["/app/bin/run.sh"]