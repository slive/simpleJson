//==============================================================================
//
//	@author Slive
//	@date  2020-11-27
//
//==============================================================================
package slive.simpleJson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 * 
 */
public class DemoDto {

    private String a;

    private int b;

    private Integer d;

    private List<String> el;

    private ISubDemoDto sdt;

    private ISubDemoDto[] sl;

    private Map<String, SubDemoDto> sdm;

    private boolean b1;

    private Boolean bb2;

    public Boolean getBb2() {
        return bb2;
    }

    public void setBb2(Boolean bb2) {
        this.bb2 = bb2;
    }

    public boolean isB1() {
        return b1;
    }

    public void setB1(boolean b1) {
        this.b1 = b1;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
    }

    public List<String> getEl() {
        return el;
    }

    public void setEl(List<String> el) {
        this.el = el;
    }

    public ISubDemoDto getSdt() {
        return sdt;
    }

    public void setSdt(ISubDemoDto sdt) {
        this.sdt = sdt;
    }

    public ISubDemoDto[] getSl() {
        return sl;
    }

    public void setSl(ISubDemoDto[] sl) {
        this.sl = sl;
    }

    public Map<String, SubDemoDto> getSdm() {
        return sdm;
    }

    public void setSdm(Map<String, SubDemoDto> sdm) {
        this.sdm = sdm;
    }

    @Override
    public String toString() {
        StringBuilder sbd = new StringBuilder();
        sbd.append("{a:").append(getA()).append(",");
        sbd.append("b:").append(getB()).append(",");
        sbd.append("d:").append(getD()).append(",");
        sbd.append("el:").append(getEl()).append(",");
        sbd.append("sdt:").append(getSdt()).append(",");
        sbd.append("sdm:").append(getSdm()).append(",");
        sbd.append("sl:").append(Arrays.toString(getSl())).append("}");
        return sbd.toString();
    }
}
