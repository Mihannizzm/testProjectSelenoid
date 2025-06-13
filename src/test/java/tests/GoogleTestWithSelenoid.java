package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.util.Arrays;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class GoogleTestWithSelenoid {

    private static final Network network = Network.newNetwork();
    private GenericContainer<?> selenoid;
    private GenericContainer<?> selenoidUi;
    private String projectRoot = Paths.get("").toAbsolutePath().getParent().toString();

    @BeforeEach
    void beforeEach() {
        selenoid =
                new GenericContainer<>(DockerImageName.parse("aerokube/selenoid"))
                        .withCommand()
                        .withExposedPorts(4444)
                        .withNetwork(network)
                        .withNetworkAliases("selenoid")
                        .withCopyFileToContainer(
                                MountableFile.forClasspathResource("browsers.json"),
                                "/etc/selenoid/browsers.json"
                        )
                        .withFileSystemBind(projectRoot.concat("/.selenoid/config/"), "/etc/selenoid")
                        .withFileSystemBind("/var/run/docker.sock", "/var/run/docker.sock")
                        .withFileSystemBind(projectRoot.concat("/video/"), "/opt/selenoid/video");
        selenoid.start();

        String selenoidHost = selenoid.getHost(); // обычно "localhost"
        Integer selenoidPort = selenoid.getMappedPort(4444);
        System.out.println("Логи контейнера: " + selenoid.getLogs());
        String host = "http://" + selenoidHost + ":" + selenoidPort + "/wd/hub";

        selenoidUi =
                new GenericContainer<>(DockerImageName.parse("aerokube/selenoid-ui"))
                        .withExposedPorts(9090)
                        .withNetwork(network)
                        .withEnv("SELENOID_URI", "http://" + selenoidHost + ":" + selenoidPort);
        selenoidUi.start();

        System.out.println("Логи контейнера selenoid: " + selenoid.getLogs());
        System.out.println("Логи контейнера selenoidUi: " + selenoidUi.getLogs());
        System.out.println("Хост --->  " + host);


        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability("browserVersion", "128.0");
        chromeOptions.setCapability("browserName", "chrome");
        chromeOptions.addArguments("--no-sandbox");

        Configuration.remote = host;
        Configuration.browserCapabilities = chromeOptions;

    }

    @Test
    public void testExample() throws InterruptedException {

        try {
            open("https://www.google.com/");
        } catch (Exception e) {
            System.out.println("\nЛоги контейнера selenoid: " + selenoid.getLogs());
            System.out.println("\nЛоги контейнера selenoidUi: " + selenoidUi.getLogs());
            System.out.println("\n еще логи ->>>> " + Arrays.toString(e.getStackTrace()));
            Thread.sleep(300000);
        }

        $x("//*[@aria-label='Найти']").shouldBe(visible).setValue("Привет");
        $x("(//*[@value='Поиск в Google'])[1]").shouldBe(visible).click();
        sleep(10000);
    }
}
