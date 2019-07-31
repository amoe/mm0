## Main Class

`uk.ac.susx.shl.micromacro.MicroMacroApplication` -- or, in fact, that which is
defined in project/properties/mainClass.


## Before compiling:

Run `mvn install` from tag-dist repo.  This is needed because tag-dist is not
always deployed.  So to satisfy it you'll need to `mvn install` every new
tag-dist version.  Also, if you wipe out your local repository, you'll need to
reinstall tag-dist.

## Known errors

> Could not find artifact uk.ac.susx.tag:method51.core.datum:jar:1.5.2-SNAPSHOT
> in arcgis (https://esri.bintray.com/arcgis)`

This means that the new version of the datum api needs to be deployed to the mvn
repository.

> Error: Could not find or load main class org.apache.maven.surefire.booter.ForkedBooter

This means you either need openjdk with a version greater than 8u191-b12, or you
need to apply this workaround to maven's settings.xml:

    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                                  https://maven.apache.org/xsd/settings-1.0.0.xsd">
      <localRepository/>
      <interactiveMode/>
      <offline/>
      <pluginGroups/>
      <servers/>
      <mirrors/>
      <proxies/>
      <profiles>
        <!-- This can be removed after stretch+1 releases. -->
        <!-- https://stackoverflow.com/questions/53010200/ -->
        <profile>
          <id>SUREFIRE-1588</id>
          <activation>
            <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
            <argLine>-Djdk.net.URLClassPath.disableClassPathURLCheck=true</argLine>
          </properties>
        </profile>
      </profiles>
      <activeProfiles/>
    </settings>

You need to get the data files: the one pointed to by 

> geoJsonPath: data/LL_PL_PA_WA_POINTS_FeaturesT.json

You also need to create a database.

You need to mvn package -- but why?

> Errors referencing assets.resourcePathToUriMappings

This means that you left off the `micromacro.yml` argument to the `server`
command of Dropwizard, ya big dummy.

# mvn compile errors

Check several things.  -- Version of parent POM?
Version of datum dependency in child POM.


You might need to change the version number on the datum dependency, not this
one's pom.

## Building the front end

To build the front end,

in `src/main/resources/assets`, you need to run `npm install`.

You need to use a globally installed tsc to generate the `built` folder.  You
can run `tsc --watch` to continuously rebuild the TypeScript files.

You might need to occasionally clear the browser cache.  Not sure what causes
this.

## Using the server

amoe@cslp019129 $ java -jar target/micromacro-1.0.2.jar  server micromacro.yml

access http://localhost:8090/index.html after this, but it doesn't work because it can't get the
dependencies.


## Rebuild all

mvn -Dmaven.test.skip=true clean compile package

`uk.ac.susx.tag.method51.core.data.store2.query.Partitioner`

## Related projects

The mm-radial-spans repo provides the various stuff, as well as some of it
was integrated into the occubrow backend.

## After import steps

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO micromacro;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO micromacro;

## Why is Kotlin being used?

It's a dependency of MapDB. 

## Resources

@Path annotated resources are as follows; SelectResource; ProximityResource.

The first stage is to work out how to log.
