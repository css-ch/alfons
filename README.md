![Alfons](https://raw.githubusercontent.com/McPringle/alfons/main/src/main/resources/META-INF/resources/images/alfons.png)

# Alfons

**An open-source application for companies to manage requests from their employees to attend a conference.
With approval workflow, budget management, and reports.**

## Architecture

*Alfons* is written using the [Java programming language](https://en.wikipedia.org/wiki/Java_(programming_language)). The main framework is [Spring](https://spring.io/). For the user interface, we use [Vaadin Flow](https://vaadin.com/flow). To access the database, we rely on [jOOQ](https://www.jooq.org/). To create and update the structure of the database, [Flyway](https://flywaydb.org/) is used. The build is managed by [Apache Maven](https://maven.apache.org/).

## Configuration

The file `application.properties` contains only some default values. To override the default values and to specify other configuration options, just set them as environment variables. The following sections describe all available configuration options. You only need to specify these options if your configuration settings differ from the defaults.

### Server

The server runs on port 8080 by default. If you don't like it, change it:

```
PORT=8080
```

### Mail

To be able to send mails, you need to specify an SMTP server (defaults are `localhost` and port`25`):

```
MAIL_HOST=localhost
MAIL_PORT=25
```

### Database

*Alfons* needs a database to store the business data. By default, *Alfons* comes with [MariaDB](https://mariadb.org/) drivers, which is recommended because we are using it during development and it is highly tested with *Alfons*. All JDBC compatible databases are supported, but you need to configure the JDBC driver dependencies accordingly. Please make sure that your database is using a unicode character set to avoid problems storing data containing unicode characters.

The `DB_USER` is used to access the *Alfons* database including automatic schema migrations and needs `ALL PRIVILEGES`.

```
DB_URL=jdbc:mariadb://localhost:3306/alfons?serverTimezone\=Europe/Zurich&allowMultiQueries=true
DB_USER=johndoe
DB_PASS=verysecret
```

The database schema will be migrated automatically by *Alfons*.

#### Important MySQL and MariaDB configuration

MySQL and MariaDB have a possible silent truncation problem with the `GROUP_CONCAT` command. To avoid this it is necessary, to configure these two databases to allow multi-queries. Just add `allowMultiQueries=true` to the JDBC database URL like in this example (you may need to scroll the example code to the right):

```
DB_URL=jdbc:mariadb://localhost:3306/alfons?serverTimezone\=Europe/Zurich&allowMultiQueries=true
```

### Admin

You will need at least one administrator. Therefore, you should add yourself as admin to the database, **after** you have started *Alfons* (because the database tables will be created at the first start):

```sql
INSERT INTO `employee` (`id`, `first_name`, `last_name`, `email`, `admin`, `password_change`)
VALUES (1, 'First name', 'Last name', 'email@domain.tld', TRUE, TRUE);
```

Then, open `http://localhost:8080/login`, enter your email address, and click on "I forgot my password" to start the password reset process (you will receive a one time password via email), and set your own admin password.

## Running the application

### Run a database

*Alfons* needs a database. If you don't have a database available, you can use Docker to easily run a MariaDB instance. You can run it with temporary storage (your data will be gone when you stop the MariaDB instance) or with permanent storage (your data will be persisted on your drive). The following examples will run MariaDB version 10.11.2 on port 3306. An empty database with the name "alfons" and a user with the name "alfons" and password "zitterbacke" will be created. The user has all privileges on the database "alfons".

#### Run MariaDB with temporary storage

```
docker run -d -p 3306:3306 --rm --name alfonsdb \
    -e MARIADB_RANDOM_ROOT_PASSWORD=yes \
    -e MARIADB_DATABASE=alfons \
    -e MARIADB_USER=alfons \
    -e MARIADB_PASSWORD=zitterbacke \
    mariadb:10.11.2
```

#### Run MariaDB with permanent storage

```
docker run -d -p 3306:3306 --rm --name alfonsdb \
    -v /your/own/datadir:/var/lib/mysql
    -e MARIADB_RANDOM_ROOT_PASSWORD=yes \
    -e MARIADB_DATABASE=alfons \
    -e MARIADB_USER=alfons \
    -e MARIADB_PASSWORD=zitterbacke \
    mariadb:10.11.2
```

Replace "/your/own/databadir" with an existing directory on one of your drives. Don't replace "mysql" in this command -- it runs MariaDB but the name of the data directory inside the container is still "mysql" (because MariaDB is a fork of MySQL).

### Run Alfons

*Alfons* needs environment variables (see [Configuration](#configuration)) to work properly. You can specify them system-wide, in your shell, or for the actual command. If you run *Alfons* from inside your IDE, you can specify the environment variables in the run configuration.

#### Run Alfons using Maven

This project is a standard Maven project. It makes use of the Maven Wrapper, so you don't need to have Maven installed on your machine. To run *Alfons* from the command line, type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open http://localhost:8080 in your browser.

#### Run Alfons from your IDE

You can also import the project to your IDE of choice as you would with any Maven project. Read more on [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Deploying to Production

### Production build

Use the following command to create a production build:

```
./mvnw clean package -Pproduction
```

This will build a JAR file with all the dependencies and front-end resources, ready to be deployed. The file can be found in the `target` folder after the build completes.  Once the JAR file is built, you can run it using

```
java -jar target/alfons-1.0-SNAPSHOT.jar
```

*Very important: Don't forget the environment variables to [configure](#configuration) *Alfons*, or it will not work!*

### Deploying using Docker

To build the Dockerized version of the project, run the [production build](#production-build) followed by:

```
docker build . -t alfons:latest
```

Once the Docker image is correctly built, you can test it locally using

```
docker run -p 8080:8080 alfons:latest
```

*Very important: Don't forget the environment variables to [configure](#configuration) *Alfons*, or it will not work!*

### Deploying using Kubernetes

We assume here that you have the Kubernetes cluster from Docker Desktop running (can be enabled in the settings).

First build the [Docker image](#deploying-using-docker) for your application. You then need to make the Docker image available to your cluster. With Docker Desktop Kubernetes, this happens automatically. With Minikube, you can run `eval $(minikube docker-env)` and then build the image to make it available. For other clusters, you need to publish to a Docker repository or check the documentation for the cluster.

The included `kubernetes.yaml` sets up a deployment with 2 pods (server instances) and a load balancer service. You can deploy the application on a Kubernetes cluster using

```
kubectl apply -f kubernetes.yaml
```

*Very important: Don't forget the environment variables to [configure](#configuration) *Alfons*, or it will not work!*

If everything works, you can access your application by opening http://localhost:8000/. If you have something else running on port 8000, you need to change the load balancer port in `kubernetes.yaml`.

Tip: If you want to understand which pod your requests go to, you can add the value of `VaadinServletRequest.getCurrent().getLocalAddr()` somewhere in your UI.

#### Troubleshooting

If something is not working, you can try one of the following commands to see what is deployed and their status.

```
kubectl get pods
kubectl get services
kubectl get deployments
```

If the pods say `Container image "alfons:latest" is not present with pull policy of Never` then you have not built your application using Docker or there is a mismatch in the name. Use `docker images ls` to see which images are available.

If you need even more information, you can run

```
kubectl cluster-info dump
```

that will probably give you too much information but might reveal the cause of a problem.

If you want to remove your whole deployment and start over, run

```
kubectl delete -f kubernetes.yaml
```

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorials at [vaadin.com/tutorials](https://vaadin.com/tutorials).
- Watch training videos and get certified at [vaadin.com/learn/training](https://vaadin.com/learn/training).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/components](https://vaadin.com/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes). 
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).

## Contributors

Special thanks for all these wonderful people who had helped this project so far ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://fihlon.swiss/"><img src="https://avatars.githubusercontent.com/u/1254039?v=4?s=100" width="100px;" alt="Marcus Fihlon"/><br /><sub><b>Marcus Fihlon</b></sub></a><br /><a href="#projectManagement-McPringle" title="Project Management">📆</a> <a href="#ideas-McPringle" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/McPringle/alfons/commits?author=McPringle" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

## Contributing

New contributors are always welcome! If you discover errors or omissions in the source code, documentation, or website content, please don’t hesitate to submit an issue or open a pull request with a fix.

Here are some ways **you** can contribute:

- by using prerelease (alpha, beta or preview) versions
- by reporting bugs
- by suggesting new features
- by writing or editing documentation
- by writing code with tests -- *no patch is too small*
  - fix typos
  - add comments
  - clean up inconsistent whitespace
  - write tests!
- by refactoring code
- by fixing [issues](https://github.com/McPringle/alfons/issues)
- by reviewing [patches](https://github.com/McPringle/alfons/pulls)

The [Contributing](CONTRIBUTING.md) guide provides information on how to create, style, and submit issues, feature requests, code, and documentation to the *Alfons* project.

## Copyright and License

[GNU Affero General Public License](https://www.gnu.org/licenses/agpl-3.0.html)

*Copyright © Marcus Fihlon and the individual contributors to Alfons.*

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
