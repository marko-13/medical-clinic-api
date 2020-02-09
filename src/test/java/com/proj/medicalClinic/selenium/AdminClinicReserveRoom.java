package com.proj.medicalClinic.selenium;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdminClinicReserveRoom {

    private WebDriver browser;
    private WelcomePage welcomePage;
    private HomePage homePage;
    private AdminClinicRequestsPage adminClinicRequestsPage;
    private AdminClinicRoomsPage adminClinicRoomsPage;
    private AdminClinicRoomCalendarPage adminClinicRoomCalendarPage;
    private AdminClinicNewDoctorModalPage adminClinicNewDoctorModalPage;

    private static final String baseUrl = "http://localhost:3000";

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        browser = new ChromeDriver();

        browser.manage().window().maximize();
        browser.navigate().to(baseUrl);

        welcomePage = PageFactory.initElements(browser, WelcomePage.class);
        homePage = PageFactory.initElements(browser, HomePage.class);
        adminClinicRequestsPage = PageFactory.initElements(browser, AdminClinicRequestsPage.class);
        adminClinicRoomsPage = PageFactory.initElements(browser, AdminClinicRoomsPage.class);
        adminClinicRoomCalendarPage = PageFactory.initElements(browser, AdminClinicRoomCalendarPage.class);
        adminClinicNewDoctorModalPage = PageFactory.initElements(browser, AdminClinicNewDoctorModalPage.class);


    }

    @Test
    public void addRoomToOperationBySelectingNewDatePass() {

        this.loginValid();
        this.requestsShown();

        adminClinicRequestsPage.setDateInputSearch("2020-05-10 03");

        adminClinicRequestsPage.ensureSerachRoomButton2IsClickable();
        assertTrue(adminClinicRequestsPage.getSerachRoomButton2().isDisplayed());
        adminClinicRequestsPage.getSerachRoomButton2().click();

        adminClinicRoomsPage.ensureIsDisplayed();
        assertTrue(adminClinicRoomsPage.getRoomsTable().isDisplayed());

        adminClinicRoomsPage.setSerachNewDateInput("2020-02-23, 09:30 AM");
        adminClinicRoomsPage.getSerachNewDateInput().sendKeys(Keys.ENTER);

        adminClinicRoomsPage.ensureSearchNewDateButtonIsClickable();
        assertTrue(adminClinicRoomsPage.getSerachNewDateButton().isDisplayed());
        adminClinicRoomsPage.getSerachNewDateButton().click();

        adminClinicRoomsPage.ensureShowCalendarButton2AfterSearchIsClickable();
        assertTrue(adminClinicRoomsPage.getShowCalendarButton2().isDisplayed());
        adminClinicRoomsPage.getShowCalendarButton2().click();

        adminClinicRoomCalendarPage.ensureIsDisplayed();
        assertTrue(adminClinicRoomCalendarPage.getRoomCalendar().isDisplayed());

        adminClinicRoomCalendarPage.getSelectDoctorCheckBox().click();

        adminClinicRoomCalendarPage.ensureScheduleRoomButtonIsClickable();
        assertTrue(adminClinicRoomCalendarPage.getScheduleRoomButton().isDisplayed());
        adminClinicRoomCalendarPage.getScheduleRoomButton().click();

        WebDriverWait wait = new WebDriverWait(browser, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        assertEquals(browser.switchTo().alert().getText(), "Appointment has been scheduled!");
        browser.switchTo().alert().accept();

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage/admin-clinic"));
        assertEquals(baseUrl + "/homepage/admin-clinic", browser.getCurrentUrl());
    }

    @Test
    public void addRoomBySelectingNewDateAndNewDoctorPass() {

        this.loginValid();
        this.requestsShown();

        adminClinicRequestsPage.setDateInputSearch("2020-05-10 04:30");

        adminClinicRequestsPage.ensureSerachRoomButtonIsClickable();
        assertTrue(adminClinicRequestsPage.getSerachRoomButton().isDisplayed());
        adminClinicRequestsPage.getSerachRoomButton().click();

        adminClinicRoomsPage.ensureIsDisplayed();
        assertTrue(adminClinicRoomsPage.getRoomsTable().isDisplayed());

        adminClinicRoomsPage.setSerachNewDateInput("2020-05-15, 4:30 PM");
        adminClinicRoomsPage.getSerachNewDateInput().sendKeys(Keys.ENTER);

        adminClinicRoomsPage.ensureSearchNewDateButtonIsClickable();
        assertTrue(adminClinicRoomsPage.getSerachNewDateButton().isDisplayed());
        adminClinicRoomsPage.getSerachNewDateButton().click();

        adminClinicRoomsPage.ensureShowCalendarButtonAfterSearchIsClickable();
        assertTrue(adminClinicRoomsPage.getShowCalendarButtonAfterSearch().isDisplayed());
        adminClinicRoomsPage.getShowCalendarButtonAfterSearch().click();

        adminClinicRoomCalendarPage.ensureIsDisplayed();
        assertTrue(adminClinicRoomCalendarPage.getRoomCalendar().isDisplayed());

        adminClinicRoomCalendarPage.ensureScheduleRoomButtonIsClickable();
        assertTrue(adminClinicRoomCalendarPage.getScheduleRoomButton().isDisplayed());
        adminClinicRoomCalendarPage.getScheduleRoomButton().click();

       adminClinicNewDoctorModalPage.ensureIsDisplayed();
       assertTrue(adminClinicNewDoctorModalPage.getModalForm().isDisplayed());

       adminClinicNewDoctorModalPage.setChoseNewDoctorInput("Dusko Jovanovic");
       adminClinicNewDoctorModalPage.getSelectedItem().click();

       adminClinicNewDoctorModalPage.ensureAddDoctorButtonIsClickable();
       assertTrue(adminClinicNewDoctorModalPage.getAddDoctorButton().isDisplayed());
       adminClinicNewDoctorModalPage.getAddDoctorButton().click();

        WebDriverWait wait = new WebDriverWait(browser, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        assertEquals(browser.switchTo().alert().getText(), "Doctor Dusko Jovanovic has been added sucessfully!");
        browser.switchTo().alert().accept();

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage/admin-clinic"));
        assertEquals(baseUrl + "/homepage/admin-clinic", browser.getCurrentUrl());

    }



    @Test
    public void adRoomToRequestPass() {
        this.loginValid();
        this.requestsShown();

        adminClinicRequestsPage.setPatientInputSearch("jeltako");
        adminClinicRequestsPage.setDateInputSearch("02-1");
        adminClinicRequestsPage.setServiceInputSerach("glave");

        adminClinicRequestsPage.ensureSerachRoomButton3IsClickable();
        assertTrue(adminClinicRequestsPage.getSerachRoomButton3().isDisplayed());
        adminClinicRequestsPage.getSerachRoomButton3().click();


        adminClinicRoomsPage.ensureIsDisplayed();
        assertTrue(adminClinicRoomsPage.getRoomsTable().isDisplayed());

        adminClinicRoomsPage.setSearchRoomName("Ordinacija");
        adminClinicRoomsPage.setSerachRoomNumber("15");

        adminClinicRoomsPage.ensureShowCalendarButton1AfterSearchIsClickable();
        assertTrue(adminClinicRoomsPage.getShowCalendarButton1().isDisplayed());
        adminClinicRoomsPage.getShowCalendarButton1().click();

        adminClinicRoomCalendarPage.ensureIsDisplayed();
        assertTrue(adminClinicRoomCalendarPage.getRoomCalendar().isDisplayed());

        adminClinicRoomCalendarPage.ensureScheduleRoomButtonIsClickable();
        assertTrue(adminClinicRoomCalendarPage.getScheduleRoomButton().isDisplayed());
        adminClinicRoomCalendarPage.getScheduleRoomButton().click();

        WebDriverWait wait = new WebDriverWait(browser, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        assertEquals(browser.switchTo().alert().getText(), "Appointment has been scheduled!");
        browser.switchTo().alert().accept();

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage/admin-clinic"));
        assertEquals(baseUrl + "/homepage/admin-clinic", browser.getCurrentUrl());


    }

    private void loginValid(){
        welcomePage.ensureLoginButtonIsClickable();

        welcomePage.setUsernameInput("Anastasija@mailinator.com");
        welcomePage.setPasswordInput("anastasija");
        welcomePage.getLoginButton().click();

        assertTrue(welcomePage.getLoginButton().isEnabled());

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage"));
        assertEquals(baseUrl + "/homepage", browser.getCurrentUrl());
    }

    public void requestsShown() {

        homePage.ensureIsDisplayed();
        assertTrue(homePage.getUserHomepageContainer().isDisplayed());

        homePage.ensureRequestButtonIsClickable();
        assertTrue(homePage.getShowRequestsButton().isDisplayed());
        homePage.getShowRequestsButton().click();
        assertTrue(homePage.getShowRequestsButton().isEnabled());

        adminClinicRequestsPage.ensureRequestTableIsDisplayed();
        assertTrue(adminClinicRequestsPage.getRequestTable().isDisplayed());

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage/admin-clinic/requests"));
        assertEquals(baseUrl + "/homepage/admin-clinic/requests", browser.getCurrentUrl());

    }

    @AfterMethod
    public void shutDown(){
        browser.close();
    }

}
