package com.proj.medicalClinic.selenium;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Getter
public class AdminClinicRoomsPage {

    private WebDriver driver;

    public AdminClinicRoomsPage(WebDriver driver){
        this.driver = driver;
    }

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[1]/div[2]")
    private WebElement roomsTable;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[1]/div[2]/div[1]/div[3]/div/div[1]/input")
    private WebElement searchRoomName;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[1]/div[2]/div[1]/div[3]/div/div[2]/input")
    private WebElement serachRoomNumber;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[1]/div[1]/div/div/div/div/div[1]/div/input")
    private WebElement serachNewDateInput;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[1]/div[1]/div/div/div/div/button")
    private WebElement serachNewDateButton;

    @FindBy(xpath = "//*[@id='user_homepage_container']/div[2]/div[2]/div[1]/div[2]/div[1]/div[4]/div[1]/div/div[3]/center/button")
    private WebElement showCalendarButtonAfterSearch;

    @FindBy(id = "show_room_calendar_5")
    private WebElement showCalendarButton2;

    @FindBy(id = "show_room_calendar_4")
    private WebElement showCalendarButton1;

    public void ensureShowCalendarButton1AfterSearchIsClickable(){
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(showCalendarButton1));
    }

    public void ensureShowCalendarButton2AfterSearchIsClickable(){
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(showCalendarButton2));
    }

    public void ensureShowCalendarButtonAfterSearchIsClickable(){
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(showCalendarButtonAfterSearch));
    }


    public void ensureSearchNewDateButtonIsClickable(){
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(serachNewDateButton));
    }

    public void setSerachNewDateInput(String input){
        WebElement e2 = getSerachNewDateInput();
        e2.clear();
        e2.sendKeys(input);
    }

    public void ensureIsDisplayed(){
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(roomsTable));
    }

    public void setSerachRoomNumber(String input){
        WebElement e2 = getSerachRoomNumber();
        e2.clear();
        e2.sendKeys(input);
    }

    public void setSearchRoomName(String input){
        WebElement e2 = getSearchRoomName();
        e2.clear();
        e2.sendKeys(input);
    }

}
