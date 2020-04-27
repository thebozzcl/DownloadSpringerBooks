import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadSpringerBooks {
    private static final String INDEX_URL_TEMPLATE = "https://link.springer.com/search/page/%d?facet-content-type=%%22Book%%22&package=openaccess";
    private static final String INDEX_BOOK_SELECTOR = "li.has-cover > div:nth-child(5) > h2:nth-child(1) > a:nth-child(1)";
    private static final String HREF = "href";
    private static final String BOOK_DL_SELECTOR = ".cta-button-container--stacked > div:nth-child(1)";

    private static final int DL_TIMEOUT_MILLIS = 60 * 1000;

    private static final int INDEX_START = 32;
    private static final int INDEX_END = 50;

    private static final String HOME_FOLDER = "/home/bozz/";
    private static final String DL_FOLDER = HOME_FOLDER + "Downloads/";
    private static final String GECKODRIVER_FOLDER = HOME_FOLDER + "selenium/geckodriver/";

    public static void main(final String[] args) throws InterruptedException {
        System.setProperty("webdriver.gecko.driver", GECKODRIVER_FOLDER);
        final FirefoxOptions options = getFirefoxOptions();

        for (int i = INDEX_START; i <= INDEX_END; i++) {
            final FirefoxDriver driver = new FirefoxDriver(options);

            getBookUrlsFromPage(driver, i).forEach(bookUrl -> downloadPdf(driver, bookUrl));
            waitForDownloads();

            driver.quit();
        }
    }

    private static FirefoxOptions getFirefoxOptions() {
        final FirefoxOptions options = new FirefoxOptions();
        final FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", "C:\\Windows\\temp");
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
        profile.setPreference("pdfjs.disabled", true);  // disable the built-in PDF viewer
        options.setProfile(profile);

        return options;
    }

    private static List<String> getBookUrlsFromPage(final FirefoxDriver driver, final int pageNumber) {
        final String url = String.format(INDEX_URL_TEMPLATE, pageNumber);
        System.out.println("Processing page " + pageNumber + " with URL " + url);

        final List<String> result = new ArrayList<String>();

        driver.get(url);
        driver.findElements(By.cssSelector(INDEX_BOOK_SELECTOR)).forEach(el -> {
            final String bookUrl = el.getAttribute(HREF);
            result.add(bookUrl);
            System.out.println("Found book URL " + bookUrl);
        });

        System.out.println("Found a total of " + result.size() + " book URLs");
        return result;
    }

    private static void downloadPdf(final FirefoxDriver driver, final String url) {
        System.out.println("Downloading book from URL " + url);

        driver.get(url);
        try {
            driver.findElement(By.cssSelector(BOOK_DL_SELECTOR)).click();
        } catch (final Exception e) {
            // I saw at least one book without a download link. This should help.
            System.out.println("Unable to download from" + url);
        }
    }

    private static final void waitForDownloads() throws InterruptedException {
        final long startTimestamp = System.currentTimeMillis();
        do {
            Thread.sleep(1000);
        } while (!pollDownloadsReady() && (System.currentTimeMillis() - startTimestamp) > DL_TIMEOUT_MILLIS);
    }

    private static final boolean pollDownloadsReady() {
        final File dlDir = new File(DL_FOLDER);
        // Just check that there's no partial download files in the downloads folder
        return dlDir.listFiles((dir, filename) -> filename.endsWith(".part")).length == 0;
    }
}
