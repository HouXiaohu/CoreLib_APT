package com.example;

import javax.annotation.processing.RoundEnvironment;

/**
 * Created by hxh on 2017/7/5.
 */
public interface IProcessor {
    void process(RoundEnvironment roundEnv, AnnotationProcessor processor);
}
