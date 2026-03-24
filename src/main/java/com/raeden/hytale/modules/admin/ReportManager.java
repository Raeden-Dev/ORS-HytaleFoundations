package com.raeden.hytale.modules.admin;

public class ReportManager {

    public static class PlayerReport {
        private IssueType issueType;
        private String reportedBy;
        private String worldReportedFrom;
        private String timeReported;
        private String description;
        private String readBy;
        private String readAt;
        private boolean solved;
    }
    public static class PlayerOffence {
        private OffenceType offenceType;
        private PunishmentType punishmentType;
        private String duration;
        private String punishedBy;
        private String punishedAt;
        private String comment;
    }
}
