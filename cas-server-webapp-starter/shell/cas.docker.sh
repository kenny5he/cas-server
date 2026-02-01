#!/bin/sh
docker run -d -p 23306:3306 --name cas -e MYSQL_ROOT_PASSWORD=LafjYBdBb%z -v /Users/apaye/workspace/docker/mysql/cas/data:/var/lib/mysql -v /Users/apaye/workspace/docker/mysql/cas/conf:/etc/mysql/conf.d mysql:5.7
