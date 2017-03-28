package GenerateReport;

/**
 * Created by aalfaruk on 3/28/2017.
 */
public class Key_Value_Pair implements Comparable<Key_Value_Pair> {

    private String _key = "";
    private String _browsersName = "";
    private String _operationSystems ="";
    private double _value = 0.0d;
    public Key_Value_Pair(String k, double v, String bn, String os)
    {
        _key = k;
        _browsersName = bn;
        _operationSystems = os;
        _value = v;
    }
    public String getKey() {
        return _key;
    }
    public String getbrowsersName(){
        return _browsersName;
    }

    public String getoperatingSystems(){
        return _operationSystems;
    }

    public double getValue() {
        return _value;
    }

    @Override
    public int compareTo(Key_Value_Pair another) {
        if (this.getValue()< another.getValue()){
            return 1;
        }else{
            return -1;
        }
    }

}
