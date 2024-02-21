package pro.johndunlap.influx;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

// Supported JSR-330 annotations
@SupportedAnnotationTypes({
    "jakarta.inject.Inject",
    "jakarta.inject.Singleton",
    "jakarta.inject.Named",
})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class InfluxProcessor extends AbstractProcessor {

    private int count = 0;

    private synchronized int next() {
        return count;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            System.out.println("Processing annotations: " + toString(annotation));
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                System.out.println("Enclosing element: " + element.getEnclosingElement().getSimpleName());
                System.out.println("Annotated element: " + element.getSimpleName());
            }
        }
        try {
            if(!classExists("platform.GeneratedClass0")) {
                generateSourceFile();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate source file: " + e.getMessage());
        }
        return false;
    }

    public String toString(TypeElement typeElement) {
        return "{TypeElement: "
            + "qualifiedName=" + typeElement.getQualifiedName().toString()
            + "}";
    }

    private void generateSourceFile() throws IOException {
        int num = next();
        Filer filer = processingEnv.getFiler();
        JavaFileObject sourceFile = filer.createSourceFile("platform.GeneratedClass" + num);

        try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
            out.println("package platform;");
            out.println("public class GeneratedClass" + num + " {");
            out.println("    public static void generatedMethod() {");
            out.println("        System.out.println(\"Generated method\");");
            out.println("    }");
            out.println("}");
        }
    }

    private boolean classExists(String fullyQualifiedName) {
        Elements elements = processingEnv.getElementUtils();
        TypeElement typeElement = elements.getTypeElement(fullyQualifiedName);
        return typeElement != null;
    }
}