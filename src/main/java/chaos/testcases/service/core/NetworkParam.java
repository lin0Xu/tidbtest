package chaos.testcases.service.core;

public class NetworkParam {

    private String device;
    private String sport;
    private String dport;
    private String type;
    private String lossPercent;
    private String delayTime;
    private String src;
    private String dst;

    /* @Params: bwCeiling: 带宽上限 */
    private String bwCeiling;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDport() {
        return dport;
    }

    public void setDport(String dport) {
        this.dport = dport;
    }

    public String getLossPercent() {
        return lossPercent;
    }

    public void setLossPercent(String lossPercent) {
        this.lossPercent = lossPercent;
    }

    public String getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(String delayTime) {
        this.delayTime = delayTime;
    }

    public String getBwCeiling() {
        return bwCeiling;
    }

    public void setBwCeiling(String bwCeiling) {
        this.bwCeiling = bwCeiling;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

}
