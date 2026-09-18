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
        // Write your solution here
        return false;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(str(is_valid(s)).lower())
""",
                """
const fs = require('fs');

function isValid(s) {
    // Write your solution here
    return null;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    solve()
""",
                """
const fs = require('fs');

function solve() {
    // Write your solution here
    return null;
}

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
        // Write your solution here
        return 0;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        tokens = [x.strip() for x in line.split(",")]
        print(eval_rpn(tokens))
""",
                """
const fs = require('fs');

function evalRPN(tokens) {
    // Write your solution here
    return null;
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
        // Write your solution here
        return new int[]{};
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
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        temps = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, daily_temperatures(temps)))}]")
""",
                """
const fs = require('fs');

function dailyTemperatures(temperatures) {
    // Write your solution here
    return null;
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
        // Write your solution here
        return 0;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        h = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(largest_rectangle_area(h))
""",
                """
const fs = require('fs');

function largestRectangleArea(heights) {
    // Write your solution here
    return null;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    solve()
""",
                """
const fs = require('fs');

function solve() {
    // Write your solution here
    return null;
}

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
        // Write your solution here
        return new int[]{};
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
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        ast = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, asteroid_collision(ast)))}]")
""",
                """
const fs = require('fs');

function asteroidCollision(asteroids) {
    // Write your solution here
    return null;
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
        // Write your solution here
        return 0;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(calculate(s))
""",
                """
const fs = require('fs');

function calculate(s) {
    // Write your solution here
    return null;
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
    # Write your solution here
    pass

if __name__ == "__main__":
    solve()
""",
                """
const fs = require('fs');

function solve() {
    // Write your solution here
    return null;
}

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
        // Write your solution here
        return new int[]{};
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
    # Write your solution here
    pass

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
    // Write your solution here
    return null;
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
        // Write your solution here
        return new ArrayList<>();
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
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        prices = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(f"[{', '.join(map(str, stock_spans(prices)))}]")
""",
                """
const fs = require('fs');

function stockSpans(prices) {
    // Write your solution here
    return null;
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
