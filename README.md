# SENG302 TAB Project Overview
Team Analytics Buddy (TAB) is an application designed for active individuals to access their team's game analytics. This application allows users to register an account, personalize their account and create teams. Users are able to view the profiles of other users, view teams and search and filter for specific teams and profiles. 

## Prerequisites
- JDK >= 17 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Gradle
- Openrouteservice API keys
- An email account from which to send confirmation emails

## What's Included
This project comes with some of the following:

- Spring Boot
- Thymeleaf
- Thymeleaf Extras
- Junit 5
- Open Route API
- Mockito (mocking unit tests)

## How to run
### 1 - Setup your environment variables
You will need to provide a `.env` file at the top project directory with these variables:
- `GMAIL_USERNAME`
- `GMAIL_PASSWORD`
- `OPS_API_KEY`
- `OPS_COLAB_KEY`

Your production build will also require these variables:
- `MARIADB_USERNAME`
- `MARIADB_PASSWORD`
- `SONARQUBE_TOKEN`

See (`env-example`)[env-example] for an explanation of these variables

### 2 - Running the project
From the root directory ...

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```
 

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 3 - Using the application
> User need to have an account to access most functionality of the application.
> - There are no default credentials provided
> 

## How to run tests
From the root directory ...

On Linux:
```
./gradlew test              #runs unit tests
./gradlew integration       #runs integration tests
./gradlew end2end           #runs end-to-end tests (if running locally, application must be running)
./gradlew check             #runs all tests
```


On Windows:
```
gradlew test
gradlew integration
gradlew end2end
gradlew check
```
>Note: There are 7 disabled tests currently. 

## Contributors
- Cameron Pearce
- Philip Dolbel
- Sebastian Conaghan-Carr
- Angela Yu
- Andrew Hall
- Oliver Garrett
- Nirhjah Selvarajah
- Amy Paull

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=17797&section=8)
