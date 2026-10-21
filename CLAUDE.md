# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture

A Selenium + TestNG UI test framework using the Page Object pattern, testing https://the-internet.herokuapp.com (login demo, credentials `tomsmith` / `SuperSecretPassword!`). Tests hit the live site, so they need network access and a local Chrome or Firefox.

- `src/main/java/pages/`: page objects (`LoginPage`, `SecureAreaPage`) extending `BasePage`. `BasePage` holds the `driver` and a shared 10-second `WebDriverWait`, and calls `PageFactory.initElements` once in its constructor, so subclasses must not call it again. Pages declare elements as private `@FindBy` `WebElement` fields and interact through explicit waits (`waitForVisibilityOfElement`, `ExpectedConditions`) rather than using them directly. Actions that navigate return the next page object (e.g. `SecureAreaPage.logout()` returns `LoginPage`), and pages expose `isLoaded()` for asserting navigation.
- `src/test/java/tests/`: TestNG test classes extending `BaseTest`. `BaseTest` gets a fresh driver from `utils` in `@BeforeMethod` and opens `baseUrl`. The browser comes from the `browser` parameter in `testng.xml`, then `-Dbrowser`, then `config.properties`; headless mode comes from `-Dheadless`, then `config.properties`. Supported browsers are `chrome` and `firefox` (add a `case` in `DriverFactory` for more); subclasses create their page objects in their own `@BeforeMethod` (e.g. `LoginTest.initPages()`), which must have a different name so it doesn't override `BaseTest.beforeMethod()`. `@AfterMethod(alwaysRun = true)` quits the driver, first saving a screenshot of failed tests to `target/screenshots/<TestClass>_<method>_<browser>_<timestamp>.png` (via `utils.ScreenshotUtil`). Test classes need a no-arg constructor for TestNG, and must create page objects inside test methods or `@BeforeMethod`, never as field initializers, since `driver` is still null when the class is built.
- `utils`: `ConfigReader` loads `src/main/resources` (`baseUrl`, `browser`, `headless`, credentials); `DriverFactory` creates the `WebDriver`.

The test framework is TestNG (`org.testng.annotations.Test`); don't use JUnit. `selenium-java` is in compile scope so page objects can live in `src/main/java`. Selenium Manager (built into Selenium 4.6+) downloads browser drivers automatically, so no WebDriverManager or driver binaries are needed.

## Build and test

The environment has Maven 3.6.3 and OpenJDK 17. There is no Maven wrapper (`.mvn/` is empty).

```bash
mvn compile                              # compile
mvn test                                 # run all tests (browser from config.properties, default chrome)
mvn test -Dbrowser=firefox               # run on Firefox
mvn test -Dheadless=true                 # run headless
mvn test -Pcross-browser                 # run testng.xml: Chrome + Firefox in parallel
mvn test -Dtest=LoginTest                # run one test class
mvn test -Dtest=LoginTest#logout         # run one test method
```

`pom.xml` sets `maven.compiler.release` to 17 and pins `maven-compiler-plugin` 3.13.0 and `maven-surefire-plugin` 3.2.5. Keep those pins: Maven 3.6.3's default compiler plugin (3.1) ignores `release` and targets Java 5, which JDK 17 rejects, and its default Surefire (2.12.4) is too old for TestNG 7. Surefire auto-detects TestNG; `testng.xml` is used only by the `cross-browser` profile. It runs each `<test>` block in parallel, which is safe because TestNG creates separate test-class instances for each `<test>`, so the `driver` field isn't shared between threads.

## CI

`Jenkinsfile` defines a Jenkins declarative pipeline (job type "Pipeline script from SCM", branch `main`). It polls GitHub every ~5 minutes and can also be run manually with parameters `BROWSER` (`chrome`/`firefox`) and `SUITE` (`testng.xml` runs `-Pcross-browser`; `Smoke`/`Regression` run `-Dgroups=<group>`). Tests always run with `-Dheadless=true`, since the Jenkins agent has no display. The agent needs JDK 17, Maven and the browsers installed. Surefire XML is published with `junit` (failed tests make the build UNSTABLE), and failure screenshots are archived as artifacts.
