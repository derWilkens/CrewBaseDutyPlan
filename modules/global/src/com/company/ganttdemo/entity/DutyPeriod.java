/*
 * Copyright (c) 2017 gantt-demo
 */
package com.company.ganttdemo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;

/**
 * @author Christian
 */
@Table(name = "DEMO_DUTY_PERIOD")
@Entity(name = "demo$DutyPeriod")
public class DutyPeriod extends StandardEntity {
    private static final long serialVersionUID = 3266549911472902906L;

    @Column(name = "LASTNAME", length = 50)
    protected String lastname;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BEGIN_DATE")
    protected Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    protected Date endDate;

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}