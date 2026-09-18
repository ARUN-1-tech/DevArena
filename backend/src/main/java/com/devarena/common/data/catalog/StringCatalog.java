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
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) sb.append(Character.toLowerCase(c));
        }
        String clean = sb.toString();
        int l = 0, r = clean.length() - 1;
        while (l < r) {
            if (clean.charAt(l++) != clean.charAt(r--)) return false;
        }
        return true;
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
    clean = [c.lower() for c in s if c.isalnum()]
    return clean == clean[::-1]

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(str(is_palindrome(s)).lower())
""",
                """
const fs = require('fs');

function isPalindrome(s) {
    const clean = s.toLowerCase().replace(/[^a-z0-9]/g, '');
    return clean === clean.split('').reverse().join('');
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
        if (s.length() != t.length()) return false;
        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
            count[t.charAt(i) - 'a']--;
        }
        for (int c : count) if (c != 0) return false;
        return true;
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
    return sorted(s) == sorted(t)

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(str(is_anagram(lines[0].strip(), lines[1].strip())).lower())
""",
                """
const fs = require('fs');

function isAnagram(s, t) {
    if (s.length !== t.length) return false;
    return s.split('').sort().join('') === t.split('').sort().join('');
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
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] ca = s.toCharArray();
            Arrays.sort(ca);
            String key = String.valueOf(ca);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        List<List<String>> res = new ArrayList<>(map.values());
        for (List<String> list : res) Collections.sort(list);
        res.sort(Comparator.comparingInt(List::size));
        return res;
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
    lookup = {}
    for s in strs:
        key = tuple(sorted(s))
        lookup.setdefault(key, []).append(s)
    res = [sorted(v) for v in lookup.values()]
    res.sort(key=len)
    return res

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
    const map = new Map();
    for (const s of strs) {
        const key = s.split('').sort().join('');
        if (!map.has(key)) map.set(key, []);
        map.get(key).push(s);
    }
    const res = Array.from(map.values()).map(arr => arr.sort());
    res.sort((a, b) => a.length - b.length);
    return res;
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
        Map<Character, Integer> map = new HashMap<>();
        int max = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c)) {
                left = Math.max(left, map.get(c) + 1);
            }
            map.put(c, right);
            max = Math.max(max, right - left + 1);
        }
        return max;
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
    used = {}
    max_len = 0
    start = 0
    for i, c in enumerate(s):
        if c in used and start <= used[c]:
            start = used[c] + 1
        else:
            max_len = max(max_len, i - start + 1)
        used[c] = i
    return max_len

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(length_of_longest_substring(s))
""",
                """
const fs = require('fs');

