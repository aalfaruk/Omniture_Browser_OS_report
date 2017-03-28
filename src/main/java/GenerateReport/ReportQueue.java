package GenerateReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import com.google.gson.Gson;
import biz.source_code.base64Coder.Base64Coder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by aalfaruk on 3/28/2017.
 */
public class ReportQueue {

    private static final String ServiceEndPoint = "https://api.omniture.com/admin/1.4/rest/";
    private static final String MethodQueue = "?method=Report.Queue";
    private static final String MethodGet = "?method=Report.Get";
    private static final String QueueJSON = "{\"reportDescription\":{\"reportSuiteID\":\"REPORT_SUITE_ID\",\"dateFrom\":\"START_DATE\",\"dateTo\":\"END_DATE\",\"metrics\":[{\"id\":\"revenue\"},{\"id\":\"visits\"}],\"sortBy\":\"revenue\",\"elements\":[{\"id\":\"browser\",\"top\":\"15\",\"startingWith\":\"1\"},{\"id\":\"operatingSystem\",\"startingWith\":\"1\"}]}}";

    private static final String GetReportSuiteId = "\"REPORT_SUITE_ID\":\"yyyyyy\"}";
    private static final String GetJSON = "{\"reportID\":\"xxxxx\"}";

    private static byte[] generateNonce() {
        String nonce = Long.toString(new Date().getTime());
        return nonce.getBytes();
    }

