package com.example.dto.user;

import com.example.model.HhToken;
import com.example.model.Resume;
import com.example.model.TelegramChat;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class UserInfoDtoFull extends UserInfoDtoAdmin {
    private HhTokenInfo hhToken;
    private List<ResumeInfo> resumes;
    private TelegramInfo telegram;

    public UserInfoDtoFull() {
    }

    public UserInfoDtoFull(User user) {
        super(user);
        this.hhToken = Optional.ofNullable(user.getHhToken())
                .map(HhTokenInfo::new)
                .orElse(null);

        this.resumes = Optional.ofNullable(user.getResume())
                .orElse(List.of())
                .stream()
                .map(ResumeInfo::new)
                .toList();

        this.telegram = Optional.ofNullable(user.getTelegramChat())
                .map(TelegramInfo::new)
                .orElse(null);
    }

    @Getter
    @Setter
    public static class HhTokenInfo implements Serializable {
        private Integer id;
        private String accessToken;
        private Long expiresIn;
        private String refreshToken;
        private LocalDateTime created;
        private LocalDateTime updatedAt;

        public HhTokenInfo(HhToken token) {
            this.id = token.getId();
            this.accessToken = token.getAccessToken();
            this.expiresIn = token.getExpiresIn();
            this.refreshToken = getRefreshToken();
            this.created = token.getCreatedAt();
            this.updatedAt = token.getUpdatedAt();
        }
    }

    @Getter
    @Setter
    public static class ResumeInfo implements Serializable {
        private Integer id;
        private String resumeId;
        private String resumeTitle;
        private LocalDateTime created;
        private LocalDateTime updatedAt;

        public ResumeInfo(Resume resume) {
            this.id = resume.getId();
            this.resumeId = resume.getResumeId();
            this.resumeTitle = resume.getResumeTitle();
            this.created = resume.getCreatedAt();
            this.updatedAt = resume.getUpdatedAt();
        }
    }

    @Getter
    @Setter
    public static class TelegramInfo implements Serializable {
        private Integer id;
        private Long telegramChatId;
        private LocalDateTime created;
        private LocalDateTime updatedAt;

        public TelegramInfo(TelegramChat chat) {
            this.id = chat.getId();
            this.telegramChatId = chat.getTelegramChatId();
            this.created = chat.getCreatedAt();
            this.updatedAt = chat.getUpdatedAt();
        }
    }
}
