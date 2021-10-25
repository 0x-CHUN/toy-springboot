package demo.sms;

import springboot.annotation.ioc.Component;

@Component(name = "aliSmsServiceImpl")
public class AliSmsServiceImpl implements SmsService {


    @Override
    public String send(SmsDto smsDto) {
        System.out.println("send message to " + smsDto.getPhone());
        return AliSmsServiceImpl.class.getSimpleName();
    }
}
