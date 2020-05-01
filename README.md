# springboot简易封装ffmpeg（rtsp转rtmp）

该项目是windows环境是基于开源项目：https://github.com/eguid/FFCH4J进行简易封装,

## 工具

JDK1.8

maven3.6.3

redis

[ffmpeg-4.2.2-win64-s]: http://ffmpeg.zeranoe.com/builds/
[nginx-rtmp-win32-mas]: https://github.com/illuspas/nginx-rtmp-win32

## 参考学习网址

https://blog.csdn.net/wenqiangluyao/article/details/98594861

https://my.oschina.net/RabbitXiao/blog/1574967

## 项目启动

application.yml ：redis为redis的地址

ffmpeg.properties:windows环境下ffmpeg安装的地址，都有注释打开看一下就知道了

```java
SelectStmpVideoURLController 简单写的一个转换逻辑
```

```java
MyffmpegApplication		项目启动类
```

