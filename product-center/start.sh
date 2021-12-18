#!/bin/bash
SERVER_NAME="product-center"
echo 'Start the program : '$SERVER_NAME'-0.0.1-SNAPSHOT.jar'
echo '-------Starting-------'
java -jar $SERVER_NAME-0.0.1-SNAPSHOT.jar &
echo 'start success'