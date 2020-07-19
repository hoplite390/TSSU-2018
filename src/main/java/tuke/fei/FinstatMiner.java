package tuke.fei;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.util.*;

/**
 * Class used to mine data from finstat
 */
public class FinstatMiner implements Runnable {
    private Logger log = LogManager.getLogger(FinstatMiner.class);

    private String chromedriverPath;
    private CsvReader csvReader;
    private Connection con;
    private int cityKey;

    /**
     * Constructor
     * @param chromedriverPath path to chrome driver
     * @param csvReader csv reader which will  be used to read invoices
     * @param con database connection
     * @param cityKey city key from database
     */
    public FinstatMiner(String chromedriverPath, CsvReader csvReader, Connection con, int cityKey) {
        this.chromedriverPath = chromedriverPath;
        this.csvReader = csvReader;
        this.con = con;
        this.cityKey = cityKey;
    }

    @Override
    public void run() {
        long startMillis = System.currentTimeMillis();
        System.setProperty("webdriver.chrome.driver", chromedriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 30);

        int n = 0;
        Invoice invoice;
        while ((invoice = csvReader.getNextInvoice()) != null) {
            n++;
            driver.get("https://finstat.sk/" + invoice.getIdentificationNumber());
            if (!driver.findElements(By.xpath("//*[contains(text(), 'Adresa neexistujúcej stránky:')]")).isEmpty()) {
                log.info("Bad organization " + invoice.getIdentificationNumber());
                continue;
            }
            String partyAddressRaw;
            try {
                partyAddressRaw = driver.findElement(By.xpath("//*[contains(text(), 'Sídlo')]/following::span")).getText().replaceAll(".+\\n", "");

            } catch (Exception deleted) {
                log.info("Organization " + invoice.getIdentificationNumber() + " deleted");
                continue;
            }
            Address partyAddress = AddressParser.parseAddress(partyAddressRaw);
            int partyAddressKey = DatabaseHandler.insertIntoAddressTable(con, partyAddress, partyAddressRaw);
            driver.get("https://finstat.sk/" + invoice.getIdentificationNumber() + "/osoby_vo_firme");

            if (driver.findElements(By.xpath("//*[contains(@id, 'rpvs-KonecnyUzivatelVyhod')]/div/div/table/tbody/tr")).isEmpty()) {
                Integer partyKeyFromDb = DatabaseHandler.selectFromPartyByIdentificationNumber(con, invoice.getIdentificationNumber());
                int partyKey;
                if (partyKeyFromDb == null) {
                    partyKey = DatabaseHandler.insertIntoPartyTable(
                            con,
                            false,
                            invoice.getIdentificationNumber(),
                            invoice.getOrganization(),
                            null,
                            partyAddressKey
                    );
                } else {
                    partyKey = partyKeyFromDb;
                }
                DatabaseHandler.insertIntoInvoiceTable(con, invoice, partyKey, cityKey);
            } else {
                List<WebElement> endUsers = driver.findElements(By.xpath("//*[contains(@id, 'rpvs-KonecnyUzivatelVyhod')]/div/div/table/tbody/tr"));

                Integer orgKeyFromDb = DatabaseHandler.selectFromPartyByIdentificationNumber(con, invoice.getIdentificationNumber());
                int orgKey;
                if (orgKeyFromDb == null) {
                    orgKey = DatabaseHandler.insertIntoPartyTable(
                            con,
                            true,
                            invoice.getIdentificationNumber(),
                            invoice.getOrganization(),
                            null,
                            partyAddressKey
                    );
                } else {
                    orgKey = orgKeyFromDb;
                }
                Map<String, Integer> persons = new HashMap<>();
                for (WebElement e : endUsers) {
                    if (!e.getText().trim().isEmpty()) {
                        handleCompEndUser(e, orgKey, false, persons);
                    }
                }
                List<WebElement> companions = driver.findElements(By.xpath("//*[contains(@id, 'orsr-Spolocnik')]/div/div/table/tbody/tr"));
                for (WebElement e : companions) {
                    if (!e.getText().trim().isEmpty()) {
                        handleCompEndUser(e, orgKey, true, persons);
                    }
                }

                DatabaseHandler.insertIntoInvoiceTable(con, invoice, orgKey, cityKey);
            }
        }
        long duration = System.currentTimeMillis() - startMillis;
        log.info("FinstatMiner miner " + Thread.currentThread() + " ended in " + duration);
        log.info("FinstatMiner miner " + Thread.currentThread() + " mined " + n + "invoices");
    }

    private void handleCompEndUser(WebElement e, int orgKey, boolean companion, Map<String, Integer> persons) {
        List<WebElement> childs = e.findElements(By.xpath(".//td"));
        String name = childs.get(0).getText().replaceAll("\\n.+", "");
        boolean isPublicOfficial = childs.get(1).getText().toUpperCase().contains("FUNKC");
        String addressRaw;
        float share = 0;
        if (companion) {
            addressRaw = childs.get(3).getText();
            share = Float.valueOf(childs.get(2).getText().replaceAll(".*?\\(", "").replaceAll("%\\)", "").replace(",", "."));
        } else {
            addressRaw = childs.get(2).getText();
        }
        if (!persons.containsKey(name)) {
            Address address = AddressParser.parseAddress(addressRaw);
            int AddressKey = 0;
            if (address != null) {
                AddressKey = DatabaseHandler.insertIntoAddressTable(con, address, addressRaw);
            }
            int endUserKey = DatabaseHandler.insertIntoPartyTable(
                    con,
                    false,
                    null,
                    name,
                    isPublicOfficial,
                    address == null ? null : AddressKey
            );
            persons.put(name, endUserKey);
        }
        if (companion) {
            DatabaseHandler.insertIntoOrganizationCompanionRelation(con, orgKey, persons.get(name), share);

        } else {
            DatabaseHandler.insertIntoOrganizationEndUserRelation(con, orgKey, persons.get(name));
        }

    }
}
