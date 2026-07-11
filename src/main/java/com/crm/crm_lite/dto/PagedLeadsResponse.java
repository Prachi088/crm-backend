package com.crm.crm_lite.dto;

import com.crm.crm_lite.model.Lead;

import java.util.List;

public class PagedLeadsResponse {

    private List<Lead> leads;
    private int        page;
    private int        size;
    private long       totalElements;
    private int        totalPages;
    private boolean    last;

    public PagedLeadsResponse(List<Lead> leads, int page, int size,
                               long totalElements, int totalPages, boolean last) {
        this.leads         = leads;
        this.page          = page;
        this.size          = size;
        this.totalElements = totalElements;
        this.totalPages    = totalPages;
        this.last          = last;
    }

    public List<Lead> getLeads()          { return leads; }
    public int        getPage()           { return page; }
    public int        getSize()           { return size; }
    public long       getTotalElements()  { return totalElements; }
    public int        getTotalPages()     { return totalPages; }
    public boolean    isLast()            { return last; }
}