package com.fieldsync.model;

import java.time.LocalDateTime;

public class Reservation {

    private int id;
    private int fieldId;
    private int reservedBy;
    private Integer approvedBy; // Nullable until approved or rejected
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // PENDING_APPROVAL, CONFIRMED, CANCELLED, COMPLETED, MAINTENANCE
    private String purpose;
    private LocalDateTime createdAt;

    public Reservation() {}

    public Reservation(int id, int fieldId, int reservedBy, Integer approvedBy, 
                       LocalDateTime startTime, LocalDateTime endTime, 
                       String status, String purpose, LocalDateTime createdAt) {
        this.id = id;
        this.fieldId = fieldId;
        this.reservedBy = reservedBy;
        this.approvedBy = approvedBy;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.purpose = purpose;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }

    public int getReservedBy() { return reservedBy; }
    public void setReservedBy(int reservedBy) { this.reservedBy = reservedBy; }

    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Reservation[ID=%d, FieldID=%d, ReservedBy=%d, ApprovedBy=%s, Status='%s', Start=%s, End=%s]",
                id, fieldId, reservedBy, approvedBy, status, startTime, endTime);
    }
}