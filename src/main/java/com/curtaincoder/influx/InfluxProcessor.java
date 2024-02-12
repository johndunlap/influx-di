package com.curtaincoder.influx;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({
    "jakarta.inject.Inject",
    "com.curtaincoder.Injectable"
})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class InfluxProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            System.out.println("Processing annotations: " + toString(typeElement));
        }
        return false;
    }
    public String toString(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString();
    }
}