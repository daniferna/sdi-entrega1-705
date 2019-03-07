package com.uniovi.es.wallapop.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_LoginView extends PO_NavView {
    static public void fillForm(WebDriver driver, String usernameString, String passwordString) {
        WebElement username = driver.findElement(By.name("username"));
        username.click();
        username.clear();
        username.sendKeys(usernameString);
        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(passwordString);
        //Pulsar el boton de Login.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}
