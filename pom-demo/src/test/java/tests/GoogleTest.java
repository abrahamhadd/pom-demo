package tests;

import org.testng.annotations.Test;
import org.testng.Assert;

import base.BaseTest;
import pages.GooglePage;

public class GoogleTest extends BaseTest {

    @Test
    public void searchTest(){
        System.out.println("*** Starting Google Search Test ***");
        
        GooglePage google = new GooglePage(driver);

        google.openGoogle();

        google.acceptCookies();

        google.search("Selenium tutorial");
        
        // Verify the page title contains "Selenium"
        String pageTitle = driver.getTitle();
        System.out.println("Page title: " + pageTitle);
        Assert.assertTrue(pageTitle.toLowerCase().contains("selenium"), 
            "Page title should contain 'Selenium'");
        
        System.out.println("*** Test Completed Successfully ***");
    }

}