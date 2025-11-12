package com.app.webnest.sh;

import com.app.webnest.service.JavaCompileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.JavaSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

@SpringBootTest
@Slf4j
public class CompilerTest {

    @Autowired
    JavaCompileService javaCompileService;

    @Test
    public void testCompiler(){
//        화면에서 받은 코드
//        String expression = "log.info(1 + 1)";
        // 고정
        String className = "CompileResult";
        String expression = "";
        String code = "public class " + className + "{" +
                    "public static void main(String[] args) { System.out.println(" + expression + "); }" +
                "}";

        log.info("실행 결과: {}",javaCompileService.execute(className, code));

    }


}
