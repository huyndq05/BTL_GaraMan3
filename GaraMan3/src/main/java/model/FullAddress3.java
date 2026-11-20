package model;

public class FullAddress3 {
    private int id;
    private String district;
    private String province;
    private String detail;

    public FullAddress3() {}

    public FullAddress3(int id, String detail, String district, String province) {
        this.id = id;
        this.detail = detail;
        this.district = district;
        this.province = province;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (detail != null && !detail.trim().isEmpty()) sb.append(detail.trim());
        if (district != null && !district.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district.trim());
        }
        if (province != null && !province.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(province.trim());
        }
        return sb.toString();
    }
}
