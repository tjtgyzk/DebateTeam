package edu.xdu.debateteam.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class LoginTicket {
    private int id;
    private long userId;
    private String ticket;
    private int status;
    private Date expired;
}
