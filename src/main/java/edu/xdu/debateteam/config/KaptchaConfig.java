package edu.xdu.debateteam.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        Properties props = new Properties();
        props.setProperty("kaptcha.image.width","100");
        props.setProperty("kaptcha.image.height","40");
        props.setProperty("kaptcha.textproducer.font.size","32");
        props.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        props.setProperty("kaptcha.textproducer.char.length","4");
        props.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(props);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
