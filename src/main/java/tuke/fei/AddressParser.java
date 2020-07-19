package tuke.fei;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressParser {

    /**
     * Parse address using https://geocode.xyz
     * @param address raw address to parse
     * @return parsed address
     */
    public static Address parseAddress(String address) {
        Address result = new Address();
        try {
            URL url = new URL("https://geocode.xyz?locate=" +  StringUtils.stripAccents(address.replaceAll(" ", "%20")) + "&json=1&auth=227101852922965348383x1151");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(60000);
            con.setReadTimeout(60000);
            con.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            String streetNumber = "";
            String city = "";
            String street = "";
            String postal = "";
            String country = "";
            try {
                streetNumber = JsonPath.read(sb.toString(), "$.standard.stnumber");
            } catch (Exception ex) {

            }
            try {
                city = JsonPath.read(sb.toString(), "$.standard.city");
            } catch (Exception ex) {

            }
            try {
                street = JsonPath.read(sb.toString(), "$.standard.addresst");
            } catch (Exception ex) {

            }
            try {
                postal = JsonPath.read(sb.toString(), "$.standard.postal");
            } catch (Exception ex) {

            }
            try {
                country = JsonPath.read(sb.toString(), "$.standard.countryname");
            } catch (Exception ex) {

            }
            result.setCity(city);
            result.setStreet(street + " " + streetNumber);
            result.setPostalCode(postal);
            result.setCountry(country);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