function lengthOfLongestSubstring(s) {
    const map = new Map();
    let max = 0, left = 0;
    for (let right = 0; right < s.length; right++) {
        const c = s[right];
        if (map.has(c)) {
            left = Math.max(left, map.get(c) + 1);
        }
        map.set(c, right);
        max = Math.max(max, right - left + 1);
    }
    return max;
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
        int[] count = new int[26];
        int maxCount = 0, maxLength = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            maxCount = Math.max(maxCount, ++count[s.charAt(right) - 'A']);
            while (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
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
    count = {}
    max_f = 0
    l = 0
    res = 0
    for r in range(len(s)):
        count[s[r]] = 1 + count.get(s[r], 0)
        max_f = max(max_f, count[s[r]])
        while (r - l + 1) - max_f > k:
            count[s[l]] -= 1
            l += 1
        res = max(res, r - l + 1)
    return res

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(character_replacement(lines[0].strip(), int(lines[1].strip())))
""",
                """
const fs = require('fs');

function characterReplacement(s, k) {
    const count = new Array(26).fill(0);
    let maxCount = 0, maxLength = 0, left = 0;
    for (let right = 0; right < s.length; right++) {
        const idx = s.charCodeAt(right) - 65;
        maxCount = Math.max(maxCount, ++count[idx]);
        while (right - left + 1 - maxCount > k) {
            count[s.charCodeAt(left) - 65]--;
            left++;
        }
        maxLength = Math.max(maxLength, right - left + 1);
    }
    return maxLength;
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
        if (s == null || s.length() < 1) return "";
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            int len1 = expand(s, i, i);
            int len2 = expand(s, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > end - start) {
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }
        return s.substring(start, end + 1);
    }

    private static int expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
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
    res = ""
    for i in range(len(s)):
        # odd length
        l, r = i, i
        while l >= 0 and r < len(s) and s[l] == s[r]:
            if (r - l + 1) > len(res):
                res = s[l:r+1]
            l -= 1
            r += 1
        # even length
        l, r = i, i + 1
        while l >= 0 and r < len(s) and s[l] == s[r]:
            if (r - l + 1) > len(res):
                res = s[l:r+1]
            l -= 1
            r += 1
    return res

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(longest_palindrome(s))
""",
                """
const fs = require('fs');

function longestPalindrome(s) {
    if (!s || s.length < 1) return "";
    let res = "";
    function expand(l, r) {
        while (l >= 0 && r < s.length && s[l] === s[r]) {
            if (r - l + 1 > res.length) res = s.substring(l, r + 1);
            l--;
            r++;
        }
    }
    for (let i = 0; i < s.length; i++) {
        expand(i, i);
        expand(i, i + 1);
    }
    return res;
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
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            count += expand(s, i, i);
            count += expand(s, i, i + 1);
        }
        return count;
    }

    private static int expand(String s, int left, int right) {
        int count = 0;
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            count++;
            left--;
            right++;
        }
        return count;
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
    count = 0
    for i in range(len(s)):
        # odd
        l, r = i, i
        while l >= 0 and r < len(s) and s[l] == s[r]:
            count += 1
            l -= 1
            r += 1
        # even
        l, r = i, i + 1
        while l >= 0 and r < len(s) and s[l] == s[r]:
            count += 1
            l -= 1
            r += 1
    return count

if __name__ == "__main__":
    s = sys.stdin.read().strip()
    print(count_substrings(s))
""",
                """
const fs = require('fs');

function countSubstrings(s) {
    let count = 0;
    function expand(l, r) {
        while (l >= 0 && r < s.length && s[l] === s[r]) {
            count++;
            l--;
            r++;
        }
    }
    for (let i = 0; i < s.length; i++) {
        expand(i, i);
        expand(i, i + 1);
    }
    return count;
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
        if (strs == null || strs.length == 0) return "";
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) return "";
            }
        }
        return prefix;
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
    if not strs:
        return ""
    prefix = strs[0]
    for s in strs[1:]:
        while not s.startswith(prefix):
            prefix = prefix[:-1]
            if not prefix:
                return ""
    return prefix

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
    if (!strs.length) return "";
    let prefix = strs[0];
    for (let i = 1; i < strs.length; i++) {
        while (strs[i].indexOf(prefix) !== 0) {
            prefix = prefix.substring(0, prefix.length - 1);
            if (!prefix) return "";
        }
    }
    return prefix;
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
        String[] parts = s.trim().split("\\\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = parts.length - 1; i >= 0; i--) {
            sb.append(parts[i]);
            if (i > 0) sb.append(" ");
        }
        return sb.toString();
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
    return " ".join(s.strip().split()[::-1])

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(reverse_words(s))
""",
                """
const fs = require('fs');

