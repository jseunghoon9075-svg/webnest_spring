package com.app.webnest.service;

import com.app.webnest.exception.QuizException;
import com.app.webnest.util.JavaSourceFromString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

@Slf4j
@Service
public class JavaCompileServiceImpl implements JavaCompileService {

    public String execute(String className, String code) {
        String result = null;
        try {
            JavaFileObject file = new JavaSourceFromString(className, code);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            String outputDir = "../compiler";
            new File(outputDir).mkdirs();

            Iterable<String> options = Arrays.asList("-d", outputDir);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, Arrays.asList(file));
            boolean success = task.call();
            fileManager.close();

            // 실패하면
            if (!success) {
                for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                    throw new QuizException("컴파일 오류");
                }

            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream oldOut = System.out;
            System.setOut(ps);

            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(outputDir).toURI().toURL()});
                Class<?> clazz = classLoader.loadClass(className);
                clazz.getMethod("main", String[].class).invoke(null, (Object) new String[]{});
                classLoader.close();
            } finally {
                System.setOut(oldOut);
            }

            result = baos.toString("UTF-8").trim();

            log.info("result: {}", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
