package com.example.dto.user;

import com.example.model.AutoResponseSchedule;
import com.example.model.TelegramChat;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class UserInfoDtoAdmin extends UserInfoDto {
    private String phone;

    private boolean hasHhToken;
    private boolean hasAutoResponseSchedule;
    private boolean isAutoResponseScheduleEnabled;
    private boolean hasTelegramChat;
    private Long telegramUserId; // Telegram's internal user ID
    private Integer resumeCount;

    public UserInfoDtoAdmin() {
    }

    public UserInfoDtoAdmin(User user) {
        super(user);
        this.phone = user.getPhone();

        this.hasHhToken = user.getHhToken() != null;
        this.hasAutoResponseSchedule = user.getAutoResponseSchedule() != null;
        this.isAutoResponseScheduleEnabled = Optional.ofNullable(user.getAutoResponseSchedule())
                .map(AutoResponseSchedule::isEnabled)
                .orElse(false);
        this.hasTelegramChat = user.getTelegramChat() != null;
        this.telegramUserId = Optional.ofNullable(user.getTelegramChat())
                .map(TelegramChat::getTelegramUserId)
                .orElse(null);
        this.resumeCount = Optional.ofNullable(user.getResume())
                .map(List::size)
                .orElse(0);
    }
}
