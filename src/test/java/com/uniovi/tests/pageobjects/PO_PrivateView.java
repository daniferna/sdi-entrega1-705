package com.uniovi.tests.pageobjects;

import com.uniovi.utils.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_PrivateView extends PO_NavView {
    static public void fillFormAddOffer(WebDriver driver, String titulo, String detalles, double precio) {
        //Esperamos 5 segundo a que carge el DOM porque en algunos equipos falla
        SeleniumUtils.esperarSegundos(driver, 5);
        //Rellenemos el campo de titulo
        WebElement title = driver.findElement(By.name("title"));
        title.clear();
        title.sendKeys(titulo);
        //Rellenemos el campo de detalles
        WebElement description = driver.findElement(By.name("description"));
        description.clear();
        description.sendKeys(detalles);
        //Rellenemos el campo de precio
        WebElement price = driver.findElement(By.name("value"));
        price.clear();
        price.sendKeys(String.valueOf(precio));
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}