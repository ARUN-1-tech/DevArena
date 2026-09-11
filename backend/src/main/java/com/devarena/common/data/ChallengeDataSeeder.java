package com.devarena.common.data;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.model.ChallengeStarterCodeEntity;
import com.devarena.challenge.model.ChallengeTestCaseEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.challenge.repository.ChallengeStarterCodeRepository;
import com.devarena.challenge.repository.ChallengeTestCaseRepository;
import com.devarena.execution.model.ExecutionLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ChallengeDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(ChallengeDataSeeder.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengeStarterCodeRepository starterCodeRepository;
    private final ChallengeTestCaseRepository testCaseRepository;

    public ChallengeDataSeeder(
            ChallengeRepository challengeRepository,
            ChallengeStarterCodeRepository starterCodeRepository,
            ChallengeTestCaseRepository testCaseRepository) {
        this.challengeRepository = challengeRepository;
        this.starterCodeRepository = starterCodeRepository;
        this.testCaseRepository = testCaseRepository;
    }

    public void seedStarterCodesAndTestCasesIfEmpty() {
        if (testCaseRepository.count() > 0) {
            return;
        }

        log.info("Seeding starter codes and test cases for DevArena challenges catalog...");

        List<ChallengeEntity> allChallenges = challengeRepository.findAll();
        for (ChallengeEntity challenge : allChallenges) {
            seedChallengeData(challenge);
        }

        log.info("Successfully seeded test cases and starter codes.");
    }

    private void seedChallengeData(ChallengeEntity c) {
        String slug = c.getSlug();

        switch (slug) {
            case "two-sum" -> seedTwoSum(c);
            case "valid-palindrome" -> seedValidPalindrome(c);
            case "valid-parentheses" -> seedValidParentheses(c);
            default -> seedGenericChallenge(c);
        }
    }

    private void seedTwoSum(ChallengeEntity c) {
        // Starter codes
        saveStarter(c, ExecutionLanguage.JAVA, """
import java.util.*;

public class Solution {
    // Return indices of the two numbers such that they add up to target
    public static int[] twoSum(int[] nums, int target) {
        // TODO: Implement your solution here
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int comp = target - nums[i];
            if (map.containsKey(comp)) {
                return new int[] { map.get(comp), i };
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) return;
        String line1 = scanner.nextLine().trim();
        if (!scanner.hasNextLine()) return;
        String line2 = scanner.nextLine().trim();

        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            nums[i] = Integer.parseInt(parts[i].trim());
        }
        int target = Integer.parseInt(line2.trim());

        int[] result = twoSum(nums, target);
        System.out.println(Arrays.toString(result));
    }
}
""");

        saveStarter(c, ExecutionLanguage.PYTHON, """
import sys

def two_sum(nums: list[int], target: int) -> list[int]:
    # TODO: Implement your solution here
    lookup = {}
    for i, n in enumerate(nums):
        diff = target - n
        if diff in lookup:
            return [lookup[diff], i]
        lookup[n] = i
    return []

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        target = int(lines[1].strip())
        res = two_sum(nums, target)
        print(f"[{res[0]}, {res[1]}]")
""");

        saveStarter(c, ExecutionLanguage.JAVASCRIPT, """
const fs = require('fs');

function twoSum(nums, target) {
    // TODO: Implement your solution here
    const map = new Map();
    for (let i = 0; i < nums.length; i++) {
        const diff = target - nums[i];
        if (map.has(diff)) {
            return [map.get(diff), i];
        }
        map.set(nums[i], i);
    }
    return [];
}

const input = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (input.length >= 2) {
    const nums = input[0].split(',').map(s => parseInt(s.trim(), 10));
    const target = parseInt(input[1].trim(), 10);
    const res = twoSum(nums, target);
    console.log(`[${res[0]}, ${res[1]}]`);
}
""");

        // Test Cases
        saveTestCase(c, "2,7,11,15\n9", "[0, 1]", false, 1, "Standard basic pair at beginning");
        saveTestCase(c, "3,2,4\n6", "[1, 2]", false, 2, "Pair located later in array");
        saveTestCase(c, "3,3\n6", "[0, 1]", true, 3, "Duplicate matching values");
        saveTestCase(c, "1,5,8,11,14\n19", "[2, 3]", true, 4, "Larger elements (8 + 11 = 19)");
    }

    private void seedValidPalindrome(ChallengeEntity c) {
        saveStarter(c, ExecutionLanguage.JAVA, """
import java.util.*;

public class Solution {
    public static boolean isPalindrome(String s) {
        // TODO: Implement your solution here
        StringBuilder clean = new StringBuilder();
        for (char ch : s.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                clean.append(Character.toLowerCase(ch));
            }
        }
        String str = clean.toString();
        int left = 0, right = str.length() - 1;
        while (left < right) {
            if (str.charAt(left++) != str.charAt(right--)) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.hasNextLine() ? scanner.nextLine() : "";
        System.out.println(isPalindrome(line));
    }
}
""");

        saveStarter(c, ExecutionLanguage.PYTHON, """
import sys

def is_palindrome(s: str) -> bool:
    # TODO: Implement your solution here
    clean = [ch.lower() for ch in s if ch.isalnum()]
    return clean == clean[::-1]

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(str(is_palindrome(s)).lower())
""");

        saveStarter(c, ExecutionLanguage.JAVASCRIPT, """
const fs = require('fs');

function isPalindrome(s) {
    // TODO: Implement your solution here
    const clean = s.toLowerCase().replace(/[^a-z0-9]/g, '');
    return clean === clean.split('').reverse().join('');
}

const s = fs.readFileSync(0, 'utf-8').replace(/[\\r\\n]+$/, '');
console.log(isPalindrome(s));
""");

        saveTestCase(c, "A man, a plan, a canal: Panama", "true", false, 1, "Classic palindrome with spaces and punctuation");
        saveTestCase(c, "race a car", "false", false, 2, "Not a palindrome");
        saveTestCase(c, " ", "true", true, 3, "Empty after filtering non-alphanumerics");
        saveTestCase(c, "0P", "false", true, 4, "Alphanumeric false match");
    }

    private void seedValidParentheses(ChallengeEntity c) {
        saveStarter(c, ExecutionLanguage.JAVA, """
import java.util.*;

public class Solution {
    public static boolean isValid(String s) {
        // TODO: Implement your solution here
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
""");

        saveStarter(c, ExecutionLanguage.PYTHON, """
import sys

def is_valid(s: str) -> bool:
    # TODO: Implement your solution here
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
""");

        saveStarter(c, ExecutionLanguage.JAVASCRIPT, """
const fs = require('fs');

function isValid(s) {
    // TODO: Implement your solution here
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
""");

        saveTestCase(c, "()", "true", false, 1, "Simple matching pair");
        saveTestCase(c, "()[]{}", "true", false, 2, "Multiple sequential pairs");
        saveTestCase(c, "(]", "false", true, 3, "Mismatched closing bracket");
        saveTestCase(c, "{[]}", "true", true, 4, "Nested brackets");
    }

    private void seedGenericChallenge(ChallengeEntity c) {
        saveStarter(c, ExecutionLanguage.JAVA, """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        // TODO: Implement challenge solution
        System.out.println(line);
    }
}
""");

        saveStarter(c, ExecutionLanguage.PYTHON, """
import sys

def solve():
    line = sys.stdin.read().strip()
    # TODO: Implement challenge solution
    print(line)

if __name__ == "__main__":
    solve()
""");

        saveStarter(c, ExecutionLanguage.JAVASCRIPT, """
const fs = require('fs');

function solve() {
    const input = fs.readFileSync(0, 'utf-8').trim();
    // TODO: Implement challenge solution
    console.log(input);
}

solve();
""");

        saveTestCase(c, "test input 1", "test input 1", false, 1, "Basic sample test case");
        saveTestCase(c, "hidden verification 2", "hidden verification 2", true, 2, "Hidden automated validation test case");
    }

    private void saveStarter(ChallengeEntity c, ExecutionLanguage lang, String code) {
        if (!starterCodeRepository.existsByChallengeIdAndLanguage(c.getId(), lang)) {
            starterCodeRepository.save(new ChallengeStarterCodeEntity(c, lang, code.trim()));
        }
    }

    private void saveTestCase(ChallengeEntity c, String input, String expected, boolean hidden, int order, String exp) {
        testCaseRepository.save(new ChallengeTestCaseEntity(c, input, expected, hidden, order, exp));
    }
}
