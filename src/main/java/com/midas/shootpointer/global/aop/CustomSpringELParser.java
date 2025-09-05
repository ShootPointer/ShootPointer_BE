package com.midas.shootpointer.global.aop;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * 전달받은 Lock의 이름을 Spring Expression Language로 파싱.
 */

public class CustomSpringELParser {
    private CustomSpringELParser(){
    }

    public static Object getDynamicValue(String[] parameterNames,
                                         Object[] args,
                                         String key
    ){
        ExpressionParser parser=new SpelExpressionParser();
        StandardEvaluationContext context=new StandardEvaluationContext();

        for(int i=0;i<parameterNames.length;i++){
            context.setVariable(parameterNames[i],args[i]);
        }

        return parser.parseExpression(key).getValue(context,Object.class);
    }
}