function reverseWords(s) {
    return s.trim().split(/\\s+/).reverse().join(' ');
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
        if (s.isEmpty() || t.isEmpty()) return "";
        Map<Character, Integer> dictT = new HashMap<>();
        for (char c : t.toCharArray()) dictT.put(c, dictT.getOrDefault(c, 0) + 1);
        int required = dictT.size();
        int l = 0, r = 0, formed = 0;
        Map<Character, Integer> windowCounts = new HashMap<>();
        int[] ans = {-1, 0, 0};

        while (r < s.length()) {
            char c = s.charAt(r);
            windowCounts.put(c, windowCounts.getOrDefault(c, 0) + 1);
            if (dictT.containsKey(c) && windowCounts.get(c).intValue() == dictT.get(c).intValue()) {
                formed++;
            }
            while (l <= r && formed == required) {
                c = s.charAt(l);
                if (ans[0] == -1 || r - l + 1 < ans[0]) {
                    ans[0] = r - l + 1;
                    ans[1] = l;
                    ans[2] = r;
                }
                windowCounts.put(c, windowCounts.get(c) - 1);
                if (dictT.containsKey(c) && windowCounts.get(c) < dictT.get(c)) {
                    formed--;
                }
                l++;
            }
            r++;
        }
        return ans[0] == -1 ? "" : s.substring(ans[1], ans[2] + 1);
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
    if not t or not s:
        return ""
    dict_t = Counter(t)
    required = len(dict_t)
    l, r = 0, 0
    formed = 0
    window_counts = {}
    ans = float("inf"), None, None

    while r < len(s):
        char = s[r]
        window_counts[char] = window_counts.get(char, 0) + 1
        if char in dict_t and window_counts[char] == dict_t[char]:
            formed += 1
        while l <= r and formed == required:
            char = s[l]
            if r - l + 1 < ans[0]:
                ans = (r - l + 1, l, r)
            window_counts[char] -= 1
            if char in dict_t and window_counts[char] < dict_t[char]:
                formed -= 1
            l += 1
        r += 1
    return "" if ans[0] == float("inf") else s[ans[1] : ans[2] + 1]

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        print(min_window(lines[0].strip(), lines[1].strip()))
""",
                """
const fs = require('fs');

function minWindow(s, t) {
    if (!s || !t) return "";
    const map = {};
    for (const c of t) map[c] = (map[c] || 0) + 1;
    let required = Object.keys(map).length;
    let l = 0, r = 0, formed = 0;
    const window = {};
    let minLen = Infinity, start = 0;

    while (r < s.length) {
        const c = s[r];
        window[c] = (window[c] || 0) + 1;
        if (map[c] && window[c] === map[c]) formed++;
        while (l <= r && formed === required) {
            if (r - l + 1 < minLen) {
                minLen = r - l + 1;
                start = l;
            }
            const leftChar = s[l];
            window[leftChar]--;
            if (map[leftChar] && window[leftChar] < map[leftChar]) formed--;
            l++;
        }
        r++;
    }
    return minLen === Infinity ? "" : s.substring(start, start + minLen);
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
        if (s == null || s.isEmpty()) return 0;
        int i = 0, n = s.length(), sign = 1;
        long total = 0;
        while (i < n && s.charAt(i) == ' ') i++;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            sign = s.charAt(i) == '+' ? 1 : -1;
            i++;
        }
        while (i < n) {
            int digit = s.charAt(i) - '0';
            if (digit < 0 || digit > 9) break;
            total = 10 * total + digit;
            if (sign == 1 && total > Integer.MAX_VALUE) return Integer.MAX_VALUE;
            if (sign == -1 && -total < Integer.MIN_VALUE) return Integer.MIN_VALUE;
            i++;
        }
        return (int) (total * sign);
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
    s = s.lstrip()
    if not s:
        return 0
    sign = 1
    idx = 0
    if s[0] in ('+', '-'):
        if s[0] == '-':
            sign = -1
        idx += 1
    res = 0
    while idx < len(s) and s[idx].isdigit():
        res = res * 10 + int(s[idx])
        idx += 1
    res *= sign
    INT_MAX = 2**31 - 1
    INT_MIN = -2**31
    if res > INT_MAX:
        return INT_MAX
    if res < INT_MIN:
        return INT_MIN
    return res

if __name__ == "__main__":
    s = sys.stdin.read().rstrip('\\r\\n')
    print(my_atoi(s))
""",
                """
const fs = require('fs');

function myAtoi(s) {
    let i = 0, sign = 1, total = 0;
    while (i < s.length && s[i] === ' ') i++;
    if (i < s.length && (s[i] === '+' || s[i] === '-')) {
        sign = s[i] === '+' ? 1 : -1;
        i++;
    }
    while (i < s.length) {
        const code = s.charCodeAt(i) - 48;
        if (code < 0 || code > 9) break;
        total = total * 10 + code;
        if (sign === 1 && total > 2147483647) return 2147483647;
        if (sign === -1 && -total < -2147483648) return -2147483648;
        i++;
    }
    return total * sign;
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
