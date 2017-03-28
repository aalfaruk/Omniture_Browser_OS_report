package GenerateReport;

/**
 * Created by aalfaruk on 3/28/2017.
 */
public class BannerHBC {

    public enum ReportSuiteID {

        LT("ltprod", "LT"),
        BAY("hbcprod", "BAY" ),
        LABAIE("hbclabaie", "LaBaie"),
        SAKS("sakscomlive", "SAKS"),
        OFFFIFTH("sakscomnewoff5thlive", "OFFFIFTH")

        //In future if we need Mobile or Saks App to be included into the Report

       /* HBCBAYMOBILE("hbcbaymobile", ),
        LTMOBILE("hbcltmobile");*/
        ;
        ReportSuiteID(final String v, String sn) {
            this.value = v;
            this.sheetName = sn;
        }

        public String getvalue() {
            return value;
        }

        public String getSheetName(){
            return sheetName;
        }

        private String value;

        private String sheetName;

    }
}
