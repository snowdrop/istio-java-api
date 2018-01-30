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
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class CEXLTypeResolver extends CEXLBaseListener {

    protected static final String DEFAULT_OPERATOR = "|";
    private final ValueType[] currentlyInferredType = new ValueType[1];

    public ValueType getExpressionType() {
        return currentlyInferredType[0];
    }

    @Override
    public void exitFirstNonEmptyExpr(CEXLParser.FirstNonEmptyExprContext ctx) {
        ctx.children
                .stream()
                .filter(child -> !child.getText().equals(DEFAULT_OPERATOR))
                .forEach(child -> {
                    System.out.println("child class = " + child.getClass().getSimpleName());
                    if (child instanceof CEXLParser.PrimaryExprContext) {
                        CEXLParser.PrimaryExprContext primaryExpr = (CEXLParser.PrimaryExprContext) child;
                        final CEXLParser.OperandContext operandExp = primaryExpr.operand();
                        if (operandExp != null) {

                            final TerminalNode identifier = operandExp.IDENTIFIER();
                            if (identifier != null) {
                                // check if attribute is a known attribute
                                final String attribute = identifier.getText();
                                final AttributeVocabulary.AttributeInfo info = AttributeVocabulary.getInfoFor(attribute);
                                if (info != null) {
                                    checkEvaluatedTypeAgainstCurrentlyInferred(attribute, info.type);
                                } else {
                                    System.out.println("Unknown attribute = " + attribute);
                                }
                            } else {
                                // we must have a literal then
                                final CEXLParser.LiteralContext literal = operandExp.literal();
                                checkLiteral(literal.INT_LIT(), ValueType.INT64);
                                checkLiteral(literal.IP_LIT(), ValueType.IP_ADDRESS);
                                checkLiteral(literal.STRING_LIT(), ValueType.STRING);
                            }
                        }
                    }
                });
    }

    private void checkLiteral(TerminalNode terminal, ValueType evaluatedType) {
        if (terminal != null) {
            checkEvaluatedTypeAgainstCurrentlyInferred(terminal.getText(), evaluatedType);
        }
    }

    private void checkEvaluatedTypeAgainstCurrentlyInferred(String value, ValueType evaluatedType) {
        final ValueType current = currentlyInferredType[0];
        if (current != null) {
            if (evaluatedType != current) {
                throw new IllegalArgumentException(String.format("Operand '%s' has incompatible type with currently inferred ('%s')", value, current.name()));
            }
        } else {
            currentlyInferredType[0] = evaluatedType;
        }
    }
}

