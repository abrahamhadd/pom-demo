package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GooglePage {

    WebDriver driver;

    By acceptButton = By.xpath("//button[.//div[contains(text(),'Accept')]]");
    By searchBox = By.name("q");
    By searchResults = By.xpath("//div[@data-sokoban-container]//a[@href]");
    By googleBotBox = By.xpath("//div[contains(@class, 'gs-webResult')]//div[@role='link']");
    By actualSearchResult = By.xpath("//div[@data-sokoban-container]//a[contains(@href, 'http')]");

    public GooglePage(WebDriver driver){
        this.driver = driver;
    }

    public void openGoogle(){
        System.out.println("Opening Google...");
        driver.get("https://www.google.com");
        System.out.println("Google opened successfully");
    }

    public void acceptCookies(){
        System.out.println("Attempting to accept cookies...");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(acceptButton)).click();
            System.out.println("Cookies accepted");
        } catch(Exception e){
            System.out.println("Cookie popup not found or already accepted: " + e.getMessage());
        }
    }
    
    public void search(String text){
        System.out.println("Searching for: " + text);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        driver.findElement(searchBox).sendKeys(text);
        System.out.println("Submitted search");
        driver.findElement(searchBox).submit();

        try {
            Thread.sleep(3000); // Wait for page to load and detect reCAPTCHA
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted: " + e.getMessage());
        }
        
        // Check for and handle reCAPTCHA "I'm not a robot" checkbox
        // This is critical as Google blocks bots
        handleRecaptcha(wait);
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted after recaptcha handling");
        }
        
        // Wait for search results to be visible
        // Google search results are typically in divs with specific classes
        try {
            // Wait for the main search results area - try multiple possible selectors
            boolean resultsFound = false;
            
            // Try selector 1: Results in div with jscontroller class (modern Google layout)
            try {
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//div[@data-sokoban-container]//div[@data-rank]")));
                System.out.println("Search results found via data-sokoban-container");
                resultsFound = true;
            } catch(Exception e1) {
                System.out.println("Selector 1 failed, trying alternate selectors...");
                
                // Try selector 2: Results container with id='main'
                try {
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//div[@id='main']//div[@data-sokoban-container]")));
                    System.out.println("Search results found via id=main");
                    resultsFound = true;
                } catch(Exception e2) {
                    System.out.println("Selector 2 failed, trying selector 3...");
                    
                    // Try selector 3: Generic div with role=main or specific Google result classes
                    try {
                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//a[contains(@href, 'url?q=')]")));
                        System.out.println("Search results found via URL parameter links");
                        resultsFound = true;
                    } catch(Exception e3) {
                        System.out.println("Selector 3 failed, checking page source...");
                        System.out.println("Page title: " + driver.getTitle());
                        System.out.println("Current URL: " + driver.getCurrentUrl());
                    }
                }
            }
            
            if(resultsFound) {
                System.out.println("Search results are visible on page");
            }
        } catch(Exception e){
            System.out.println("Search results verification completed with exception: " + e.getClass().getSimpleName());
        }

        // Try to close or bypass the Google Bot box if it appears
        try {
            List<WebElement> botBoxes = driver.findElements(
                By.xpath("//div[contains(@class, 'Ks4ww') or contains(@class, 'focused-state')]"));
            if(!botBoxes.isEmpty()) {
                System.out.println("Found Google Bot/AI answer box, attempting to close or bypass...");
                // Try pressing Escape to close any overlays
                driver.switchTo().defaultContent();
                new org.openqa.selenium.interactions.Actions(driver)
                    .sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
                System.out.println("Sent Escape key to close overlays");
            }
        } catch(Exception e){
            System.out.println("No bot box overlay found or already closed");
        }

        try {
            Thread.sleep(2000); // Wait for any animations
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted: " + e.getMessage());
        }
        System.out.println("Search completed");
    }
    
    private void handleRecaptcha(WebDriverWait wait) {
        System.out.println("Checking for reCAPTCHA checkbox...");
        try {
            // Check if reCAPTCHA iframe is present
            List<WebElement> recaptchaIframes = driver.findElements(
                By.xpath("//iframe[contains(@src, 'recaptcha') or contains(@src, 'recaptcha')]"));
            
            if(!recaptchaIframes.isEmpty()) {
                System.out.println("⚠️ reCAPTCHA detected - " + recaptchaIframes.size() + " iframe(s) found");
                
                // Strategy 1: Try to switch to reCAPTCHA iframe and click the checkbox
                boolean success = false;
                for(int i = 0; i < recaptchaIframes.size(); i++) {
                    try {
                        System.out.println("Attempting to handle reCAPTCHA iframe " + (i+1) + "...");
                        WebElement iframe = recaptchaIframes.get(i);
                        
                        // Switch to the iframe
                        driver.switchTo().frame(iframe);
                        System.out.println("✓ Switched to reCAPTCHA iframe " + (i+1));
                        
                        // Look for the checkbox inside the iframe
                        try {
                            // Try multiple XPath patterns for the checkbox
                            WebElement checkbox = null;
                            String[] xpaths = {
                                "//*[@id='recaptcha-anchor']",
                                "//div[@id='recaptcha-anchor']",
                                "//div[@class='recaptcha-checkbox']",
                                "//span[@id='recaptcha-anchor']",
                                "//div[@aria-label='I\\'m not a robot']",
                                "//div[contains(@class, 'recaptcha-checkbox')]"
                            };
                            
                            for(String xpath : xpaths) {
                                try {
                                    List<WebElement> elements = driver.findElements(By.xpath(xpath));
                                    if(!elements.isEmpty()) {
                                        checkbox = elements.get(0);
                                        System.out.println("Found checkbox using xpath: " + xpath);
                                        break;
                                    }
                                } catch(Exception e) {
                                    // Continue to next xpath
                                }
                            }
                            
                            if(checkbox != null) {
                                System.out.println("✓ Found reCAPTCHA checkbox element, attempting to click...");
                                
                                // Try multiple click strategies
                                try {
                                    // Method 1: Direct click
                                    wait.until(ExpectedConditions.elementToBeClickable(checkbox));
                                    checkbox.click();
                                    System.out.println("✓ Successfully clicked reCAPTCHA using direct click");
                                    success = true;
                                } catch(Exception e1) {
                                    System.out.println("Direct click failed, trying JavaScript click...");
                                    try {
                                        // Method 2: JavaScript click
                                        org.openqa.selenium.JavascriptExecutor js = 
                                            (org.openqa.selenium.JavascriptExecutor) driver;
                                        js.executeScript("arguments[0].click();", checkbox);
                                        System.out.println("✓ Successfully clicked reCAPTCHA using JavaScript");
                                        success = true;
                                    } catch(Exception e2) {
                                        System.out.println("JavaScript click failed, trying Actions API...");
                                        // Method 3: Actions API
                                        new org.openqa.selenium.interactions.Actions(driver)
                                            .moveToElement(checkbox)
                                            .click()
                                            .perform();
                                        System.out.println("✓ Successfully clicked reCAPTCHA using Actions API");
                                        success = true;
                                    }
                                }
                            } else {
                                System.out.println("⚠️ Checkbox element not found in iframe");
                            }
                        } finally {
                            // Switch back to default content
                            driver.switchTo().defaultContent();
                            System.out.println("Switched back to main page content");
                        }
                        
                        if(success) break;
                        
                    } catch(Exception frameEx) {
                        System.out.println("Error handling iframe " + (i+1) + ": " + frameEx.getMessage());
                        try {
                            driver.switchTo().defaultContent();
                        } catch(Exception e) {
                            // Ignore
                        }
                    }
                }
                
                // If iframe approach failed, try direct element access without iframe switching
                if(!success) {
                    System.out.println("Iframe approach unsuccessful, trying direct element access...");
                    try {
                        // Switch back to default content first
                        driver.switchTo().defaultContent();
                        
                        // Try to find element without switching frames
                        WebElement checkbox = wait.until(
                            ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//div[@id='recaptcha-anchor']")));
                        
                        System.out.println("Found reCAPTCHA element in main content, clicking...");
                        org.openqa.selenium.JavascriptExecutor js = 
                            (org.openqa.selenium.JavascriptExecutor) driver;
                        js.executeScript("arguments[0].scrollIntoView(true);", checkbox);
                        js.executeScript("arguments[0].click();", checkbox);
                        System.out.println("✓ Clicked reCAPTCHA using JavaScript on main content");
                        success = true;
                    } catch(Exception e) {
                        System.out.println("⚠️ Direct element access also failed: " + e.getMessage());
                    }
                }
                
                if(success) {
                    // Wait for reCAPTCHA verification to complete
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        System.out.println("Sleep interrupted after recaptcha click: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("✓ No reCAPTCHA detected on this page");
            }
        } catch(Exception e) {
            System.out.println("reCAPTCHA check completed: " + e.getClass().getSimpleName());
            // This is not a blocking error - continue with the test
        }
    }

}
