import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class CurrencyConv {
    public static void main(String[] args) {
        HashMap<Integer, String> currencyCodes = new HashMap<Integer, String>();
        
        // Currency codes
        currencyCodes.put(1, "INR");
        currencyCodes.put(2, "WON");
        currencyCodes.put(3, "USD");
        currencyCodes.put(4, "EUR");
        currencyCodes.put(5, "CAD");
        currencyCodes.put(6, "HKO");
        
        String fromCode, toCode;
        double amount;

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Currency Converter");

        System.out.println("Currency Converter FROM?");
        System.out.println("1: INR (Indian Rupees) \t 2: WON (Korean Won) \t 3: USD (US Dollars) \t 4: EUR (European Euros)\t 5: CAD (Canadian Dollar)\t 6: HKO (Hong Kong Dollar)");
        fromCode = currencyCodes.get(sc.nextInt());

        System.out.println("Currency Converter TO?");
        System.out.println("1: INR (Indian Rupees) \t 2: WON (Korean Won) \t 3: USD (US Dollars) \t 4: EUR (European Euros)\t 5: CAD (Canadian Dollar)\t 6: HKO (Hong Kong Dollar)");
        toCode = currencyCodes.get(sc.nextInt());

        System.out.println("Amount you wish to convert");
        amount = sc.nextDouble();

        sendHttpGetRequest(fromCode, toCode, amount);
        System.out.println("Thank you for using Currency Converter");
    }

    private static void sendHttpGetRequest(String fromCode, String toCode, double amount) {
        try {
            String GET_URL = "https://api.exchangerate-api.com/v4/latest/" + fromCode;
            URL url = new URL(GET_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                double exchangeRate = parseExchangeRate(jsonResponse, toCode);
                double convertedAmount = amount * exchangeRate;

                System.out.println(amount + " " + fromCode + " = " + convertedAmount + " " + toCode);
            } else {
                System.out.println("GET request failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double parseExchangeRate(String jsonResponse, String toCode) {
        String searchString = "\"" + toCode + "\":";
        int startIndex = jsonResponse.indexOf(searchString) + searchString.length();
        int endIndex = jsonResponse.indexOf(",", startIndex);
        if (endIndex == -1) { // handle last element in JSON
            endIndex = jsonResponse.indexOf("}", startIndex);
        }
        String exchangeRateString = jsonResponse.substring(startIndex, endIndex).trim();
        return Double.parseDouble(exchangeRateString);
    }
}
