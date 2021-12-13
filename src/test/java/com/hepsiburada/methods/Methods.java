package com.hepsiburada.methods;

import com.hepsiburada.base.Driver;
import com.hepsiburada.helper.ElementHelper;
import com.hepsiburada.helper.StoreHelper;
import com.hepsiburada.model.ElementInfo;
import groovy.util.logging.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Methods extends Driver {

    private static Log4jLoggerAdapter logger = (Log4jLoggerAdapter) LoggerFactory
            .getLogger(Methods.class);

    private static String SAVED_ATTRIBUTE;
    WebDriver driver;
    FluentWait<WebDriver> wait;
    com.hepsiburada.methods.JsMethods jsMethods;
    MethodsUtil methodsUtil;
    ActionMethods actionMethods;
    long waitElementTimeout;
    long pollingEveryValue;

    public Methods(){

        this.driver = Driver.driver;
        setWaitElementTimeout();
        setPollingEveryValue();
        wait = setFluentWait(waitElementTimeout);
        jsMethods = new com.hepsiburada.methods.JsMethods(driver);
        actionMethods = new ActionMethods(driver);
        methodsUtil = new MethodsUtil();
    }

    private void setWaitElementTimeout(){

        waitElementTimeout = Driver.isTestinium ? Long.parseLong(Driver.ConfigurationProp
                .getString("testiniumWaitElementTimeout")) : Long.parseLong(Driver.ConfigurationProp
                .getString("localWaitElementTimeout"));
    }

    private void setPollingEveryValue(){

        pollingEveryValue = Driver.isTestinium ? Long.parseLong(Driver.ConfigurationProp
                .getString("testiniumPollingEveryMilliSecond")) : Long.parseLong(Driver.ConfigurationProp
                .getString("localPollingEveryMilliSecond"));
    }

    public FluentWait<WebDriver> setFluentWait(long timeout){

        FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver);
        fluentWait.withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofMillis(pollingEveryValue))
                .ignoring(NoSuchElementException.class);
        return fluentWait;
    }

    public ElementInfo getElementInfo(String key) {

        return StoreHelper.INSTANCE.findElementInfoByKey(key);
    }

    public By getBy(String key) {

        logger.info("Element " + key + " değerinde tutuluyor");
        return ElementHelper.getElementInfoToBy(getElementInfo(key));
    }

    public List<String> getByValueAndSelectorType(By by){

        List<String> list = new ArrayList<String>();
        String[] values = by.toString().split(": ",2);
        list.add(values[1].trim());
        list.add(getSelectorTypeName(values[0].replace("By.","").trim()));
        return list;
    }

    public WebElement findElement(String key){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        WebDriverWait webDriverWait = new WebDriverWait(driver, 5);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    public List<WebElement> findElements(String key){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        return driver.findElements(infoParam);
    }

    public void clickElement(String key) {
        findElement(key).click();
        logger.info("Elemente tıklandı.");
    }

    public void waitElementIsVisible(String key) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(key)));
        logger.info("Element görünür");
    }

    public void clearElement(String key) {

        findElement(key).clear();
    }

    public void sendKeys(String key, String text) {

        findElement(key).sendKeys(text);
        logger.info("Elemente " + text + " texti yazıldı.");
    }

    public String getText(String key) {

        return findElement(key).getText();
    }

    public String getAttribute(String key, String attribute) {

        return findElement(key).getAttribute(attribute);
    }

    public void focusToElement(String key) {

        WebElement webElement = findElement(key);
        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("arguments[0].scrollIntoView();", webElement);
        jse.executeScript("arguments[0].focus();", webElement);
    }

    public void focusToElementAndClick(String key) {

        WebElement webElement = findElement(key);
        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("arguments[0].scrollIntoView();", webElement);
        jse.executeScript("arguments[0].focus();", webElement);
        jse.executeScript("var evt = document.createEvent('MouseEvents');"
                + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                + "arguments[0].dispatchEvent(evt);", webElement);
    }

    public void waitByMilliSeconds(long milliSeconds) {

        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitBySeconds(long seconds) {

        logger.info(seconds + " saniye bekleniyor...");
        waitByMilliSeconds(seconds * 1000);
    }

    public boolean isElementVisible(String key) {
        WebDriverWait wait = new WebDriverWait(driver,20);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(key)));
            logger.info(key+" elementi görünür durumdadır.");
            return true;
        } catch (Exception e) {
            logger.info("Element görünür değil");
            return false;
        }
    }

    public boolean isElementVisibleCustomTime(String key, int time){

        WebDriverWait wait = new WebDriverWait(driver,time);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(key)));
            logger.info(key+" elementi görünür durumdadır.");
            return true;
        } catch (Exception e) {
            logger.info("Element görünür değil");
            return false;
        }
    }

    public boolean isElementInVisible(String key) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(getBy(key)));
            return true;
        } catch (Exception e) {
            logger.info("Element görünüyor");
            return false;
        }
    }

    public boolean isElementPresent(String key){
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(getBy(key)));
            return true;
        } catch (Exception e) {
            logger.info("Element sayfada mevcut");
            return false;
        }
    }

    public boolean isElementClickable(String key) {
        WebDriverWait wait = new WebDriverWait(driver,20);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(getBy(key)));
            return true;
        } catch (Exception e) {
            logger.info("Element görünür değil");
            return false;
        }

    }
    public boolean isElementClickable(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            return true;
        } catch (Exception e) {
            logger.info("Element görünür değil");
            return false;
        }

    }

    public WebElement findElement(By by){

        logger.info("Element " + by.toString() + " by değerine sahip");
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement findElementWithoutWait(By by){

        logger.info("Element " + by.toString() + " by değerine sahip");
        return driver.findElement(by);
    }

    public WebElement findElementForJs(By by, String type){

        WebElement webElement = null;
        switch (type){
            case "1":
                webElement = findElement(by);
                break;
            case "2":
                webElement = findElementWithoutWait(by);
                break;
            case "3":
                List<String> byValueList = getByValueAndSelectorType(by);
                webElement = jsMethods.findElement(byValueList.get(0),byValueList.get(1));
                break;
            default:
                Assert.fail("type hatalı");
        }
        return webElement;
    }

    public List<WebElement> findElements(By by){

        logger.info("Element " + by.toString() + " by değerine sahip");
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    public List<WebElement> findElementsWithOutError(By by){

        logger.info("Element " + by.toString() + " by değerine sahip");
        List<WebElement> list = new ArrayList<>();
        try {
            list.addAll(wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by)));
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public List<WebElement> findElementsWithoutWait(By by){

        logger.info("Element " + by.toString() + " by değerine sahip");
        return driver.findElements(by);
    }

    public List<WebElement> findElementsForJs(By by, String type){

        List<WebElement> webElementList = null;
        switch (type){
            case "1":
                webElementList = findElements(by);
                break;
            case "2":
                webElementList = findElementsWithoutWait(by);
                break;
            case "3":
                List<String> byValueList = getByValueAndSelectorType(by);
                webElementList = jsMethods.findElements(byValueList.get(0),byValueList.get(1));
                break;
            default:
                Assert.fail("type hatalı");
        }
        return webElementList;
    }

    private String getSelectorTypeName(String type){

        String selectorType = "";
        switch (type) {

            case "id":
                selectorType = "id";
                break;

            case "name":
                selectorType = "name";
                break;

            case "className":
                selectorType = "class";
                break;

            case "cssSelector":
                selectorType = "css";
                break;

            case "xpath":
                selectorType = "xpath";
                break;

            default:
                Assert.fail("HATA");
                break;
        }
        return selectorType;
    }

    public void hoverElementAction(By by) {

        WebElement webElement = findElementForJs(by,"1");
        jsMethods.scrollElement(webElement);
        actionMethods.hoverElement(webElement);
    }

    public void moveAndClickElement(By by) {

        WebElement webElement = findElementForJs(by,"1");
        jsMethods.scrollElement(webElement);
        actionMethods.moveAndClickElement(webElement);
    }

    public void clickElementWithAction(By by){

        WebElement webElement = findElementForJs(by,"1");
        jsMethods.scrollElement(webElement);
        actionMethods.clickElement(webElement);
    }

    public void doubleClickElementWithAction(By by){

        WebElement webElement = findElementForJs(by,"1");
        jsMethods.scrollElement(webElement);
        actionMethods.doubleClickElement(webElement);
    }

    public void selectAction(By by, int optionIndex){

        WebElement webElement = findElementForJs(by,"1");
        jsMethods.scrollElement(webElement);
        actionMethods.select(webElement, optionIndex);
    }

    public void focusToElementJs(By by){

        WebElement webElement = findElementForJs(by,"3");
        waitByMilliSeconds(1000);
        jsMethods.scrollElement(webElement);
        waitByMilliSeconds(100);
        jsMethods.scrollElement(webElement);
        waitByMilliSeconds(1000);
    }

    public String getText(By by){

        return findElement(by).getText();
    }

    public boolean containsControlUrl(String currentUrl, String urls){

        String[] urlArray = urls.split(",");
        boolean result=false;
        for(int i = 0; i < urlArray.length; i++){
            if(!urlArray[i].equals("")) {
                result = currentUrl.contains(urlArray[i]);
            }
            if(result){
                break;
            }
        }
        return result;
    }
    private void hoverElement(WebElement element){
        Actions actions= new Actions(driver);
        actions.moveToElement(element).build().perform();
    }

    public void hoverElement(String key) {
        WebElement element = findElement(key);
        Actions hoverAction = new Actions(driver);
        hoverAction.moveToElement(element).perform();
    }

    Double price1 = 0.0;
    Double price2 = 0.0;

    public void comparePrices(String key1, String key2) {
        WebElement element1 = findElement(key1);
        price1 = Double.valueOf(element1.getText().split(" ")[0].trim());
        System.out.println(price1);

        WebElement element2 = findElement(key2);
        price2 = Double.valueOf(element2.getText().split(" ")[0].trim());
        System.out.println(price2);

        Assert.assertEquals(price1, price2);
    }


    String price = null;

    public String getPrices(String key){
        WebElement element = findElement(key);
        price = element.getText();
        return price;
    }

    public void afterPrice(String key){
        WebElement element = findElement(key);
         String afterPrice = element.getText();
        Assert.assertEquals(price, afterPrice);
    }


    public WebElement waitForElementVisibility(String key, int maxWaitTime){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By by = ElementHelper.getElementInfoToBy(elementInfo);

        WebDriverWait wait = new WebDriverWait(driver, maxWaitTime);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }



    public boolean doesUrl(String url, int count, String condition){

        int againCount = 0;
        boolean isUrl = false;
        String takenUrl = "";
        logger.info("Beklenen url: " + url);
        while (!isUrl) {
            waitByMilliSeconds(400);
            if (againCount == count) {
                logger.info("Alınan url: " + takenUrl);
                return false;
            }
            takenUrl = driver.getCurrentUrl();
            if (takenUrl != null) {
                isUrl = conditionValueControl(url,takenUrl,condition);
            }
            againCount++;
        }
        logger.info("Alınan url: " + takenUrl);
        return true;
    }
    public boolean conditionValueControl(String expectedValue, String actualValue,String condition){

        boolean result = false;
        switch (condition){
            case "equal":
                result = actualValue.equals(expectedValue);
                break;
            case "contain":
                result = actualValue.contains(expectedValue);
                break;
            case "startWith":
                result = actualValue.startsWith(expectedValue);
                break;
            case "endWith":
                result = actualValue.endsWith(expectedValue);
                break;
            default:
                Assert.fail("hatali durum: " + condition);
        }
        return result;
    }

    public void waitUntilPresenceOfElement(String key){
        WebDriverWait wait = new WebDriverWait(driver,30);

        logger.info(key+" elementinin sayfada mevcut olması beklendi.");
        try {
            findElement(key);
            //wait.until(ExpectedConditions.presenceOfElementLocated(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key))));

        }
        catch(WebDriverException ex)
        {
            wait.until(ExpectedConditions.presenceOfElementLocated(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key))));
        }

    }

    public void waitUntilElementToBeClickableAndClick(String key){
        WebDriverWait wait = new WebDriverWait(driver,30);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key)))).click();

        }
        catch(ElementClickInterceptedException ex)
        {
            wait.until(ExpectedConditions.elementToBeClickable(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key)))).click();

        }
        //wait.until(ExpectedConditions.elementToBeClickable(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key)))).click();
        logger.info(key+" elementinin sayfada tıklanabilir olması beklendi ve tıklandı.");

    }

    private void clickElementByKeyWithHover(WebElement element){
        element.click();
    }
    public String getElementText(String key){
        return findElement(key).getText();
    }
    public void javascriptExecutor(String script){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(script);
    }



}

