package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    @Parameters({"browser"})
    public void setup(@org.testng.annotations.Optional("chrome") String browser) {
        System.out.println("=== Setup Method Started ===");
        System.out.println("Browser parameter received: " + browser);

        if(browser == null || browser.trim().isEmpty()) {
            browser = "chrome";
            System.out.println("Browser was null/empty, defaulting to: " + browser);
        }

        browser = browser.trim().toLowerCase();
        System.out.println("Initializing " + browser + " driver...");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name"));
        
        try {
            if(browser.equals("chrome")){
                System.out.println("Setting up ChromeDriver...");
                try {
                    WebDriverManager.chromedriver().setup();
                } catch(Exception wdmEx) {
                    System.out.println("WebDriverManager setup failed, trying direct initialization: " + wdmEx.getMessage());
                }
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(options);
                System.out.println("Chrome driver initialized");
            } else if(browser.equals("firefox")){
                System.out.println("Setting up FirefoxDriver...");
                try {
                    WebDriverManager.firefoxdriver().setup();
                } catch(Exception wdmEx) {
                    System.out.println("WebDriverManager setup failed, trying direct initialization: " + wdmEx.getMessage());
                }
                FirefoxOptions options = new FirefoxOptions();
                driver = new FirefoxDriver(options);
                System.out.println("Firefox driver initialized");
            } else if(browser.equals("edge")){
                System.out.println("Setting up EdgeDriver...");
                try {
                    // CRITICAL: Disable SeleniumManager completely to avoid DNS issues
                    System.setProperty("webdriver.manager.edgedriver.disable", "true");
                    System.setProperty("webdriver.manager.web.download.disable", "true");
                    System.setProperty("wdm.manager.path", "/dev/null"); // Prevent any driver downloads
                    
                    // Ensure EdgeDriver is pre-downloaded and set the path explicitly
                    System.setProperty("webdriver.edge.driver", "C:\\Edgewebdriver\\msedgedriver.exe");
                    EdgeOptions options = new EdgeOptions();
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    driver = new EdgeDriver(options);
                    System.out.println("Edge driver initialized successfully");
                } catch(Exception edgeEx) {
                    System.out.println("⚠️ Edge initialization failed: " + edgeEx.getClass().getSimpleName());
                    String errorMsg = edgeEx.getMessage();
                    if(errorMsg != null && (errorMsg.contains("UnknownHostException") || 
                                            errorMsg.contains("dns error") || 
                                            errorMsg.contains("No such host") ||
                                            errorMsg.contains("network") ||
                                            errorMsg.contains("msedgedriver"))) {
                        System.out.println("Skipping Edge test due to network/DNS issues");
                        System.out.println("To bypass: Either connect to internet or download EdgeDriver manually");
                        throw new RuntimeException("Edge test skipped - network connectivity required", edgeEx);
                    } else {
                        throw edgeEx;
                    }
                }
            } else {
                System.out.println("ERROR: Unsupported browser: " + browser);
                throw new IllegalArgumentException("Browser not supported: " + browser);
            }

            if(driver != null) {
                driver.manage().window().maximize();
                System.out.println("=== Driver initialized successfully ===");
            }
        } catch(Exception e) {
            System.out.println("=== ERROR in setup: " + e.getMessage() + " ===");
            System.out.println("Exception type: " + e.getClass().getName());
            // Check if it's Edge DNS error - allow graceful skip
            if(browser.equals("edge") && e.getMessage() != null && 
               (e.getMessage().contains("UnknownHostException") || 
                e.getMessage().contains("dns error"))) {
                System.out.println("Skipping Edge test due to DNS resolution error");
                throw new RuntimeException("Skipped Edge test - DNS error", e);
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver for browser: " + browser, e);
        }
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("=== Teardown Method Started ===");
        if(driver != null) {
            try {
                driver.quit();
                System.out.println("Driver quit successfully");
            } catch(Exception e) {
                System.out.println("Error closing driver: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("=== Teardown Complete ===");
    }
}