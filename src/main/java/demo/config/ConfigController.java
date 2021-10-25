package demo.config;

import springboot.annotation.config.Value;
import springboot.annotation.ioc.Autowired;
import springboot.annotation.mvc.GetMapping;
import springboot.annotation.mvc.RequestParam;
import springboot.annotation.mvc.RestController;
import springboot.core.config.ConfigurationManager;

@RestController("/config")
public class ConfigController {
    @Autowired
    private ConfigurationManager configurationManager;

    @Value("project.info")
    private String projectInfo;

    @GetMapping
    public String getConfig(@RequestParam("key") String key) {
        return configurationManager.getString(key);
    }

    @GetMapping("/project-info")
    public String getProjectInfo() {
        return projectInfo;
    }

}
