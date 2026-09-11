package com.devarena.admin.dto;

public class AdminOverviewDto {

    private long totalUsers;
    private long activeUsers;
    private long totalChallenges;
    private long totalSubmissions;
    private long totalBattles;
    private long openReports;
    private long highRiskIntegrityAlerts;
    private long totalAiQueriesToday;

    public AdminOverviewDto() {}

    public AdminOverviewDto(long totalUsers, long activeUsers, long totalChallenges, long totalSubmissions,
                            long totalBattles, long openReports, long highRiskIntegrityAlerts, long totalAiQueriesToday) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalChallenges = totalChallenges;
        this.totalSubmissions = totalSubmissions;
        this.totalBattles = totalBattles;
        this.openReports = openReports;
        this.highRiskIntegrityAlerts = highRiskIntegrityAlerts;
        this.totalAiQueriesToday = totalAiQueriesToday;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

    public long getTotalChallenges() { return totalChallenges; }
    public void setTotalChallenges(long totalChallenges) { this.totalChallenges = totalChallenges; }

    public long getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; }

    public long getTotalBattles() { return totalBattles; }
    public void setTotalBattles(long totalBattles) { this.totalBattles = totalBattles; }

    public long getOpenReports() { return openReports; }
    public void setOpenReports(long openReports) { this.openReports = openReports; }

    public long getHighRiskIntegrityAlerts() { return highRiskIntegrityAlerts; }
    public void setHighRiskIntegrityAlerts(long highRiskIntegrityAlerts) { this.highRiskIntegrityAlerts = highRiskIntegrityAlerts; }

    public long getTotalAiQueriesToday() { return totalAiQueriesToday; }
    public void setTotalAiQueriesToday(long totalAiQueriesToday) { this.totalAiQueriesToday = totalAiQueriesToday; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalUsers;
        private long activeUsers;
        private long totalChallenges;
        private long totalSubmissions;
        private long totalBattles;
        private long openReports;
        private long highRiskIntegrityAlerts;
        private long totalAiQueriesToday;

        public Builder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public Builder activeUsers(long activeUsers) { this.activeUsers = activeUsers; return this; }
        public Builder totalChallenges(long totalChallenges) { this.totalChallenges = totalChallenges; return this; }
        public Builder totalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; return this; }
        public Builder totalBattles(long totalBattles) { this.totalBattles = totalBattles; return this; }
        public Builder openReports(long openReports) { this.openReports = openReports; return this; }
        public Builder highRiskIntegrityAlerts(long highRiskIntegrityAlerts) { this.highRiskIntegrityAlerts = highRiskIntegrityAlerts; return this; }
        public Builder totalAiQueriesToday(long totalAiQueriesToday) { this.totalAiQueriesToday = totalAiQueriesToday; return this; }

        public AdminOverviewDto build() {
            return new AdminOverviewDto(totalUsers, activeUsers, totalChallenges, totalSubmissions, totalBattles, openReports, highRiskIntegrityAlerts, totalAiQueriesToday);
        }
    }
}
