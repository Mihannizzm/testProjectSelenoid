//package tests;
//
//import com.codeborne.selenide.Configuration;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.openqa.selenium.remote.DesiredCapabilities;
//
//import static com.codeborne.selenide.Condition.visible;
//import static com.codeborne.selenide.Selenide.*;
//
//public class GoogleTest {
//
//    @BeforeEach
//    void beforeEach() {
//        Configuration.remote = "http://88.210.20.169:4444/";
//        Configuration.browser = "chrome";
//        Configuration.browserVersion = "128.0"; // зависит от образа
//        Configuration.browserSize = "1920x1080";
//        Configuration.timeout = 10000;
//
//        DesiredCapabilities capabilities = new DesiredCapabilities();
//        capabilities.setCapability("enableVNC", true);
//        capabilities.setCapability("enableVideo", false); // или true, если настроено
//        Configuration.browserCapabilities = capabilities;
//    }
//
//    @Test
//    public void testExample() {
//        open("https://www.google.com/");
//        $x("//*[@aria-label='Найти']").shouldBe(visible).setValue("Привет");
//        $x("(//*[@value='Поиск в Google'])[1]").shouldBe(visible).click();
//        sleep(10000);
//    }
//}
