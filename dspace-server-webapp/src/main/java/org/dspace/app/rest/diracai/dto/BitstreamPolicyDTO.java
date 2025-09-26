package org.dspace.app.rest.diracai.dto;

import java.util.Date;

public class BitstreamPolicyDTO {

    private String name;
    private String description;
    private String policyType;
    private String action;
    private String startDate;
    private String endDate;
    private Integer pageStart;
    private Integer pageEnd;
    private boolean download;
    private boolean print;

    // Constructors
    public BitstreamPolicyDTO() {
    }

    public BitstreamPolicyDTO(String name, String description, String policyType, String action,
                              String startDate, String endDate, Integer pageStart, Integer pageEnd,
                              boolean download, boolean print) {
        this.name = name;
        this.description = description;
        this.policyType = policyType;
        this.action = action;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.download = download;
        this.print = print;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public void setPageStart(Integer pageStart) {
        this.pageStart = pageStart;
    }

    public Integer getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(Integer pageEnd) {
        this.pageEnd = pageEnd;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public boolean isPrint() {
        return print;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }
}
