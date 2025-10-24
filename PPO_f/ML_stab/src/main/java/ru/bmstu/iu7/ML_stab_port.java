package ru.bmstu.iu7;

import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.IML_port;
import ru.bmstu.iu7.API.model.ATag;

import java.io.IOException;
import java.util.List;


public class ML_stab_port implements IML_port {
    private String olamaHost;
    private final AppLogger logger;

    public ML_stab_port(String host, AppLogger applogger) {
        this.olamaHost = host;
        this.logger = applogger;
        if (logger != null) logger.info("ML_port initialized with host {}", olamaHost);
    }

    public ML_stab_port(String host) {
        this(host, null);
    }


    @Override
    public List<ATag> get_tags_names(String question, String answer, List<ATag> tags) throws IOException, InterruptedException {
        if (logger != null) logger.info("Generating tags for question '{}' and answer '{}'", question, answer);
        List<ATag> answer_tags = List.of(tags.getFirst());
        return answer_tags;
    }
}
