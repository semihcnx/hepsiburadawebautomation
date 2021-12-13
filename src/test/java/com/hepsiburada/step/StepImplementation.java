package com.hepsiburada.step;


import com.hepsiburada.base.Driver;
import com.hepsiburada.methods.Methods;
import com.thoughtworks.gauge.Step;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StepImplementation extends Driver {

    private Logger logger = LoggerFactory.getLogger(getClass());
    Methods methods = new Methods();


    @Step("Sayfada <x> ve <y> koordinatlarına scroll yap")
    public void scrollTo(int x, int y) {
        String script = String.format("window.scrollTo(%d, %d);", x, y);
        executeJS(script, true);
    }

    private Object executeJS(String script, boolean wait) {

        return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
    }

    private JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }


    @Step("<key> elementine tıkla")
    public void clickElement(String key) {
        methods.waitUntilPresenceOfElement(key);
        methods.waitUntilElementToBeClickableAndClick(key);
    }

    @Step("<key> elementi üzerinde bekle ve tıkla")
    public void focusAndClickElement(String key) {
        methods.isElementVisible(key);
        methods.focusToElement(key);
        methods.clickElement(key);
    }

    @Step("<key> elementi üzerinde bekle")
    public void focusElement(String key) {
        methods.isElementVisible(key);
        methods.focusToElement(key);
    }

    @Step("<key> elementi üzerinde hover bekle")
    public void hoverWaitElement(String key) {
        methods.hoverElementAction(By.xpath(key));
    }

    @Step("<key> elementine js ile tıkla")
    public void clickByElementWithJS(String key) {
        WebElement element = methods.findElement(key);
        getJSExecutor().executeScript("arguments[0].click();", element);
    }

    public static String bankNameText="";
    @Step("Anında Ödeme seçeneklerinden <bankName> bankasını sec")
    public void chooseBank(String bankName){
        switch (bankName){
            case "Akbank":
                clickByElementWithJS("chooseBankAkbank");
                bankNameText=methods.findElement("chooseBankAkbank").getText().trim();
                    break;

            case "Vakıfbank":
                clickByElementWithJS("chooseBankVakifbank");
                bankNameText=methods.findElement("chooseBankVakifbank").getText();
                break;

            case "İş Bankası":
                clickByElementWithJS("chooseBankIsBankasi");
                bankNameText=methods.findElement("chooseBankIsBankasi").getText();
                break;

            case "Kuveyt Türk":
                clickByElementWithJS("chooseBankKuveytTurk");
                bankNameText=methods.findElement("chooseBankKuveytTurk").getText();
                break;
            case "AlBaraka Türk":
                clickByElementWithJS("chooseBankAlbarakaTurk");
                bankNameText=methods.findElement("chooseBankAlbarakaTurk").getText();
                break;
        }
        logger.info("Ödeme Sayfasındaki seçilen banka bilgisi:"+bankNameText);

    }

    public static String siparisOzetiBankNameText="";
    @Step("Sipariş özeti sayfasındaki ödeme bilgilerinin kontrol edilmesi")
    public void controlBankName(){
        siparisOzetiBankNameText=methods.findElement("txtSiparisOzetiBankName").getText();
        logger.info("Siparis özeti sayfasındaki bank bilgisi:"+ siparisOzetiBankNameText);
        logger.info("Ödeme sayfasındaki bank bilgisi:"+ bankNameText);
        Assert.assertEquals(siparisOzetiBankNameText,bankNameText);
    }


    @Step("<key> element listesinin <index> sıradaki elemanına scroll yap")
    public void scrollToElementByIndex(String key, int index) {

        Point location = methods.findElements(key).get(index - 1).getLocation();
        scrollTo(location.getX(), location.getY());
    }

    @Step("<key> element listesinin <index> sıradaki elemanına scroll yap ve tıkla")
    public void scrollToElementByIndexClick(String key, int index) {

        WebElement webElement = methods.findElements(key).get(index - 1);
        scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY());
        waitBySeconds(2);
        webElement.click();
    }

    @Step("<key> element listesinin <index> sıradaki elemanına tıkla")
    public void clickElementByIndex(String key, int index) {

        methods.findElements(key).get(index - 1).click();
    }

    @Step("<key> elementine <text> ini yolla")
    public void sendKeysElement(String key, String text) {
        methods.sendKeys(key, text);
    }

    @Step("<key> elementine odaklan <text> ini yolla")
    public void focusandSendKeys(String key, String text) {
        methods.isElementVisible(key);
        // methods.focusToElement(key);
        methods.sendKeys(key, text);

    }

    @Step({"<key> elementinin text değeri <expectedText> değerine eşit mi",
            "get text <key> element and control <expectedText>"})
    public void getElementText(String key, String expectedText) {
        methods.waitElementIsVisible(key);
        String actualText;
        if (methods.getAttribute(key, "value") != null) {
            actualText = methods.getAttribute(key, "value").trim().replace("\r", "").replace("\n", "");
        } else {
            actualText = methods.getText(key).trim().replace("\r", "").replace("\n", "");
        }
        logger.info("Beklenen text: " + expectedText);
        logger.info("Alınan text: " + actualText);
        Assert.assertEquals("Text değerleri eşit değil", expectedText.replace(" ", ""), actualText.replace(" ", ""));
        logger.info("Text değerleri eşit");
    }

    @Step({"<key> elementinin text değeri <expectedText> değerini içeriyor mu",
            "get text <key> element and control contains <expectedText>"})
    public void getElementTextContain(String key, String expectedText) {
        methods.waitElementIsVisible(key);

        String actualText;
        if (methods.getAttribute(key, "value") != null) {
            actualText = methods.getAttribute(key, "value").trim().replace("\r", "").replace("\n", "");
        } else {
            actualText = methods.getText(key).trim().replace("\r", "").replace("\n", "");
        }

        logger.info("Beklenen text: " + expectedText);
        logger.info("Alınan text: " + actualText);
        Assert.assertTrue("Text değerleri eşit değil", actualText.replace(" ", "").contains(expectedText.replace(" ", "")));
        logger.info("Text değerleri eşit");
    }

    @Step("<seconds> saniye bekle")
    public void waitBySeconds(long seconds) {

        methods.waitBySeconds(seconds);
    }

    @Step("<key> elementinin sayfada mevcut olması kontrol edilir")
    public void controlIsElementPresent(String key) {

        Assert.assertTrue("Element mevcut değil", methods.isElementPresent(key));
    }

    @Step("<key> elementinin text alanını temizle")
    public void clearToElement(String key) {
        methods.clearElement(key);
    }

    @Step("<aspectUrl> sayfa urli dogru mu kontrol et")
    public void currentUrl(String aspectUrl) {
        Assert.assertEquals(driver.getCurrentUrl(), aspectUrl);
    }

    @Step({"<key> elementinin text değeri <expectedText> değerinden farklı mı"})
    public void checkElementNotEquals(String key, String expectedText) {
        methods.waitElementIsVisible(key);
        String actualText = methods.getText(key).trim().replace("\r", "").replace("\n", "");
        logger.info("Beklenen text: " + expectedText);
        logger.info("Alınan text: " + actualText);
        Assert.assertNotEquals("Text değerleri eşit", expectedText.replace(" ", ""), actualText.replace(" ", ""));
        logger.info("Text değerleri eşit değil");
    }


    @Step("<key> elementi ile fiyatini karsilastir")
    public void getAfterPriceofElement(String key) {
        methods.afterPrice(key);
    }

    @Step("<text1> text degeri <text2> degerine degismis mi")
    public void getTextChanging(String text1, String text2) {
        Assert.assertEquals(methods.getText(text1), methods.getText(text2));
    }

    @Step("<key> elementinin <attribute> niteliği <expectedValue> değerine eşit mi")
    public void checkElementAttribute(String key, String attribute, String expectedValue) {

        String attributeValue = methods.getAttribute(key, attribute);
        Assert.assertNotNull("Elementin değeri bulunamadi", attributeValue);
        Assert.assertEquals("Elementin değeri eslesmedi", expectedValue, attributeValue);
    }

    @Step("<key> elementinin text değerini gör")
    public void getTextValue(String key) {
        logger.info("=======================================");
        logger.info(key + " elementinin text değeri -->> " + methods.getText(key));
        logger.info("=======================================");
    }

    @Step("Şu anki url <url> ile aynı mı")
    public void doesUrlEqual(String url) {
        Assert.assertTrue("Beklenen url, sayfa url ine eşit değil", methods.doesUrl(url, 75, "equal"));
    }

    @Step("Şu anki url <url> içeriyor mu")
    public void doesUrlContain(String url) {
        Assert.assertTrue("Beklenen url, sayfa url ine eşit değil", methods.doesUrl(url, 75, "contain"));
    }

    @Step("<key> elementinin değerini temizle")
    public void clearElement(String key) {
        methods.clearElement(key);
    }

    }




