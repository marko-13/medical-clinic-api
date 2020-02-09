package com.proj.medicalClinic.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class LoginTest {

    private WebDriver browser;
    private WelcomePage welcomePage;

    private static final String baseUrl = "http://localhost:3000";

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        browser = new ChromeDriver();

        browser.manage().window().maximize();
        browser.navigate().to(baseUrl);

        welcomePage = PageFactory.initElements(browser, WelcomePage.class);

    }


    @Test
    public void testLoginValidData() {
       welcomePage.ensureLoginButtonIsClickable();

       welcomePage.setUsernameInput("Miljana@mailinator.com");
       welcomePage.setPasswordInput("miljana");
       welcomePage.getLoginButton().click();

       assertTrue(welcomePage.getLoginButton().isEnabled());

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage"));
        assertEquals(baseUrl + "/homepage", browser.getCurrentUrl());



    }


    @AfterMethod
    public void shutDown(){
        browser.close();
    }
}
