//==============================================================================
//
//	@author Slive
//	@date  2020-11-27
//
//==============================================================================
package slive.simpleJson;

import java.util.Arrays;


/**
 * 描述：
 * 
 */
public class SubDemoDto implements ISubDemoDto {

    private String sa;

    private int sb;

    private int[] sc;

    /**
     * @see ISubDemoDto#getSa()
     */
    @Override
    public String getSa() {
        return sa;
    }

    /**
     * @see ISubDemoDto#setSa(java.lang.String)
     */
    @Override
    public void setSa(String sa) {
        this.sa = sa;
    }

    /**
     * @see ISubDemoDto#getSb()
     */
    @Override
    public int getSb() {
        return sb;
    }

    /**
     * @see ISubDemoDto#setSb(int)
     */
    @Override
    public void setSb(int sb) {
        this.sb = sb;
    }

    /**
     * @see ISubDemoDto#getSc()
     */
    @Override
    public int[] getSc() {
        return sc;
    }

    /**
     * @see ISubDemoDto#setSc(int[])
     */
    @Override
    public void setSc(int[] sc) {
        this.sc = sc;
    }

    @Override
    public String toString() {
        StringBuilder sbd = new StringBuilder();
        sbd.append("{sa:").append(getSa()).append(",");
        sbd.append("sb:").append(getSb()).append(",");
        sbd.append("sc:").append(Arrays.toString(getSc())).append("}");
        return sbd.toString();
    }
}
