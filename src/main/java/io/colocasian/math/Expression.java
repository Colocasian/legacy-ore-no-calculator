package io.colocasian.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

public class Expression {
    private HashMap<String, BigDecimal> vars;

    public Expression() {
        this.vars = new HashMap<>();
    }

    private static boolean isNum(char c) {
        return ((c >= '0' && c <= '9') || c == '.');
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

    public BigDecimal evaluate(String infix) throws ArithmeticException {
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
                int j = i;
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
            else if (at == '+' || at == '-' || at == '*' || at == '/') {
                if (at == '*' || at == '/' || at == '&') {
                    if (!nxt2op)
                        throw new ArithmeticException("not expecting bin operator");
                    while (chars.peek() == '*' || chars.peek() == '/' || chars.peek() == '&') {
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
                        while (chars.peek() == '*' || chars.peek() == '/' || chars.peek() == '+' ||
                                chars.peek() == '-' || chars.peek() == '@') {
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
                            while (chars.peek() == '@') {
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
        Pattern r = Pattern.compile("[^A-Za-z_]");
        Matcher m = r.matcher(name);
        if (m.lookingAt())
            return false;
        vars.put(name, num);
        return true;
    }

}
