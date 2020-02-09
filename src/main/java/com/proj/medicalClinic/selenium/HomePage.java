package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class HomePage {

    private WebDriver driver;

    public HomePage(WebDriver driver){
        this.driver = driver;
    }

    @FindBy(id = "user_homepage_container")
    private WebElement userHomepageContainer;

    @FindBy(id = "inspect_clinics_allClinics_table")
    private WebElement allClinicsTable;

    @FindBy(id = "inspect_clinics_appointment_form")
    private WebElement appointmentDateAndTypeForm;

    @FindBy(xpath = "//button[text()='Inspect clinics']")
    private WebElement inspectClinicsButton;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[1]/div[2]/div/div/div[6]/div/div/a/button")
    private WebElement showRequestsButton;


    public void ensureRequestButtonIsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(showRequestsButton));
    }

    public void ensureInspectClinicsButtonIsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(inspectClinicsButton));
    }

    public void ensureIsDisplayed(){
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(userHomepageContainer));
    }

    public void ensureClinicsAreDisplayed(){
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(allClinicsTable));
    }

    public void ensureFromAppointmentFormIsDisplayed(){
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(appointmentDateAndTypeForm));
    }
}
