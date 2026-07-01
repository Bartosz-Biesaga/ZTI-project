package com.hireme.dto;

import com.hireme.model.enums.ApplicationStatus;

public class ApplicationUpdateRequest {

    private ApplicationStatus status;
    private String note;

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
