package com.example.dto.negotiation;

public record PlatformStatistic(
        long total,
        long views,
        long responses,
        long interviewInvites,
        long offers,
        long noAnswer,
        long resumeRejected,
        long interviewRejected,
        long interviewPassed
) {
}
