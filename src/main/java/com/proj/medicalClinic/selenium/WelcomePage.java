package com.proj.medicalClinic.selenium;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class WelcomePage {

    private WebDriver webDriver;

    public WelcomePage(WebDriver driver){
        this.webDriver = driver;
    }

    @FindBy(id = "login_button")
    private WebElement loginButton;

    @FindBy(id = "login_form")
    private WebElement loginForm;

    @FindBy(id = "login_input_username")
    private WebElement usernameInput;

    @FindBy(id = "login_input_password")
    private WebElement passwordInput;

    public void setUsernameInput(String input){
        WebElement e2 = getUsernameInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setPasswordInput(String input){
        WebElement e2 = getPasswordInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void ensureIsDisplayed(){
        (new WebDriverWait(webDriver, 5)).until(ExpectedConditions.visibilityOf(loginForm));
    }

    public void ensureLoginButtonIsClickable() {
        (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.elementToBeClickable(loginButton));
    }


}
