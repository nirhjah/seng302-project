# SENG302 TAB Project Overview
Team Analytics Buddy (TAB) is an application designed for active individuals to access their team's game analytics. This application allows users to register an account, personalize their account and create teams. Users are able to view the profiles of other users, view teams and search and filter for specific teams and profiles. 

## Prerequisites
- JDK >= 17 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Gradle

## What's Included
This project comes with some of the following:

- Spring Boot
- Thymeleaf
- Thymeleaf Extras
- Junit 5
- Open Route API
- Mockito (mocking unit tests)

## How to run
### 1 - Running the project
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

### 2 - Using the application
> User need to have an account to access most functionality of the application.
> - There is a default account, that is already confirmed
> - Username/Email: admin@gmail.comes
> - Password: 1

## How to run tests
From the root directory ...

On Linux:
```
./gradlew test
```

On Windows:
```
gradlew test
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
