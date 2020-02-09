package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class PatientAllClinicsPage {

    private WebDriver driver;

    public PatientAllClinicsPage(WebDriver driver){
        this.driver = driver;
    }

    @FindBy(className = "rbt-input-main")
    private WebElement serviceInput;

    @FindBy(xpath = "//button[text()='Submit']")
    private WebElement submitButton;

    @FindBy(className = "dropdown-item")
    private WebElement selectedItem;

    @FindBy(id = "inspect_clinics_datepicker")
    private WebElement dateInput;

    @FindBy(id = "fast_exam_button_1")
    private WebElement fastExamButton1;

    @FindBy(id = "fast_exam_button_2")
    private WebElement fastExamButton2;

    @FindBy(xpath = "//*[@id='inspect_clinics_allClinics_table']/div/div[1]/div[2]/div/div[1]/input")
    private WebElement serachNameInput;

    @FindBy(xpath = "//*[@id='inspect_clinics_allClinics_table']/div/div[1]/div[2]/div/div[2]/input")
    private WebElement serachAdressInput;

    @FindBy(id = "input_clinic_score")
    private WebElement inputScore;


    public void setServiceInput(String input){
        WebElement e2 = getServiceInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setInputScore(String input){
        WebElement e2 = getInputScore();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setDateInput(String input){
        WebElement e2 = getDateInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setSerachNameInput(String input){
        WebElement e2 = getSerachNameInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setSerachAdressInput(String input){
        WebElement e2 = getSerachAdressInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void ensureServiceInputIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(serviceInput));
    }

    public void ensureInputDateIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(dateInput));
    }

    public void ensureSubmitButtonIsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public void ensureFastExamButton1IsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(fastExamButton1));
    }

    public void ensureFastExamButton2IsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(fastExamButton2));
    }



    public void isAlertPresent(){
        (new WebDriverWait(driver, 5)).until(ExpectedConditions.alertIsPresent());
    }

}
