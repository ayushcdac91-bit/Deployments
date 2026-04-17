package com.statewide.login.requestdto;

import lombok.Data;

@Data
public class UserLogDateRequest {

    private String fromDate;
    private String toDate;
    private int page = 0; // default
    private int size = 10; // default
}
