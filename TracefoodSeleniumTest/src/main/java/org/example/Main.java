package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/data/Documents/ING2/algebra/Advanced programming paradigms/chromedriver_linux64/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("http://10.42.0.1:8081/signIn");
        Thread.sleep(1000L);
        testSignIn(driver);
        Thread.sleep(1000L);
        testErrorMessage(driver);
        testSignInSuccessful(driver);
        WebElement productButton = driver.findElement(By.cssSelector("a[href='/userSearchForAProduct']"));
        productButton.click();
        Thread.sleep(1000L);
        testResearchAboutAProduct(driver);
        Thread.sleep(1000L);

        driver.quit();
    }
    //functional programming

    private static void testResearchAboutAProduct(WebDriver driver) {
        executeTest(driver, "Test Search Product", webDriver -> {
            fillInputField(webDriver, By.name("productId"), "15");
            clickElement(webDriver, By.cssSelector("#searchForm button[type='submit']"));
            return true;
        });
    }
    private static void testErrorMessage(WebDriver driver) {
        executeTest(driver, "Test Error Message", webDriver ->
                webDriver.findElement(By.className("error-message")).isDisplayed());
    }

    private static void testSignIn(WebDriver driver) throws InterruptedException{
        System.out.println("Testing Sign In");
        executeTest(driver, "Sign in a processor with email='test@example.com' and password='password'", webDriver -> {
            fillInputField(webDriver, "emailAddress", "test@example.com");
            fillInputField(webDriver, "password", "password");
            selectDropdownOption(webDriver, "type", "Processor");
            webDriver.findElement(By.cssSelector("#signInForm button[type='submit']")).click();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return webDriver.getCurrentUrl().equals("http://10.42.0.1:8081/userHomePage");
        });
    }

    private static void testSignInSuccessful(WebDriver driver) throws InterruptedException{
        System.out.println("Testing Sign In");
        executeTest(driver, "Sign in a producer with email='app@test.com' and password='123456'", webDriver -> {
            fillInputField(webDriver, "emailAddress", "app@test.com");
            fillInputField(webDriver, "password", "123456");
            selectDropdownOption(webDriver, "type", "Producer");
            webDriver.findElement(By.cssSelector("#signInForm button[type='submit']")).click();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return webDriver.getCurrentUrl().equals("http://10.42.0.1:8081/userHomePage");
        });
    }

    private static void executeTest(WebDriver driver, String testName, Function<WebDriver, Boolean> testFunction) {
        System.out.println("Executing Test: " + testName);
        boolean result = testFunction.apply(driver);
        if (result) {
            System.out.println("Test Passed!");
        } else {
            System.out.println("Test Failed!");
        }
    }

    private static void fillInputField(WebDriver driver, String inputId, String value) {
        WebElement inputField = driver.findElement(By.id(inputId));
        inputField.clear();
        inputField.sendKeys(value);
    }

    private static void selectDropdownOption(WebDriver driver, String selectId, String optionValue) {
        WebElement dropdown = driver.findElement(By.id(selectId));
        dropdown.sendKeys(optionValue);
    }

    private static void fillInputField(WebDriver driver, By locator, String value) {
        WebElement inputField = driver.findElement(locator);
        inputField.clear();
        inputField.sendKeys(value);
    }

    private static void clickElement(WebDriver driver, By locator) {
        WebElement element = driver.findElement(locator);
        element.click();
    }
}
