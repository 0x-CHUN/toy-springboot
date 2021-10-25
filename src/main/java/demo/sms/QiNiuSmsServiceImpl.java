package demo.sms;

import springboot.annotation.ioc.Component;

@Component(name = "qiNiuSmsServiceImpl")
public class QiNiuSmsServiceImpl implements SmsService {

    @Override
    public String send(SmsDto smsDto) {
        System.out.println("send message to " + smsDto.getPhone());
        return QiNiuSmsServiceImpl.class.getSimpleName();
    }
}
