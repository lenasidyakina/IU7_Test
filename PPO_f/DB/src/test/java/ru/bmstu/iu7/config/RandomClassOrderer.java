package ru.bmstu.iu7.config;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;

import java.util.Collections;

public class RandomClassOrderer implements ClassOrderer {
    @Override
    public void orderClasses(ClassOrdererContext context) {
        Collections.shuffle(context.getClassDescriptors());
    }
}
