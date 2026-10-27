# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture

A Selenium + TestNG UI test framework using the Page Object pattern, testing https://the-internet.herokuapp.com (login demo, credentials `tomsmith` / `SuperSecretPassword!`). Tests hit the live site, so they need network access and a local Chrome or Firefox.

- `src/main/java/pages/`: page objects (`LoginPage`, `SecureAreaPage`) extending `BasePage`. `BasePage` holds the `driver` and a shared 10-second `WebDriverWait`, and calls `PageFactory.initElements` once in its constructor, so subclasses must not call it again. Pages declare elements as private `@FindBy` `WebElement` fields and interact through explicit waits (`waitForVisibilityOfElement`, `ExpectedConditions`) rather than using them directly. Actions that navigate return the next page object (e.g. `SecureAreaPage.logout()` returns `LoginPage`), and pages expose `isLoaded()` for asserting navigation.
- `src/test/java/tests/`: TestNG test classes extending `BaseTest`. `BaseTest` gets a fresh driver from `utils` in `@BeforeMethod` and opens `baseUrl`. The browser comes from `-Dbrowser`, then `config.properties`. (`beforeMethod` has an `@Optional String browser` argument meant for a `testng.xml` parameter, but without `@Parameters("browser")` TestNG never fills it, so a `<parameter>` in `testng.xml` has no effect.) headless mode comes from `-Dheadless`, then `config.properties`. Supported browsers are `chrome` and `firefox` (add a `case` in `DriverFactory` for more); subclasses create their page objects in their own `@BeforeMethod` (e.g. `LoginTest.initPages()`), which must have a different name so it doesn't override `BaseTest.beforeMethod()`. `@AfterMethod(alwaysRun = true)` quits the driver, first saving a screenshot of failed tests to `target/screenshots/<TestClass>_<method>_<browser>_<timestamp>.png` (via `utils.ScreenshotUtil`). Test classes need a no-arg constructor for TestNG, and must create page objects inside test methods or `@BeforeMethod`, never as field initializers, since `driver` is still null when the class is built.
- Visual tests: `VisualTest` (group `Visual`, pages Home, Add/Remove Elements, Hovers, Tables, with page objects `HomePage`, `AddRemoveElementsPage`, `HoversPage`, `TablesPage`) extends `VisualBaseTest extends BaseTest`, which provides `assertMatchesBaseline(name)` (viewport, scrolled to top, mouse moved away) and `assertMatchesBaseline(name, element)` (element only, hover kept). Before each capture it injects CSS that disables transitions/animations (the site's buttons fade color for 300 ms after a click, which otherwise makes screenshots flaky) and hides the caret. `utils.ImageComparator` does a plain-Java pixel diff: a pixel differs when any RGB channel differs by more than `visualColorTolerance`; the test fails when more than `visualThreshold` % of pixels differ or the sizes differ. Baselines live in `src/test/baselines/<browser>[-headless]/<TestClass>/<name>.png` (committed; headed and headless viewports differ, so each has its own folder). A missing baseline is recorded and the test SKIPPED; `-DupdateBaselines=true` re-records all of them (review the PNG changes before committing). Failures write `<name>_actual.png` and `<name>_diff.png` (differences in red) to `target/visual/...` and attach `expected`/`actual`/`diff` with label `testType=screenshotDiff`, which Allure's screen-diff plugin shows side by side. Jenkins runs headless, so record baselines with `-Dheadless=true` on a machine that renders like the Jenkins agent.
- `utils`: `ConfigReader` loads `src/main/resources` (`baseUrl`, `browser`, `headless`, credentials); `DriverFactory` creates the `WebDriver`.

The test framework is TestNG (`org.testng.annotations.Test`); don't use JUnit. `selenium-java` is in compile scope so page objects can live in `src/main/java`. Selenium Manager (built into Selenium 4.6+) downloads browser drivers automatically, so no WebDriverManager or driver binaries are needed.

## Build and test

The environment has Maven 3.6.3 and OpenJDK 21. There is no Maven wrapper (`.mvn/` is empty).

```bash
mvn compile                              # compile
mvn test                                 # run all tests (browser from config.properties, default chrome)
mvn test -Dbrowser=firefox               # run on Firefox
mvn test -Dheadless=true                 # run headless
mvn test -Pcross-browser                 # run testng.xml: Smoke block, then Regression block (one browser)
mvn test -Dtest=LoginTest                # run one test class
mvn test -Dtest=LoginTest#logout         # run one test method
mvn test -Dgroups=Visual -Dheadless=true # visual tests (first run records baselines and skips)
mvn test -Dgroups=Visual -Dheadless=true -DupdateBaselines=true   # re-record all baselines
mvn allure:serve                         # build the Allure report from the last run and open it
mvn allure:report                        # write the report to target/site/allure-maven-plugin
```

Allure (`allure-testng`, compile scope so pages can use `@Step`) writes results to `target/allure-results` (set in `src/test/resources/allure.properties`). Page objects mark their public actions with `@Step`; `@Step` records every method argument in the result files (even with `@Param(mode = HIDDEN)`), so actions that take a secret use `Allure.step("name", () -> ...)` instead, as `LoginPage.login` does. Tests carry `@Epic`/`@Feature`/`@Severity`/`@Description`. `BaseTest` attaches failure screenshots to the report and writes `environment.properties` (browsers, headless, base URL). Steps only get recorded with the AspectJ agent that Surefire's `argLine` adds; when running tests from IntelliJ, put the same `-javaagent:` in the run configuration's VM options. `allure-maven` downloads the Allure CLI itself (`reportVersion`), so nothing needs installing locally.

`pom.xml` sets `maven.compiler.release` to 21 and pins `maven-compiler-plugin` 3.13.0 and `maven-surefire-plugin` 3.2.5. Keep those pins: Maven 3.6.3's default compiler plugin (3.1) ignores `release` and targets Java 5, which JDK 21 rejects, and its default Surefire (2.12.4) is too old for TestNG 7. Surefire auto-detects TestNG; `testng.xml` is used only by the `cross-browser` profile. Despite the profile's name, it does not run several browsers: it has three `<test>` blocks, "Smoke Test" (`LoginTest`, `AlertsTest`, group `Smoke`), "Visual Test" (`VisualTest`, group `Visual`) and "Regression Test" (the five functional classes, groups `Smoke` + `Regression`), run one after the other (`thread-count="1"`, no `parallel`) in the browser chosen by `-Dbrowser`. Smoke tests therefore run twice, 18 test runs in total. Run it with `-Dheadless=true` (as Jenkins does) so the visual tests use the `chrome-headless` baselines.

## CI

`Jenkinsfile` defines a Jenkins declarative pipeline (job type "Pipeline script from SCM", branch `main`). It polls GitHub every ~5 minutes and can also be run manually with parameters `BROWSER` (`chrome`/`firefox`) and `SUITE` (`testng.xml` runs `-Pcross-browser`; `Smoke`/`Regression`/`Visual` run `-Dgroups=<group>`). Tests always run with `-Dheadless=true`, since the Jenkins agent has no display. The agent needs JDK 21 (a full JDK, not just the JRE), Maven and the browsers installed. Jenkins and the build both use JDK 21 (`/usr/lib/jvm/java-21-openjdk-amd64`, set as `JAVA_HOME` in the Jenkins systemd override). Surefire XML is published with `junit` (failed tests make the build UNSTABLE), and failure screenshots plus visual-test actual/diff images (`target/visual/`) are archived as artifacts. The `allure` post step publishes the Allure report (with trend history) on each build; it needs the Allure Jenkins plugin and an Allure Commandline installation named `allure` under Manage Jenkins → Tools.
