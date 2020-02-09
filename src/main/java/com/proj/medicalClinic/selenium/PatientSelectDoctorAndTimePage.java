package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class PatientSelectDoctorAndTimePage {

    private WebDriver driver;

    public PatientSelectDoctorAndTimePage(WebDriver driver){
        this.driver = driver;
    }

    @FindBy(id = "inspect_clinics_reserve_button")
    private WebElement reserveSubmitButton;

    @FindBy(id = "inspect_clinics_select_time")
    private WebElement selectTimeInput;

    @FindBy(className = "rbt-input-main")
    private WebElement selectDoctorInput;

    @FindBy(className = "dropdown-item")
    private WebElement selectedItem;

    public void ensureReserveSubmitButtonIsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(reserveSubmitButton));
    }

    public void ensureInputTimeIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(selectTimeInput));
    }

    public void setSelectTimeInput(String input){
        WebElement e2 = getSelectTimeInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setSelectDoctorInput(String input){
        WebElement e2 = getSelectDoctorInput();
        e2.clear();
        e2.sendKeys(input);
    }


    public void ensureSelectDoctorInputIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(selectDoctorInput));
    }

}