    private static String generateTimestamp() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatter.format(new Date());
    }

    private static String base64Encode(byte[] bytes) {
        return Base64Coder.encodeLines(bytes);
    }

    private static synchronized String getBase64Digest(byte[] nonce,
                                                       byte[] created, byte[] password) {
        try {
            MessageDigest messageDigester = MessageDigest.getInstance("SHA-1");
            messageDigester.reset();
            messageDigester.update(nonce);
            messageDigester.update(created);
            messageDigester.update(password);
            return base64Encode(messageDigester.digest());
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHeader() throws UnsupportedEncodingException {
        byte[] nonceB = generateNonce();
        String nonce = base64Encode(nonceB);
        String created = generateTimestamp();
        String password64 = getBase64Digest(nonceB, created.getBytes("UTF-8"),
                "f290f636f5b51c087353af2596e5fe60".getBytes("UTF-8"));
        StringBuffer header = new StringBuffer("UsernameToken Username=\"");
        header.append("aalfaruk:HBC");
        header.append("\", ");
        header.append("PasswordDigest=\"");
        header.append(password64.trim());
        header.append("\", ");
        header.append("Nonce=\"");
        header.append(nonce.trim());
        header.append("\", ");
        header.append("Created=\"");
        header.append(created);
        header.append("\"");
        return header.toString();
    }

    public static String post(String url, String method, String jsonRequest) {
        String json = "";

        // Ensure Not NULL

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){

            HttpPost request = new HttpPost(url + method);
            StringEntity entity = new StringEntity(jsonRequest, "UTF-8");
            request.addHeader("content-type", "application/json");

            String wsse = ReportQueue.getHeader();
            System.out.println(wsse);
            request.addHeader("X-WSSE", wsse);

            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            HttpEntity respEntity = response.getEntity();
            if (respEntity != null) {
                json = EntityUtils.toString(respEntity, "UTF-8");
            }
            System.out.println(json);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
        }

        return json;
    }

    public static String get(String servicUrl) {
        String json = "";

        // Ensure Not NULL

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){

            HttpGet request = new HttpGet(servicUrl);
            request.addHeader("content-type", "application/json");
            request.addHeader("X-WSSE", ReportQueue.getHeader());
            HttpResponse result = httpClient.execute(request);
            json = EntityUtils.toString(result.getEntity(), "UTF-8");
            System.out.println(json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public class elementsObj {
        String id;
        String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class reportsuiteObj {
        String id;
        String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class metricObj {
        String id;
        String name;
        String type;
        String decimals;
        String latency;
        String current;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getDecimals() {
            return decimals;
        }

        public String getLatency() {
            return latency;
        }

        public String getCurrent() {
            return current;
        }
    }

    public class response {
        reportObj report;

        public reportObj getReport() {
            return report;
        }
    }

    public class reportObj {
        String type;
        String period;
        List<elementsObj> elements;
        reportsuiteObj reportSuite;
        List<metricObj> metrics;
        List<dataObj> data;
        List<String> totals;

        public String getType() {
            return type;
        }

        public String getPeriod() {
            return period;
        }

        public List<elementsObj> getElements() {
            return elements;
        }

        public reportsuiteObj getReportSuite() {
            return reportSuite;
        }

        public List<metricObj> getMetrics() {
            return metrics;
        }

        public List<dataObj> getData() {
            return data;
        }

        public List<String> getTotals() {
            return totals;
        }

    }

    public class osObj {
        String name;
        String url;
        List<String> counts;
        double percentage = 0.0d;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public List<String> getCounts() {
            return counts;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
    }

    public class dataObj {
        String name;
        String url;
        List<String> counts;
        List<osObj> breakdown;

        double percentage = 0.0d;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public List<String> getCounts() {
            return counts;
        }

        public List<osObj> getBreakdown() {
            return breakdown;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
    }

    private static String getStartDate(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, -1);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDate=df.format(date.getTime());
        return startDate;
    }

    private static String getEndDate(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.DAY_OF_MONTH, -1);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String endDate=df.format(date.getTime());
        return endDate;
    }


    private static void generateExcelReport(String reportResult, BannerHBC.ReportSuiteID reportSuiteID) {

        Gson gson = new Gson();
        response myresponse = gson.fromJson(reportResult, response.class);

        List<Key_Value_Pair> result2 = new ArrayList<Key_Value_Pair>();
        double totalRevenue = Double.parseDouble(myresponse.getReport().getTotals().get(0));
        System.out.println("Total Revenue : " + totalRevenue);

        for (dataObj d : myresponse.getReport().getData()) {
            double B_Revenue = Double.parseDouble(d.counts.get(0));
            System.out.println("Browser " + d.getName() + " Browser Revenue : " + B_Revenue);

            double pctFilterBrowser = (B_Revenue / totalRevenue) * 100.0d;
            d.setPercentage(pctFilterBrowser);
            System.out.println("pctFilterBrowser" + d.getName() + " value: " + pctFilterBrowser);

            for (osObj os : d.getBreakdown()) {
                double OS_Revenue = Double.parseDouble(os.getCounts().get(0));
                double pctFilterBrowserOS = (OS_Revenue / B_Revenue) * 100.0d;
                os.setPercentage(pctFilterBrowserOS);
                System.out.println("pctFilterBrowserOS" + os.getName() + " value: " + pctFilterBrowserOS);

                String key = d.getName() + "_" + os.getName();
                double value = (pctFilterBrowserOS * pctFilterBrowser)/100;
                Key_Value_Pair kv = new Key_Value_Pair(key, value, d.getName(), os.getName());
                result2.add(kv);
                break;

            }
        }

        Collections.sort(result2);
        for (Key_Value_Pair kv : result2) {
            System.out.println("Browsers and Operating Systems: " + kv.getKey() + " value: " + kv.getValue());
            ReportQueue.excelWriter(result2, reportSuiteID.getSheetName());
            reportSuiteID.getSheetName();
        }

    }


    private static void generateReport(BannerHBC.ReportSuiteID reportSuiteID) {
        try {
            String jsonBody = QueueJSON.replace("REPORT_SUITE_ID", reportSuiteID.getvalue());
            jsonBody = jsonBody.replace("START_DATE", getStartDate());
            jsonBody = jsonBody.replace("END_DATE", getEndDate());
            String reportID = ReportQueue.post(ServiceEndPoint, MethodQueue,jsonBody);
            String id = reportID.replace("{\"reportID\":", "");

            id = id.replace("}", "");
            System.out.println("ReportID is " + id);
            String jsonPost = GetJSON.replace("xxxxx", id);
            System.out.println(jsonPost);
            String reportResult = "";
            while (true) {
                Thread.sleep(5000);
                reportResult = ReportQueue.post(ServiceEndPoint, MethodGet, jsonPost);
                System.out.println(reportResult);
                if (reportResult.contains("metrics")) {
                    break;
                }
            }

            if ((reportResult != null) && (!reportResult.isEmpty())
                    && (reportResult.contains("metrics"))) {
                generateExcelReport(reportResult, reportSuiteID);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public static void main(String[] args) {

        String reportSuiteID = GetReportSuiteId.replace("\"REPORT_SUITE_ID\":", "");
        reportSuiteID = reportSuiteID.replace("}", "");
        for (BannerHBC.ReportSuiteID reportId : BannerHBC.ReportSuiteID.values()) {
            generateReport(reportId);
            System.out.println(reportId);
        }

    }


    private static void excelWriter(List<Key_Value_Pair> kv, String sn) {

        try {
            String fileName = "Browser_OS compatibility.xls";
            File f1 = new File("");
            System.out.println(f1.getAbsolutePath());
            FileInputStream fs = new FileInputStream(fileName);
            HSSFWorkbook workbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = workbook.getSheet(sn);

            int rowNumber = 0;
            int lastRow = sheet.getLastRowNum();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext() && (rowNumber < lastRow)) {
                Row row = rowIterator.next();
                int lastCell = row.getLastCellNum();
                rowNumber++;
                int colNumber = 0;
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext() && (colNumber < lastCell)) {
                    Cell cell = cellIterator.next();
                    colNumber++;
                    System.out.println("| Row : " + rowNumber + " | Column : " + colNumber);

                    if ((rowNumber >= 8) && (rowNumber <= 17)) {
                        if (colNumber == 3) {
                            cell.setCellValue(kv.get(rowNumber - 8).getbrowsersName());
                        }
                        else if (colNumber == 4) {
                            cell.setCellValue(kv.get(rowNumber - 8).getoperatingSystems());
                        }
                        else if (colNumber == 5) {
                            cell.setCellValue(rowNumber - 7);
                        }
						/*else if (colNumber == 6) {
							cell.setCellValue(kv.get(rowNumber - 8).getValue());
						}*/
                        else if (colNumber > 6)
                        {
                            break;
                        }
                    }
                }
            } //end while
            fs.close();
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
            System.out.println(fileOut + "is Successfully written");
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }


}
