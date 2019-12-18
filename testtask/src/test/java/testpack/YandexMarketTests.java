package testpack;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class YandexMarketTests {

    @Before
    public void prepareForTesting() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\chromedriver_win32\\chromedriver.exe");
    }

    // URL
    private static final String GOOGLE = "https://google.com";

    // CSS SELECTOR
    private static final String GOOGLE_LOGO = "#hplogo";
    private static final String YANDEX_MARKET_LINK = "#rso > div:nth-child(1) > div > div > div > div > div.r > a > div > cite";

    @Test
    public void yandexMarketSearchTest() {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            driver.get(GOOGLE);
            WebElement googleSearchButton = wait.until(presenceOfElementLocated(By.cssSelector(GOOGLE_LOGO)));
            Assert.assertEquals(
                    "Error: google.com is not opened",
                    "Google",
                    googleSearchButton.getAttribute("alt")
            );
            driver.findElement(By.name("q")).sendKeys("яндекс маркет" + Keys.ENTER);
            WebElement yandexMarketLink = wait.until(presenceOfElementLocated(By.cssSelector(YANDEX_MARKET_LINK)));
            Assert.assertEquals(
                    "Error: Page does not contain yandex market link",
                    "https://market.yandex.ru",
                    yandexMarketLink.getAttribute("textContent")
            );
            yandexMarketLink.click();
            WebElement yandexMarketLogo = wait.until(presenceOfElementLocated(By.className(MARKET_LOGO)));
            Assert.assertEquals(
                    "Error: Yandex market did not opened",
                    "Маркет",
                    yandexMarketLogo.getText()
            );

        } finally {
            driver.quit();
        }
    }

    // URL
    private static final String YANDEX_MARKET = "https://market.yandex.ru";

    // CLASS NAME
    private static final String SHOW_ALL_MANUFACTURERS = "_2Wg9rE1HzR";
    private static final String MANUFACTURERS_LIST = "_5ropeY89h";
    private static final String MANUFACTURERS_INPUT_FIELD = "_1JYTt02WxW";
    private static final String MANUFACTURER_CHECKBOX = "_2RDCAZB4Gk";
    private static final String MAXIMUM_PRODUCT_COST_FIELD = "_1f2usTwyAs";
    private static final String SHOW_ALL_MANUFACTURERS_LINK = "_2Wg9rE1HzR";
    private static final String MARKET_LOGO = "logo_part_market";
    private static final String VACUUM_CLEANERS_CATEGORIES_LIST = "_2BLXswkhGO";
    private static final String VACUUM_CLEANERS_CATEGORY = "_38YjhiFMWW";

    // CSS SELECTOR
    private static final String POLARIS_CHECKBOX = "#\\37 893318_288426";
    private static final String VITEK_CHECKBOX = "#\\37 893318_152837";
    private static final String FIRST_PRODUCT_IN_LIST = "#product-1722785411 > div.n-snippet-card2__part.n-snippet-card2__part_type_center > div.n-snippet-card2__header.n-snippet-card2__header_has_rating > h3 > a";

    @Test
    public void yandexMarketFiltrationTest() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            // YANDEX MARKET OPENING
            driver.get(YANDEX_MARKET);
            WebElement yandexMarketLogo = wait.until(presenceOfElementLocated(By.className(MARKET_LOGO)));
            Assert.assertEquals(
                    "Error: Yandex market is not opened",
                    "Маркет",
                    yandexMarketLogo.getText()
            );

            // VACUUM CLEANER SEARCHING
            driver.findElement(By.id("header-search")).sendKeys("пылесосы" + Keys.ENTER);
            WebElement firstElement = wait.until(presenceOfElementLocated(By.cssSelector(FIRST_PRODUCT_IN_LIST)));
            Assert.assertTrue(
                    "Error: Element does not contrain word 'Пылесос'",
                    firstElement.getAttribute("title").contains("Пылесос")
            );

            // CATEGORY SELECTION
            WebElement vacuumCleanersCategory = getVacuumCleanersCategory(driver);
            vacuumCleanersCategory.click();
            WebElement showAllManufacturer = wait.until(visibilityOfElementLocated(By.className(SHOW_ALL_MANUFACTURERS)));
            showAllManufacturer.click();
            wait.until(presenceOfElementLocated(By.className(MANUFACTURERS_LIST)));

            // DATA COLLECTION
            Set<String> manufacturersSet = collectManufacturers(driver);
            saveToFile(manufacturersSet);

            // FILTERING
            WebElement manufacturersInputField = driver.findElement(By.className(MANUFACTURERS_INPUT_FIELD));
            manufacturersInputField.click();
            manufacturersInputField.sendKeys("Polaris");
            waitOneSecond();
            driver.findElement(By.className(MANUFACTURER_CHECKBOX)).click();
            manufacturersInputField.click();
            manufacturersInputField.clear();
            waitOneSecond();
            manufacturersInputField.sendKeys("Vitek");
            waitOneSecond();
            driver.findElement(By.className(MANUFACTURER_CHECKBOX)).click();
            driver.findElement(By.className(MAXIMUM_PRODUCT_COST_FIELD)).sendKeys("6000");
            driver.findElement(By.className(SHOW_ALL_MANUFACTURERS_LINK)).click();
            waitOneSecond();

            // FILTRATION CHECKING
            Assert.assertTrue(
                    "Error: Search result row not found",
                    driver.findElement(By.className("n-search-preciser__text")).getText().contains("Найдено")
            );

            Assert.assertEquals(
                    "Error: Incorrect value in the 'maximum cost' field",
                    "6000",
                    driver.findElement(By.className(MAXIMUM_PRODUCT_COST_FIELD)).getAttribute("value")
            );

            Assert.assertTrue(
                    "Error: Polaris checkbox is not selected",
                    driver.findElement(By.cssSelector(POLARIS_CHECKBOX)).isSelected()
            );

            Assert.assertTrue(
                    "Error: Vitek checkbox is not selected",
                    driver.findElement(By.cssSelector(VITEK_CHECKBOX)).isSelected()
            );

        } finally {
            driver.quit();
        }
    }

    private static void waitOneSecond() {
        wait(1000);
    }

    private static void wait500ms() {
        wait(500);
    }

    private static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> collectManufacturers(WebDriver driver) {
        Set<String> set = new TreeSet<>();
        boolean needToContinue = true;
        while (needToContinue) {
            driver.findElement(By.className(MANUFACTURERS_LIST)).click();
            Actions scrollAction = new Actions(driver);
            scrollAction.sendKeys(Keys.PAGE_DOWN).perform();
            wait500ms();
            List<WebElement> allManufacturersList = driver.findElement(By.className(MANUFACTURERS_LIST))
                    .findElements(By.tagName("li"));

            for (WebElement we : allManufacturersList) {
                String manufacturer = we.getText();
                if (manufacturer.equals("Энергия")) {
                    needToContinue = false;
                }
                if (manufacturer.length() > 1) {
                    set.add(manufacturer);
                }
            }
        }
        return set;
    }

    private static void saveToFile(Set<String> set) {
        FileWriter writer;
        try {
            writer = new FileWriter("manufacturers_list.txt");
            for (String str : set) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static WebElement getVacuumCleanersCategory(WebDriver driver) {
        WebElement vacuumCleanersCategory = driver
                .findElements(By.className(VACUUM_CLEANERS_CATEGORIES_LIST)).get(3)
                .findElement(By.className(VACUUM_CLEANERS_CATEGORY));

        if (vacuumCleanersCategory.getText().contains("Профессиональные")) {
            vacuumCleanersCategory = driver
                    .findElements(By.className(VACUUM_CLEANERS_CATEGORIES_LIST)).get(1)
                    .findElement(By.className(VACUUM_CLEANERS_CATEGORY));
        }

        return vacuumCleanersCategory;
    }

}
