package tuke.fei;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String CHROME_DRIVER_PATH = "C:\\Users\\tomas.daneshjo\\Desktop\\dataMining\\chromedriver.exe";


    private static String hostName = "tssu-2018-assignment-1.database.windows.net";
    private static String dbName = "tssu-2018-assignment-1";
    private static String user = "admin_user";
    private static String password = "Password1";
    private static String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);

    public static void main(String... args) throws IOException, InterruptedException, SQLException {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        mineSvidnikFinstat();
    }

    private static void mineDigitalneMestoSnina() throws InterruptedException {
        CsvWriter csvWriter = new CsvWriter("snina.txt");

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        executorService.execute(new DigitalneMestoMiner("https://snina.digitalnemesto.sk/zverejnovanie/#snina/organizacia/snina/2018/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://snina.digitalnemesto.sk/zverejnovanie/#snina/organizacia/snina/2017/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://snina.digitalnemesto.sk/zverejnovanie/#snina/organizacia/snina/2016/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://snina.digitalnemesto.sk/zverejnovanie/#snina/organizacia/snina/2015/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://snina.digitalnemesto.sk/zverejnovanie/#snina/organizacia/snina/2014/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://snina.digitalnemesto.sk/zverejnovanie/#snina/organizacia/snina/2013/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.shutdown();
        executorService.awaitTermination(24, TimeUnit.HOURS);

        csvWriter.stop();
    }

    private static void mineDigitalneMestoSvidnik() throws InterruptedException {
        CsvWriter csvWriter = new CsvWriter("svidnik.txt");

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2018/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2017/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2016/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2015/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2014/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2013/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2012/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.execute(new DigitalneMestoMiner("https://zs-komenskeho-svidnik.digitalnemesto.sk/zverejnovanie/#zs-komenskeho-svidnik/organizacia/svidnik/2011/FDOD","dodavatelska", CHROME_DRIVER_PATH, csvWriter));
        executorService.shutdown();
        executorService.awaitTermination(24, TimeUnit.HOURS);

        csvWriter.stop();
    }

    private static void mineSvidnikFinstat() throws IOException, SQLException, InterruptedException {
        CsvReader csvReader = new CsvReader(
                "svidnik.txt",
                true,
                Pattern.compile("\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\""),
                Pattern.compile("\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\".*?\\\"\\t\\\"(.*?)\\\"")
        );
        Connection con1 = DriverManager.getConnection(url);
        Connection con2 = DriverManager.getConnection(url);
        Connection con3 = DriverManager.getConnection(url);
        Connection con4 = DriverManager.getConnection(url);
        Connection con5 = DriverManager.getConnection(url);
        Connection con6 = DriverManager.getConnection(url);
        Connection con7 = DriverManager.getConnection(url);
        Connection con8 = DriverManager.getConnection(url);

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con1, 2));
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con2, 2));
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con3, 2));
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con4, 2));
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con5, 2));
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con6, 2));
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con7, 2));
        executorService.execute(new FinstatMiner(CHROME_DRIVER_PATH, csvReader, con8, 2));
        executorService.shutdown();

        executorService.awaitTermination(24, TimeUnit.HOURS);
    }

    private static void mineStaraLubovna() throws InterruptedException {
        CsvWriter csvWriter = new CsvWriter("sl.txt");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.get("https://egov.staralubovna.sk/default.aspx?NavigationState=779:0:");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//input[contains(@class, ' x-form-text x-form-field x-form-num-field x-tbar-page-number')]")));
        driver.findElements(By.xpath("//input[contains(@class, ' x-form-text x-form-field x-form-num-field x-tbar-page-number')]")).get(1).clear();
        driver.findElements(By.xpath("//input[contains(@class, ' x-form-text x-form-field x-form-num-field x-tbar-page-number')]")).get(1).sendKeys("80");
        driver.findElements(By.xpath("//input[contains(@class, ' x-form-text x-form-field x-form-num-field x-tbar-page-number')]")).get(1).sendKeys(Keys.RETURN);
        Thread.sleep(6000);

        while(check((ChromeDriver) driver)){
            Thread.sleep(1000);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("UpdateProgress")));
            List<WebElement> linklist = ((ChromeDriver) driver).findElementsByXPath("//div[contains(@class, 'DetailIcon')]");
            Thread.sleep(500);

            for (WebElement e : linklist) {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//button[contains(text(), 'Zavr')]")));
                Actions actions = new Actions(driver);
                actions.moveToElement(e);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("UpdateProgress")));

                boolean gote=false;
                try{
                    e.click();
                    gote=true;
                }catch(Exception exp){

                    Thread.sleep(2000);
                    try{
                        e.click();
                        gote=true;
                    }
                    catch(org.openqa.selenium.StaleElementReferenceException ex){
                        gote=false;
                    }
                }

                //Thread.sleep(900);
                if(gote==true) {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("WM_winDet144102_Det_tc_ttS144102031")));
                    String cisloFaktury = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102002_Lbl")).getText();
                    String predmet = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102010_Lbl")).getText();
                    String ico = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102005_Lbl")).getText();
                    String dodavatel = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102004_Lbl")).getText();
                    String adresa = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102026_Lbl")).getText();
                    String typOsoby = "";
                    String suma = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102006_Lbl")).getText();
                    String datumZverejnenia = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102021_Lbl")).getText();
                    String datumVystavenia = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102014_Lbl")).getText();
                    String datumSplatnosti = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102015_Lbl")).getText();
                    String datumDorucenia = "";
                    String datumUhrady = driver.findElement(By.id("WM_winDet144102_Det_tc_S144102024_Lbl")).getText();
                    String datumStorna = "";

                    csvWriter.writeln(
                            "\"" + cisloFaktury + "\"\t" +
                                    "\"" + predmet + "\"\t" +
                                    "\"" + ico + "\"\t" +
                                    "\"" + dodavatel + "\"\t" +
                                    "\"" + adresa + "\"\t" +
                                    "\"" + typOsoby + "\"\t" +
                                    "\"" + suma + "\"\t" +
                                    "\"" + datumZverejnenia + "\"\t" +
                                    "\"" + datumVystavenia + "\"\t" +
                                    "\"" + datumSplatnosti + "\"\t" +
                                    "\"" + datumDorucenia + "\"\t" +
                                    "\"" + datumUhrady + "\"\t" +
                                    "\"" + datumStorna + "\"\t"
                    );
                    driver.findElement(By.xpath("//button[contains(text(), 'Zavr')]")).click();
                }
            }
            Thread.sleep(1500);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("UpdateProgress")));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'x-btn-text x-tbar-page-next')]")));
            try{driver.findElement(By.xpath("//button[contains(@class, 'x-btn-text x-tbar-page-next')]")).click();}catch(Exception exp){
                Thread.sleep(1000);
                driver.findElement(By.xpath("//button[contains(@class, 'x-btn-text x-tbar-page-next')]")).click();
            }

        }
        csvWriter.stop();
    }

    private static boolean check(ChromeDriver driver){
        String s = driver.findElement(By.className("x-paging-info")).getText();
        Pattern outof = Pattern.compile("([0-9]+)[\\D]*([0-9]+)[\\D]*([0-9]+)");
        Matcher matcheroutof = outof.matcher(s);
        matcheroutof.find();
        //System.out.println(matcheroutof.group(2));
        //System.out.println(matcheroutof.group(3));
        return Integer.parseInt(matcheroutof.group(2))<Integer.parseInt(matcheroutof.group(3));
    }
}
