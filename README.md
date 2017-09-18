ManyWho Email Service
=====================

This service allows you to send email using the flow.


### Build
To build the service, you will need to have Apache Ant, Maven 3 and a Java 8 implementation installed.

You will need to generate a configuration file for the service by running the provided `build.xml` script with Ant, and 
passing in a valid URL to a Redis instance:

To keep the attached files during the flow this service use aws s3.

```bash
$ ant -Ds3.bucket_name=s3bucketname \
-Ds3.aws_access_key_id=s3id \
-Ds3.aws_secret_access_key=s3secretkey
```

Now you can build the runnable shaded JAR:

```bash
$ mvn clean package
```
#### Running

The service is a Jersey JAX-RS application, that by default is run under the Grizzly2 server on port 8080 (if you use 
the packaged JAR).

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
##### Usage
The service configuration can specify S3 parameters to be used for attachment.
If neither is specified, a default S3 bucket is provided to upload file that need to be attached.
If your own S3 is used and the attached files need to be retained set the flag 'RetainS3File' to true.
###### Configurations
 - Host
 - Port
- Username
- Transport
- BoxAppUserId
- BoxEnterpriseId
- UseBoxForAttachment
- S3AccessKeyId
- s3AccessSecret
- S3BucketName
- S3Region
- RetainFiles

###### Box config from S3
If Box is used for email attachment this app requires you to setup its box config from an encrypted s3 bucket. 

In AWS add s3 bucket with an "access key user" + policy for read access to the bucket

login to your box developer account and create enterprise app,
Generate Keys and add resulting json config file to your s3 bucket.
The json file should have the name enterpriseId_publicKeyId_config.json e.g. this is how box will generate the file for you.

Example file: it contains all config required by box sdk to connect and sign requests
```
515799_1beowk9s_config.json
```

```json
{
  "boxAppSettings": {
    "clientID": "dummyClientId",
    "clientSecret": "dummySecret",
    "appAuth": {
      "publicKeyID": "1beowk9s",
      "privateKey": "-----BEGIN ENCRYPTED PRIVATE KEY-----\nsomeKey=\n-----END ENCRYPTED PRIVATE KEY-----\n",
      "passphrase": "passphrase"
    }
  },
  "enterpriseID": "515799"
}
```
ask for this app to be authorised by admin


## Contributing

Contribution are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
