package ru.blogic.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private ContextRefresher refresher;

    /**
     *  Динамическое изменение и добавление параметров
     *  По хорошему spring.cloud.config.name лучше прописывать сразу в bootstrap.properties spring.application.name=sample-config,new-sample-config
     */
    @PostConstruct
    public void init() {
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, Object> map = new HashMap<>();
        map.put("spring.cloud.config.name", "sample-config,new-sample-config"); // конфиги прописываются через запятую
        propertySources.addFirst(new MapPropertySource("defaultProperties", map));
        applicationContext.setEnvironment(environment);
    }

    /**
     *  Получение параметров с sample-config
     */
    @GetMapping("/first")
    public String sayFirst() throws Exception {
        refreshObjects();
        return environment.getProperty("my.config.first.word");
    }

    /**
     *  Получение параметров с new-sample-config
     */
    @GetMapping("/second")
    public String saySecond() throws Exception {
        refreshObjects();
        return environment.getProperty("my.config.second.word");
    }

    /**
     *  Обновление контекста actuator-а.
     *  Либо как вариант использовать пустой post-запрос на "{ссылка}/actuator/refresh"
     */
    public void refreshObjects() throws Exception {
        refresher.refresh();
    }
}