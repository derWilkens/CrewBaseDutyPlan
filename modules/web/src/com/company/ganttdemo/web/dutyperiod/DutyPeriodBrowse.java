/*
 * Copyright (c) 2017 gantt-demo
 */
package com.company.ganttdemo.web.dutyperiod;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import org.tltv.gantt.Gantt;
import org.tltv.gantt.client.shared.AbstractStep;
import org.tltv.gantt.client.shared.Step;

import com.company.ganttdemo.entity.DutyPeriod;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.SplitPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Component.Event;
/**
 * @author Christian
 */
public class DutyPeriodBrowse extends AbstractLookup {
	 private Gantt gantt;
	 
    /**
     * The {@link CollectionDatasource} instance that loads a list of {@link DutyPeriod} records
     * to be displayed in {@link DutyPeriodBrowse#dutyPeriodsTable} on the left
     */
    @Inject
    private CollectionDatasource<DutyPeriod, UUID> dutyPeriodsDs;

    /**
     * The {@link Datasource} instance that contains an instance of the selected entity
     * in {@link DutyPeriodBrowse#dutyPeriodsDs}
     * <p/> Containing instance is loaded in {@link CollectionDatasource#addItemChangeListener}
     * with the view, specified in the XML screen descriptor.
     * The listener is set in the {@link DutyPeriodBrowse#init(Map)} method
     */
    @Inject
    private Datasource<DutyPeriod> dutyPeriodDs;

    /**
     * The {@link Table} instance, containing a list of {@link DutyPeriod} records,
     * loaded via {@link DutyPeriodBrowse#dutyPeriodsDs}
     */
    @Inject
    private Table<DutyPeriod> dutyPeriodsTable;

    /**
     * The {@link BoxLayout} instance that contains components on the left side
     * of {@link SplitPanel}
     */
    @Inject
    private BoxLayout lookupBox;

    /**
     * The {@link BoxLayout} instance that contains buttons to invoke Save or Cancel actions in edit mode
     */
    @Inject
    private BoxLayout actionsPane;

    /**
     * The {@link FieldGroup} instance that is linked to {@link DutyPeriodBrowse#dutyPeriodDs}
     * and shows fields of the selected {@link DutyPeriod} record
     */
    @Inject
    private FieldGroup fieldGroup;

    /**
     * The {@link RemoveAction} instance, related to {@link DutyPeriodBrowse#dutyPeriodsTable}
     */
    @Named("dutyPeriodsTable.remove")
    private RemoveAction dutyPeriodsTableRemove;

    @Inject
    private DataSupplier dataSupplier;

    /**
     * {@link Boolean} value, indicating if a new instance of {@link DutyPeriod} is being created
     */
    private boolean creating;

