package com.raeden.hytale.modules.admin;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.modules.chat.AffixManager;
import com.raeden.hytale.utils.TimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.core.config.ConfigManager.REPORT_LOG_FILE_NAME;
import static com.raeden.hytale.core.config.ConfigManager.REPORT_LOG_PATH;
import static com.raeden.hytale.utils.FileUtils.*;

public class ReportManager {
    private final HytaleFoundations hytaleFoundations;

    public ReportManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;

        createDirectory(REPORT_LOG_PATH, true);
    }

    public List<IssueReport> getAllIssueReports() {
        if(!Files.exists(REPORT_LOG_PATH)) return null;
        List<IssueReport> list = new ArrayList<>();
        try {
            for(File file : Objects.requireNonNull(REPORT_LOG_PATH.toFile().listFiles())) {
                if(file.toPath().endsWith(".json")) {
                    Type type = new TypeToken<IssueReport>() {}.getType();
                    list.add(loadJsonFile(file.getName(), file.toPath(), type, false));
                }
            }
        } catch (Exception e) {
            logError("getAllIssueReports", e);
        }
        return list;
    }

    public IssueReport getIssueReport(UUID id) {
        if(Files.exists(REPORT_LOG_PATH)) {
            Path reportFileName = REPORT_LOG_PATH.resolve(String.valueOf(id));
            if(Files.exists(reportFileName)) {
                Type type = new TypeToken<IssueReport>(){}.getType();
                return loadJsonFile(String.valueOf(id), REPORT_LOG_PATH, type, false);
            }
        }
        return null;
    }

    public void saveIssueReport(IssueReport issueReport, boolean isUpdate) {
        if(issueReport == null) return;
        String type = issueReport.getIssueType().name().toLowerCase();
        String fileName = REPORT_LOG_FILE_NAME
                .replace("{player}", issueReport.getReportedBy())
                .replace("{type}", type)
                .replace("{id}", String.valueOf(issueReport.getReportID()));
        File reportFile = new File(REPORT_LOG_PATH.toString(), fileName);

        if(isUpdate) {
            Path issueLogFile = REPORT_LOG_PATH.resolve(fileName);
            if(Files.exists(issueLogFile)) {
                try {
                    Files.delete(issueLogFile);
                } catch (Exception e) {
                    logError("saveIssueReport - isUpdated", e);
                    myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOG_UPDATE_FAIL,fileName, "report", fileName).getAnsiMessage());
                    return;
                }
            }
        }

        Path jsonFilePath = REPORT_LOG_PATH.resolve(issueReport.getReportID() + ".json");
        String jsonName = issueReport.getReportID() + ".json";
        saveJsonFile(jsonName, jsonFilePath, issueReport, !isUpdate);
        // Report .txt file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            writer.write("---- REPORT LOG ["+ issueReport.getReportID() + "] ----");
            writer.newLine();
            writer.write("STATUS: " + (issueReport.isSolved() ? "Solved" : "Unsolved"));
            writer.newLine();

            if(issueReport.isSolved()) {
                writer.write("Solved By: " + issueReport.getSolvedBy());
                writer.newLine();
            }
            if(issueReport.getTimeReported() != null && !issueReport.getTimeReported().isEmpty()) {
                writer.write("Read By: " + issueReport.getReadBy());
                writer.newLine();
                writer.write("Read At: " + issueReport.getReadAt());
                writer.newLine();
            }
            writer.write("Last Updated: " + TimeUtils.getTimeNow());

            writer.newLine();
            writer.write("---- REPORTER INFO ----");
            writer.newLine();
            writer.write("  Reported At: " + issueReport.getTimeReported());
            writer.newLine();
            writer.write("  Reported By: " + issueReport.getReportedBy());
            writer.newLine();
            writer.write("  World reported from: " + issueReport.getWorldReportedFrom());
            writer.newLine();

            writer.newLine();
            writer.write("---- ISSUE INFO ----");
            writer.newLine();
            writer.write("  Issue Type: " + issueReport.getIssueType().name().toLowerCase());
            writer.newLine();
            writer.write("  Issue Context: " + issueReport.getIssueContext().name().toLowerCase());
            writer.newLine();
            if(issueReport.getIssueSeverity() != null) {
                writer.write("  Issue Severity: " + issueReport.getIssueSeverity().name().toLowerCase());
                writer.newLine();
            }
            if(issueReport.getAbuserName() != null) {
                writer.write("  Abuser Name: " + issueReport.getAbuserName());
                writer.newLine();
            }
            writer.write("  Occurred At: " + issueReport.getIssueOccurTime());
            writer.newLine();
            writer.write("  Description: " + issueReport.getDescription());
            writer.newLine();
            writer.write("---- x ----");

            if(!isUpdate) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOG_EXPORT_SUCCESS,fileName, "report", fileName).getAnsiMessage());
            else myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOG_UPDATE_SUCCESS,fileName, "report", fileName).getAnsiMessage());

        } catch (Exception e) {
            logError("saveIssueReport", e);
            if(!isUpdate) myLogger.atSevere().log(LM.getConsoleMessage(LangKey.LOG_EXPORT_FAIL, "report", fileName).getAnsiMessage());
            else myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOG_UPDATE_FAIL,fileName, "report", fileName).getAnsiMessage());
        }
    }




    public static class IssueReport {
        @SerializedName("id")
        private final UUID reportID = UUID.randomUUID();
        @SerializedName("issue_type")
        private final IssueType issueType;
        @SerializedName("issue_context")
        private final IssueContext issueContext;
        @SerializedName("issue_severity")
        private final IssueSeverity issueSeverity;
        @SerializedName("reported_by")
        private final String reportedBy;
        @SerializedName("report_time")
        private final String timeReported;
        @SerializedName("world_reported_from")
        private final String worldReportedFrom;
        @SerializedName("abuser_name")
        private final String abuserName;
        @SerializedName("issue_occurrence_time")
        private final String issueOccurTime;
        @SerializedName("description")
        private final String description;
        @SerializedName("report_read_by")
        private String readBy;
        @SerializedName("report_read_at")
        private String readAt;
        @SerializedName("solved_by")
        private String solvedBy;
        @SerializedName("solved")
        private boolean solved;

        public IssueReport(
                IssueType issueType,
                IssueContext issueContext,
                IssueSeverity issueSeverity,
                String reportedBy,
                String timeReported,
                String worldReportedFrom,
                String abuserName,
                String issueOccurTime,
                String description
        ) {
            this.issueType = issueType;
            this.issueContext = issueContext;
            this.issueSeverity = issueSeverity;
            this.reportedBy = reportedBy;
            this.timeReported = timeReported;
            this.worldReportedFrom = worldReportedFrom;
            this.abuserName = abuserName;
            this.issueOccurTime = issueOccurTime;
            this.description = description;
            this.readBy = "None";
            this.readAt = "-";
            this.solvedBy = "None";
            this.solved = false;
        }

        public IssueType getIssueType() {return issueType;}
        public IssueContext getIssueContext() {return issueContext;}
        public IssueSeverity getIssueSeverity() {return issueSeverity;}
        public String getReportedBy() {return reportedBy;}
        public String getTimeReported() {return timeReported;}
        public String getWorldReportedFrom() {return worldReportedFrom;}
        public String getAbuserName() {return abuserName;}
        public String getIssueOccurTime() {return issueOccurTime;}
        public String getDescription() {return description;}

        public String getReadBy() {return readBy;}
        public void setReadBy(String readBy) {this.readBy = readBy;}
        public String getReadAt() {return readAt;}
        public void setReadAt(String readAt) {this.readAt = readAt;}
        public boolean isSolved() {return solved;}
        public void setSolved(boolean solved) {this.solved = solved;}
        public UUID getReportID() {return reportID;}
        public String getSolvedBy() {return solvedBy;}
        public void setSolvedBy(String solvedBy) {this.solvedBy = solvedBy;}
    }
    public static class PlayerOffence {
        private IssueContext issueContext;
        private PunishmentManager.PunishmentType punishmentType;
        private String duration;
        private String punishedBy;
        private String punishedAt;
        private String comment;

        public PlayerOffence() {}
        public PlayerOffence(IssueContext issueContext, PunishmentManager.PunishmentType punishmentType, String duration, String punishedBy,
                             String punishedAt, String comment) {
            this.issueContext = issueContext;
            this.punishmentType = punishmentType;
            this.duration = duration;
            this.punishedBy = punishedBy;
            this.punishedAt = punishedAt;
            this.comment = comment;
        }

        public IssueContext getIssueContext() {return issueContext;}
        public void setIssueContext(IssueContext issueContext) {this.issueContext = issueContext;}
        public PunishmentManager.PunishmentType getPunishmentType() {return punishmentType;}
        public void setPunishmentType(PunishmentManager.PunishmentType punishmentType) {this.punishmentType = punishmentType;}
        public String getDuration() {return duration;}
        public void setDuration(String duration) {this.duration = duration;}
        public String getPunishedBy() {return punishedBy;}
        public void setPunishedBy(String punishedBy) {this.punishedBy = punishedBy;}
        public String getPunishedAt() {return punishedAt;}
        public void setPunishedAt(String punishedAt) {this.punishedAt = punishedAt;}
        public String getComment() {return comment;}
        public void setComment(String comment) {this.comment = comment;}
    }

    public enum IssueType {
        PLAYER_ABUSE,
        STAFF_ABUSE,
        BUG,
        GLITCH,
        SERVER_LAG,
        PURCHASE,
        OTHER;
        public static IssueType fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                return OTHER;
            }
            try {
                return IssueType.valueOf(text.trim().toUpperCase());
            } catch (Exception e) {
                return OTHER;
            }
        }
    }

    public enum IssueContext {
        HACKING,
        XRAYING,
        BUG_ABUSE,
        GLITCH_ABUSE,
        ADVERTISEMENT,
        TOXIC_BEHAVIOUR,
        RACISM,
        SPAMMING,
        HARASSMENT,
        IMPERSONATION,
        HATE_SPEECH,
        TROLLING,
        EXTORTION,
        THREATS,
        GRIEFING,
        CUSSING,
        PURCHASE_FAILED,
        PURCHASE_NOT_RECEIVED,
        PURCHASE_MISSING_ITEMS,
        PURCHASE_REFUND,
        SERVER_PROBLEM,
        OTHER;
        public static IssueContext fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                return OTHER;
            }
            try {
                return IssueContext.valueOf(text.trim().toUpperCase());
            } catch (Exception e) {
                return OTHER;
            }
        }
    }

    public enum IssueSeverity {
        MINOR,
        MAJOR,
        UNPLAYABLE,
        BREAKS_GAMEPLAY,
        SERVER_CRASH,
        UNNECESSARY,
        UNKNOWN;
        public static IssueSeverity fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                return UNKNOWN;
            }
            try {
                return IssueSeverity.valueOf(text.trim().toUpperCase());
            } catch (Exception e) {
                return UNKNOWN;
            }
        }
    }

}
