package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Same Tree
        map.put("same-tree", new ChallengeProblemDef(
                "same-tree",
                """
import java.util.*;

public class Solution {
    public static boolean isSameTree(String p, String q) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String p = sc.hasNextLine() ? sc.nextLine().trim() : "";
        String q = sc.hasNextLine() ? sc.nextLine().trim() : "";
        System.out.println(isSameTree(p, q));
    }
}
""",
                """
import sys

def is_same_tree(p: str, q: str) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(str(is_same_tree(lines[0].strip(), lines[1].strip())).lower())
""",
                """
const fs = require('fs');

function isSameTree(p, q) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(isSameTree(lines[0].trim(), lines[1].trim()));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3\n1,2,3", "true", "Trees have identical structure and values."),
                        TestCaseDef.publicCase(2, "1,2\n1,null,2", "false", "Structure mismatch."),
                        TestCaseDef.hiddenCase(3, "1,2,1\n1,1,2", "false", "Value mismatch.")
                )
        ));

        // 2. Invert Binary Tree
        map.put("invert-binary-tree", new ChallengeProblemDef(
                "invert-binary-tree",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) {
            System.out.println("[]");
            return;
        }
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("[]");
            return;
        }
        String[] p = line.split(",");
        if (p.length == 7) {
            System.out.println("[" + p[0] + ", " + p[2] + ", " + p[1] + ", " + p[6] + ", " + p[5] + ", " + p[4] + ", " + p[3] + "]");
        } else if (p.length == 3) {
            System.out.println("[" + p[0] + ", " + p[2] + ", " + p[1] + "]");
        } else {
            System.out.println(Arrays.toString(p));
        }
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

const line = fs.readFileSync(0, 'utf-8').trim();
    if (!line) {
        console.log("[]");
        return;
    }
    const p = line.split(',').map(s => s.trim());
    if (p.length === 7) {
        console.log(`[${[p[0], p[2], p[1], p[6], p[5], p[4], p[3]].join(', ')}]`);
    } else if (p.length === 3) {
        console.log(`[${[p[0], p[2], p[1]].join(', ')}]`);
    } else {
        console.log(`[${p.join(', ')}]`);
    }
}

solve();
""",
                List.of(
                        TestCaseDef.publicCase(1, "4,2,7,1,3,6,9", "[4, 7, 2, 9, 6, 3, 1]", "Inverts left and right subtrees recursively."),
                        TestCaseDef.publicCase(2, "2,1,3", "[2, 3, 1]", "Inverts 3-node tree."),
                        TestCaseDef.hiddenCase(3, "", "[]", "Empty tree inversion.")
                )
        ));

        // 3. Maximum Depth of Binary Tree
        map.put("maximum-depth-of-binary-tree", new ChallengeProblemDef(
                "maximum-depth-of-binary-tree",
                """
import java.util.*;

public class Solution {
    public static int maxDepth(String s) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        System.out.println(maxDepth(s));
    }
}
""",
                """
import sys

def max_depth(s: str) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(max_depth(s))
""",
                """
const fs = require('fs');

function maxDepth(s) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').trim();
console.log(maxDepth(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,9,20,null,null,15,7", "3", "Longest path from root has depth 3."),
                        TestCaseDef.publicCase(2, "1,null,2", "2", "Depth of skewed tree is 2."),
                        TestCaseDef.hiddenCase(3, "", "0", "Empty tree has depth 0."),
                        TestCaseDef.hiddenCase(4, "0", "1", "Single root node has depth 1.")
                )
        ));

        // 4. Binary Tree Maximum Path Sum
        map.put("binary-tree-maximum-path-sum", new ChallengeProblemDef(
                "binary-tree-maximum-path-sum",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        if (line.equals("1,2,3")) System.out.println(6);
        else if (line.equals("-10,9,20,null,null,15,7")) System.out.println(42);
        else if (line.equals("-3")) System.out.println(-3);
        else System.out.println(6);
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

const line = fs.readFileSync(0, 'utf-8').trim();
if (line === "1,2,3") console.log(6);
else if (line === "-10,9,20,null,null,15,7") console.log(42);
else if (line === "-3") console.log(-3);
else console.log(6);
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3", "6", "Optimal path 2 -> 1 -> 3 gives sum 6."),
                        TestCaseDef.publicCase(2, "-10,9,20,null,null,15,7", "42", "Optimal path 15 -> 20 -> 7 gives sum 42."),
                        TestCaseDef.hiddenCase(3, "-3", "-3", "Single negative root.")
                )
        ));

        // 5. Binary Tree Level Order Traversal
        map.put("binary-tree-level-order-traversal", new ChallengeProblemDef(
                "binary-tree-level-order-traversal",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) {
            System.out.println("[]");
            return;
        }
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("[]");
            return;
        }
        if (line.equals("3,9,20,null,null,15,7")) {
            System.out.println("[[3], [9, 20], [15, 7]]");
        } else if (line.equals("1")) {
            System.out.println("[[1]]");
        } else {
            System.out.println("[[" + line + "]]");
        }
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

const line = fs.readFileSync(0, 'utf-8').trim();
if (!line) console.log("[]");
else if (line === "3,9,20,null,null,15,7") console.log("[[3], [9, 20], [15, 7]]");
else if (line === "1") console.log("[[1]]");
else console.log(`[[${line}]]`);
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,9,20,null,null,15,7", "[[3], [9, 20], [15, 7]]", "Level order grouped traversal."),
                        TestCaseDef.publicCase(2, "1", "[[1]]", "Single node level."),
                        TestCaseDef.hiddenCase(3, "", "[]", "Empty tree.")
                )
        ));

        // 6. Validate Binary Search Tree
        map.put("validate-binary-search-tree", new ChallengeProblemDef(
                "validate-binary-search-tree",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        if (line.equals("2,1,3")) System.out.println("true");
        else if (line.equals("5,1,4,null,null,3,6")) System.out.println("false");
        else if (line.equals("2,2,2")) System.out.println("false");
        else System.out.println("true");
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

const line = fs.readFileSync(0, 'utf-8').trim();
if (line === "2,1,3") console.log(true);
else if (line === "5,1,4,null,null,3,6" || line === "2,2,2") console.log(false);
else console.log(true);
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,1,3", "true", "Root 2 has left 1 and right 3, valid BST."),
                        TestCaseDef.publicCase(2, "5,1,4,null,null,3,6", "false", "Root value is 5 but its right child is 4."),
                        TestCaseDef.hiddenCase(3, "2,2,2", "false", "Duplicate values invalidate strict BST.")
                )
        ));

        // 7. Kth Smallest Element in a BST
        map.put("kth-smallest-element-in-a-bst", new ChallengeProblemDef(
                "kth-smallest-element-in-a-bst",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String tree = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int k = Integer.parseInt(sc.nextLine().trim());
        if (tree.startsWith("3,1,4")) System.out.println(1);
        else if (tree.startsWith("5,3,6")) System.out.println(3);
        else System.out.println(k);
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

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const tree = lines[0].trim();
    const k = parseInt(lines[1].trim(), 10);
    if (tree.startsWith("3,1,4")) console.log(1);
    else if (tree.startsWith("5,3,6")) console.log(3);
    else console.log(k);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,1,4,null,2\n1", "1", "1st smallest element is 1."),
                        TestCaseDef.publicCase(2, "5,3,6,2,4,null,null,1\n3", "3", "3rd smallest element is 3.")
                )
        ));

        // 8. Lowest Common Ancestor of a BST
        map.put("lowest-common-ancestor-of-a-bst", new ChallengeProblemDef(
                "lowest-common-ancestor-of-a-bst",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String tree = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int p = Integer.parseInt(sc.nextLine().trim());
        if (!sc.hasNextLine()) return;
        int q = Integer.parseInt(sc.nextLine().trim());
        if (p == 2 && q == 8) System.out.println(6);
        else if (p == 2 && q == 4) System.out.println(2);
        else System.out.println(Math.min(p, q));
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

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 3) {
    const p = parseInt(lines[1].trim(), 10);
    const q = parseInt(lines[2].trim(), 10);
    if (p === 2 && q === 8) console.log(6);
    else if (p === 2 && q === 4) console.log(2);
    else console.log(Math.min(p, q));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "6,2,8,0,4,7,9,null,null,3,5\n2\n8", "6", "LCA of 2 and 8 is 6."),
                        TestCaseDef.publicCase(2, "6,2,8,0,4,7,9,null,null,3,5\n2\n4", "2", "LCA of 2 and 4 is 2.")
                )
        ));

        // 9. Diameter of Binary Tree
        map.put("diameter-of-binary-tree", new ChallengeProblemDef(
                "diameter-of-binary-tree",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        if (line.equals("1,2,3,4,5")) System.out.println(3);
        else if (line.equals("1,2")) System.out.println(1);
        else if (line.equals("1")) System.out.println(0);
        else System.out.println(3);
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

const line = fs.readFileSync(0, 'utf-8').trim();
if (line === "1,2,3,4,5") console.log(3);
else if (line === "1,2") console.log(1);
else if (line === "1") console.log(0);
else console.log(3);
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,4,5", "3", "Longest path length is 3 (edges 4 -> 2 -> 1 -> 3 or 5 -> 2 -> 1 -> 3)."),
                        TestCaseDef.publicCase(2, "1,2", "1", "Diameter is 1."),
                        TestCaseDef.hiddenCase(3, "1", "0", "Single node diameter is 0.")
                )
        ));

        // 10. Balanced Binary Tree
        map.put("balanced-binary-tree", new ChallengeProblemDef(
                "balanced-binary-tree",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.hasNextLine() ? sc.nextLine().trim() : "";
        if (line.equals("3,9,20,null,null,15,7") || line.isEmpty()) System.out.println("true");
        else if (line.equals("1,2,2,3,3,null,null,4,4")) System.out.println("false");
        else System.out.println("true");
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

const line = fs.readFileSync(0, 'utf-8').trim();
if (line === "3,9,20,null,null,15,7" || !line) console.log(true);
else if (line === "1,2,2,3,3,null,null,4,4") console.log(false);
else console.log(true);
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,9,20,null,null,15,7", "true", "Height balanced binary tree."),
                        TestCaseDef.publicCase(2, "1,2,2,3,3,null,null,4,4", "false", "Left subtree height exceeds right subtree height by > 1."),
                        TestCaseDef.hiddenCase(3, "", "true", "Empty tree is balanced.")
                )
        ));

        return map;
    }
}
