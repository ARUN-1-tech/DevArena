package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BacktrackingCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Subsets
        map.put("subsets", new ChallengeProblemDef(
                "subsets",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }

    private static void backtrack(List<List<Integer>> list, List<Integer> tempList, int[] nums, int start) {
        list.add(new ArrayList<>(tempList));
        for (int i = start; i < nums.length; i++) {
            tempList.add(nums[i]);
            backtrack(list, tempList, nums, i + 1);
            tempList.remove(tempList.size() - 1);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(subsets(nums));
    }
}
""",
                """
import sys

def subsets(nums: list[int]) -> list[list[int]]:
    res = []
    def backtrack(start, path):
        res.append(list(path))
        for i in range(start, len(nums)):
            path.append(nums[i])
            backtrack(i + 1, path)
            path.pop()
    backtrack(0, [])
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(subsets(nums)))
""",
                """
const fs = require('fs');

function subsets(nums) {
    const res = [];
    function backtrack(start, path) {
        res.push([...path]);
        for (let i = start; i < nums.length; i++) {
            path.push(nums[i]);
            backtrack(i + 1, path);
            path.pop();
        }
    }
    backtrack(0, []);
    return res;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(subsets(nums)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3", "[[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]]", "All power set subsets."),
                        TestCaseDef.publicCase(2, "0", "[[], [0]]", "Power set of single element.")
                )
        ));

        // 2. Permutations
        map.put("permutations", new ChallengeProblemDef(
                "permutations",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        backtrack(list, new ArrayList<>(), nums);
        return list;
    }

    private static void backtrack(List<List<Integer>> list, List<Integer> tempList, int[] nums) {
        if (tempList.size() == nums.length) {
            list.add(new ArrayList<>(tempList));
        } else {
            for (int i = 0; i < nums.length; i++) {
                if (tempList.contains(nums[i])) continue;
                tempList.add(nums[i]);
                backtrack(list, tempList, nums);
                tempList.remove(tempList.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(permute(nums));
    }
}
""",
                """
import sys

def permute(nums: list[int]) -> list[list[int]]:
    res = []
    def backtrack(curr):
        if len(curr) == len(nums):
            res.append(list(curr))
            return
        for n in nums:
            if n not in curr:
                curr.append(n)
                backtrack(curr)
                curr.pop()
    backtrack([])
    return res

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(permute(nums)))
""",
                """
const fs = require('fs');

function permute(nums) {
    const res = [];
    function backtrack(curr) {
        if (curr.length === nums.length) {
            res.push([...curr]);
            return;
        }
        for (const n of nums) {
            if (!curr.includes(n)) {
                curr.push(n);
                backtrack(curr);
                curr.pop();
            }
        }
    }
    backtrack([]);
    return res;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(permute(nums)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3", "[[1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]]", "All 6 permutations."),
                        TestCaseDef.publicCase(2, "0,1", "[[0, 1], [1, 0]]", "2 permutations."),
                        TestCaseDef.hiddenCase(3, "1", "[[1]]", "Single element permutation.")
                )
        ));

        // 3. Combination Sum
        map.put("combination-sum", new ChallengeProblemDef(
                "combination-sum",
                """
import java.util.*;

public class Solution {
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> list = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(list, new ArrayList<>(), candidates, target, 0);
        return list;
    }

    private static void backtrack(List<List<Integer>> list, List<Integer> tempList, int[] nums, int remain, int start) {
        if (remain < 0) return;
        else if (remain == 0) list.add(new ArrayList<>(tempList));
        else {
            for (int i = start; i < nums.length; i++) {
                tempList.add(nums[i]);
                backtrack(list, tempList, nums, remain - nums[i], i);
                tempList.remove(tempList.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int target = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(combinationSum(nums, target));
    }
}
""",
                """
import sys

def combination_sum(candidates: list[int], target: int) -> list[list[int]]:
    res = []
    candidates.sort()
    def backtrack(remain, curr, start):
        if remain == 0:
            res.append(list(curr))
            return
        for i in range(start, len(candidates)):
            if candidates[i] > remain:
                break
            curr.append(candidates[i])
            backtrack(remain - candidates[i], curr, i)
            curr.pop()
    backtrack(target, [], 0)
    return res

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        c = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        t = int(lines[1].strip())
        print(str(combination_sum(c, t)))
""",
                """
const fs = require('fs');

function combinationSum(candidates, target) {
    const res = [];
    candidates.sort((a, b) => a - b);
    function backtrack(remain, curr, start) {
        if (remain === 0) {
            res.push([...curr]);
            return;
        }
        for (let i = start; i < candidates.length; i++) {
            if (candidates[i] > remain) break;
            curr.push(candidates[i]);
            backtrack(remain - candidates[i], curr, i);
            curr.pop();
        }
    }
    backtrack(target, [], 0);
    return res;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const c = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const t = parseInt(lines[1].trim(), 10);
    console.log(JSON.stringify(combinationSum(c, t)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,3,6,7\n7", "[[2, 2, 3], [7]]", "Combinations summing to 7."),
                        TestCaseDef.publicCase(2, "2,3,5\n8", "[[2, 2, 2, 2], [2, 3, 3], [3, 5]]", "Combinations summing to 8."),
                        TestCaseDef.hiddenCase(3, "2\n1", "[]", "No combination reaches target 1.")
                )
        ));

        // 4. Letter Combinations of a Phone Number
        map.put("letter-combinations-of-a-phone-number", new ChallengeProblemDef(
                "letter-combinations-of-a-phone-number",
                """
import java.util.*;

public class Solution {
    private static final String[] MAPPINGS = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    public static List<String> letterCombinations(String digits) {
        List<String> res = new ArrayList<>();
        if (digits == null || digits.isEmpty()) return res;
        backtrack(res, new StringBuilder(), digits, 0);
        return res;
    }

    private static void backtrack(List<String> res, StringBuilder sb, String digits, int index) {
        if (index == digits.length()) {
            res.add(sb.toString());
            return;
        }
        String letters = MAPPINGS[digits.charAt(index) - '0'];
        for (char c : letters.toCharArray()) {
            sb.append(c);
            backtrack(res, sb, digits, index + 1);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        System.out.println(letterCombinations(s));
    }
}
""",
                """
import sys

MAPPINGS = ["", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"]

def letter_combinations(digits: str) -> list[str]:
    if not digits:
        return []
    res = []
    def backtrack(idx, curr):
        if idx == len(digits):
            res.append(curr)
            return
        for char in MAPPINGS[int(digits[idx])]:
            backtrack(idx + 1, curr + char)
    backtrack(0, "")
    return res

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    res = letter_combinations(s)
    print(str(res).replace("'", '"'))
""",
                """
const fs = require('fs');

const MAPPINGS = ["", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"];

function letterCombinations(digits) {
    if (!digits) return [];
    const res = [];
    function backtrack(idx, curr) {
        if (idx === digits.length) {
            res.push(curr);
            return;
        }
        for (const char of MAPPINGS[parseInt(digits[idx], 10)]) {
            backtrack(idx + 1, curr + char);
        }
    }
    backtrack(0, "");
    return res;
}

const s = fs.readFileSync(0, 'utf-8').trim();
console.log(JSON.stringify(letterCombinations(s)).replace(/,/g, ', '));
""",
                List.of(
                        TestCaseDef.publicCase(1, "23", "[\"ad\", \"ae\", \"af\", \"bd\", \"be\", \"bf\", \"cd\", \"ce\", \"cf\"]", "Phone keypad letter combinations."),
                        TestCaseDef.publicCase(2, "", "[]", "Empty digits returns []."),
                        TestCaseDef.hiddenCase(3, "2", "[\"a\", \"b\", \"c\"]", "Single digit returns its mapped characters.")
                )
        ));

        // 5. Word Search
        map.put("word-search", new ChallengeProblemDef(
                "word-search",
                """
import java.util.*;

public class Solution {
    public static boolean exist(char[][] board, String word) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (dfs(board, word, r, c, 0)) return true;
            }
        }
        return false;
    }

    private static boolean dfs(char[][] board, String word, int r, int c, int k) {
        if (k == word.length()) return true;
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != word.charAt(k)) return false;
        char temp = board[r][c];
        board[r][c] = '#';
        boolean found = dfs(board, word, r + 1, c, k + 1) ||
                        dfs(board, word, r - 1, c, k + 1) ||
                        dfs(board, word, r, c + 1, k + 1) ||
                        dfs(board, word, r, c - 1, k + 1);
        board[r][c] = temp;
        return found;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String word = sc.nextLine().trim();
        String[] rows = line1.split(",");
        char[][] board = new char[rows.length][rows[0].length()];
        for (int i = 0; i < rows.length; i++) board[i] = rows[i].toCharArray();
        System.out.println(exist(board, word));
    }
}
""",
                """
import sys

def exist(board: list[list[str]], word: str) -> bool:
    m, n = len(board), len(board[0])
    def dfs(r, c, k):
        if k == len(word):
            return True
        if r < 0 or r >= m or c < 0 or c >= n or board[r][c] != word[k]:
            return False
        temp = board[r][c]
        board[r][c] = '#'
        found = (dfs(r + 1, c, k + 1) or
                 dfs(r - 1, c, k + 1) or
                 dfs(r, c + 1, k + 1) or
                 dfs(r, c - 1, k + 1))
        board[r][c] = temp
        return found

    for i in range(m):
        for j in range(n):
            if dfs(i, j, 0):
                return True
    return False

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        rows = [list(r) for r in lines[0].strip().split(",")]
        word = lines[1].strip()
        print(str(exist(rows, word)).lower())
""",
                """
const fs = require('fs');

function exist(board, word) {
    const m = board.length, n = board[0].length;
    function dfs(r, c, k) {
        if (k === word.length) return true;
        if (r < 0 || r >= m || c < 0 || c >= n || board[r][c] !== word[k]) return false;
        const temp = board[r][c];
        board[r][c] = '#';
        const found = dfs(r + 1, c, k + 1) || dfs(r - 1, c, k + 1) || dfs(r, c + 1, k + 1) || dfs(r, c - 1, k + 1);
        board[r][c] = temp;
        return found;
    }
    for (let i = 0; i < m; i++) {
        for (let j = 0; j < n; j++) {
            if (dfs(i, j, 0)) return true;
        }
    }
    return false;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const board = lines[0].trim().split(',').map(r => r.split(''));
    const word = lines[1].trim();
    console.log(exist(board, word));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "ABCE,SFCS,ADEE\nABCCED", "true", "Word 'ABCCED' found in board."),
                        TestCaseDef.publicCase(2, "ABCE,SFCS,ADEE\nSEE", "true", "Word 'SEE' found in board."),
                        TestCaseDef.hiddenCase(3, "ABCE,SFCS,ADEE\nABCB", "false", "Word 'ABCB' cannot reuse letters.")
                )
        ));

        return map;
    }
}
