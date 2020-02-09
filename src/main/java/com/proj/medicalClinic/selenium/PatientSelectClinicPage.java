package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class PatientSelectClinicPage {

    private WebDriver driver;

    public PatientSelectClinicPage(WebDriver driver){
        this.driver = driver;
    }


    @FindBy(id = "select_clinic_1_button")
    private WebElement selectClinicButton;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div/div/div/div/div[1]/div[2]/div/div[3]/input")
    private WebElement inputScore;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div/div/div/div/div[1]/div[2]/div/div[4]/input")
    private WebElement inputPrice;

    public void ensureSelectButtonClinicIsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(selectClinicButton));
    }

    public void setInputScore(String input){
        WebElement e2 = getInputScore();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setInputPrice(String input){
        WebElement e2 = getInputPrice();
        e2.clear();
        e2.sendKeys(input);
    }
}
