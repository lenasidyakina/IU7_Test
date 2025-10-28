package ru.bmstu.iu7.API;

import ru.bmstu.iu7.API.model.ATag;

import java.io.IOException;
import java.util.List;

public interface IML_port {
    List<ATag> get_tags_names(String question, String answer, List<ATag> tags) throws IOException, InterruptedException;
}
