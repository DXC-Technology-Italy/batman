# BATMAN
Hi and welcome to BATMAN (**BA**tch **T**ask **MAN**agment)!  
*In the darkness of the night of batch, BATMAN it is the beacon that illuminates and comes to your rescue.*
Based on Spring Batch and Spring Boot, BATMAN is designed to aid batch development, release and manage.
With a simple setup and little coding, you'll be able to develop batches in no time.

# Disclaimer
BATMAN is still under development.
You can use it for demos to demonstrate its potential (see batman-war description) and collaborate with us to improve it.

# What you need
You need only these projects for start your journey with BATMAN:

- [x] batman-parent
- [x] batman-core
- [ ] batman-sample-impl
- [ ] batman-sample-war

## batman-parent
Little things to say, this project contains the POM's dependencies and plugins for the maven stuff.

## batman-core
Contains, you see, all the **core** java classes. All magical things happen thanks to him.  
Two are the most important classes:

 - BatmanCommonComponent - abstract class to extend to create a new Job
 - BatmanCommonConfig - class that deals with the management of Jobs and configurations

For other considerations please see the javadocs and examples in the batman-sample-impl project.
Fell free to share your concerns and tips with us.

## batman-sample-impl
Contains three simple examples of Job managed by BATMAN and basic configuration in the *resources* path.

## batman-sample-war
Sample war distribution ready-to-deploy with batman-sample-impl

### Configuring a server
First, you need to create a new server with this arguments:

	-Dspring.config.location=<application-profile.yml path position> -Dspring.profiles.active=<active profile> -Dlogs.path=<log path position> -Dlogging.config=file://<logback.xml position>
Below a sample server start arguments configuration:

	-Dspring.config.location=/conf/appConfig/batman/ -Dspring.profiles.active=dev -Dlogs.path=/conf/appLog/ -Dlogging.config=file:///conf/appConfig/batman/logback.xml

You can now deploy the batman-sample-war.war in the new server and activate it. Simple!

*Remember to deploy application-profile.yml and logback.xml in the specified path before the server start*

### REST services
The REST services are exposed on */batman/* root.
Below a sample call for one-shot-run *BatchComponentThree* job:

	http://localhost:7011/batman/runJob?jobName=BatchComponentThree

# Roadmap

- Better and specific documentation for application.yml
- GUI
- Inner support for multiple database
- . . .

# Version

 - 0.0.1-SNAPSHOT