# Setup Instructions for pom-demo Tests

## Important: Pre-requisites

### 1. Install Required Browsers
Before running tests, ensure these browsers are installed on your machine:
- **Google Chrome** - https://www.google.com/chrome/
- **Mozilla Firefox** - https://www.mozilla.org/firefox/
- **Microsoft Edge** - https://www.microsoft.com/edge

### 2. Verify Java Installation
```cmd
java -version
```
Should show Java 11 or higher. If not installed:
- Download from: https://www.oracle.com/java/technologies/downloads/

### 3. Environment Variables (Optional but Recommended)
Create a directory for drivers and set environment variable:
```cmd
# Create directory
mkdir C:\drivers

# Set system environment variable (Windows):
setx DRIVERS_PATH C:\drivers
```

## Project Setup Steps

### Step 1: Clean and Rebuild Project
1. In Eclipse, right-click on the project
2. Select **Maven → Update Project**
   - Force Update should be checked
   - Click OK
3. Wait for dependencies to download
4. Right-click project → **Project → Clean**
5. Right-click project → **Project → Build Project**

### Step 2: Verify All Dependencies Installed
- Check Maven Repository is accessible
- Verify internet connection is active
- pom.xml should show no errors in Eclipse

### Step 3: Run Tests

#### Option A: Run from Eclipse (Recommended)
1. Right-click on `testng.xml`
2. Select **Run As → TestNG Suite**

#### Option B: Run Simple Test First (For Debugging)
1. Right-click on `testng-simple.xml`
2. Select **Run As → TestNG Suite**

#### Option C: Run Individual Test Class
1. Right-click on `GoogleTest.java`
2. Select **Run As → TestNG Test**

## Expected First Run Behavior

**First time running test (may take 2-5 minutes):**
- WebDriverManager will download browser drivers (~100-300 MB)
- Drivers will be cached for future runs
- Console will show detailed logging
- Browsers will open and close automatically

**Subsequent runs:**
- Should be faster (30 seconds - 2 minutes per browser)
- Browsers will open, perform search, and close

## Troubleshooting

### Issue: "No value present" Error
**Solution:**
- Ensure browsers are installed on your machine
- Check internet connection (WebDriverManager needs to download drivers)
- Try running testng-simple.xml first (Chrome only)

### Issue: WebDriver Path Not Found
**Solution:**
- Ensure you have write permissions in project directory
- Try creating the drivers folder manually:
  ```cmd
  mkdir C:\Users\abrah\eclipse-workspace\pom-demo\drivers
  ```

### Issue: Port Already in Use
**Solution:**
- Close any existing browser windows
- Kill existing driver processes:
  ```cmd
  taskkill /IM chromedriver.exe /F
  taskkill /IM geckodriver.exe /F
  taskkill /IM msedgedriver.exe /F
  ```

### Issue: Firefox/Edge Not Found
**Solution:**
- Edit testng.xml to comment out FirefoxTest and/or EdgeTest
- Focus on Chrome which is most reliable
- Install Firefox/Edge if all 3 browsers are needed

## Console Output Guide

### Good Output (Test Passing)
```
=== Setup Method Started ===
Browser parameter received: chrome
Initializing chrome driver...
Chrome driver initialized
=== Driver initialized successfully ===
Opening Google...
Google opened successfully
Attempting to accept cookies...
Searching for: Selenium tutorial
Page title: Selenium - Google Search
*** Test Completed Successfully ***
=== Teardown Complete ===
```

### Common Errors

**"No value present"**
- WebDriverManager failed to initialize
- Usually means browser not installed or path issues

**"Connection refused"**
- Browser failed to start
- Check browser installation

**"TimeoutException"**
- Element not found within 10 seconds
- Network issue or page loading slowly

## Project Structure

```
pom-demo/
├── pom.xml                          # Maven configuration with all dependencies
├── testng.xml                       # All browsers test suite (Chrome, Firefox, Edge)
├── testng-simple.xml               # Simple test suite (Chrome only)
├── webdrivermanager.properties     # WebDriverManager configuration
├── src/test/java/
│   ├── base/
│   │   └── BaseTest.java          # Base test class with WebDriver setup
│   ├── pages/
│   │   └── GooglePage.java        # Google page object model
│   └── tests/
│       └── GoogleTest.java        # Main test class
└── drivers/                        # (Auto-created) WebDriver cache directory
```

## Test Execution Flow

1. **Setup (@BeforeMethod)**
   - WebDriverManager downloads driver if needed
   - Initializes WebDriver (Chrome/Firefox/Edge)
   - Maximizes browser window

2. **Test (@Test)**
   - Opens Google.com
   - Accepts cookies popup
   - Searches for "Selenium tutorial"
   - Validates page title contains "Selenium"

3. **Teardown (@AfterMethod)**
   - Closes browser
   - Quits driver

## Performance Notes

- Chrome: Fastest (typically ~30 seconds per test)
- Firefox: Medium (~45 seconds per test)
- Edge: Medium-Slow (~60 seconds per test)

Total time for all 3 browsers: ~2-3 minutes on first run, ~3-4 minutes on subsequent runs

## Next Steps

1. Ensure all browsers are installed
2. Run `testng-simple.xml` first to verify setup
3. If successful, run full `testng.xml` suite
4. Check console output for any issues
5. Refer to TROUBLESHOOTING.md for additional help

---
**Last Updated:** March 15, 2026
**Version:** 2.0
**Status:** Production Ready
