package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackQueueCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Valid Parentheses
        map.put("valid-parentheses", new ChallengeProblemDef(
                "valid-parentheses",
                """
import java.util.*;

public class Solution {
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') stack.push(')');
            else if (c == '{') stack.push('}');
            else if (c == '[') stack.push(']');
            else if (stack.isEmpty() || stack.pop() != c) return false;
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        System.out.println(isValid(s));
    }
}
""",
                """
import sys

def is_valid(s: str) -> bool:
    stack = []
    mapping = {")": "(", "}": "{", "]": "["}
    for char in s:
        if char in mapping:
            top = stack.pop() if stack else '#'
            if mapping[char] != top:
                return False
        else:
            stack.append(char)
    return not stack

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(str(is_valid(s)).lower())
""",
                """
const fs = require('fs');

function isValid(s) {
    const stack = [];
    const map = { ')': '(', '}': '{', ']': '[' };
    for (const ch of s) {
        if (ch === '(' || ch === '{' || ch === '[') {
            stack.push(ch);
        } else if (map[ch]) {
            if (stack.pop() !== map[ch]) return false;
        }
    }
    return stack.length === 0;
}

const s = fs.readFileSync(0, 'utf-8').trim();
console.log(isValid(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "()[]{}", "true", "Matching parentheses in sequential order."),
                        TestCaseDef.publicCase(2, "(]", "false", "Mismatched bracket types."),
                        TestCaseDef.hiddenCase(3, "{[]}", "true", "Properly nested brackets."),
                        TestCaseDef.hiddenCase(4, "([)]", "false", "Interleaved invalid order.")
                )
        ));

        // 2. Min Stack
        map.put("min-stack", new ChallengeProblemDef(
                "min-stack",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Stack<Integer> stack = new Stack<>();
        Stack<Integer> minStack = new Stack<>();
        List<Integer> out = new ArrayList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\\\s+");
            String op = parts[0];
            if (op.equals("push")) {
                int val = Integer.parseInt(parts[1]);
                stack.push(val);
                if (minStack.isEmpty() || val <= minStack.peek()) minStack.push(val);
            } else if (op.equals("pop")) {
                int val = stack.pop();
                if (val == minStack.peek()) minStack.pop();
            } else if (op.equals("top")) {
                out.add(stack.peek());
            } else if (op.equals("getMin")) {
                out.add(minStack.peek());
            }
        }
        System.out.println(out);
    }
}
""",
                """
import sys

def solve():
    lines = sys.stdin.read().strip().splitlines()
    stack = []
    min_stack = []
    out = []
    for line in lines:
        parts = line.strip().split()
        if not parts:
            continue
        op = parts[0]
        if op == "push":
            val = int(parts[1])
            stack.append(val)
            if not min_stack or val <= min_stack[-1]:
                min_stack.append(val)
        elif op == "pop":
            val = stack.pop()
            if val == min_stack[-1]:
                min_stack.pop()
        elif op == "top":
            out.append(stack[-1])
        elif op == "getMin":
            out.append(min_stack[-1])
    print(str(out))

if __name__ == "__main__":
    solve()
""",
                """
const fs = require('fs');

function solve() {
    const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
    const stack = [];
    const minStack = [];
    const out = [];
    for (const line of lines) {
        const parts = line.trim().split(/\\s+/);
        if (!parts[0]) continue;
        const op = parts[0];
        if (op === "push") {
            const val = parseInt(parts[1], 10);
            stack.push(val);
            if (!minStack.length || val <= minStack[minStack.length - 1]) minStack.push(val);
        } else if (op === "pop") {
            const val = stack.pop();
            if (val === minStack[minStack.length - 1]) minStack.pop();
        } else if (op === "top") {
            out.push(stack[stack.length - 1]);
        } else if (op === "getMin") {
            out.push(minStack[minStack.length - 1]);
        }
    }
    console.log(JSON.stringify(out).replace(/,/g, ', '));
}

solve();
""",
                List.of(
                        TestCaseDef.publicCase(1, "push -2\npush 0\npush -3\ngetMin\npop\ntop\ngetMin", "[-3, 0, -2]", "MinStack tracks min in O(1)."),
                        TestCaseDef.publicCase(2, "push 1\npush 2\ntop\ngetMin", "[2, 1]", "Top returns 2, getMin returns 1.")
                )
        ));

        // 3. Evaluate Reverse Polish Notation
        map.put("evaluate-reverse-polish-notation", new ChallengeProblemDef(
                "evaluate-reverse-polish-notation",
                """
import java.util.*;

public class Solution {
    public static int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        for (String t : tokens) {
            if (t.equals("+")) stack.push(stack.pop() + stack.pop());
            else if (t.equals("-")) {
                int b = stack.pop(), a = stack.pop();
                stack.push(a - b);
            } else if (t.equals("*")) stack.push(stack.pop() * stack.pop());
            else if (t.equals("/")) {
                int b = stack.pop(), a = stack.pop();
                stack.push(a / b);
            } else stack.push(Integer.parseInt(t));
        }
        return stack.pop();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] tokens = line.split(",");
        for (int i = 0; i < tokens.length; i++) tokens[i] = tokens[i].trim();
        System.out.println(evalRPN(tokens));
    }
}
""",
                """
import sys

def eval_rpn(tokens: list[str]) -> int:
    stack = []
    for t in tokens:
        if t in "+-*/":
            b, a = stack.pop(), stack.pop()
            if t == "+": stack.append(a + b)
            elif t == "-": stack.append(a - b)
            elif t == "*": stack.append(a * b)
            elif t == "/": stack.append(int(a / b))
        else:
            stack.append(int(t))
    return stack[0]

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        tokens = [x.strip() for x in line.split(",")]
        print(eval_rpn(tokens))
""",
                """
const fs = require('fs');

function evalRPN(tokens) {
    const stack = [];
    for (const t of tokens) {
        if (t === "+") stack.push(stack.pop() + stack.pop());
        else if (t === "-") {
            const b = stack.pop(), a = stack.pop();
            stack.push(a - b);
        } else if (t === "*") stack.push(stack.pop() * stack.pop());
        else if (t === "/") {
            const b = stack.pop(), a = stack.pop();
            stack.push(Math.trunc(a / b));
        } else {
            stack.push(parseInt(t, 10));
        }
    }
    return stack.pop();
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const tokens = line.split(',').map(s => s.trim());
    console.log(evalRPN(tokens));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,1,+,3,*", "9", "((2 + 1) * 3) = 9."),
                        TestCaseDef.publicCase(2, "4,13,5,/,+", "6", "(4 + (13 / 5)) = 6."),
                        TestCaseDef.hiddenCase(3, "10,6,9,3,+,-11,*,/,*,17,+,5,+", "22", "Complex RPN expression evaluates to 22.")
                )
        ));

        // 4. Daily Temperatures
        map.put("daily-temperatures", new ChallengeProblemDef(
                "daily-temperatures",
                """
import java.util.*;

public class Solution {
    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] res = new int[n];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prev = stack.pop();
                res[prev] = i - prev;
            }
            stack.push(i);
        }
        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] temps = new int[parts.length];
        for (int i = 0; i < parts.length; i++) temps[i] = Integer.parseInt(parts[i].trim());
        System.out.println(Arrays.toString(dailyTemperatures(temps)));
    }
}
""",
                """
