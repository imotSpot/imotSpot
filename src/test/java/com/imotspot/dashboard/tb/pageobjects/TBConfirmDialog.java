package com.imotspot.dashboard.tb.pageobjects;

import com.imotspot.dashboard.view.reports.ReportsView;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;

public class TBConfirmDialog extends TestBenchTestCase {

    private final WindowElement scope;

    public TBConfirmDialog(WebDriver driver) {
        setDriver(driver);
        scope = $(WindowElement.class).id(ReportsView.CONFIRM_DIALOG_ID);
    }

    public void discard() {
        $(ButtonElement.class).caption("Discard Changes").first().click();
    }

}