    @Override
    public void init(Map<String, Object> params) {
    	createGantt();
    	ganttListener = null;
    	/*
    	 *Wird noch gebraucht, wenn eine Table aktualisiert werden soll... 
    	 */
//        Component wrapper = UriFragmentWrapperFactory.wrapByUriFragment(UI
//                .getCurrent().getPage().getUriFragment(), gantt);
//        if (wrapper instanceof GanttListener) {
//            ganttListener = (GanttListener) wrapper;
//        }
        /*
         * Adding {@link com.haulmont.cuba.gui.data.Datasource.ItemChangeListener} to {@link dutyPeriodsDs}
         * The listener reloads the selected record with the specified view and sets it to {@link dutyPeriodDs}
         */
        dutyPeriodsDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                DutyPeriod reloadedItem = dataSupplier.reload(e.getDs().getItem(), dutyPeriodDs.getView());
                dutyPeriodDs.setItem(reloadedItem);
                
                //refreshStep(reloadedItem);
                AbstractStep step =gantt.getStep(reloadedItem.getUuid().toString());
                gantt.markStepDirty(step);//funktioniert nicht, hat keine Auswirkung
                
                if (ganttListener != null && step instanceof Step) {
                    ganttListener.stepModified((Step) step);
                }
            }
        });

        /*
         * Adding {@link CreateAction} to {@link dutyPeriodsTable}
         * The listener removes selection in {@link dutyPeriodsTable}, sets a newly created item to {@link dutyPeriodDs}
         * and enables controls for record editing
         */
        dutyPeriodsTable.addAction(new CreateAction(dutyPeriodsTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity newItem, Datasource parentDs, Map<String, Object> params) {
                dutyPeriodsTable.setSelected(Collections.emptyList());
                dutyPeriodDs.setItem((DutyPeriod) newItem);
                refreshOptionsForLookupFields();
                enableEditControls(true);
            }
        });

        /*
         * Adding {@link EditAction} to {@link dutyPeriodsTable}
         * The listener enables controls for record editing
         */
        dutyPeriodsTable.addAction(new EditAction(dutyPeriodsTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                if (dutyPeriodsTable.getSelected().size() == 1) {
                    refreshOptionsForLookupFields();
                    enableEditControls(false);
                }
            }
        });

        /*
         * Setting {@link RemoveAction#afterRemoveHandler} for {@link dutyPeriodsTableRemove}
         * to reset record, contained in {@link dutyPeriodDs}
         */
        dutyPeriodsTableRemove.setAfterRemoveHandler(removedItems -> dutyPeriodDs.setItem(null));

        disableEditControls();
        
        
        ComponentContainer cc = (ComponentContainer) WebComponentsHelper.unwrap(this.getComponent("ganttBox"));
        cc.addComponent(gantt);
        
    }

    private void refreshOptionsForLookupFields() {
        for (Component component : fieldGroup.getOwnComponents()) {
            if (component instanceof LookupField) {
                CollectionDatasource optionsDatasource = ((LookupField) component).getOptionsDatasource();
                if (optionsDatasource != null) {
                    optionsDatasource.refresh();
                }
            }
        }
    }

    /**
     * Method that is invoked by clicking Ok button after editing an existing or creating a new record
     */
    public void save() {
        if (!validate(Collections.singletonList(fieldGroup))) {
            return;
        }
        getDsContext().commit();

        DutyPeriod editedItem = dutyPeriodDs.getItem();
        if (creating) {
            dutyPeriodsDs.includeItem(editedItem);
        } else {
            dutyPeriodsDs.updateItem(editedItem);
        }
        dutyPeriodsTable.setSelected(editedItem);
        refreshStep(editedItem);
        disableEditControls();
    }

    /**
     * Method that is invoked by clicking Cancel button, discards changes and disables controls for record editing
     */
    public void cancel() {
        DutyPeriod selectedItem = dutyPeriodsDs.getItem();
        if (selectedItem != null) {
            DutyPeriod reloadedItem = dataSupplier.reload(selectedItem, dutyPeriodDs.getView());
            dutyPeriodsDs.setItem(reloadedItem);
        } else {
            dutyPeriodDs.setItem(null);
        }

        disableEditControls();
    }

    /**
     * Enabling controls for record editing
     * @param creating indicates if a new instance of {@link DutyPeriod} is being created
     */
    private void enableEditControls(boolean creating) {
        this.creating = creating;
        initEditComponents(true);
        fieldGroup.requestFocus();
    }

    /**
     * Disabling editing controls
     */
    private void disableEditControls() {
        initEditComponents(false);
        dutyPeriodsTable.requestFocus();
    }

    /**
     * Initiating edit controls, depending on if they should be enabled/disabled
     * @param enabled if true - enables editing controls and disables controls on the left side of the splitter
     *                if false - visa versa
     */
    private void initEditComponents(boolean enabled) {
        fieldGroup.setEditable(enabled);
        actionsPane.setVisible(enabled);
        lookupBox.setEnabled(!enabled);
    }
    
    /*
     * Hier fängt der ganze Gantt-Kram an..
     */
    private void createGantt() {
        gantt = new Gantt();
        gantt.setWidth(100, Unit.PERCENTAGE);
        gantt.setHeight(500, Unit.PIXELS);
        gantt.setResizableSteps(true);
        gantt.setMovableSteps(true);
        gantt.addAttachListener(ganttAttachListener);
        //gantt.setTimeZone(getDefaultTimeZone());
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

        for (Step step : getStepsFromDatasource()) {
			gantt.addStep(step);
		} 
        
        
        gantt.addStep(step1);
        
        gantt.addClickListener(new Gantt.ClickListener() {

            @Override
            public void onGanttClick(org.tltv.gantt.Gantt.ClickEvent event) {
//                if (event.getDetails().isDoubleClick()) {
//                    Notification.show(String.format("Double Click on Step %s", event.getStep().getCaption()),
//                            Type.TRAY_NOTIFICATION);
//                }
//                if (MouseButton.RIGHT.equals(event.getDetails().getButton())) {
//                    Notification.show(String.format("Right Click on Step %s", event.getStep().getCaption()),
//                            Type.TRAY_NOTIFICATION);
//                } else {
//                    openStepEditor(event.getStep());
//                }
                
            }
        });
    }
    
    private Collection<Step> getStepsFromDatasource() {
		List<Step> stepList = new ArrayList<Step>();
		dutyPeriodsDs.refresh();
		for (DutyPeriod dutyPeriod : dutyPeriodsDs.getItems()) {
			Step step = new Step(dutyPeriod.getLastname());
			step.setUid(dutyPeriod.getUuid().toString());
			step.setStartDate(dutyPeriod.getBeginDate());
			step.setEndDate(dutyPeriod.getEndDate());
			stepList.add(step);
		}
		return stepList;
	}

	private GanttListener ganttListener;

    private AttachListener ganttAttachListener = new AttachListener() {

        @Override
        public void attach(AttachEvent event) {
            //syncLocaleAndTimezone();
        }
    };
    
    private void refreshStep(DutyPeriod dutyPeriod){
    	for (Step step : gantt.getSteps()) {
			if (step.getUid().equals(dutyPeriod.getUuid().toString())){
				step.setCaption(dutyPeriod.getLastname());
				step.setStartDate(dutyPeriod.getBeginDate());
				step.setEndDate(dutyPeriod.getEndDate());
				gantt.markStepDirty(step);
			}
		}
    }
    
    
    public interface GanttListener {

        void stepModified(Step step);

        void stepDeleted(Step step);

        void stepMoved(Step step, int newStepIndex, int oldStepIndex);
    }
}