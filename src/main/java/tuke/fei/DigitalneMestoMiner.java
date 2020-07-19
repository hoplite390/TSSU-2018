package tuke.fei;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.stream.Collectors;

/**
 * Class used for mining from digitalne mesto
 */
public class DigitalneMestoMiner implements Runnable {
    private String startingUrl;
    private String chromedriverPath;
    private String invoiceType;
    private CsvWriter csvWriter;
    private Logger log = LogManager.getLogger(DigitalneMestoMiner.class);

    /**
     * Constructor
     * @param startingUrl url from which should this miner mine
     * @param invoiceType type of invoice
     * @param chromedriverPath path to chromedriver
     * @param csvWriter csv writer which will be used to write results
     */
    public DigitalneMestoMiner(String startingUrl, String invoiceType, String chromedriverPath, CsvWriter csvWriter) {
        this.startingUrl = startingUrl;
        this.chromedriverPath = chromedriverPath;
        this.invoiceType = invoiceType;
        this.csvWriter = csvWriter;
    }

    @Override
    public void run() {
        log.info("Starting miner for url: " + startingUrl);
        long startMillis = System.currentTimeMillis();
        System.setProperty("webdriver.chrome.driver", chromedriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        driver.get(startingUrl);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'x-action-col-icon x-action-col-0  x-fa fa-search-plus')]")));
        driver.findElement(By.xpath("//div[contains(@class, 'x-action-col-icon x-action-col-0  x-fa fa-search-plus')]")).click();
        int n = 0;
        try {
            boolean shouldContinue = true;
            do {
                wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//span[contains(@class, 'x-btn-button x-btn-button-default-small  x-btn-no-text x-btn-icon x-btn-icon-left x-btn-button-center ')]")));
                csvWriter.writeln("\"" + String.join("\"\t\"", driver.findElements(By.xpath("//div[contains(@id,'inputEl')]")).stream().map(we -> we.getText().replace("\t", " ").replace("\"", "")).collect(Collectors.toList())) + "\"\t\"" + invoiceType + "\"" );
                n++;
                wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//span[contains(@class, 'x-btn-button x-btn-button-default-small  x-btn-no-text x-btn-icon x-btn-icon-left x-btn-button-center ')]")));
                try {
                    driver.findElement(By.xpath("//span[contains(@class, 'x-btn-button x-btn-button-default-small  x-btn-no-text x-btn-icon x-btn-icon-left x-btn-button-center ')]")).click();
                } catch (Exception e) {
                    shouldContinue = false;
                }
            } while (shouldContinue);
        } catch (Exception e) {
            log.info("Miner for url " + startingUrl + " failed on: " + e.getMessage());
        }
        long endMillis = System.currentTimeMillis();
        log.info("Miner for url " + startingUrl + " mined: " + n + " invoices");
        log.info("Miner for url " + startingUrl + " ended in " + (endMillis - startMillis) + " millis");
        driver.close();
    }
}
