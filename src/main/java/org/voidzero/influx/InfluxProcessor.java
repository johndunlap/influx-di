package org.voidzero.influx;

import javax.lang.model.element.Modifier;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Supported JSR-330 annotations
@SupportedAnnotationTypes({
    "jakarta.inject.Inject",
    "jakarta.inject.Singleton",
    "jakarta.inject.Named",
    "org.voidzero.influx.Injector",
})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class InfluxProcessor extends AbstractProcessor {

    /**
     * Cache of fully qualified class names which have already been processed.
     */
    private final static Set<String> classCache = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // We can't claim annotations that we don't have
        if (annotations.isEmpty()) {
            return false;
        }

        // Iterate over all annotations which were found by the compiler
        for (TypeElement annotation : annotations) {
            System.out.println("Processing annotation: " + annotation.getQualifiedName().toString());
            switch (annotation.getQualifiedName().toString()) {
                case "jakarta.inject.Singleton":
                    processSingleton(annotation, roundEnv);
                    break;
                case "jakarta.inject.Named":
                    processNamed(annotation, roundEnv);
                    break;
                case "jakarta.inject.Inject":
                    processInject(annotation, roundEnv);
                    break;
                case "org.voidzero.influx.Injector":
                    processInjector(annotation, roundEnv);
                    break;
            }
        }

        return true;
    }

    private void processSingleton(TypeElement annotation, RoundEnvironment roundEnv) {
        // Get all elements that are annotated with the current annotation
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);

        // Iterate over all elements that are annotated with the current annotation
        for (Element element : elements) {
            TypeMirror typeMirror = element.asType();
            TypeKind typeKind = typeMirror.getKind();

            // We're only interested in class types
            if (typeKind != TypeKind.DECLARED) {
                continue;
            }

            TypeElement typeElement = (TypeElement) element;

            // Ugly...
            String className = typeElement.getQualifiedName().toString();

            // Skip classes we've already processed
            if (classCache.contains(className)) {
                continue;
            }

            // Remember that we've processed this class before
            classCache.add(className);

            // Perform some basic sanity checks
            assertNotInterface(typeElement);
            assertNotAbstractClass(typeElement);
            assertHasNoArgConstructor(typeElement);

            System.out.println("\tProcessing singleton: " + className);
            System.out.println("\t\tIs interface: " + isInterface(typeElement));
            System.out.println("\t\tIs class: " + isClass(typeElement));
            System.out.println("\t\tIs concrete class: " + isConcreteClass(typeElement));
            System.out.println("\t\tIs abstract class: " + isAbstractClass(typeElement));
        }
    }

    private void processNamed(TypeElement annotation, RoundEnvironment roundEnv) {
        System.out.println("\tProcessing named: " + annotation.getEnclosingElement().getKind().toString());
    }

    private void processInject(TypeElement annotation, RoundEnvironment roundEnv) {
        System.out.println("\tProcessing inject: " + annotation.getEnclosingElement().getKind().toString());
    }

    private void processInjector(TypeElement annotation, RoundEnvironment roundEnv) {
        System.out.println("\tProcessing injector: " + annotation.getEnclosingElement().getKind().toString());
    }

    private boolean isInterface(TypeElement classElement) {
        return classElement.getKind() == ElementKind.INTERFACE;
    }

    private boolean isClass(TypeElement classElement) {
        return classElement.getKind() == ElementKind.CLASS;
    }

    private boolean isConcreteClass(TypeElement classElement) {
        return isClass(classElement) && !classElement.getModifiers().contains(Modifier.ABSTRACT);
    }

    private boolean isAbstractClass(TypeElement classElement) {
        return isClass(classElement) && classElement.getModifiers().contains(Modifier.ABSTRACT);
    }

    private boolean hasNoArgConstructor(TypeElement classElement) {
        // Get enclosed elements of the class, including constructors
        List<? extends Element> enclosedElements = classElement.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            // Check if the enclosed element is a constructor
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosedElement;
                // Check if the constructor has no parameters
                if (constructorElement.getParameters().isEmpty()) {
                    return true; // Found a default constructor
                }
            }
        }
        return false; // No default constructor found
    }

    private void assertHasNoArgConstructor(TypeElement typeElement) {
        String className = typeElement.getQualifiedName().toString();
        if (!hasNoArgConstructor(typeElement)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                className + " must have a no-arg or default constructor because it is annotated with @Singleton",
                typeElement
            );
        }
    }

    private void assertNotInterface(TypeElement typeElement) {
        String className = typeElement.getQualifiedName().toString();
        if (isInterface(typeElement)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                className + " cannot be an interface because it is annotated with @Singleton",
                typeElement
            );
        }
    }

    private void assertNotAbstractClass(TypeElement typeElement) {
        String className = typeElement.getQualifiedName().toString();
        if (isAbstractClass(typeElement)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                className + " cannot be abstract because it is annotated with @Singleton",
                typeElement
            );
        }
    }

    private void generateSourceFile() throws IOException {
        Filer filer = processingEnv.getFiler();
        JavaFileObject sourceFile = filer.createSourceFile("platform.GeneratedClass");

        try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
            out.println("package platform;");
            out.println("public class GeneratedClass {");
            out.println("    public static void generatedMethod() {");
            out.println("        System.out.println(\"Generated method\");");
            out.println("    }");
            out.println("}");
        }
    }
}