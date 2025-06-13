package tests;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class GoogleTestWithSelenoid {

    private static final Network network = Network.newNetwork();

    @BeforeEach
    void beforeEach() {
        GenericContainer<?> selenoid =
                new GenericContainer<>(DockerImageName.parse("aerokube/selenoid"))
                        .withCommand()
                .withExposedPorts(4444)
                        .withNetwork(network)
                        .withNetworkAliases("selenoid")
                        .withCopyFileToContainer(
                                MountableFile.forClasspathResource("browsers.json"),
                                "/etc/selenoid/browsers.json"
                        );

        GenericContainer<?> selenoidUi =
                new GenericContainer<>(DockerImageName.parse("aerokube/selenoid-ui"))
                        .withExposedPorts(9090)
                        .withNetwork(network)
                        .withEnv("SELENOID_URI", "http://88.210.20.169:4444");

        selenoid.start();
        selenoidUi.start();

        Configuration.remote = "http://88.210.20.169:4444/wd/hub";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;

    }

    @Test
    public void testExample() {
        open("https://www.google.com/");
        $x("//*[@aria-label='Найти']").shouldBe(visible).setValue("Привет");
        $x("(//*[@value='Поиск в Google'])[1]").shouldBe(visible).click();
        sleep(10000);
    }
}
