# Troubleshooting Guide - pom-demo Tests

## If Tests Still Fail After Applying Fixes

### 1. **Configuration Failures Still Appearing**
**Solution:**
- Clean and rebuild the project:
  - Project → Clean
  - Right-click project → Maven → Update Project
  - Project → Build Project

### 2. **SLF4J Warning Still Showing**
**Solution:**
- Verify slf4j-simple dependency is in pom.xml
- Right-click project → Maven → Update Project (Force Update)
- Close and reopen Eclipse

### 3. **Chrome/Firefox/Edge Driver Not Found**
**Solution:**
- WebDriverManager should automatically download drivers
- Check that internet connection is available (needed for first run)
- If still failing, verify antivirus isn't blocking driver downloads
- Check logs for specific driver errors

### 4. **Tests Timeout or Hang**
**Solution:**
- Verify you have a graphical display available
- Increase timeout in GooglePage.java:
  ```java
  new WebDriverWait(driver, Duration.ofSeconds(20)); // Increase from 10
  ```
- Check system resources (RAM, CPU)

### 5. **Browser Window Not Closing Properly**
**Solution:**
- This is handled by `driver.quit()` in tearDown()
- If processes remain, manually kill them:
  ```
  taskkill /IM chromedriver.exe /F
  taskkill /IM geckodriver.exe /F
  taskkill /IM msedgedriver.exe /F
  ```

### 6. **Only One Browser Test Needed**
**Solution:**
- Use testng-simple.xml instead of testng.xml
- Or modify testng.xml to include only the desired test

### 7. **Eclipse Not Recognizing TestNG**
**Solution:**
- Ensure TestNG is installed in Eclipse:
  - Help → Eclipse Marketplace
  - Search "TestNG"
  - Install if not already installed
- Restart Eclipse after installation

### 8. **Project Build Errors**
**Solution:**
- Delete target folder
- Project → Clean
- Project → Build Project
- Wait for build to complete

## Console Logs to Check

### Expected Logs:
```
=== Setup Method Started ===
Browser parameter received: chrome
Initializing chrome driver...
Chrome driver initialized
=== Driver initialized successfully ===
Opening Google...
Google opened successfully
```

### Error Logs to Look For:
- `Failed to initialize WebDriver` - Driver setup failed
- `Browser parameter received: null` - Parameter not passed
- `Browser not supported: xxx` - Invalid browser name
- `ERROR in setup:` - Exception during setup

## Quick Checklist

- [ ] pom.xml updated with all 4 dependencies (selenium, testng, webdrivermanager, slf4j-simple)
- [ ] pom.xml updated with Maven Compiler and Surefire plugins
- [ ] BaseTest.java has @Optional annotation
- [ ] GoogleTest.java has assertions
- [ ] testng.xml has proper parameter configuration
- [ ] Project cleaned and rebuilt
- [ ] No syntax errors in IDE

## Running Tests from Command Line

If IDE testing fails, try command line (requires Maven):

```cmd
# Navigate to project directory
cd C:\Users\abrah\eclipse-workspace\pom-demo

# Clean and test
mvn clean test

# Test specific suite
mvn -Dtest=tests.GoogleTest test

# Skip tests during build
mvn clean package -DskipTests
```

## Further Debugging

To capture more detailed logs, modify logback configuration or add to pom.xml:

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.11</version>
</dependency>
```

## Contact Points for Issues

If tests still fail after all these steps:
1. Check browser compatibility (Chrome/Firefox/Edge versions)
2. Verify network connectivity (for WebDriverManager)
3. Check Java version (requires Java 8+, tested with Java 11)
4. Verify Selenium version compatibility
5. Check TestNG version (using 7.9.0)

## Performance Tips

- Firefox driver takes longer to initialize
- Chrome is typically fastest
- First run downloads drivers, subsequent runs are faster
- Tests take 5+ seconds due to Thread.sleep() in search method

---
**Last Updated:** March 15, 2026
**Project:** pom-demo
**Test Framework:** Selenium + TestNG
