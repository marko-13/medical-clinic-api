package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class AdminClinicRoomCalendarPage {

    private WebDriver driver;

    public AdminClinicRoomCalendarPage(WebDriver driver){
        this.driver = driver;
    }

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[2]/div")
    private WebElement roomCalendar;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[2]/div/div[3]/button")
    private WebElement scheduleRoomButton;

    @FindBy(xpath = "//*[@id='select-4']")
    private WebElement selectDoctorCheckBox;


    public void ensureScheduleRoomButtonIsClickable(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(scheduleRoomButton));
    }


    public void ensureIsDisplayed(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(roomCalendar));
    }




}
