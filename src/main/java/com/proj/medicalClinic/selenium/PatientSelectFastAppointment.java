package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class PatientSelectFastAppointment {

    private WebDriver driver;

    public PatientSelectFastAppointment(WebDriver driver){
        this.driver = driver;
    }

    @FindBy(id = "schedule_fast_appointment_button_2")
    private WebElement scheduleFastAppButton;

    public void ensureSelectButtonClinicIsClickable() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(scheduleFastAppButton));
    }


}
