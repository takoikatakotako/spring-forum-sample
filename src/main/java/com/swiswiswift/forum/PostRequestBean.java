package com.swiswiswift.forum;

import lombok.Data;
import java.io.Serializable;

@Data
public class PostRequestBean implements Serializable {
    private String nickname;
    private String message;
}

