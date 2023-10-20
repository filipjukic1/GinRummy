package com.ginrummy.reflectionDocGeneration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Genrate Docs Using Reflection API
 */
public class DocGen {
    public static void generateDocumentation(Class<?> clazz) throws IOException {
        String folderName = "autoDocs/";
        String className = clazz.getName();
        String classFileName = className.replace(".", "_") + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(folderName + classFileName))) {
            // Class-level documentation
            writer.write("Class: " + className);
            writer.newLine();
            writer.write("Description: " + className);
            writer.newLine();
            writer.newLine();

            // Method-level documentation
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                writer.write("Method: " + method.getName());
                writer.newLine();
                writer.write("Description: " + method.getName());
                writer.newLine();
                writer.write("Return Type: " + method.getReturnType().getName());
                writer.newLine();
                writer.write("Parameters:");
                writer.newLine();
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    writer.write("  " + parameter.getType().getName() + " " + parameter.getName());
                    writer.newLine();
                }
                writer.newLine();
            }

            // Field-level documentation
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                writer.write("Field: " + field.getName());
                writer.newLine();
                writer.write("Description: " + field.getName());
                writer.newLine();
                writer.write("Type: " + field.getType().getName());
                writer.newLine();
                writer.newLine();
            }
        }
    }
}
