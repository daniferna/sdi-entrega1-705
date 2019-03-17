package com.uniovi.tests.pageobjects;

import com.uniovi.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;

public class PO_HomeView extends PO_NavView {

    static public void checkWelcome(WebDriver driver, int language) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("welcome.message", language), getTimeout());
    }

    static public void checkChangeIdiomPaginaPrincipal(WebDriver driver, String textIdiom1, String
            textIdiom2, int locale1, int locale2) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        PO_HomeView.checkWelcome(driver, locale1);
        //Cambiamos a segundo idioma
        PO_HomeView.changeIdiom(driver, textIdiom2);
        //Comprobamos que el texto de bienvenida haya cambiado a segundo idioma
        PO_HomeView.checkWelcome(driver, locale2);
        //Volvemos a Español.
        PO_HomeView.changeIdiom(driver, textIdiom1);
        //Esperamos a que se cargue el saludo de bienvenida en Español
        PO_HomeView.checkWelcome(driver, locale1);
    }

    static public void checkChangeIdiomOfferAdd(WebDriver driver, String textIdiom1, String
            textIdiom2, int locale1, int locale2) {
        PO_HomeView.checkOfferAdd(driver, locale1);

        PO_HomeView.changeIdiom(driver, textIdiom2);

        PO_HomeView.checkOfferAdd(driver, locale2);

        PO_HomeView.changeIdiom(driver, textIdiom1);

        PO_HomeView.checkOfferAdd(driver, locale1);
    }

    static public void checkChangeIdiomHome(WebDriver driver, String textIdiom1, String
            textIdiom2, int locale1, int locale2) {
        PO_HomeView.checkHome(driver, locale1);

        PO_HomeView.changeIdiom(driver, textIdiom2);

        PO_HomeView.checkHome(driver, locale2);

        PO_HomeView.changeIdiom(driver, textIdiom1);

        PO_HomeView.checkHome(driver, locale1);
    }

    static public void checkChangeIdiomUsersManagement(WebDriver driver, String textIdiom1, String
            textIdiom2, int locale1, int locale2) {
        PO_HomeView.checkUsersManage(driver, locale1);

        PO_HomeView.changeIdiom(driver, textIdiom2);

        PO_HomeView.checkUsersManage(driver, locale2);

        PO_HomeView.changeIdiom(driver, textIdiom1);

        PO_HomeView.checkUsersManage(driver, locale1);
    }

    private static void checkUsersManage(WebDriver driver, int language) {
        SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("usersInSystem.message", language), getTimeout());
    }

    private static void checkHome(WebDriver driver, int language) {
        SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("price.message", language), getTimeout());
    }

    private static void checkOfferAdd(WebDriver driver, int language) {
        SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("send.message", language), getTimeout());
    }

}