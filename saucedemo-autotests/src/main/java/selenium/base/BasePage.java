package selenium.base;


import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.actions.*;
import selenium.interfaces.*;
import selenium.models.FilterSelectionResult;
import selenium.utils.AssertionHelper;
import selenium.utils.PopupHelper;
import selenium.utils.ShopFilterHelper;

import java.time.Duration;
import java.util.List;

@Getter
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // Композиция: внедряем действия как зависимости
    protected final ElementActions elementActions;
    protected final ScrollActions scrollActions;
    protected final WaitActions waitActions;
    protected final JavaScriptActions jsActions;
    protected final AdvancedActions advancedActions;
    protected final FormActions formActions;
    protected final PageLoadActions pageLoadActions;
    protected final FilterSeleniumActions filterSeleniumActions;
    protected final FilterActions filterActions;
    protected final SearchActions searchActions;
    protected final VerificationActions verificationActions;
    protected final AssertionHelper assertionHelper;
    protected final PopupHelper popupHelper;
    protected final ShopFilterHelper shopFilter;  // ← Добавляем



    // Стандартный Actions из Selenium
    protected final Actions seleniumActions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        this.filterActions = filterActions;

        this.assertionHelper = new AssertionHelper();
        this.popupHelper = new PopupHelper(driver);


        // Создаем единый экземпляр действий
        CustomSeleniumActions customSeleniumActions = new CustomSeleniumActions(driver, wait);
        this.scrollActions = customSeleniumActions;
        this.waitActions = customSeleniumActions;
        this.elementActions = customSeleniumActions;

        this.jsActions = customSeleniumActions;

        // Инициализация стандартного Selenium Actions
        this.seleniumActions = new Actions(driver);
        this.advancedActions = new AdvancedSeleniumActions(driver, wait);
        this.formActions = new FormSeleniumActions(driver, wait);
        this.pageLoadActions = new PageLoadSeleniumActions(driver, wait, scrollActions, waitActions);
        this.filterSeleniumActions = new FilterSeleniumActions(driver, wait, scrollActions, waitActions, assertionHelper);
        this.filterActions = new FilterSeleniumActions(driver, wait, scrollActions, waitActions, assertionHelper);
        this.searchActions = new SearchSeleniumActions(driver, wait, elementActions, waitActions, pageLoadActions);
        this.verificationActions = new VerificationSeleniumActions(driver, waitActions);
        this.shopFilter = new ShopFilterHelper(driver, wait, scrollActions, waitActions, assertionHelper);


        PageFactory.initElements(driver, this);
    }

    // Делегируем часто используемые методы для удобства

    protected boolean isTextPresent(By locator, String text) {
        return verificationActions.isTextPresent(locator, text);
    }

    protected boolean isTextContains(By locator, String text) {
        return verificationActions.isTextContains(locator, text);
    }

    protected List<String> getAllTexts(By locator) {
        return verificationActions.getAllTexts(locator);
    }

    protected boolean hasMinimumCount(By locator, int minCount) {
        return verificationActions.hasMinimumCount(locator, minCount);
    }

    protected FilterSelectionResult selectCheckboxesWithResult(By searchField, String checkboxTemplate, List<String> items) {
        return filterActions.selectCheckboxesWithResult(searchField, checkboxTemplate, items);
    }

    protected List<String> selectCheckboxes(By searchField, String checkboxTemplate, List<String> items) {
        return filterActions.selectCheckboxes(searchField, checkboxTemplate, items);
    }


    protected void click(By locator) {
        elementActions.click(locator);
    }

    protected void scrollToElement(WebElement element) {
        scrollActions.scrollToElement(element);
    }

    protected void scrollDown() {
        scrollActions.scrollDown();
    }

    protected void sleep(long millis) {
        waitActions.sleep(millis);
    }

    protected boolean isPageEndReached() {
        return scrollActions.isPageEndReached();
    }

    protected void clearAndType(By locator, String text) {
        elementActions.clearAndType(locator, text);
    }

    protected void pressEnter(By locator) {
        formActions.pressEnter(locator);
    }

    protected void waitForContentLoad(By locator) {
        pageLoadActions.waitForContentLoad(locator);
    }

    protected void waitForLoaderDisappear() {
        pageLoadActions.waitForLoaderDisappear();
    }

    protected void hoverOver(By locator) {
        advancedActions.hoverOver(locator);
    }

    /**
     * Закрывает все всплывающие окна
     */
    protected void closePopups() {
        popupHelper.closeAllPopups();
        sleep(500);
    }
}