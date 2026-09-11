package com.devarena.ai.dto;

import java.util.ArrayList;
import java.util.List;

public class AiCoachResponseDto {

    private String reply;
    private int hintLevel;
    private List<String> suggestedFollowUps = new ArrayList<>();
    private int remainingDailyQueries;

    public AiCoachResponseDto() {}

    public AiCoachResponseDto(String reply, int hintLevel, List<String> suggestedFollowUps, int remainingDailyQueries) {
        this.reply = reply;
        this.hintLevel = hintLevel;
        this.suggestedFollowUps = suggestedFollowUps;
        this.remainingDailyQueries = remainingDailyQueries;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public int getHintLevel() { return hintLevel; }
    public void setHintLevel(int hintLevel) { this.hintLevel = hintLevel; }

    public List<String> getSuggestedFollowUps() { return suggestedFollowUps; }
    public void setSuggestedFollowUps(List<String> suggestedFollowUps) { this.suggestedFollowUps = suggestedFollowUps; }

    public int getRemainingDailyQueries() { return remainingDailyQueries; }
    public void setRemainingDailyQueries(int remainingDailyQueries) { this.remainingDailyQueries = remainingDailyQueries; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String reply;
        private int hintLevel;
        private List<String> suggestedFollowUps = new ArrayList<>();
        private int remainingDailyQueries;

        public Builder reply(String reply) { this.reply = reply; return this; }
        public Builder hintLevel(int hintLevel) { this.hintLevel = hintLevel; return this; }
        public Builder suggestedFollowUps(List<String> suggestedFollowUps) { this.suggestedFollowUps = suggestedFollowUps; return this; }
        public Builder remainingDailyQueries(int remainingDailyQueries) { this.remainingDailyQueries = remainingDailyQueries; return this; }

        public AiCoachResponseDto build() {
            return new AiCoachResponseDto(reply, hintLevel, suggestedFollowUps, remainingDailyQueries);
        }
    }
}
