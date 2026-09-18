package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Valid Palindrome
        map.put("valid-palindrome", new ChallengeProblemDef(
                "valid-palindrome",
                """
import java.util.*;

public class Solution {
    public static boolean isPalindrome(String s) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        System.out.println(isPalindrome(s));
    }
}
""",
                """
import sys

def is_palindrome(s: str) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(str(is_palindrome(s)).lower())
""",
                """
const fs = require('fs');

function isPalindrome(s) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').replace(/[\\r\\n]+$/, '');
console.log(isPalindrome(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "A man, a plan, a canal: Panama", "true", "Classic palindrome with spaces and punctuation."),
                        TestCaseDef.publicCase(2, "race a car", "false", "Not a palindrome."),
                        TestCaseDef.hiddenCase(3, " ", "true", "Empty string after removing non-alphanumeric."),
                        TestCaseDef.hiddenCase(4, "0P", "false", "Single mismatch.")
                )
        ));

        // 2. Valid Anagram
        map.put("valid-anagram", new ChallengeProblemDef(
                "valid-anagram",
                """
import java.util.*;

public class Solution {
    public static boolean isAnagram(String s, String t) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String s = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String t = sc.nextLine().trim();
        System.out.println(isAnagram(s, t));
    }
}
""",
                """
import sys

def is_anagram(s: str, t: str) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(str(is_anagram(lines[0].strip(), lines[1].strip())).lower())
""",
                """
const fs = require('fs');

function isAnagram(s, t) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(isAnagram(lines[0].trim(), lines[1].trim()));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "anagram\nnagaram", "true", "Anagram matching."),
                        TestCaseDef.publicCase(2, "rat\ncar", "false", "Different characters."),
                        TestCaseDef.hiddenCase(3, "a\na", "true", "Single character match."),
                        TestCaseDef.hiddenCase(4, "ab\na", "false", "Length mismatch.")
                )
        ));

        // 3. Group Anagrams
        map.put("group-anagrams", new ChallengeProblemDef(
                "group-anagrams",
                """
import java.util.*;

public class Solution {
    public static List<List<String>> groupAnagrams(String[] strs) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] strs = line.isEmpty() ? new String[]{""} : line.split(",");
        for (int i = 0; i < strs.length; i++) strs[i] = strs[i].trim();
        System.out.println(groupAnagrams(strs));
    }
}
""",
                """
import sys

def group_anagrams(strs: list[str]) -> list[list[str]]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        strs = [x.strip() for x in line.split(",")]
    else:
        strs = [""]
    res = group_anagrams(strs)
    print(str(res).replace("'", '"'))
""",
                """
const fs = require('fs');

function groupAnagrams(strs) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
const strs = line ? line.split(',').map(s => s.trim()) : [""];
console.log(JSON.stringify(groupAnagrams(strs)).replace(/,/g, ', '));
""",
                List.of(
                        TestCaseDef.publicCase(1, "eat,tea,tan,ate,nat,bat", "[[\"bat\"], [\"nat\", \"tan\"], [\"ate\", \"eat\", \"tea\"]]", "Anagram groupings sorted by cluster size."),
                        TestCaseDef.publicCase(2, "", "[[\"\"]]", "Empty string group."),
                        TestCaseDef.hiddenCase(3, "a", "[[\"a\"]]", "Single char group.")
                )
        ));

        // 4. Longest Substring Without Repeating Characters
        map.put("longest-substring-without-repeating-characters", new ChallengeProblemDef(
                "longest-substring-without-repeating-characters",
                """
import java.util.*;

public class Solution {
    public static int lengthOfLongestSubstring(String s) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        System.out.println(lengthOfLongestSubstring(s));
    }
}
""",
                """
import sys

def length_of_longest_substring(s: str) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(length_of_longest_substring(s))
""",
                """
const fs = require('fs');

function lengthOfLongestSubstring(s) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').replace(/[\\r\\n]+$/, '');
console.log(lengthOfLongestSubstring(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "abcabcbb", "3", "The answer is 'abc', with length 3."),
                        TestCaseDef.publicCase(2, "bbbbb", "1", "The answer is 'b', length 1."),
                        TestCaseDef.hiddenCase(3, "pwwkew", "3", "The answer is 'wke', length 3."),
                        TestCaseDef.hiddenCase(4, "", "0", "Empty string has length 0.")
                )
        ));

        // 5. Longest Repeating Character Replacement
        map.put("longest-repeating-character-replacement", new ChallengeProblemDef(
                "longest-repeating-character-replacement",
                """
import java.util.*;

public class Solution {
    public static int characterReplacement(String s, int k) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String s = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int k = Integer.parseInt(sc.nextLine().trim());
        System.out.println(characterReplacement(s, k));
    }
}
""",
                """
import sys

def character_replacement(s: str, k: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(character_replacement(lines[0].strip(), int(lines[1].strip())))
""",
                """
const fs = require('fs');

function characterReplacement(s, k) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(characterReplacement(lines[0].trim(), parseInt(lines[1].trim(), 10)));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "ABAB\n2", "4", "Replace two 'A's with 'B's or vice versa."),
                        TestCaseDef.publicCase(2, "AABABBA\n1", "4", "Replace the middle 'A' to form 'BBBB'."),
                        TestCaseDef.hiddenCase(3, "AAAA\n2", "4", "All same characters"),
                        TestCaseDef.hiddenCase(4, "ABCDE\n1", "2", "All distinct characters with k=1")
                )
        ));

        // 6. Longest Palindromic Substring
        map.put("longest-palindromic-substring", new ChallengeProblemDef(
                "longest-palindromic-substring",
                """
import java.util.*;

public class Solution {
    public static String longestPalindrome(String s) {
        // Write your solution here
        return "";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        System.out.println(longestPalindrome(s));
    }
}
""",
                """
import sys

def longest_palindrome(s: str) -> str:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(longest_palindrome(s))
""",
                """
const fs = require('fs');

function longestPalindrome(s) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').trim();
console.log(longestPalindrome(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "babad", "bab", "Longest palindromic substring is 'bab' (or 'aba')."),
                        TestCaseDef.publicCase(2, "cbbd", "bb", "'bb' is palindrome."),
                        TestCaseDef.hiddenCase(3, "a", "a", "Single character"),
                        TestCaseDef.hiddenCase(4, "ac", "a", "First single character")
                )
        ));

        // 7. Palindromic Substrings
        map.put("palindromic-substrings", new ChallengeProblemDef(
                "palindromic-substrings",
                """
import java.util.*;

public class Solution {
    public static int countSubstrings(String s) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        System.out.println(countSubstrings(s));
    }
}
""",
                """
import sys

def count_substrings(s: str) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(count_substrings(s))
""",
                """
const fs = require('fs');

function countSubstrings(s) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').trim();
console.log(countSubstrings(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "abc", "3", "Three palindromic substrings: 'a', 'b', 'c'."),
                        TestCaseDef.publicCase(2, "aaa", "6", "Six palindromic substrings: 'a', 'a', 'a', 'aa', 'aa', 'aaa'."),
                        TestCaseDef.hiddenCase(3, "a", "1", "Single character"),
                        TestCaseDef.hiddenCase(4, "aba", "4", "Substrings 'a', 'b', 'a', 'aba'")
                )
        ));

        // 8. Longest Common Prefix
        map.put("longest-common-prefix", new ChallengeProblemDef(
                "longest-common-prefix",
                """
import java.util.*;

public class Solution {
    public static String longestCommonPrefix(String[] strs) {
        // Write your solution here
        return "";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] strs = line.split(",");
        for (int i = 0; i < strs.length; i++) strs[i] = strs[i].trim();
        System.out.println(longestCommonPrefix(strs));
    }
}
""",
                """
import sys

def longest_common_prefix(strs: list[str]) -> str:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        strs = [x.strip() for x in line.split(",")]
        print(longest_common_prefix(strs))
    else:
        print("")
""",
                """
const fs = require('fs');

function longestCommonPrefix(strs) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const strs = line.split(',').map(s => s.trim());
    console.log(longestCommonPrefix(strs));
} else {
    console.log("");
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "flower,flow,flight", "fl", "Common prefix is 'fl'."),
                        TestCaseDef.publicCase(2, "dog,racecar,car", "", "There is no common prefix among the input strings."),
                        TestCaseDef.hiddenCase(3, "interspecies,interstellar,interstate", "inters", "Prefix is 'inters'"),
                        TestCaseDef.hiddenCase(4, "throne", "throne", "Single word")
                )
        ));

        // 9. Reverse Words in a String
        map.put("reverse-words-in-a-string", new ChallengeProblemDef(
                "reverse-words-in-a-string",
                """
import java.util.*;

public class Solution {
    public static String reverseWords(String s) {
        // Write your solution here
        return "";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        System.out.println(reverseWords(s));
    }
}
""",
                """
import sys

def reverse_words(s: str) -> str:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(reverse_words(s))
""",
                """
const fs = require('fs');

function reverseWords(s) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').replace(/[\\r\\n]+$/, '');
console.log(reverseWords(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "the sky is blue", "blue is sky the", "Reverses word ordering."),
                        TestCaseDef.publicCase(2, "  hello world  ", "world hello", "Removes leading/trailing spaces."),
                        TestCaseDef.hiddenCase(3, "a good   example", "example good a", "Multiple multiple spaces compressed to single")
                )
        ));

        // 10. Minimum Window Substring
        map.put("minimum-window-substring", new ChallengeProblemDef(
                "minimum-window-substring",
                """
import java.util.*;

public class Solution {
    public static String minWindow(String s, String t) {
        // Write your solution here
        return "";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String s = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String t = sc.nextLine().trim();
        System.out.println(minWindow(s, t));
    }
}
""",
                """
import sys
from collections import Counter

def min_window(s: str, t: str) -> str:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(min_window(lines[0].strip(), lines[1].strip()))
""",
                """
const fs = require('fs');

function minWindow(s, t) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    console.log(minWindow(lines[0].trim(), lines[1].trim()));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "ADOBECODEBANC\nABC", "BANC", "The minimum window substring is 'BANC'."),
                        TestCaseDef.publicCase(2, "a\na", "a", "Single char match."),
                        TestCaseDef.hiddenCase(3, "a\naa", "", "Target requires 2 'a's, but only 1 exists.")
                )
        ));

        // 11. String to Integer (atoi)
        map.put("string-to-integer-atoi", new ChallengeProblemDef(
                "string-to-integer-atoi",
                """
import java.util.*;

public class Solution {
    public static int myAtoi(String s) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        System.out.println(myAtoi(s));
    }
}
""",
                """
import sys

def my_atoi(s: str) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(my_atoi(s))
""",
                """
const fs = require('fs');

function myAtoi(s) {
    // Write your solution here
    return null;
}

const s = fs.readFileSync(0, 'utf-8').replace(/[\\r\\n]+$/, '');
console.log(myAtoi(s));
""",
                List.of(
                        TestCaseDef.publicCase(1, "42", "42", "Parses 42."),
                        TestCaseDef.publicCase(2, "   -42", "-42", "Handles leading whitespace and negative sign."),
                        TestCaseDef.hiddenCase(3, "1337c0d3", "1337", "Stops on first non-digit char."),
                        TestCaseDef.hiddenCase(4, "0-1", "0", "Stops reading after initial 0.")
                )
        ));

        return map;
    }
}
