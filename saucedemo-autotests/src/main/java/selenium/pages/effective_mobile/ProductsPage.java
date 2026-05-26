package selenium.pages.effective_mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static selenium.constants.Messages.PRODUCTS_PAGE_TITLE;

public class ProductsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(className = "title")
    private WebElement pageTitle;

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpened() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(pageTitle)).isDisplayed()
                    && getPageTitle().equals(PRODUCTS_PAGE_TITLE);
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageTitle() {
        return pageTitle.getText();
    }
}