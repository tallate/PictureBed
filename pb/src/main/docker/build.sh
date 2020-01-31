#!/usr/bin/env bash
# 先定位到项目根目录
cd ../../..
# 构建镜像，嫌麻烦可以加入选项跳过测试用例：-DskipTests
mvn clean install -e docker:build
