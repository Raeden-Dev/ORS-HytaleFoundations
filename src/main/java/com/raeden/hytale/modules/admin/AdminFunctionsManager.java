package com.raeden.hytale.modules.admin;

import com.raeden.hytale.HytaleFoundations;

public class AdminFunctionsManager {
    private final ReportManager reportManager;
    private final PunishmentManager punishmentManager;

    public AdminFunctionsManager(HytaleFoundations hytaleFoundations) {
        reportManager = new ReportManager(hytaleFoundations);
        punishmentManager = new PunishmentManager(hytaleFoundations);
    }

    public ReportManager getReportManager() {return reportManager;}
    public PunishmentManager getPunishmentManager() {return punishmentManager;}
}
