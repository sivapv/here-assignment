package steps;

import io.cucumber.java.en.Given;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DocumentsSteps {
    WebDriver driver = null;

    @Given("User opens browser")
    public void user_opens_browser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Given("User navigates to homepage")
    public void user_navigates_to_homepage() {
        driver.navigate().to("https://developer.here.com/");
    }

    @Given("^user clicks documentation link$")
    public void user_clicks_documentation_link() throws Throwable {
        Thread.sleep(3000);
        driver.findElement(By.xpath("(//a[contains(.,'Documentation')])[3]")).click();
//        WebDriverWait wait = new WebDriverWait(driver,30);
        List<WebElement> links = driver.findElements(By.tagName("a"));
        int sizeOfAllLinks = links.size();
        WebElement elm= null;
        for (int index=0; index<sizeOfAllLinks; index++ ) {
            elm = getElementWithIndex(By.tagName("a"), index);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elm);
            Thread.sleep(4000);
            driver.navigate().back();
            waitForAngular();
        }
    }


    public WebElement getElementWithIndex(By by, int index) {
        List<WebElement> elements = driver.findElements(By.tagName("a"));
        return elements.get(index);
    }

   public void waitForAngular() {
        final String javaScriptToLoadAngular =
                "var injector = window.angular.element('body').injector();" + 
                "var $http = injector.get('$http');" + 
                "return ($http.pendingRequests.length === 0)";

        ExpectedCondition<Boolean> pendingHttpCallsCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript(javaScriptToLoadAngular).equals(true);
            }
        };
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(pendingHttpCallsCondition);
    }
}
