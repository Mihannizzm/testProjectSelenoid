import org.junit.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class GoogleTest {

    @Test
    public void testExample() {
        open("https://www.google.com/");
        $x("//*[@aria-label='Найти']").shouldBe(visible).setValue("Привет");
        $x("(//*[@value='Поиск в Google'])[1]").shouldBe(visible).click();
        sleep(10000);
    }
}
