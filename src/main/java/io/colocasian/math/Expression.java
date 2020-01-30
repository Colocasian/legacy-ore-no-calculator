package io.colocasian.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

public class Expression {
    private HashMap<String, BigDecimal> vars;
    private HashMap<String, String> vars;

    public Expression() {
        this.vars = new HashMap<>();
    }

    private static boolean isNum(char c) {
        return ((c >= '0' && c <= '9') || c == '.');
    }

    private static boolean isVar(char c) {
        return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_'));
    }

    private static BigDecimal power(BigDecimal a, BigDecimal b) {
        boolean inv = (b.compareTo(BigDecimal.ZERO) < 0);
        if (inv)
            b = b.negate();
        BigDecimal tmpa = a.pow(b.intValue());
        BigDecimal tmpb = BigDecimal.valueOf(Math.pow(a.doubleValue(),
                    b.remainder(BigDecimal.ONE).doubleValue()));

        return (inv? BigDecimal.ONE.divide(tmpa.multiply(tmpb),
                    MathContext.DECIMAL128): tmpa.multiply(tmpb));
    }

    private static BigDecimal solvePostfix(ArrayList<Integer> postNote, ArrayList<BigDecimal> numList) {
        Stack<BigDecimal> boya = new Stack<>();
        for (int i = 0; i < postNote.size(); i++) {
            int at = postNote.get(i);
            if (at <= 0)
                boya.push(numList.get(-at));
            else {
                BigDecimal a, b;
                switch ((char)at) {
                    case '^':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(power(a, b));
                        break;

                    case '@':
                        a = boya.peek();
                        boya.pop();
                        boya.push(a.negate());
                        break;

                    case '*':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a.multiply(b));
                        break;

                    case '/':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a.divide(b, MathContext.DECIMAL128));
                        break;

                    case '+':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a.add(b));
                        break;

                    case '-':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a.subtract(b));
                        break;
                }
            }
        }

        return boya.peek();
    }

    public BigDecimal evaluate(String infix) throws ArithmeticException, NoSuchElementException {
        if (infix.trim().isEmpty())
            throw new ArithmeticException("empty string passed");
        ArrayList<BigDecimal> nums = new ArrayList<>();
        Stack<Character> chars = new Stack<>();
        chars.push('(');
        
        ArrayList<Integer> postfix = new ArrayList<>();

        boolean nxtNum = true;
        boolean nxt1op = true;
        boolean nxt2op = false;
        boolean nxtBro = true;
        boolean nxtBrc = false;
        int lvl = 0;

        for (int i = 0; i < infix.length(); i++) {
            char at = infix.charAt(i);

            if (at == '(' || at == '[' || at == '{') {
                if (!nxtBro)
                    throw new ArithmeticException("not expecting opening braces");
                chars.push(at);
                lvl++;

                nxtNum = true;
                nxt1op = true;
                nxt2op = false;
                nxtBro = true;
                nxtBrc = false;
            }
            else if (at == ')' || at == ']' || at == '}') {
                if (!nxtBrc)
                    throw new ArithmeticException("not expecting closing braces");

                while (chars.peek() != '(' && chars.peek() != '[' &&
                        chars.peek() != '{') {
                    postfix.add(chars.peek().hashCode());
                    chars.pop();
                }

                if ((at == ')' && chars.peek() != '(') || (at == ']' && chars.peek() != '[') || 
                        (at == '}' && chars.peek() != '{'))
                    throw new ArithmeticException("incorrect bracket matching");

                chars.pop();
                lvl--;

                nxtNum = false;
                nxt1op = false;
                nxt2op = true;
                nxtBro = false;
                nxtBrc = (lvl > 0);
            }
            else if (isNum(at)) {
                if (!nxtNum)
                    throw new ArithmeticException("not expecting number");
                int j = i+1;
                while (j < infix.length() && isNum(infix.charAt(j)))
                    j++;

                postfix.add(-nums.size());
                nums.add(new BigDecimal(infix.substring(i, j)));
                i = j-1;

                nxtNum = false;
                nxt1op = false;
                nxt2op = true;
                nxtBro = false;
                nxtBrc = (lvl > 0);
            }
            else if (isVar(at)) {
                if (!nxtNum)
                    throw new ArithmeticException("not expecting variable name");
                int j = i+1;
                while (j < infix.length() && (isVar(infix.charAt(j)) ||
                            (infix.charAt(j) >= '0' && infix.charAt(j) <= '9')))
                    j++;

                String varName = infix.substring(i, j);
                if (!vars.containsKey(varName))
                    throw new NoSuchElementException("no such variable exists");
                postfix.add(-nums.size());
                nums.add(vars.get(varName));
                i = j-1;

                nxtNum = false;
                nxt1op = false;
                nxt2op = true;
                nxtBro = false;
                nxtBrc = (lvl > 0);
            }
            else if (at == '+' || at == '-' || at == '*' || at == '/' || at == '^') {
                if (at == '^') {
                    if (!nxt2op)
                        throw new ArithmeticException("not expecting bin operator");
                    chars.push(at);

                    nxtNum = true;
                    nxt1op = false;
                    nxt2op = false;
                    nxtBro = true;
                    nxtBrc = false;
                }
                if (at == '*' || at == '/') {
                    if (!nxt2op)
                        throw new ArithmeticException("not expecting bin operator");
                    while (chars.peek() == '^' || chars.peek() == '@' || chars.peek() == '*' ||
                            chars.peek() == '/') {
                        postfix.add(chars.peek().hashCode());
                        chars.pop();
                    }
                    chars.push(at);

                    nxtNum = true;
                    nxt1op = false;
                    nxt2op = false;
                    nxtBro = true;
                    nxtBrc = false;
                }
                else if (at == '+' || at == '-') {
                    if (nxt2op) {
                        while (chars.peek() == '^' || chars.peek() == '@' || chars.peek() == '*' ||
                                chars.peek() == '/' || chars.peek() == '+' || chars.peek() == '-') {
                            postfix.add(chars.peek().hashCode());
                            chars.pop();
                        }
                        chars.push(at);

                        nxtNum = true;
                        nxt1op = false;
                        nxt2op = false;
                        nxtBro = true;
                        nxtBrc = false;
                    }
                    else {
                        if (!nxt1op)
                            throw new ArithmeticException("not expecting operator");
                        if (at == '-') {
                            while (chars.peek() == '^') {
                                postfix.add(chars.peek().hashCode());
                                chars.pop();
                            }
                            chars.push('@');
                        }

                        nxtNum = true;
                        nxt1op = false;
                        nxt2op = false;
                        nxtBro = true;
                        nxtBrc= false;
                    }
                }
            }
            else if (at != ' ')
                throw new ArithmeticException("unexpected character");
        }
        if (lvl != 0)
            throw new ArithmeticException("unbalanced braces");

        while (chars.peek() != '(') {
            postfix.add(chars.peek().hashCode());
            chars.pop();
        }

        return solvePostfix(postfix, nums);
    }

    public boolean setVariable(String name, BigDecimal num) {
        if (name.isEmpty() || (!isVar(name.charAt(0))))
            return false;
        for (int i = 1; i < name.length(); i++) {
            if ((!isVar(name.charAt(i))) && (name.charAt(i) < '0' || name.charAt(i) > '9'))
                return false;
        }
        vars.put(name, num);
        return true;
    }

}
