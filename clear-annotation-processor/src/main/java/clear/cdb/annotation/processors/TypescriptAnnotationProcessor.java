package clear.cdb.annotation.processors;

import clear.cdb.support.logger.LogLocation;
import clear.cdb.support.logger.ProcessorLogger;
import clear.cdb.support.template.FreemarkerUtil;
import com.farata.dto2extjs.annotations.JSClass;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@SupportedAnnotationTypes("com.farata.dto2extjs.annotations.*")
public class TypescriptAnnotationProcessor extends BaseAnnotationProcessor {

    private String dtoGeneratedPath = "/tmp";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        dtoGeneratedPath = processingEnv.getOptions().get("dtoGeneratedPath");
        for (TypeElement annotaion : annotations) {
            // process only JSCLass annotations
            if (JS_CLASS_ANNOTATION_TYPE.equals(annotaion.asType().toString())) {
                final Set<? extends Element> annotatedTypes = roundEnv.getElementsAnnotatedWith(JSClass.class);

                for (Element elem : annotatedTypes) {
                    final ElementKind elementKind = elem.getKind();

                    switch (elementKind) {
                        case ENUM:
                            logger.warning(LogLocation.MESSAGER, "Enum not supported");
                        case CLASS:
                            try { generateDTO(roundEnv, (TypeElement)elem); }
                            catch (Exception e) {
                                logger.warning(LogLocation.MESSAGER, "Error " + elem.getSimpleName()
                                        + " " + ProcessorLogger.exceptionToString(e));
                            }
                            break;
                        case INTERFACE:
                            logger.warning(LogLocation.MESSAGER, "Interface not supported");
                            break;
                        default:
                            continue;
                    }
                }
            }
        }
        return true;
    }

    private void generateDTO(RoundEnvironment roundEnv, TypeElement elem) throws Exception {
        Map<String, Object> context = getDTOContext(roundEnv, elem);
        String packageName = context.get("package").toString();
        String filename = elem.getSimpleName().toString() + ".ts";
        File file = new File(filename);
        if (!file.exists()) {
            String source = FreemarkerUtil.render("/ftl/typescript/typescript-model-custom.ftl", context);
            File destinationFolder = new File(dtoGeneratedPath + "/" + packageName);
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }
            String destination = destinationFolder + "/" + filename;
            Files.write(FileSystems.getDefault().getPath(destination), source.getBytes());
            logger.note(LogLocation.MESSAGER, "Generated " + filename);
        }

        String filename2 = elem.getSimpleName().toString() + "_.ts";
        String destination2 = dtoGeneratedPath + "/" + packageName + "/" + filename2;
        String source2 = FreemarkerUtil.render("/ftl/typescript/typescript-model-generated.ftl", context);
        Files.write(FileSystems.getDefault().getPath(destination2), source2.getBytes());
        logger.note(LogLocation.MESSAGER, "Generated " + filename2);
    }

    private Map<String, Object> getDTOContext(RoundEnvironment roundEnv, TypeElement elem) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("package", ((PackageElement)elem.getEnclosingElement()).getQualifiedName().toString());
        result.put("classSimpleName", elem.getSimpleName().toString());
        result.put("fields", getFields(roundEnv, elem));
        return result;
    }

    private List<DTOField> getFields(RoundEnvironment roundEnv, TypeElement elem) {
        // TODO get fields




        return Collections.EMPTY_LIST;
    }

    public String convertFromJava(Class type) {
        String t = type.getSimpleName().toLowerCase();
        if (t.contains("[]")) return "any[]";
        if (t.equals("boolean")) {
            return "boolean";
        } else if (t.equals("string")) {
            return "string";
        } else if (t.equals("byte") || t.equals("int") || t.equals("short") || t.equals("long") || t.equals("float") || t.equals("double")) {
            return "number";
        } else if (t.equals("date")) {
            return "Date";
        } else if (t.equals("list")) {
            return "any[]";
        } else if (t.equals("map")) {
            return "Object";
        }
        throw new RuntimeException("Unknown Java to Typescript type conversion: " + t);
    }

    public static class DTOField {
        public String name, type;
        public boolean key;

        DTOField(String name, String type, boolean key) {
            this.name = name;
            this.type = type;
            this.key = key;
        }
    }


}