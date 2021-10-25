package demo.sms;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.ioc.Qualifier;
import springboot.annotation.mvc.PostMapping;
import springboot.annotation.mvc.RequestBody;
import springboot.annotation.mvc.RestController;

@RestController("/sms")
public class SmsController {
    @Autowired
    @Qualifier("aliSmsServiceImpl")
    private SmsService smsService;

    @PostMapping("/send")
    public String send(@RequestBody SmsDto smsDto) {
        return smsService.send(smsDto);
    }

}
