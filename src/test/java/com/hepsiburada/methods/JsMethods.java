package com.hepsiburada.methods;

import org.junit.Assert;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


public class JsMethods {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    WebDriver driver;
    JavascriptExecutor jsDriver;

    public JsMethods(WebDriver driver){

        this.driver = driver;
        jsDriver = (JavascriptExecutor) driver;
    }


    public WebElement findElement(String byValue, String selectorType){

        String jsString = getjsFindString(byValue, selectorType);
        String script = "return " + jsString + ";";
        return (WebElement) jsDriver.executeScript(script);
    }

    public void scrollElement(WebElement webElement){

        jsDriver.executeScript("arguments[0].scrollIntoView();", webElement);
    }


    public String getjsFindString(String byValue, String selectorType){

        String jsString = "";
        byValue = byValue.replace("\"","'");
        switch (selectorType){

            case "id":
                jsString ="document.getElementById(\""+ byValue +"\")";
                break;

            case "name":
                jsString ="document.getElementsByName(\""+ byValue +"\")[0]";
                break;

            case "tagName":
                jsString = "document.getElementsByTagName(\"" + byValue + "\")[0]";
                break;

            case "class":
                jsString ="document.getElementsByClassName(\""+ byValue +"\")[0]";
                break;

            case "css":
                jsString ="document.querySelector(\""+ byValue +"\")";
                break;

            case "xpath":
                jsString = "document.evaluate(\"" + byValue
                        + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue";
                break;

            default:
                Assert.fail("HATA");
                break;
        }

        return jsString;
    }

    public String getjsFindsString(String byValue, String selectorType){

        String jsString = "";
        byValue = byValue.replace("\"","'");
        switch (selectorType){

            case "id":
                jsString = "return document.querySelectorAll(\"#" + byValue + "\");";
                break;

            case "name":
                jsString = "return document.getElementsByName(\"" + byValue + "\");";
                break;

            case "tagName":
                jsString = "return document.getElementsByTagName(\"" + byValue + "\");";
                break;

            case "class":
                jsString = "return document.getElementsByClassName(\"" + byValue + "\");";
                break;

            case "css":
                jsString = "return document.querySelectorAll(\"" + byValue + "\");";
                break;

            case "xpath":
                jsString = "var result = [];\n" +
                        "var nodesSnapshot = document.evaluate(\"" + byValue + "\",\n" +
                        "document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null );\n" +
                        "for ( var i=0 ; i < nodesSnapshot.snapshotLength; i++ ){\n" +
                        "result.push( nodesSnapshot.snapshotItem(i) );\n" +
                        "}\n" +
                        "return result;";
                break;

            default:
                Assert.fail("HATA");
        }

        return jsString;
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> findElements(String byValue, String selectorType){

        return (List<WebElement>) jsDriver.executeScript(getjsFindsString(byValue, selectorType));
    }


}
