package com.proj.medicalClinic.selenium;


import lombok.Getter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@Getter
public class PatientScheduleAppointmentTest {

    private WebDriver browser;
    private PatientAllClinicsPage inspectClinicsPage;
    private WelcomePage welcomePage;
    private HomePage homePage;
    private PatientSelectClinicPage patientSelectClinicPage;
    private PatientSelectDoctorAndTimePage patientSelectDoctorAndTimePage;
    private PatientSelectFastAppointment patientSelectFastAppointment;

    private static final String baseUrl = "http://localhost:3000";

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        browser = new ChromeDriver();

        browser.manage().window().maximize();
        browser.navigate().to(baseUrl);

        welcomePage = PageFactory.initElements(browser, WelcomePage.class);
        inspectClinicsPage = PageFactory.initElements(browser, PatientAllClinicsPage.class);
        homePage = PageFactory.initElements(browser, HomePage.class);
        patientSelectClinicPage = PageFactory.initElements(browser, PatientSelectClinicPage.class);
        patientSelectDoctorAndTimePage = PageFactory.initElements(browser, PatientSelectDoctorAndTimePage.class);
        patientSelectFastAppointment = PageFactory.initElements(browser, PatientSelectFastAppointment.class);

    }


    @Test
    public void noTimeSelectedForSelectedClinic(){
        this.loginValid();

        this.clinicsShown();

        inspectClinicsPage.ensureServiceInputIsDisplayed();
        assertTrue(inspectClinicsPage.getServiceInput().isDisplayed());

        inspectClinicsPage.setServiceInput("Pregled glave");
        inspectClinicsPage.getSelectedItem().click();

        inspectClinicsPage.ensureInputDateIsDisplayed();
        assertTrue(inspectClinicsPage.getDateInput().isDisplayed());

        inspectClinicsPage.setDateInput("26-Feb-2020");
        inspectClinicsPage.getDateInput().sendKeys(Keys.ENTER);

        inspectClinicsPage.ensureSubmitButtonIsClickable();
        assertTrue(inspectClinicsPage.getSubmitButton().isDisplayed());
        inspectClinicsPage.getSubmitButton().click();

        patientSelectClinicPage.ensureSelectButtonClinicIsClickable();
        assertTrue(patientSelectClinicPage.getSelectClinicButton().isDisplayed());
        patientSelectClinicPage.getSelectClinicButton().click();

        patientSelectDoctorAndTimePage.ensureReserveSubmitButtonIsClickable();
        assertTrue(getPatientSelectDoctorAndTimePage().getReserveSubmitButton().isDisplayed());
        patientSelectDoctorAndTimePage.getReserveSubmitButton().click();

        assertEquals(browser.switchTo().alert().getText(), "Time must be selected");
        browser.switchTo().alert().dismiss();

    }

    @Test
    public void noFastExamsAvailableInClinic(){

        this.loginValid();

        this.clinicsShown();

        inspectClinicsPage.ensureFastExamButton2IsClickable();
        assertTrue(inspectClinicsPage.getFastExamButton2().isDisplayed());
        inspectClinicsPage.getFastExamButton2().click();

        WebDriverWait wait = new WebDriverWait(browser, 10);
        wait.until(ExpectedConditions.alertIsPresent());

        assertEquals(browser.switchTo().alert().getText(), "There are no available fast exams for selected clinic: Zakuco");
        browser.switchTo().alert().dismiss();

    }

    @Test
    public void noDateSelected() {

        this.loginValid();

        this.clinicsShown();

        inspectClinicsPage.ensureServiceInputIsDisplayed();
        assertTrue(inspectClinicsPage.getServiceInput().isDisplayed());

        inspectClinicsPage.setServiceInput("Pregled glave");
        inspectClinicsPage.getSelectedItem().click();

        inspectClinicsPage.ensureSubmitButtonIsClickable();
        assertTrue((inspectClinicsPage.getSubmitButton().isDisplayed()));
        inspectClinicsPage.getSubmitButton().click();

        assertEquals(browser.switchTo().alert().getText(), "Date must be selected");
        browser.switchTo().alert().dismiss();

    }

    @Test
    public void noServiceSelected(){
        this.loginValid();

        this.clinicsShown();

        inspectClinicsPage.ensureInputDateIsDisplayed();
        assertTrue(inspectClinicsPage.getDateInput().isDisplayed());

        inspectClinicsPage.setDateInput("26-Feb-2020");
        inspectClinicsPage.getDateInput().sendKeys(Keys.ENTER);

        inspectClinicsPage.ensureSubmitButtonIsClickable();
        assertTrue((inspectClinicsPage.getSubmitButton().isDisplayed()));
        inspectClinicsPage.getSubmitButton().click();

        assertEquals(browser.switchTo().alert().getText(), "Service must be selected");
        browser.switchTo().alert().dismiss();


    }


    @Test
    public void shceduleFastAppointmentPass() {
        this.loginValid();

        this.clinicsShown();

        inspectClinicsPage.ensureFastExamButton1IsClickable();
        assertTrue(inspectClinicsPage.getFastExamButton1().isDisplayed());
        inspectClinicsPage.getFastExamButton1().click();

        patientSelectFastAppointment.ensureSelectButtonClinicIsClickable();
        assertTrue(patientSelectFastAppointment.getScheduleFastAppButton().isDisplayed());
        patientSelectFastAppointment.getScheduleFastAppButton().click();

        WebDriverWait wait = new WebDriverWait(browser, 10);
        wait.until(ExpectedConditions.alertIsPresent());

        assertEquals(browser.switchTo().alert().getText(), "Appointment scheduled");
        browser.switchTo().alert().accept();

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage"));
        assertEquals(baseUrl + "/homepage", browser.getCurrentUrl());

    }

    @Test
    public void scheduleAppointmentPass(){
        this.loginValid();

        this.clinicsShown();

        inspectClinicsPage.ensureServiceInputIsDisplayed();
        assertTrue(inspectClinicsPage.getServiceInput().isDisplayed());

        inspectClinicsPage.setSerachNameInput("Sve");
        inspectClinicsPage.setSerachAdressInput("Bul");
        inspectClinicsPage.setInputScore("3");

        inspectClinicsPage.setServiceInput("Pregled glave");
        inspectClinicsPage.getSelectedItem().click();

        inspectClinicsPage.ensureInputDateIsDisplayed();
        assertTrue(inspectClinicsPage.getDateInput().isDisplayed());

        inspectClinicsPage.setDateInput("26-Feb-2020");
        inspectClinicsPage.getDateInput().sendKeys(Keys.ENTER);

        inspectClinicsPage.ensureSubmitButtonIsClickable();
        assertTrue(inspectClinicsPage.getSubmitButton().isDisplayed());
        inspectClinicsPage.getSubmitButton().click();

        patientSelectClinicPage.ensureSelectButtonClinicIsClickable();

        patientSelectClinicPage.setInputScore("3.5");
        patientSelectClinicPage.setInputPrice("25");

        patientSelectClinicPage.ensureSelectButtonClinicIsClickable();
        assertTrue(patientSelectClinicPage.getSelectClinicButton().isDisplayed());
        patientSelectClinicPage.getSelectClinicButton().click();

        patientSelectDoctorAndTimePage.ensureInputTimeIsDisplayed();
        assertTrue(patientSelectDoctorAndTimePage.getSelectTimeInput().isDisplayed());
        patientSelectDoctorAndTimePage.setSelectTimeInput("11:30 AM");
        patientSelectDoctorAndTimePage.getSelectTimeInput().sendKeys(Keys.ENTER);

        patientSelectDoctorAndTimePage.ensureSelectDoctorInputIsDisplayed();
        assertTrue(patientSelectDoctorAndTimePage.getSelectDoctorInput().isDisplayed());
        patientSelectDoctorAndTimePage.setSelectDoctorInput("Sara Loncar");
        patientSelectDoctorAndTimePage.getSelectedItem().click();

        patientSelectDoctorAndTimePage.ensureReserveSubmitButtonIsClickable();
        assertTrue(getPatientSelectDoctorAndTimePage().getReserveSubmitButton().isDisplayed());
        patientSelectDoctorAndTimePage.getReserveSubmitButton().click();

        WebDriverWait wait = new WebDriverWait(browser, 10);
        wait.until(ExpectedConditions.alertIsPresent());

        assertEquals(browser.switchTo().alert().getText(), "Appointment requested. Please wait for confirmation email");
        browser.switchTo().alert().accept();

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage"));
        assertEquals(baseUrl + "/homepage", browser.getCurrentUrl());

    }


    private void loginValid(){
        welcomePage.ensureLoginButtonIsClickable();

        welcomePage.setUsernameInput("Miljana@mailinator.com");
        welcomePage.setPasswordInput("miljana");
        welcomePage.getLoginButton().click();

        assertTrue(welcomePage.getLoginButton().isEnabled());

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage"));
        assertEquals(baseUrl + "/homepage", browser.getCurrentUrl());
    }

    private void clinicsShown(){

        homePage.ensureIsDisplayed();
        assertTrue(homePage.getUserHomepageContainer().isDisplayed());

        homePage.ensureInspectClinicsButtonIsClickable();
        assertTrue(homePage.getInspectClinicsButton().isDisplayed());

        homePage.getInspectClinicsButton().click();
        assertTrue(homePage.getInspectClinicsButton().isEnabled());

        homePage.ensureClinicsAreDisplayed();
        assertTrue(homePage.getAllClinicsTable().isDisplayed());

        homePage.ensureFromAppointmentFormIsDisplayed();
        assertTrue(homePage.getAppointmentDateAndTypeForm().isDisplayed());

        (new WebDriverWait(browser, 8)).until(ExpectedConditions.urlContains("/homepage/patient/clinics"));
        assertEquals(baseUrl + "/homepage/patient/clinics", browser.getCurrentUrl());
    }

    public boolean isAlertPresent()
    {
        try
        {
            browser.switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException Ex)
        {
            return false;
        }
    }

    @AfterMethod
    public void shutDown(){
        browser.close();
    }


}
