package com.uniovi.tests;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.repositories.UsersRepository;
import com.uniovi.services.RolesService;
import com.uniovi.services.UsersService;
import com.uniovi.tests.pageobjects.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Se ordenan las pruebas por el nombre del metodo
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class WallapopTest {

    @Autowired
    private UsersService usersService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private UsersRepository usersRepository;

    static String URLlocal = "http://localhost:8080";
    static String URLremota = "ec2-35-180-86-141.eu-west-3.compute.amazonaws.com:8080";
    static String URL = URLlocal; // Se va a probar con la URL remota, sino URL=URLlocal

    //En Windows (Debe ser la versión 65.0.1 y desactivar las actualizacioens automáticas)):
    static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckdriver024 = "C:\\Users\\Daniel\\OneDrive - Universidad de Oviedo" +
            "\\Tercer Curso\\SDI\\Practicas\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
    //Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);

    public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckdriver);
        WebDriver driver = new FirefoxDriver();
        return driver;
    }

    //Después de cada prueba se borran las cookies del navegador
    @After
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeClass
    static public void begin() {
    }

    @Before
    public void setUp() {
        driver.navigate().to(URL);
        reloadUsers();
    }

    //Al finalizar la última prueba
    @AfterClass
    static public void end() {
        //Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    //PR01. Prueba del formulario de registro. registro con datos correctos
    @Test
    public void PR01() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Rellenamos el formulario.
        PO_RegisterView.fillForm(driver, "prueba@mail.com", "Josefo", "Perez", "77777",
                "77777");
        //Comprobamos que entramos en la sección privada
        PO_View.checkElement(driver, "text", "Ofertas del usuario");
    }

    //PR06. Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos).
    @Test
    public void PR02() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Email vacio
        PO_RegisterView.fillForm(driver, " ", "Josefo", "Perez", "77777",
                "77777");
        PO_View.getP();
        //Comprobamos el error de campo vacio
        PO_RegisterView.checkKey(driver, "Error.empty",
                PO_Properties.getSPANISH());
        //Rellenamos el formulario.
        PO_RegisterView.fillForm(driver, "email@email.com", " ", "Perez", "77777",
                "77777");
        PO_View.getP();
        //Comprobamos el error de campo vacio
        PO_RegisterView.checkKey(driver, "Error.empty",
                PO_Properties.getSPANISH());
        //Rellenamos el formulario.
        PO_RegisterView.fillForm(driver, "email@email.com", "Jose", " ", "77777",
                "77777");
        //Comprobamos el error de campo vacio.
        PO_RegisterView.checkKey(driver, "Error.empty",
                PO_Properties.getSPANISH());
    }

    //PR03. Registro de Usuario con datos inválidos (repetición de contraseña inválida)
    @Test
    public void PR03() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Contraseña mal repetida
        PO_RegisterView.fillForm(driver, "email@email.com", "Josefo", "Perez", "77577",
                "77777");
        PO_View.getP();
        //Comprobamos el error de contraseña
        PO_RegisterView.checkKey(driver, "Error.signup.passwordConfirm.coincidence",
                PO_Properties.getSPANISH());
    }

    //PR04. Registro de Usuario con datos inválidos (email existente)
    @Test
    public void PR04() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Email repetido
        PO_RegisterView.fillForm(driver, "user1@email.com", "Josefo", "Perez", "77577",
                "77777");
        PO_View.getP();
        //Comprobamos el error de campo vacio
        PO_RegisterView.checkKey(driver, "Error.signup.email.duplicate",
                PO_Properties.getSPANISH());
    }

    //PR05: Identificación válida con usuario de ROL administrador
    @Test
    public void PR05() {

        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        PO_LoginView.fillForm(driver, "admin@email.com", "admin");

        PO_NavView.checkElement(driver, "id", "users-menu");
    }

    //PR06: Inicio de sesión con datos válidos (usuario estándar).
    @Test
    public void PR06() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        PO_LoginView.fillForm(driver, "user1@email.com", "user1");

        PO_NavView.checkElement(driver, "id", "offers-menu");
    }

    //PR07: Inicio de sesión con datos inválidos (usuario estándar, campo email y contraseña vacíos).
    @Test
    public void PR07() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        PO_LoginView.fillForm(driver, "", "");

        PO_NavView.checkElement(driver, "id", "btn_login");
    }

    //PR08: Inicio de sesión con datos válidos (usuario estándar, email existente, pero contraseña
    //incorrecta).
    @Test
    public void PR08() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        PO_LoginView.fillForm(driver, "user1@email.com", "error");

        PO_NavView.checkElement(driver, "id", "btn_login");
    }

    //PR09: Inicio de sesión con datos inválidos (usuario estándar, email no existente en la aplicación).
    @Test
    public void PR09() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        PO_LoginView.fillForm(driver, "user66@email.com", "user66");

        PO_NavView.checkElement(driver, "id", "btn_login");
    }

    //PR10. Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de inicio
    //de sesión (Login).
    @Test
    public void PR10() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Nos desconectamos
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        //Comprobamos que estamos en la pagina de login mirando a ver si esta el boton de login
        PO_LoginView.checkElement(driver, "id", "btn_login");
    }

    //PR11: Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
    @Test
    public void PR11() {
        WebElement resultado =
                (new WebDriverWait(driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Daniel')]")));
        assertTrue(resultado != null);
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'Desconectar')]")).isEmpty());
    }

    //PR12: Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el
    //sistema.
    @Test
    public void PR12() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Comprobamos que estan todos los usuarios
        PO_View.checkElement(driver, "text", "user1@email.com");
        PO_View.checkElement(driver, "text", "user2@email.com");
        PO_View.checkElement(driver, "text", "user3@email.com");
        PO_View.checkElement(driver, "text", "user4@email.com");
    }

    //PR13: Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza
    //y dicho usuario desaparece.
    @Test
    public void PR13() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Eliminamos el primer usuario
        PO_View.checkElement(driver, "id", "remove-user1@email.com").get(0).click();
        //Comprobamos que estan todos
        PO_View.checkElement(driver, "text", "user2@email.com");
        PO_View.checkElement(driver, "text", "user3@email.com");
        PO_View.checkElement(driver, "text", "user4@email.com");
        //Y el no
        WebElement resultado =
                (new WebDriverWait(driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Daniel')]")));
        assertTrue(resultado != null);
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user1@email.com')]")).isEmpty());
    }

    //PR14: Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
    //y dicho usuario desaparece.
    @Test
    public void PR14() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Eliminamos el primer usuario
        PO_View.checkElement(driver, "id", "remove-user4@email.com").get(0).click();
        //Comprobamos que estan todos
        PO_View.checkElement(driver, "text", "user2@email.com");
        PO_View.checkElement(driver, "text", "user3@email.com");
        PO_View.checkElement(driver, "text", "user1@email.com");
        //Y el no
        WebElement resultado =
                (new WebDriverWait(driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Daniel')]")));
        assertTrue(resultado != null);
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user4@email.com')]")).isEmpty());
    }

    //PR15:Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se actualiza y dichos
    //usuarios desaparecen.
    @Test
    public void PR15() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Clickamos las casillas de los tres primeros usuarios
        PO_View.checkElement(driver, "id", "ckDelete-user1@email.com").get(0).click();
        PO_View.checkElement(driver, "id", "ckDelete-user2@email.com").get(0).click();
        PO_View.checkElement(driver, "id", "ckDelete-user3@email.com").get(0).click();
        //Clickamos el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton").get(0).click();
        //Comprobamos que esta el cuarto aun
        PO_View.checkElement(driver, "text", "user4@email.com");
        // Y el resto no
        WebElement resultado =
                (new WebDriverWait(driver, 2)).until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Daniel')]")));
        assertTrue(resultado != null);
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user2@email.com')]")).isEmpty());
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user1@email.com')]")).isEmpty());
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user3@email.com')]")).isEmpty());
    }

    //PR16: Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
    //Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
    @Test
    public void PR16() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de añadir oferta
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/add')]");
        //Pinchamos en agregar oferta.
        elementos.get(0).click();
        //Ahora vamos a rellenar la oferta
        PO_PrivateView.fillFormAddOffer(driver, "Oferta nueva", "Oferta de prueba", 15);
        //Vamos a home
        driver.navigate().to(URL + "/home");
        //Comprobamos que sale la oferta nueva
        PO_View.checkElement(driver, "text", "Oferta nueva");
    }

    //PR17: Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío) y pulsar
    //el botón Submit. Comprobar que se muestra el mensaje de campo obligatorio.
    @Test
    public void PR17() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de añadir oferta
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/add')]");
        //Pinchamos en agregar oferta.
        elementos.get(0).click();
        //Ahora vamos a rellenar la oferta
        PO_PrivateView.fillFormAddOffer(driver, "", "Oferta de prueba", 15);
        //Comprobamos que sale el error de campo obligatorio
        assertEquals("Rellene este campo.", driver.findElement(By.name("title")).getAttribute("validationMessage"));
    }

    //PR18: Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas los que
    //existen para este usuario.
    @Test
    public void PR18() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Comprobamos que se listan todas las ofertas del usuario
        PO_HomeView.checkElement(driver, "text", "Chupete");
        PO_HomeView.checkElement(driver, "text", "TV");
        PO_HomeView.checkElement(driver, "text", "PS3");
        PO_HomeView.checkElement(driver, "text", "PS4");
    }

    //PR19: Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza y
    //que la oferta desaparece.
    @Test
    public void PR19() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/list')]");
        //Pinchamos en ver ofertas.
        elementos.get(0).click();
        //Eliminamos la primera oferta de la lista
        elementos = PO_View.checkElement(driver, "text", "Eliminar");
        elementos.get(0).click();
        //Comprobamos que no esta en la lista mediante hashes
        WebElement elementoBorrado = elementos.get(0);
        ArrayList<Integer> hashes = new ArrayList<>();
        PO_View.checkElement(driver, "text", "Eliminar")
                .forEach(elemento -> hashes.add(elemento.hashCode()));
        assertEquals(false, hashes.contains(elementoBorrado.hashCode()));
    }

    //PR20: Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza y
    //que la oferta desaparece
    @Test
    public void PR20() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/list')]");
        //Pinchamos en ver ofertas.
        elementos.get(0).click();
        //Vamos a la ultima pagina de ofertas
        driver.navigate().to(URL + "/offer/list?page=3");
        //Eliminamos la ultima oferta de la lista
        elementos = PO_View.checkElement(driver, "text", "Eliminar");
        elementos.get(elementos.size() - 1).click();
        //Comprobamos que no esta en la lista mediante hashes
        WebElement elementoBorrado = elementos.get(0);
        driver.navigate().to(URL + "/offer/list?page=2");
        ArrayList<Integer> hashes = new ArrayList<>();
        PO_View.checkElement(driver, "text", "Eliminar")
                .forEach(elemento -> hashes.add(elemento.hashCode()));
        assertEquals(false, hashes.contains(elementoBorrado.hashCode()));
    }

    //PR21: Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
    //corresponde con el listado de las ofertas existentes en el sistema
    @Test
    public void PR21() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/list')]");
        //Pinchamos en ver ofertas.
        elementos.get(0).click();
        //Pinchamos en actualizar
        PO_View.checkElement(driver, "text", "Actualizar").get(0).click();
        //Guardamos los nombres de las ofertas
        ArrayList<String> titulosOfertas = new ArrayList<>();
        List<WebElement> ofertas = PO_View.checkElement(driver, "id", "title");
        ofertas.forEach(oferta -> titulosOfertas.add(oferta.getText()));
        //Pinchamos en actualizar
        elementos = PO_View.checkElement(driver, "text", "Actualizar");
        elementos.get(0).click();
        //Comprobamos que hay los mismos nombres
        ArrayList<String> titulosOfertasNuevas = new ArrayList<>();
        ofertas = PO_View.checkElement(driver, "id", "title");
        ofertas.forEach(oferta -> titulosOfertasNuevas.add(oferta.getText()));
        assertTrue(titulosOfertas.containsAll(titulosOfertas));
    }

    //PR22: Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
    //muestra la página que corresponde, con la lista de ofertas vacía.
    @Test
    public void PR22() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/list')]");
        //Pinchamos en ver ofertas.
        elementos.get(0).click();
        //Hacemos la consulta de algo inexistente
        WebElement campoBusqueda = PO_View.checkElement(driver, "id", "searchBox").get(0);
        campoBusqueda.clear();
        campoBusqueda.sendKeys("No existo");
        //Pinchamos en actualizar
        PO_View.checkElement(driver, "class", "btn btn-default").get(0).click();
        //Comprobamos que no hay ofertas
        boolean noExistenOfertas = false;
        try {
            WebElement algunaOferta = driver.findElement(By.xpath("//*[contains(@id,'" + "title" + "')]"));
        } catch (NoSuchElementException exception) {
            noExistenOfertas = true;
        }
        assertTrue(noExistenOfertas);
    }

    //PR23: Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
    //un saldo positivo en el contador del comprobador. Y comprobar que el contador se actualiza
    //correctamente en la vista del comprador.
    @Test
    public void PR23() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/list')]");
        //Pinchamos en ver ofertas.
        elementos.get(0).click();
        //Hacemos la consulta de algo que queremos comprar
        WebElement campoBusqueda = PO_View.checkElement(driver, "id", "searchBox").get(0);
        campoBusqueda.clear();
        campoBusqueda.sendKeys("Ob3");
        //Pinchamos en actualizar
        PO_View.checkElement(driver, "class", "btn btn-default").get(0).click();
        //Compramos la oferta
        PO_View.checkElement(driver, "text", "Comprar").get(0).click();
        //Comprobamos el dinero
        driver.navigate().to(URL + "/offer/list");
        Float cantidad = Float.valueOf(PO_View.checkElement(driver, "id", "userMoney")
                .get(0).getText().replace('€', ' '));
        assertTrue(cantidad < 100 && cantidad > 0);
    }

    //PR24: Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
    //un saldo 0 en el contador del comprobador. Y comprobar que el contador se actualiza correctamente en
    //la vista del comprador.
    @Test
    public void PR24() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user2@email.com", "123456");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/list')]");
        //Pinchamos en ver ofertas.
        elementos.get(0).click();
        //Hacemos la consulta de algo que queremos comprar
        WebElement campoBusqueda = PO_View.checkElement(driver, "id", "searchBox").get(0);
        campoBusqueda.clear();
        campoBusqueda.sendKeys("Objeto medio");
        //Pinchamos en actualizar
        PO_View.checkElement(driver, "class", "btn btn-default").get(0).click();
        //Compramos la oferta
        PO_View.checkElement(driver, "text", "Comprar").get(0).click();
        //Comprobamos el dinero
        driver.navigate().to(URL + "/offer/list");
        float cantidad = Float.parseFloat(PO_View.checkElement(driver, "id", "userMoney")
                .get(0).getText().replace('€', ' '));
        assertEquals(0, cantidad, 0.0);
    }

    //PR25: Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
    //que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje de
    //saldo no suficiente.
    @Test
    public void PR25() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/list')]");
        //Pinchamos en ver ofertas.
        elementos.get(0).click();
        //Hacemos la consulta de algo que queremos comprar
        WebElement campoBusqueda = PO_View.checkElement(driver, "id", "searchBox").get(0);
        campoBusqueda.clear();
        campoBusqueda.sendKeys("Objeto caro");
        //Pinchamos en actualizar
        PO_View.checkElement(driver, "class", "btn btn-default").get(0).click();
        //Intentamos comprar la oferta
        PO_View.checkElement(driver, "text", "Comprar").get(0).click();
        //Comprobamos que nos da error
        WebElement alerta = PO_View.checkElement(driver, "class", "alert alert-warning").get(0);
        assertTrue(alerta.isDisplayed());
    }

    //PR26: Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
    //las ofertas que deben aparecer.
    @Test
    public void PR26() {
        PR23();
        driver.navigate().to(URL + "/login");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
        elementos.get(0).click();
        //Esperamos a aparezca la opción de ver ofertas
        elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, 'offer/boughtList')]");
        //Pinchamos en ver compras.
        elementos.get(0).click();
        //Hacemos la consulta de algo que queremos comprar
        List<WebElement> ofertas = PO_View.checkElement(driver, "id", "titleOffer");
        List<String> titulosOfertas = ofertas.stream().map(WebElement::getText).collect(Collectors.toList());
        assertTrue(titulosOfertas.contains("Ob3"));
    }

    //PR27: Visualizar al menos cuatro páginas en Español/Inglés/Español (comprobando que algunas
    //de las etiquetas cambian al idioma correspondiente). Página principal/Opciones Principales de
    //Usuario/Listado de Usuarios de Admin/Vista de alta de Oferta
    @Test
    public void PR27() {
        PO_HomeView.checkChangeIdiomPaginaPrincipal(driver, "btnSpanish", "btnEnglish",
                PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        // Comprobamos idioma opciones usuario
        PO_HomeView.checkChangeIdiomHome(driver, "btnSpanish", "btnEnglish",
                PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
        //Vamos a añadir oferta
        driver.navigate().to(URL + "/offer/add");
        //Comprobamos idioma añadir oferta
        PO_HomeView.checkChangeIdiomOfferAdd(driver, "btnSpanish", "btnEnglish",
                PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
        //Hacemos logout
        driver.navigate().to(URL + "/logout");
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        // Comprobamos idioma opciones usuario
        PO_HomeView.checkChangeIdiomUsersManagement(driver, "btnSpanish", "btnEnglish",
                PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
    }

    //PR28: Intentar acceder sin estar autenticado a la opción de listado de usuarios del administrador. Se
    //deberá volver al formulario de login.
    @Test
    public void PR28() {
        driver.navigate().to(URL + "/user/list");
        PO_LoginView.checkElement(driver, "id", "btn_login");
    }

    //PR29: Intentar acceder sin estar autenticado a la opción de listado de ofertas propias de un usuario
    //estándar. Se deberá volver al formulario de login.
    @Test
    public void PR29() {
        driver.navigate().to(URL + "/home");
        PO_LoginView.checkElement(driver, "id", "btn_login");
    }

    //PR30: Estando autenticado como usuario estándar intentar acceder a la opción de listado de
    //usuarios del administrador. Se deberá indicar un mensaje de acción prohibida.
    @Test
    public void PR30() {
        //Vamos al formulario de logueo.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Intentamos acceder a la lista de usuarios
        driver.navigate().to(URL + "/user/list");
        //Miramos que haya saltado el aviso
        assertTrue(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));
    }

    private void reloadUsers() {
        usersRepository.deleteAll();
        User user1 = new User("user1@email.com", "Pedro", "Díaz");
        user1.setPassword("user1");
        user1.setRole(rolesService.getRoles()[0]);
        User user2 = new User("user2@email.com", "Lucas", "Núñez");
        user2.setPassword("123456");
        user2.setRole(rolesService.getRoles()[0]);
        User user3 = new User("user3@email.com", "María", "Rodríguez");
        user3.setPassword("123456");
        user3.setRole(rolesService.getRoles()[0]);
        User user4 = new User("user4@email.com", "Marta", "Almonte");
        user4.setPassword("123456");
        user4.setRole(rolesService.getRoles()[0]);
        User user5 = new User("admin@email.com", "Daniel", "Fernandez");
        user5.setPassword("admin");
        user5.setRole(rolesService.getRoles()[1]);
        Set user1Offers = new HashSet<Offer>() {
            {
                add(new Offer("Chupete", "Chupete gastado", 10.0, user1));
                add(new Offer("TV", "Tele vieja", 9.0, user1));
                add(new Offer("Consola vieja", "PS3", 7.0, user1));
                add(new Offer("Consola nueva", "PS4", 9.5, user1));
            }
        };
        user1.setOffers(user1Offers);
        Set user2Offers = new HashSet<Offer>() {
            {
                add(new Offer("Ob1", "Objeto B1", 5.0, user2));
                add(new Offer("Ob2", "Objeto B2", 4.3, user2));
                add(new Offer("Ob3", "Objeto B3", 8.0, user2));
                add(new Offer("Ob4", "Objeto B4", 3.5, user2));
                add(new Offer("Objeto caro", "Muy caro", 150d, user2));
            }
        };
        user2.setOffers(user2Offers);
        Set user3Offers = new HashSet<Offer>() {
            {
                add(new Offer("Oc1", "Objeto C1", 5.5, user3));
                add(new Offer("Oc2", "Objeto C2", 6.6, user3));
                add(new Offer("Oc3", "Objeto C3", 7.0, user3));
                add(new Offer("Objeto medio", "Objeto medio", 100.0, user3));
            }
        };
        user3.setOffers(user3Offers);
        Set user4Offers = new HashSet<Offer>() {
            {
                add(new Offer("Od1", "Objeto D1", 10.0, user4));
                add(new Offer("Od2", "Objeto D2", 8.0, user4));
                add(new Offer("Od3", "Objeto D3", 9.0, user4));
            }
        };
        user4.setOffers(user4Offers);
        usersService.addUser(user1);
        usersService.addUser(user2);
        usersService.addUser(user3);
        usersService.addUser(user4);
        usersService.addUser(user5);
    }

}