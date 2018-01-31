/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.cexl.parser;

import me.snowdrop.istio.api.model.v1.cexl.AttributeVocabulary;
import me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * A "simple" CEXL expression validator. Operator precedence is not implemented so you need to use parentheses to combine expressions.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class CEXLTypeResolver extends CEXLBaseListener {
    
    private static final String DEFAULT_OPERATOR = "|";
    private ValueType expressionType;
    
    public ValueType getExpressionType() {
        return expressionType;
    }
    
    @Override
    public void exitExpression(CEXLParser.ExpressionContext ctx) {
        expressionType = evaluate(ctx);
    }
    
    private ValueType evaluate(CEXLParser.ExpressionContext ctx) {
        final CEXLParser.ParenExprContext parenExprContext = ctx.parenExpr();
        if (parenExprContext != null) {
            return evaluate(parenExprContext.expression());
        }
        
        final CEXLParser.FirstNonEmptyExprContext firstNonEmptyExprContext = ctx.firstNonEmptyExpr();
        if (firstNonEmptyExprContext != null) {
            return evaluate(firstNonEmptyExprContext);
        }
        
        final CEXLParser.PrimaryExprContext primaryExprContext = ctx.primaryExpr();
        if (primaryExprContext != null) {
            return evaluate(primaryExprContext);
        }
        
        // we have a binary expression so check both sides
        final CEXLParser.ExpressionContext left = ctx.expression(0);
        final CEXLParser.ExpressionContext right = ctx.expression(1);
        
        final ValueType leftEvaluation = evaluate(left);
        final ValueType rightEvaluation = evaluate(right);
        if (leftEvaluation != rightEvaluation) {
            throw new IllegalArgumentException(String.format("Both sides of expression don't evaluate to same type. Left side '%s' evaluates to '%s', while right side '%s' evaluates to '%s'",
                left.getText(), leftEvaluation.name(), right.getText(), rightEvaluation.name()));
        }
        
        return ValueType.BOOL;
    }
    
    private ValueType evaluate(CEXLParser.FirstNonEmptyExprContext ctx) {
        return ctx.primaryExpr()
            .stream()
            .filter(child -> !child.getText().equals(DEFAULT_OPERATOR))
            .map(primaryExprContext -> evaluate(primaryExprContext))
            .reduce(ValueType.VALUE_TYPE_UNSPECIFIED, (valueType, valueType2) -> {
                if (valueType == ValueType.VALUE_TYPE_UNSPECIFIED || valueType == valueType2) {
                    return valueType2;
                } else {
                    throw new IllegalArgumentException(String.format("Expression '%s' doesn't evaluate to a consistent type", ctx.getText()));
                }
            });
    }
    
    private ValueType evaluate(CEXLParser.PrimaryExprContext ctx) {
        final CEXLParser.OperandContext operandExp = ctx.operand();
        if (operandExp != null) {
            return evaluate(operandExp);
        }
    
        final CEXLParser.IndexExprContext indexExpr = ctx.indexExpr();
        if (indexExpr != null) {
            final TerminalNode identifier = indexExpr.IDENTIFIER();
            if (identifier != null) {
                AttributeVocabulary.getInfoFor(identifier.getText())
                    .filter(it -> it.type == ValueType.STRING_MAP)
                    .orElseThrow(() ->
                        new IllegalArgumentException(
                            String.format("Indexed expression '%s' doesn't start with a valid identifier or is not of type '%s'",
                                indexExpr.getText(),
                                ValueType.STRING_MAP)
                        )
                    );
                return ValueType.STRING;
            }
        }
        
        return ValueType.VALUE_TYPE_UNSPECIFIED;
    }
    
    private ValueType evaluate(CEXLParser.OperandContext operandExp) {
        final TerminalNode identifier = operandExp.IDENTIFIER();
        if (identifier != null) {
            // check if attribute is a known attribute
            final String attribute = identifier.getText();
            return AttributeVocabulary.getInfoFor(attribute)
                .map(info -> info.type)
                .orElseThrow(() -> new IllegalArgumentException("Unknown attribute " + attribute));
        } else {
            // we must have a literal then
            final CEXLParser.LiteralContext literal = operandExp.literal();
            TerminalNode intLit = literal.INT_LIT();
            if (intLit != null) {
                return ValueType.INT64;
            }
            TerminalNode ipLit = literal.IP_LIT();
            if (ipLit != null) {
                return ValueType.IP_ADDRESS;
            }
            TerminalNode stringLit = literal.STRING_LIT();
            if (stringLit != null) {
                return ValueType.STRING;
            }
        }
        
        return ValueType.VALUE_TYPE_UNSPECIFIED;
    }
}

