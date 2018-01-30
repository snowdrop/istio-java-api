/**
 * A CEXL grammar for ANTLR 4 derived from the Istio CEXL description
 * https://istio.io/docs/reference/config/mixer/expression-language.html
 */
grammar CEXL;

@header {
package me.snowdrop.istio.api.model.v1.cexl.parser;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Expressions

expression
    : primaryExpr
    | expression ('||' | '&&' | '==' | '!=') expression
    | '(' expression ')'
    | defaultOpExpr
    ;

defaultOpExpr
    : primaryExpr '|' primaryExpr ( '|' primaryExpr )*
    ;

operand
    : IDENTIFIER
    | INT_LIT
    | IP_LIT
    | STRING_LIT
    ;


//PrimaryExpr =
//	Operand |
//	PrimaryExpr Index |
//  matchExpr | // Glob match match(destination.service, "*.ns1.svc.cluster.local") Matches prefix or suffix based on the location of *
//  ipExpr | // Convert a textual IPv4 address into the IP_ADDRESS type e.g. source.ip == ip("10.11.12.13")
//  timestampExpr | // Convert a textual timestamp in RFC 3339 format into the TIMESTAMP type e.g. timestamp("2015-01-02T15:04:35Z")
//  matchesExpr | // Regular expression match e.g. "svc.*".matches(destination.service) matches destination.service against "svc.*".
//  startsWithExpr | // string prefix match e.g. destination.service.startsWith("acme")
// endsWithExpr | //  string postfix match e.g. destination.service.endsWith("acme"
//
//Index          = "[" Expression "]" .


primaryExpr
    : operand
    | primaryExpr index
    | matchExpr
    | ipExpr
    | timestampExpr
    | matchesExpr
    | startsWithExpr
    | endsWithExpr
    ;

index
    : '["' STRING_LIT '"]'
    ;

matchExpr
    : 'match(' IDENTIFIER ', "' STRING_LIT '")'
    ;

ipExpr
    : 'ip("' IP_LIT '")'
    ;

timestampExpr
    : 'timestamp("' STRING_LIT '")'
    ;

matchesExpr
    : '"' STRING_LIT '".matches(' IDENTIFIER ')'
    ;

startsWithExpr
    : IDENTIFIER '.startsWith("' STRING_LIT '")'
    ;

endsWithExpr
    : IDENTIFIER '.endsWith("' STRING_LIT '")'
    ;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// LEXER


// Identifiers
IDENTIFIER
    : UNICODE_CHAR ( UNICODE_CHAR | DECIMAL_DIGIT | '.' )*
    ;

// IP
IP_LIT
    : IP_FRAG_LIT '.' IP_FRAG_LIT '.' IP_FRAG_LIT '.' IP_FRAG_LIT
    ;

fragment IP_FRAG_LIT
    : [1-2] DECIMAL_DIGIT? DECIMAL_DIGIT?
    ;

INT_LIT
    : [1-9] DECIMAL_DIGIT*
    ;


STRING_LIT
    : '"' (UNICODE_CHAR)* '"'
    ;


fragment DECIMAL_DIGIT
    : [0-9]
    ;


//unicode_char = /* an arbitrary Unicode code point except newline */ .
fragment UNICODE_CHAR   : ~[\u000A] ;

// Whitespace
WS  :  [ \t]+ -> channel(HIDDEN)
    ;