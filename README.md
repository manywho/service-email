ManyWho Email Service
=====================

This service allows you to send emails from a Boomi Flow. It temporarily stores attached files on AWS S3, before deleting
them after the email is sent successfully.

## Running

### Heroku

The service is compatible with Heroku, and can be deployed by clicking the button below:

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/manywho/service-email/tree/develop)

### Locally

The service is a Jersey JAX-RS application, that by default is run under the Grizzly2 server on port 8080 (if you use 
the packaged JAR).

#### Building

To build the service, you will need to have Apache Ant, Maven 3 and a Java 8 implementation installed.

You will need to generate a configuration file for the service by running the provided `build.xml` script with Ant, and 
passing in valid credentials for AWS S3:


```bash
$ ant -Ds3.bucket=s3bucketname \
        -Ds3.accessKey=s3id \
        -Ds3.secretKey=s3secretkey
```

Now you can build the runnable shaded JAR:

```bash
$ mvn clean package
```

##### Defaults

Running the following command will start the service listening on `0.0.0.0:8080/api/email/1`:

```bash
$ java -jar target/demo-1.0-SNAPSHOT.jar
```

##### Custom Port

You can specify a custom port to run the service on by passing the `server.port` property when running the JAR. The
following command will start the service listening on port 9090 (`0.0.0.0:9090/api/email/1`):

```bash
$ java -Dserver.port=9090 -jar target/demo-1.0-SNAPSHOT.jar
```

## Contributing

Contributions are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
