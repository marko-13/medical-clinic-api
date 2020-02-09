package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class AdminClinicNewDoctorModalPage {

    private WebDriver driver;

    public AdminClinicNewDoctorModalPage(WebDriver driver){
        this.driver = driver;
    }

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[2]/div/div[6]")
    private WebElement modalForm;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[2]/div/div[6]/div/div[2]/div[1]/input")
    private WebElement choseNewDoctorInput;

    @FindBy(className = "dropdown-item")
    private WebElement selectedItem;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[2]/div/div[6]/div/div[3]/button[2]")
    private WebElement addDoctorButton;

    public void ensureAddDoctorButtonIsClickable(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(addDoctorButton));
    }


    public void ensureIsDisplayed(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(modalForm));
    }

    public void setChoseNewDoctorInput(String input){
        WebElement e2 = getChoseNewDoctorInput();
        e2.clear();
        e2.sendKeys(input);
    }

}
