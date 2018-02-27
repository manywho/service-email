ManyWho Email Service
=====================

[![Build Status](https://travis-ci.org/manywho/service-email.svg)](https://travis-ci.org/manywho/service-email)

This service allows you to send emails from a Boomi Flow app. It supports attachments both as `$File` uploads and from an 
external source, configurable from the Flow.

## Running

### Heroku

The service is compatible with Heroku, and can be deployed by clicking the button below, which also handles any 
required configuration.

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/manywho/service-email/tree/develop)

### Locally

The service is a JAX-RS application, that by default runs on port 8080 (if you use the packaged JAR). To configure and 
build the service, you will need to have Apache Ant, Maven 3 and JDK 8+ installed.

#### Configuring

If you want to support uploading attachments or fetching them from an external source, you will need to generate a 
configuration file for the service by running the provided `build.xml` script with Ant, and passing in any required
credentials for the desired source.

##### Example: Configuring the service to store attachments in S3

An S3 bucket will need to be created, along with an IAM user with `s3:GetObject` permissions on that bucket (along 
with `s3:PutObject` permissions if you wish to enable uploads using the service).

```bash
$ ant -Ds3.bucket=bucket -Ds3.accessKey=*** -Ds3.secretKey=*** -Ds3.region=***
```

##### Example: Configuring the service to fetch attachments from Box

To use Box in your own self-hosted instance of the service, you'll need to [create a new app](https://app.box.com/developers/console),
configured to use "OAuth 2.0 with JWT (Server Authentication)", with Enterprise access and the "Manage users" scope. The 
option to "Generate User Access Tokens" must also be enabled.

```bash
$ ant -Ds3.box.appUserId=1234567 -Dbox.clientId=*** -Dbox.clientSecret=*** -Dbox.publicKeyId=a1b2c3d4 -Dbox.privateKey=*** -Dbox.privateKeyPassword=*** -Dredis.url=***
```

#### Building

Once any configuration is complete, you can build the runnable shaded JAR:

```bash
$ mvn clean package
```

##### Defaults

Running the following command will start the service listening on `0.0.0.0:8080/api/email/1`:

```bash
$ java -jar target/email-*.jar
```

##### Custom Port

You can specify a custom port to run the service on by passing the `server.port` property when running the JAR. The
following command will start the service listening on port 9090 (`0.0.0.0:9090/api/email/1`):

```bash
$ java -Dserver.port=9090 -jar target/email-*.jar
```

## Contributing

Contributions are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
