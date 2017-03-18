/*
 * Copyright (c) 2016 gantt-demo
 */
package com.company.ganttdemo.web.screens;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComponentContainer;
import org.tltv.gantt.Gantt;
import org.tltv.gantt.client.shared.Step;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author yuriy
 */
public class Demo extends AbstractWindow {
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        ComponentContainer cc = (ComponentContainer) WebComponentsHelper.unwrap(this);

        Gantt gantt = new Gantt();
        gantt.setWidth(100, Sizeable.Unit.PERCENTAGE);
        gantt.setHeight(500, Sizeable.Unit.PIXELS);
        gantt.setResizableSteps(true);
        gantt.setMovableSteps(true);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        gantt.setStartDate(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        gantt.setEndDate(cal.getTime());

        cal.setTime(new Date());
        Step step1 = new Step("First step");
        step1.setStartDate(cal.getTime().getTime());
        cal.add(Calendar.MONTH, 2);
        step1.setEndDate(cal.getTime().getTime());

        gantt.addStep(step1);

        cc.addComponent(gantt);
    }
}