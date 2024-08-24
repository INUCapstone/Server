package com.CapStone.inu.taxi.global.aop;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

//@DistributeLock의 key를 SpringExpression으로 전달하고 이를 파싱하는 util클래스
public class CustomSpringELParser {
    /*
    parameterNames: 메소드의 파라미터 이름 배열. 예를 들어, ["userId", "amount"].
    args: 메소드 호출 시 전달된 실제 인자 값 배열. 예를 들어, [12345, 100].
    key: SpEL 표현식으로 정의된 문자열. 예를 들어, "#userId + '-lock'".
     */
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 파라미터 이름과 값을 컨텍스트에 설정
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // SpEL 표현식을 평가하여 동적 값을 생성
        return parser.parseExpression(key).getValue(context, Object.class);
    }
}
