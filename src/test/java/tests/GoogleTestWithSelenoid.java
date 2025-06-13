package tests;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class GoogleTestWithSelenoid {

    private static final Network network = Network.newNetwork();
    private GenericContainer<?> selenoid;
    private GenericContainer<?> selenoidUi;
    private String projectRoot = Paths.get("").toAbsolutePath().getParent().toString();

    @BeforeEach
    void beforeEach() {
        selenoid = new GenericContainer<>("aerokube/selenoid")
                .withNetwork(network)
                .withNetworkAliases("selenoid")
                .withExposedPorts(4444)
                .withCommand(
                        "-limit", "1",
//                        "-conf", "/etc/selenoid/browsers.json",
                        "-video-output-dir", "/opt/selenoid/video",
                        "-video-recorder-image", "selenoid/video-recorder:latest-release",
                        "-log-output-dir", "/opt/selenoid/logs",
                        "-container-network", network.getId(),
                        "-timeout", "%sm".formatted(5)
                )
                .withEnv("TZ", "Europe/Moscow")
                .withEnv("LANG", "en_US:en")
                .withEnv("LANGUAGE", "en_US:en")
                .withEnv("LC_ALL", "en_US.UTF-8")
                .withEnv("OVERRIDE_VIDEO_OUTPUT_DIR", projectRoot.concat("/video"))
                .withEnv("JAVA_OPTS", "-Xmx1024m")
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

        selenoidUi = new GenericContainer<>(DockerImageName.parse("aerokube/selenoid-ui"))
                .withExposedPorts(8080)
                .withNetwork(network)
                .withCommand("--selenoid-uri=http://selenoid:%s".formatted(
                        4444
                ));
        selenoidUi.start();

        System.out.println("Логи контейнера selenoid: " + selenoid.getLogs());
        System.out.println("Логи контейнера selenoidUi: " + selenoidUi.getLogs());
        System.out.println("Хост --->  " + host);


        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.setCapability("selenoid:options", new HashMap<String, Object>() {{
            /* How to add test badge */
            put("name", "Test badge...");

            /* How to set session timeout */
            put("sessionTimeout", "15m");

            /* How to set timezone */
            put("env", new ArrayList<String>() {{
                add("TZ=UTC");
            }});

            /* How to add "trash" button */
            put("labels", new HashMap<String, Object>() {{
                put("manual", "true");
            }});

            /* How to enable video recording */
            put("enableVideo", true);

            put("enableVnc", true);
        }});

        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--ignore-urlfetcher-cert-requests");
        chromeOptions.addArguments("--enable-automation");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-browser-side-navigation");
        chromeOptions.addArguments("--disable-gpu");

        Configuration.remote = host;
        Configuration.browserCapabilities = chromeOptions;

    }

    @Test
    public void testExample() throws InterruptedException {

        try {
            open("https://www.google.com/");
            System.out.println("\nЛоги контейнера selenoid после старта сессии: " + selenoid.getLogs());
            System.out.println("\nЛоги контейнера selenoidUi после старта сессии: " + selenoidUi.getLogs());
        } catch (Exception e) {
            System.out.println("\nЛоги контейнера selenoid: " + selenoid.getLogs());
            System.out.println("\nЛоги контейнера selenoidUi: " + selenoidUi.getLogs());
            System.out.println("\n еще логи ->>>> " + Arrays.toString(e.getStackTrace()));
        }
        System.out.println("Дошли до sleep");
        sleep(300000);
        $x("//*[@aria-label='Найти']").shouldBe(visible).setValue("Привет");
        $x("(//*[@value='Поиск в Google'])[1]").shouldBe(visible).click();

    }
}