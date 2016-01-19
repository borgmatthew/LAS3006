***************************************************************************************
**** SERVER
***************************************************************************************

To execute the server, navigate to the broker directory and run the following command:
mvn exec:java

This executes the server with the default parameters. These are:
__________________________________________________________________
Parameter          | Description                 | Default Value |
=================================================================|
serverPort         | port used by the server     |     3254      |
-----------------------------------------------------------------|
maxInactiveMinutes | maximum idle minutes before |       1       |
                   | closing a connection        |               |
_________________________________________________________________|

To override any or both of these values, give the desired values in the following format:

-DparameterName=value

For example: mvn exec:java -DserverPort=1234 -DmaxInactiveMinutes=5


***************************************************************************************
**** CLIENT-PUBLISHER
***************************************************************************************

To execute the client-publisher, navigate to the client-publisher directory and run the 
following command:

mvn exec:java

This executes the client-publisher with the default parameters. These are:
__________________________________________________________________
Parameter          | Description                 | Default Value |
=================================================================|
serverIp           | Ip address of the server    |   127.0.0.1   |
-----------------------------------------------------------------|
serverPort         | port used by the server     |     3254      |
-----------------------------------------------------------------|
publishTopic       | the topic to publish on     |      #        |
-----------------------------------------------------------------|
messageFrequency   | Frequency of message        |      4        |
                   | generation in seconds       |               |
_________________________________________________________________|

To override any or both of these values, give the desired values in the following format:

-DparameterName=value

For example: mvn exec:java -DmessageFrequency=2 -DpublishTopic="home/kitchen/fridge/temperature"


***************************************************************************************
**** CLIENT-SUBSCRIBER
***************************************************************************************

To execute the client-subscriber, navigate to the client-subscriber directory and run the 
following command:

mvn exec:java

This executes the client-subscriber with the default parameters. These are:
__________________________________________________________________
Parameter          | Description                 | Default Value |
=================================================================|
serverIp           | Ip address of the server    |   127.0.0.1   |
-----------------------------------------------------------------|
serverPort         | port used by the server     |     3254      |
-----------------------------------------------------------------|
subscribeTopic     | the topic to subscribe to   |      #        |
-----------------------------------------------------------------|
messageFrequency   | Frequency of message        |      4        |
                   | generation in seconds       |               |
_________________________________________________________________|

To override any or both of these values, give the desired values in the following format:

-DparameterName=value

For example: mvn exec:java -DmessageFrequency=2 -DsubscribeTopic="home/kitchen/fridge/temperature"
