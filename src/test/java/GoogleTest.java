import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class GoogleTest {

    @AfterEach
    void afterEach() {
        Configuration.remote = "http://88.210.20.169/4444";
    }

    @Test
    public void testExample() {
        open("https://www.google.com/");
        $x("//*[@aria-label='Найти']").shouldBe(visible).setValue("Привет");
        $x("(//*[@value='Поиск в Google'])[1]").shouldBe(visible).click();
        sleep(10000);
    }
}
