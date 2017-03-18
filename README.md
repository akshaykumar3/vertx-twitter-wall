# Vert.x Twitter Wall

A simple Twitter application written in Vert.x Scala.

## Build

To build the code and generate package:

```
sbt assembly
```

Then you can run the application directly:

```
java -jar target/scala-2.12/vertx-twitter-wall-assembly-1.0.jar -conf project/config.json
```

## Configuration

You can put your config into a JSON file and provide the path when running the application.
Here are the configurations:

- `app.port`: the port of the application, by default **8080**
- `app.update.interval`: the interval of fetching the latest tweets
- `app.consumerKey`: consumer key of Twitter API
- `app.consumerSecret`: consumer secret key of Twitter API


