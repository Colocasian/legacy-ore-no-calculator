package io.colocasian.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

public class Expression {
    private HashMap<String, Double> vars;

    public Expression() {
        this.vars = new HashMap<>();
    }

    private static boolean isNum(char c) {
        return ((c >= '0' && c <= '9') || c == '.');
    }

    private static boolean isVar(char c) {
        return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_'));
    }

    private static double solveFormula(String name, double[] params) throws NoSuchElementException {
        switch (name + "#" + Integer.toString(params.length)) {
            case "sin#1":
                return Math.sin(params[0]);
            case "cos#1":
                return Math.cos(params[0]);
            case "tan#1":
                return Math.tan(params[0]);
            case "asin#1":
                return Math.asin(params[0]);
            case "acos#1":
                return Math.acos(params[0]);
            case "atan#1":
                return Math.atan(params[0]);

            case "exp#1":
                return Math.exp(params[0]);
            case "expm1#1":
                return Math.expm1(params[0]);
            case "log#1":
                return Math.log(params[0]);
            case "log10#1":
                return Math.log10(params[0]);
            case "log1p#1":
                return Math.log1p(params[0]);
            case "log#2":
                return (Math.log(params[0]) / Math.log(params[1]));

            case "sqrt#1":
                return Math.sqrt(params[0]);
            case "cbrt#1":
                return Math.cbrt(params[0]);
            case "hypot#2":
                return Math.hypot(params[0], params[1]);

            default:
                throw new NoSuchElementException("no such function yet");
        }
    }

    private static double solvePostfix(ArrayList<Integer> postNote, ArrayList<Double> numList) {
        Stack<Double> boya = new Stack<>();
        for (int i = 0; i < postNote.size(); i++) {
            int at = postNote.get(i);
            if (at <= 0)
                boya.push(numList.get(-at));
            else {
                double a, b;
                switch ((char)at) {
                    case '^':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(Math.pow(a, b));
                        break;

                    case '@':
                        a = boya.peek();
                        boya.pop();
                        boya.push(-a);
                        break;

                    case '*':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a * b);
                        break;

                    case '/':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a / b);
                        break;

                    case '+':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a + b);
                        break;

                    case '-':
                        b = boya.peek();
                        boya.pop();
                        a = boya.peek();
                        boya.pop();
                        boya.push(a - b);
                        break;
                }
            }
        }

        return boya.peek();
    }

    public double evaluate(String infix) throws ArithmeticException, NoSuchElementException {
        if (infix.trim().isEmpty())
            throw new ArithmeticException("empty string passed");
        ArrayList<Double> nums = new ArrayList<>();
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
                nums.add(Double.parseDouble(infix.substring(i, j)));
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
                if (j != infix.length() && infix.charAt(j) == '(') {
                    int k = j+1;
                    int funcLvl = 1;
                    while (k != infix.length() && funcLvl != 0) {
                        switch (infix.charAt(k)) {
                            case '(':
                                funcLvl++;
                                break;
                            case ')':
                                funcLvl--;
                                break;
                        }
                        k++;
                    }
                    String[] paramStr = infix.substring(j+1, k-1).split(",");
                    int paramNum = paramStr.length;
                    double[] paramDbl = new double[paramNum];

                    for (int l = 0; l < paramNum; l++)
                        paramDbl[l] = this.evaluate(paramStr[l]);

                    postfix.add(-nums.size());
                    nums.add(solveFormula(varName, paramDbl));
                    i = k-1;
                }
                else {
                    if (!vars.containsKey(varName))
                        throw new NoSuchElementException("no such variable exists");
                    postfix.add(-nums.size());
                    nums.add(vars.get(varName));
                    i = j-1;
                }

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

    public boolean setVariable(String name, double num) {
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
