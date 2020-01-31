# PictureBed
一个简易的图片库，给自己保存图片用。

## 后端项目pb
1. 本地运行测试
按SpringBoot方式运行pb即可
1. 修改配置
configs.properties：`image.rootPath=/home/hgc/nginx/sources`，即nginx通过HTTP代理的静态文件目录。
1. 打包到Docker镜像
在pb目录下运行打包命令`mvn clean install -e docker:build`，实际上Dockerfile文件的位置在`pb/src/main/docker/Dockerfile`。
1. 上传到DockerHub
    ```
    docker push tallate/pb
    ```
1. 在目标机器上运行Docker容器
    ```
    docker pull tallate/pb
    docker run -d -p 8084:8084 -v /home/hgc/nginx/sources:/home/hgc/nginx/sources tallate/pb
    ```
1. 检查
访问`targetHost:8084/cgh/image/list`接口可检查服务器是否正常运行
有问题可以登到服务器上`docker logs {containerID}`查看日志。
1. 停止
    ```
    docker container stop pb
    docker container rm pb
    ```

## 前端项目front
1. 安装包
`npm install`
1. 本地运行测试
`npm start`
1. 打包
`npm run build`生成的静态页面文件在build目录下。
1. 安装
通过Nginx反向代理前端页面，为了方便还是用docker运行：
`docker run -d -p 80:80 -v /home/hgc/nginx/sources:/usr/share/nginx/html -v /home/hgc/nginx/logs:/var/log/nginx nginx`
`/home/hgc/nginx/sources`目录不仅用于存放打包后的html文件，也用于后端存储图片文件。
将静态页面上传到服务器上：`scp -r build/* root@47.88.24.11:/home/hgc/nginx/sources`
1. 检查
浏览器内访问目标服务器的IP即可（我自己租的服务器是47.88.24.11）。


## 遇到的一些问题
### 打包Docker镜像时报2375端口不可用
docker在mac下有不少问题，比如没有直接开放2375端口，据说是为了安全：
[Docker for Mac doesn't listen on 2375](https://github.com/docker/for-mac/issues/770)
解决办法是重定向一下：
```
docker run -d -v /var/run/docker.sock:/var/run/docker.sock -p 127.0.0.1:2375:2375 bobrik/socat TCP-LISTEN:2375,fork UNIX-CONNECT:/var/run/docker.sock
export DOCKER_HOST=tcp://localhost:2375
```
### mac下Docker占用了过多的磁盘空间
[Docker.raw reserving too much size](https://github.com/docker/for-mac/issues/2297)
用这条命令：
```
docker system prune -a
```
### Docker中运行Java读取文件路径如果有中文会乱码
查询文件列表的时候发现中文文件名都是乱码，进行了一番排查：
* 怀疑是web服务暴露的接口返回数据时会对其转码，远程Debug（配置可见[SpringBoot进行远程debug](https://blog.csdn.net/weixin_42740530/article/details/89524509)）发现`File.listFiles返回的文件列表就是乱码，因此不是SpringBoot本身的锅；
* 考虑是上传的文件本身编码不对，于是将读取到的文件名转码（`new String(file.getName().getBytes(System.getProperty("file.encoding")), StandardCharsets.UTF_8)`），但是不起作用。
后来直接登录进镜像建一个假图片文件，发现返回的也还是乱码，因此可以确定上传文件的编码本身没有问题；
* 怀疑是Docker本身没有`UTF-8`字符集，这样Java再按`UTF-8`编码进行解码显然会导致乱码。
镜像可以用`docker search`命令搜索，或直接到`DockerHub`上查询。
刚开始使用的jdk基础镜像是基于centos的，它本身没有`UTF-8`字符集（安装字符集方法可参考[CentOS安装中文支持语言包](https://www.cnblogs.com/wr2020/p/11985835.html)），安装过程中发现还是有不少命令不支持，遂放弃，改成找一个本身就支持UTF-8字符集的镜像，镜像的选择可见`pb/src/main/docker/Dockerfile`，最后发现`ringcentral/jdk`这个镜像可以满足要求，当然，更严谨的方法是自己整一个镜像。



## 参考
1. [React](https://reactjs.org/)
1. [React Bootstrap](https://react-bootstrap.github.io/)
1. [Redux](https://redux.js.org/)
1. [React file upload: proper and easy way, with NodeJS!](https://programmingwithmosh.com/javascript/react-file-upload-proper-server-side-nodejs-easy/)
[krissnawat / simple-react-upload](https://github.com/krissnawat/simple-react-upload)
1. [bytedeco / javacpp](https://github.com/bytedeco/javacpp)
1. [bytedeco / javacv](https://github.com/bytedeco/javacv)
[samples](https://github.com/bytedeco/javacv/tree/master/samples/)
