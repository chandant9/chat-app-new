package com.zack.projects.chatapp.VO;

import com.zack.projects.chatapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOnlineStatusResponseTemplate {

    private String username;
    private boolean online;
    private String availability;

    public UserOnlineStatusResponseTemplate(User user) {
        this.username = user.getUsername();
        this.online = user.isOnline();
        this.availability = user.getAvailability();
    }

}
