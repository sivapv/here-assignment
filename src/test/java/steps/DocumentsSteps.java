package steps;

import io.cucumber.java.en.Given;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DocumentsSteps {
    WebDriver driver = null;

    @Given("User opens browser")
    public void user_opens_browser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
//        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Given("User navigates to homepage")
    public void user_navigates_to_homepage() {
        driver.navigate().to("https://developer.here.com/");
    }

    @Given("^user clicks documentation link$")
    public void user_clicks_documentation_link() throws Throwable {
        waitForPageToLoad();
        WebElement docTab = driver.findElement(By.xpath("(//a[contains(.,'Documentation')])[3]"));
        Actions act = new Actions(driver);
        act.moveToElement(docTab).build().perform();

        List<WebElement> links = driver.findElements(By.xpath("//a[contains(@href,'documentation#')]"));
        List<String> linkText = new ArrayList<>();
        int sizeOfAllLinks = links.size();
        System.out.println("Total Links: " + sizeOfAllLinks);
        int count = 0;
        for (WebElement elm : links) {
            if (elm.getText().length() > 0) {
                linkText.add(elm.getText());
            }
        }
        WebElement elm = null;
        for (String txt : linkText) {
            getClickableElement("(//a[contains(@href,'documentation#')][contains(text(),'" + txt + "')])[2]").click();

            Assert.assertTrue(RestAssured.get(driver.getCurrentUrl()).statusCode() == 200);
            System.out.println("Clicked link: " + txt);
            waitForPageToLoad();
            driver.get("https://developer.here.com/");
            waitForPageToLoad();
            WebDriverWait wait = new WebDriverWait(driver, 20);
            elm=getClickableElement("//a[contains(.,'Documentation')]");
            act = new Actions(driver);
            act.moveToElement(elm).build().perform();
        }

    }

    public WebElement getClickableElement(String xp) {
        List<WebElement> elements = driver.findElements(By.xpath(xp));
        for (WebElement element : elements) {
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
        }
        return null;
    }


    public void waitForPageToLoad() {
        ExpectedCondition<Boolean> javascriptDone = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
            }
        };
        WebDriverWait wait = new WebDriverWait(driver, 100);
        wait.until(javascriptDone);
    }
}
