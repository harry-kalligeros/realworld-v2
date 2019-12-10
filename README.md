# Realworld-v2

Re-imagining the [Realworld REST API](https://github.com/gothinkster/realworld/tree/master/api),
while splitting its functionality in microservices with [Quarkus](https://quarkus.io/).

## The build system

The build system is Maven and is configured by a set of properties and profiles, as follows:

### Build properties

The following properties are local to an environment; they can be specified as `-Dpropname=propvalue` command line arguments,
or placed in a local Maven profile in `~/.m2/settings.xml`.

- `database.user.url`, `database.article.url`: The JDBC URL of the database for the respective microservice
- `database.user.username`, `database.article.username`: The DB user name
- `database.user.password`, `database.article.password`: The DB password
- **(TODO)** `db.env` (default: `dev`): Needed only by Liquibase to indicate which environment-specific [contexts](https://www.liquibase.org/documentation/contexts.html)
will it activate; e.g. `dev` will activate the `data-dev` context

### Build profiles

- `article-h2`, `user-h2`: Activate the H2 database for the server and Liquibase for the respective microservice (currently `h2` and `postgres` are the only DB options)
- `article-dbupdate`, `user-dbupdate`: Execute Liquibase to bring the respective database up-to-date (in the case of embedded H2 it will create it if it doesn't exist; just make sure that the directory exists)
- `test-h2`: This will activate the DAO tests, using an in-memory H2 database
- `article-quarkus-dev`, `user-quarkus-dev`: Activate `quarkus:dev` for the respective microservice; no not use together in the same command
  (naturally there is no problem running them in parallel, as long as they run from different shells)
- `docker`: Activating the Docker image build

### Updating dependencies

The versions of all dependencies are controlled by Maven properties in the form `version.<uniqueId>`,
where `<uniqueId>` is a unique identifier for the dependency, preferably the artifact id, but anything
unique and sufficiently descriptive will do. As such, detecting updates is as simple as running:

```shell
mvn versions:display-property-updates
```

If a dependency is left behind for a reason, please add a comment in the parent pom.

## Building

### Creating/updating the DB

Decide the database to use and make sure Maven picks up the corresponding properties.

Assuming that the properties are defined through a Maven profile, e.g. like the following in `~/.m2/settings.xml`:

```xml
                <profile>
                        <id>realworld-v2-local-h2</id>
                        <properties>
                                <database.article.url>jdbc:h2:/home/myuser/h2/article</database.article.url>
                                <database.article.username>sa</database.article.username>
                                <database.article.password>sa</database.article.password>
                                <database.user.url>jdbc:h2:/home/myuser/h2/user</database.user.url>
                                <database.user.username>sa</database.user.username>
                                <database.user.password>sa</database.user.password>
                        </properties>
                </profile>
```

Then make sure that the directory `/home/myuser/h2` exists and run:

```shell
mvn process-resources -Particle-h2,user-h2,article-dbupdate,user-dbupdate,realworld-v2-local-h2
```

Otherwise, you have to specify the properties by command line:

```shell
mvn process-resources -Particle-h2,user-h2,article-dbupdate,user-dbupdate -Ddatabase.article.url=... -Ddatabase.article.username=... -Ddatabase.article.password=... -D...
```

### Building the JAR artifacts

```shell
mvn clean package -Puser-h2,article-h2,test-h2,realworld-v2-local-h2
```

You can omit `test-h2` to skip the DB tests.

### Building the native artifacts

**TODO**

### Building the Docker image

Building the Docker images occurs during the `package` phase.
It is not active by default; activate it with the `docker` profile.
I.e. the Maven command line should be amended as follows:

```shell
mvn ... package -Pdocker,...
```

E.g.

```shell
mvn clean package -Pdocker,user-h2,article-h2,test-h2,realworld-v2-local-h2
```

#### Docker compose

There is a `docker-compose` file under `realworld-v2-docker/docker-compose` that can be used to start the collaborating
applications: Zookeeper, Kafka and Keycloak with a predefined domain.
Make sure the images are built first!
To run:

```shell
cd realworld-v2-docker/docker-compose
docker-compose -p rwl up -d    # the first time
docker-compose -p rwl start    # to start
docker-compose -p rwl stop     # to stop
docker-compose -p rwl down     # to remove the containers, without removing the persistent volumes
docker-compose -p rwl down -v  # to remove the containers, also removing the persistent volumes
```

## Launching

You have to build first! At least `mvn ... package` is required.

Development launch, assuming the profile `realworld-v2-local-h2` is defined in `settings.xml` as above:

```shell
mvn process-classes -Particle-quarkus-dev,article-h2,realworld-v2-local-h2
```

Likewise for user:

```shell
mvn process-classes -Puser-quarkus-dev,user-h2,realworld-v2-local-h2
```

Note that it requires the `process-classes` goal, not just `compile`. The reason is that some modules
need to be indexed by Jandex, and the Jandex goal runs in the `process-classes` phase, which is right after `compile`
[by default](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).
