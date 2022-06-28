package edu.xdu.debateteam;

import edu.xdu.debateteam.util.MailCilent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UtilTest {
    @Autowired
    MailCilent mailCilent;
    @Test
    void mailTest() {
        mailCilent.sendMail("864793683@qq.com","te","asd");
    }
}