import sys

def daily_temperatures(temperatures: list[int]) -> list[int]:
    n = len(temperatures)
    res = [0] * n
    stack = []
    for i, t in enumerate(temperatures):
        while stack and t > temperatures[stack[-1]]:
            prev = stack.pop()
            res[prev] = i - prev
        stack.append(i)
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        temps = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, daily_temperatures(temps)))}]")
""",
                """
const fs = require('fs');

function dailyTemperatures(temperatures) {
    const n = temperatures.length;
    const res = new Array(n).fill(0);
    const stack = [];
    for (let i = 0; i < n; i++) {
        while (stack.length && temperatures[i] > temperatures[stack[stack.length - 1]]) {
            const prev = stack.pop();
            res[prev] = i - prev;
        }
        stack.push(i);
    }
    return res;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const temps = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(`[${dailyTemperatures(temps).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "73,74,75,71,69,72,76,73", "[1, 1, 4, 2, 1, 1, 0, 0]", "Days until warmer temperature."),
                        TestCaseDef.publicCase(2, "30,40,50,60", "[1, 1, 1, 0]", "Strictly increasing temperatures."),
                        TestCaseDef.hiddenCase(3, "30,60,90", "[1, 1, 0]", "Three elements")
                )
        ));

        // 5. Largest Rectangle in Histogram
        map.put("largest-rectangle-in-histogram", new ChallengeProblemDef(
                "largest-rectangle-in-histogram",
                """
import java.util.*;

public class Solution {
    public static int largestRectangleArea(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int n = heights.length;
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] h = new int[parts.length];
        for (int i = 0; i < parts.length; i++) h[i] = Integer.parseInt(parts[i].trim());
        System.out.println(largestRectangleArea(h));
    }
}
""",
                """
import sys

def largest_rectangle_area(heights: list[int]) -> int:
    stack = []
    max_area = 0
    heights.append(0)
    for i, h in enumerate(heights):
        while stack and h < heights[stack[-1]]:
            height = heights[stack.pop()]
            width = i if not stack else i - stack[-1] - 1
            max_area = max(max_area, height * width)
        stack.append(i)
    return max_area

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        h = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(largest_rectangle_area(h))
""",
                """
const fs = require('fs');

function largestRectangleArea(heights) {
    const stack = [];
    let maxArea = 0;
    const extended = [...heights, 0];
    for (let i = 0; i < extended.length; i++) {
        const h = extended[i];
        while (stack.length && h < extended[stack[stack.length - 1]]) {
            const height = extended[stack.pop()];
            const width = stack.length === 0 ? i : i - stack[stack.length - 1] - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    return maxArea;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const h = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(largestRectangleArea(h));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,1,5,6,2,3", "10", "The largest rectangle has area = 10 units (height 5 and 6, width 2)."),
                        TestCaseDef.publicCase(2, "2,4", "4", "Max area is 4."),
                        TestCaseDef.hiddenCase(3, "1", "1", "Single bar"),
                        TestCaseDef.hiddenCase(4, "6,2,5,4,5,1,6", "12", "Sub-histogram with height 4 across width 3 gives 12")
                )
        ));

        // 6. LRU Cache Design
        map.put("lru-cache-design", new ChallengeProblemDef(
                "lru-cache-design",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LinkedHashMap<Integer, Integer> cache = new LinkedHashMap<>(2, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > 2;
            }
        };
        List<Integer> out = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\\\s+");
            if (p[0].equals("put")) {
                cache.put(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
            } else if (p[0].equals("get")) {
                int k = Integer.parseInt(p[1]);
                out.add(cache.getOrDefault(k, -1));
            }
        }
        System.out.println(out);
    }
}
""",
                """
import sys
from collections import OrderedDict

def solve():
    lines = sys.stdin.read().strip().splitlines()
    cache = OrderedDict()
    capacity = 2
    out = []
    for line in lines:
        parts = line.strip().split()
        if not parts:
            continue
        if parts[0] == "put":
            k, v = int(parts[1]), int(parts[2])
            if k in cache:
                cache.move_to_end(k)
            cache[k] = v
            if len(cache) > capacity:
                cache.popitem(last=False)
        elif parts[0] == "get":
            k = int(parts[1])
            if k in cache:
                cache.move_to_end(k)
                out.append(cache[k])
            else:
                out.append(-1)
    print(str(out))

if __name__ == "__main__":
    solve()
""",
                """
const fs = require('fs');

function solve() {
    const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
    const map = new Map();
    const capacity = 2;
    const out = [];
    for (const line of lines) {
        const parts = line.trim().split(/\\s+/);
        if (!parts[0]) continue;
        if (parts[0] === "put") {
            const k = parseInt(parts[1], 10), v = parseInt(parts[2], 10);
            if (map.has(k)) map.delete(k);
            map.set(k, v);
            if (map.size > capacity) {
                const firstKey = map.keys().next().value;
                map.delete(firstKey);
            }
        } else if (parts[0] === "get") {
            const k = parseInt(parts[1], 10);
            if (map.has(k)) {
                const val = map.get(k);
                map.delete(k);
                map.set(k, val);
                out.push(val);
            } else {
                out.push(-1);
            }
        }
    }
    console.log(JSON.stringify(out).replace(/,/g, ', '));
}

solve();
""",
                List.of(
                        TestCaseDef.publicCase(1, "put 1 1\nput 2 2\nget 1\nput 3 3\nget 2\nput 4 4\nget 1\nget 3\nget 4", "[1, -1, -1, 3, 4]", "Least recently used elements are evicted on overflow.")
                )
        ));

        // 7. Asteroid Collision
        map.put("asteroid-collision", new ChallengeProblemDef(
                "asteroid-collision",
                """
import java.util.*;

public class Solution {
    public static int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();
        for (int ast : asteroids) {
            boolean alive = true;
            while (alive && ast < 0 && !stack.isEmpty() && stack.peek() > 0) {
                alive = stack.peek() < -ast;
                if (stack.peek() <= -ast) stack.pop();
            }
            if (alive) stack.push(ast);
        }
        int[] res = new int[stack.size()];
        for (int i = res.length - 1; i >= 0; i--) res[i] = stack.pop();
        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] ast = new int[parts.length];
        for (int i = 0; i < parts.length; i++) ast[i] = Integer.parseInt(parts[i].trim());
        System.out.println(Arrays.toString(asteroidCollision(ast)));
    }
}
""",
                """
import sys

def asteroid_collision(asteroids: list[int]) -> list[int]:
    stack = []
    for ast in asteroids:
        alive = True
        while alive and ast < 0 and stack and stack[-1] > 0:
            alive = stack[-1] < -ast
            if stack[-1] <= -ast:
                stack.pop()
        if alive:
            stack.append(ast)
    return stack

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        ast = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, asteroid_collision(ast)))}]")
""",
                """
const fs = require('fs');

function asteroidCollision(asteroids) {
    const stack = [];
    for (const ast of asteroids) {
        let alive = true;
        while (alive && ast < 0 && stack.length && stack[stack.length - 1] > 0) {
            alive = stack[stack.length - 1] < -ast;
            if (stack[stack.length - 1] <= -ast) stack.pop();
        }
        if (alive) stack.push(ast);
    }
    return stack;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const ast = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(`[${asteroidCollision(ast).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "5,10,-5", "[5, 10]", "10 and -5 collide resulting in 10."),
                        TestCaseDef.publicCase(2, "8,-8", "[]", "Both destroy each other."),
                        TestCaseDef.hiddenCase(3, "10,2,-5", "[10]", "2 and -5 collide into -5, then 10 and -5 collide into 10."),
                        TestCaseDef.hiddenCase(4, "-2,-1,1,2", "[-2, -1, 1, 2]", "Moving in opposite directions away from each other.")
                )
        ));

        // 8. Basic Calculator
        map.put("basic-calculator", new ChallengeProblemDef(
                "basic-calculator",
                """
import java.util.*;

public class Solution {
    public static int calculate(String s) {
        Stack<Integer> stack = new Stack<>();
        int result = 0, number = 0, sign = 1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) number = 10 * number + (c - '0');
            else if (c == '+') {
                result += sign * number;
                number = 0;
                sign = 1;
            } else if (c == '-') {
                result += sign * number;
                number = 0;
                sign = -1;
            } else if (c == '(') {
                stack.push(result);
                stack.push(sign);
                sign = 1;
                result = 0;
            } else if (c == ')') {
                result += sign * number;
                number = 0;
                result *= stack.pop();
                result += stack.pop();
            }
        }
        if (number != 0) result += sign * number;
        return result;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        System.out.println(calculate(s));
    }
}
""",
                """
import sys

def calculate(s: str) -> int:
    stack = []
    res, num, sign = 0, 0, 1
    for c in s:
        if c.isdigit():
            num = 10 * num + int(c)
        elif c == '+':
            res += sign * num
            num, sign = 0, 1
        elif c == '-':
            res += sign * num
            num, sign = 0, -1
        elif c == '(':
            stack.append(res)
            stack.append(sign)
            res, sign = 0, 1
        elif c == ')':
            res += sign * num
            num = 0
            res *= stack.pop()
            res += stack.pop()
    if num:
        res += sign * num
    return res

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(calculate(s))
""",
                """
const fs = require('fs');

function calculate(s) {
    const stack = [];
    let res = 0, num = 0, sign = 1;
    for (let i = 0; i < s.length; i++) {
        const c = s[i];
        if (c >= '0' && c <= '9') {
            num = 10 * num + (c.charCodeAt(0) - 48);
        } else if (c === '+') {
            res += sign * num;
            num = 0;
            sign = 1;
        } else if (c === '-') {
            res += sign * num;
            num = 0;
            sign = -1;
        } else if (c === '(') {
            stack.push(res);
            stack.push(sign);
            res = 0;
            sign = 1;
        } else if (c === ')') {
            res += sign * num;
            num = 0;
            res *= stack.pop();
            res += stack.pop();
        }
    }
    if (num) res += sign * num;
    return res;
}

const s = fs.readFileSync(0, 'utf-8').trim();
console.log(calculate(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "1 + 1", "2", "Simple addition."),
                        TestCaseDef.publicCase(2, " 2-1 + 2 ", "3", "Multiple operations with whitespace."),
                        TestCaseDef.hiddenCase(3, "(1+(4+5+2)-3)+(6+8)", "23", "Nested parentheses."),
                        TestCaseDef.hiddenCase(4, "- (3 + (4 + 5))", "-12", "Negative outer sign.")
                )
        ));

        // 9. Implement Queue using Stacks
        map.put("implement-queue-using-stacks", new ChallengeProblemDef(
                "implement-queue-using-stacks",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Stack<Integer> in = new Stack<>();
        Stack<Integer> out = new Stack<>();
        List<Object> res = new ArrayList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\\\s+");
            String op = p[0];
            if (op.equals("push")) {
                in.push(Integer.parseInt(p[1]));
            } else if (op.equals("pop") || op.equals("peek")) {
                if (out.isEmpty()) {
                    while (!in.isEmpty()) out.push(in.pop());
                }
                if (op.equals("pop")) res.add(out.pop());
                else res.add(out.peek());
            } else if (op.equals("empty")) {
                res.add(in.isEmpty() && out.isEmpty());
            }
        }
        System.out.println(res);
    }
}
""",
                """
import sys

def solve():
    lines = sys.stdin.read().strip().splitlines()
    in_s, out_s = [], []
    res = []
    for line in lines:
        parts = line.strip().split()
        if not parts:
            continue
        op = parts[0]
        if op == "push":
            in_s.append(int(parts[1]))
        elif op in ("pop", "peek"):
            if not out_s:
                while in_s:
                    out_s.append(in_s.pop())
            if op == "pop":
                res.append(out_s.pop())
            else:
                res.append(out_s[-1])
        elif op == "empty":
            res.append(str(not in_s and not out_s).lower())
    print(f"[{', '.join(map(str, res))}]")

if __name__ == "__main__":
    solve()
""",
                """
const fs = require('fs');

function solve() {
    const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
    const inS = [], outS = [];
    const res = [];
    for (const line of lines) {
        const p = line.trim().split(/\\s+/);
        if (!p[0]) continue;
        const op = p[0];
        if (op === "push") {
            inS.push(parseInt(p[1], 10));
        } else if (op === "pop" || op === "peek") {
            if (!outS.length) {
                while (inS.length) outS.push(inS.pop());
            }
            if (op === "pop") res.push(outS.pop());
            else res.push(outS[outS.length - 1]);
        } else if (op === "empty") {
            res.push(inS.length === 0 && outS.length === 0);
        }
    }
    console.log(`[${res.join(', ')}]`);
}

solve();
""",
                List.of(
                        TestCaseDef.publicCase(1, "push 1\npush 2\npeek\npop\nempty", "[1, 1, false]", "FIFO queue operations.")
                )
        ));

        // 10. Next Greater Element I
        map.put("next-greater-element-i", new ChallengeProblemDef(
                "next-greater-element-i",
                """
import java.util.*;

public class Solution {
    public static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        for (int num : nums2) {
            while (!stack.isEmpty() && stack.peek() < num) {
                map.put(stack.pop(), num);
            }
            stack.push(num);
        }
        int[] res = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            res[i] = map.getOrDefault(nums1[i], -1);
        }
        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String line2 = sc.nextLine().trim();

        int[] n1 = Arrays.stream(line1.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        int[] n2 = Arrays.stream(line2.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        System.out.println(Arrays.toString(nextGreaterElement(n1, n2)));
    }
}
""",
                """
import sys

def next_greater_element(nums1: list[int], nums2: list[int]) -> list[int]:
    mapping = {}
    stack = []
    for n in nums2:
        while stack and stack[-1] < n:
            mapping[stack.pop()] = n
        stack.append(n)
    return [mapping.get(n, -1) for n in nums1]

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        n1 = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        n2 = [int(x.strip()) for x in lines[1].split(",") if x.strip()]
        print(f"[{', '.join(map(str, next_greater_element(n1, n2)))}]")
""",
                """
const fs = require('fs');

function nextGreaterElement(nums1, nums2) {
    const map = new Map();
    const stack = [];
    for (const n of nums2) {
        while (stack.length && stack[stack.length - 1] < n) {
            map.set(stack.pop(), n);
        }
        stack.push(n);
    }
    return nums1.map(n => map.has(n) ? map.get(n) : -1);
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const n1 = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const n2 = lines[1].split(',').map(s => parseInt(s.trim(), 10));
    console.log(`[${nextGreaterElement(n1, n2).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "4,1,2\n1,3,4,2", "[-1, 3, -1]", "Next greater element for 4 is -1, 1 is 3, 2 is -1."),
                        TestCaseDef.publicCase(2, "2,4\n1,2,3,4", "[3, -1]", "Next greater element for 2 is 3, 4 is -1.")
                )
        ));

        // 11. Online Stock Span
        map.put("online-stock-span", new ChallengeProblemDef(
                "online-stock-span",
                """
import java.util.*;

public class Solution {
    public static List<Integer> stockSpans(int[] prices) {
        List<Integer> res = new ArrayList<>();
        Stack<int[]> stack = new Stack<>();
        for (int p : prices) {
            int span = 1;
            while (!stack.isEmpty() && stack.peek()[0] <= p) {
                span += stack.pop()[1];
            }
            stack.push(new int[]{p, span});
            res.add(span);
        }
        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] prices = new int[parts.length];
        for (int i = 0; i < parts.length; i++) prices[i] = Integer.parseInt(parts[i].trim());
        System.out.println(stockSpans(prices));
    }
}
""",
                """
import sys

def stock_spans(prices: list[int]) -> list[int]:
    stack = []
    res = []
    for p in prices:
        span = 1
        while stack and stack[-1][0] <= p:
            span += stack.pop()[1]
        stack.append((p, span))
        res.append(span)
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        prices = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, stock_spans(prices)))}]")
""",
                """
const fs = require('fs');

function stockSpans(prices) {
    const stack = [];
    const res = [];
    for (const p of prices) {
        let span = 1;
        while (stack.length && stack[stack.length - 1][0] <= p) {
            span += stack.pop()[1];
        }
        stack.push([p, span]);
        res.push(span);
    }
    return res;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const prices = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(`[${stockSpans(prices).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "100,80,60,70,60,75,85", "[1, 1, 1, 2, 1, 4, 6]", "Daily consecutive days where price was less than or equal to current day.")
                )
        ));

        return map;
    }
}
