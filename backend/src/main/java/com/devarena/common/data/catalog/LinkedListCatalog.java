package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedListCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Reverse Linked List
        map.put("reverse-linked-list", new ChallengeProblemDef(
                "reverse-linked-list",
                """
import java.util.*;

public class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    public static ListNode reverseList(ListNode head) {
        // Write your solution here
        return new ArrayList<>();
    }

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
        String[] parts = line.split(",");
        ListNode dummy = new ListNode(0), curr = dummy;
        for (String p : parts) {
            curr.next = new ListNode(Integer.parseInt(p.trim()));
            curr = curr.next;
        }
        ListNode rev = reverseList(dummy.next);
        List<Integer> out = new ArrayList<>();
        while (rev != null) {
            out.add(rev.val);
            rev = rev.next;
        }
        System.out.println(out);
    }
}
""",
                """
import sys

def reverse_list(head: list[int]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(reverse_list(nums)))
    else:
        print("[]")
""",
                """
const fs = require('fs');

function reverseList(head) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(reverseList(nums)).replace(/,/g, ', '));
} else {
    console.log("[]");
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,4,5", "[5, 4, 3, 2, 1]", "Reverses the linked list [1,2,3,4,5] to [5,4,3,2,1]."),
                        TestCaseDef.publicCase(2, "1,2", "[2, 1]", "Reverses [1,2] to [2,1]."),
                        TestCaseDef.hiddenCase(3, "", "[]", "Empty linked list returns [].")
                )
        ));

        // 2. Merge Two Sorted Lists
        map.put("merge-two-sorted-lists", new ChallengeProblemDef(
                "merge-two-sorted-lists",
                """
import java.util.*;

public class Solution {
    public static List<Integer> mergeTwoLists(int[] l1, int[] l2) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line1 = sc.hasNextLine() ? sc.nextLine().trim() : "";
        String line2 = sc.hasNextLine() ? sc.nextLine().trim() : "";

        int[] l1 = line1.isEmpty() ? new int[0] : Arrays.stream(line1.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        int[] l2 = line2.isEmpty() ? new int[0] : Arrays.stream(line2.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

        System.out.println(mergeTwoLists(l1, l2));
    }
}
""",
                """
import sys

def merge_two_lists(l1: list[int], l2: list[int]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().splitlines()
    l1 = [int(x.strip()) for x in lines[0].split(",") if x.strip()] if len(lines) > 0 and lines[0].strip() else []
    l2 = [int(x.strip()) for x in lines[1].split(",") if x.strip()] if len(lines) > 1 and lines[1].strip() else []
    print(str(merge_two_lists(l1, l2)))
""",
                """
const fs = require('fs');

function mergeTwoLists(l1, l2) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').split('\\n');
const l1 = lines[0] && lines[0].trim() ? lines[0].trim().split(',').map(s => parseInt(s.trim(), 10)) : [];
const l2 = lines[1] && lines[1].trim() ? lines[1].trim().split(',').map(s => parseInt(s.trim(), 10)) : [];
console.log(JSON.stringify(mergeTwoLists(l1, l2)).replace(/,/g, ', '));
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,4\n1,3,4", "[1, 1, 2, 3, 4, 4]", "Merges two sorted lists into one sorted list."),
                        TestCaseDef.publicCase(2, "\n", "[]", "Both empty lists."),
                        TestCaseDef.hiddenCase(3, "\n0", "[0]", "One empty, one single element.")
                )
        ));

        // 3. Linked List Cycle
        map.put("linked-list-cycle", new ChallengeProblemDef(
                "linked-list-cycle",
                """
import java.util.*;

public class Solution {
    public static boolean hasCycle(int pos) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int pos = Integer.parseInt(sc.nextLine().trim());
        System.out.println(hasCycle(pos));
    }
}
""",
                """
import sys

def has_cycle(pos: int) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        pos = int(lines[1].strip())
        print(str(has_cycle(pos)).lower())
""",
                """
const fs = require('fs');

function hasCycle(pos) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const pos = parseInt(lines[1].trim(), 10);
    console.log(hasCycle(pos));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,2,0,-4\n1", "true", "Tail connects to node index 1, forming a cycle."),
                        TestCaseDef.publicCase(2, "1,2\n0", "true", "Tail connects to node index 0, cycle exists."),
                        TestCaseDef.hiddenCase(3, "1\n-1", "false", "pos = -1, no cycle exists.")
                )
        ));

        // 4. Linked List Cycle II
        map.put("linked-list-cycle-ii", new ChallengeProblemDef(
                "linked-list-cycle-ii",
                """
import java.util.*;

public class Solution {
    public static int detectCycle(int pos) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int pos = Integer.parseInt(sc.nextLine().trim());
        System.out.println(detectCycle(pos));
    }
}
""",
                """
import sys

def detect_cycle(pos: int) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        pos = int(lines[1].strip())
        print(detect_cycle(pos))
""",
                """
const fs = require('fs');

function detectCycle(pos) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const pos = parseInt(lines[1].trim(), 10);
    console.log(detectCycle(pos));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "3,2,0,-4\n1", "1", "Cycle begins at node index 1."),
                        TestCaseDef.publicCase(2, "1,2\n0", "0", "Cycle begins at node index 0."),
                        TestCaseDef.hiddenCase(3, "1\n-1", "-1", "No cycle exists, returns -1.")
                )
        ));

        // 5. Remove Nth Node From End of List
        map.put("remove-nth-node-from-end-of-list", new ChallengeProblemDef(
                "remove-nth-node-from-end-of-list",
                """
import java.util.*;

public class Solution {
    public static List<Integer> removeNthFromEnd(int[] nums, int n) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        int n = Integer.parseInt(sc.nextLine().trim());
        String[] parts = line1.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(removeNthFromEnd(nums, n));
    }
}
""",
                """
import sys

def remove_nth_from_end(nums: list[int], n: int) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        n = int(lines[1].strip())
        print(str(remove_nth_from_end(nums, n)))
""",
                """
const fs = require('fs');

function removeNthFromEnd(nums, n) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const nums = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const n = parseInt(lines[1].trim(), 10);
    console.log(JSON.stringify(removeNthFromEnd(nums, n)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,4,5\n2", "[1, 2, 3, 5]", "Removing 2nd node from end removes 4."),
                        TestCaseDef.publicCase(2, "1\n1", "[]", "Removing 1st node from end yields []."),
                        TestCaseDef.hiddenCase(3, "1,2\n1", "[1]", "Removing 1st node from end removes 2.")
                )
        ));

        // 6. Reorder List
        map.put("reorder-list", new ChallengeProblemDef(
                "reorder-list",
                """
import java.util.*;

public class Solution {
    public static List<Integer> reorderList(int[] nums) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(reorderList(nums));
    }
}
""",
                """
import sys

def reorder_list(nums: list[int]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(reorder_list(nums)))
""",
                """
const fs = require('fs');

function reorderList(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(reorderList(nums)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,3,4", "[1, 4, 2, 3]", "Reorders [1,2,3,4] to [1,4,2,3]."),
                        TestCaseDef.publicCase(2, "1,2,3,4,5", "[1, 5, 2, 4, 3]", "Reorders [1,2,3,4,5] to [1,5,2,4,3]."),
                        TestCaseDef.hiddenCase(3, "1", "[1]", "Single node returns itself.")
                )
        ));

        // 7. Merge k Sorted Lists
        map.put("merge-k-sorted-lists", new ChallengeProblemDef(
                "merge-k-sorted-lists",
                """
import java.util.*;

public class Solution {
    public static List<Integer> mergeKLists(List<List<Integer>> lists) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) {
            System.out.println("[]");
            return;
        }
        String line = sc.nextLine().trim();
        if (line.equals("[]") || line.equals("[[]]")) {
            System.out.println("[]");
            return;
        }
        line = line.substring(1, line.length() - 1);
        String[] parts = line.split("(?<=\\]),\\s*(?=\\[)");
        List<List<Integer>> lists = new ArrayList<>();
        for (String p : parts) {
            String clean = p.replaceAll("[\\[\\]]", "").trim();
            if (clean.isEmpty()) continue;
            List<Integer> list = new ArrayList<>();
            for (String num : clean.split(",")) list.add(Integer.parseInt(num.trim()));
            lists.add(list);
        }
        System.out.println(mergeKLists(lists));
    }
}
""",
                """
import sys
import json

def merge_k_lists(lists: list[list[int]]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        lists = json.loads(line)
        print(str(merge_k_lists(lists)))
    else:
        print("[]")
""",
                """
const fs = require('fs');

function mergeKLists(lists) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const lists = JSON.parse(line);
    console.log(JSON.stringify(mergeKLists(lists)).replace(/,/g, ', '));
} else {
    console.log("[]");
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[1,4,5],[1,3,4],[2,6]]", "[1, 1, 2, 3, 4, 4, 5, 6]", "Merges 3 sorted lists into one sorted linked list."),
                        TestCaseDef.publicCase(2, "[]", "[]", "Empty array of lists."),
                        TestCaseDef.hiddenCase(3, "[[]]", "[]", "Array containing one empty list.")
                )
        ));

        // 8. Add Two Numbers
        map.put("add-two-numbers", new ChallengeProblemDef(
                "add-two-numbers",
                """
import java.util.*;

public class Solution {
    public static List<Integer> addTwoNumbers(int[] l1, int[] l2) {
        // Write your solution here
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String line2 = sc.nextLine().trim();
        int[] l1 = Arrays.stream(line1.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        int[] l2 = Arrays.stream(line2.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
        System.out.println(addTwoNumbers(l1, l2));
    }
}
""",
                """
import sys

def add_two_numbers(l1: list[int], l2: list[int]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        l1 = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
        l2 = [int(x.strip()) for x in lines[1].split(",") if x.strip()]
        print(str(add_two_numbers(l1, l2)))
""",
                """
const fs = require('fs');

function addTwoNumbers(l1, l2) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const l1 = lines[0].split(',').map(s => parseInt(s.trim(), 10));
    const l2 = lines[1].split(',').map(s => parseInt(s.trim(), 10));
    console.log(JSON.stringify(addTwoNumbers(l1, l2)).replace(/,/g, ', '));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2,4,3\n5,6,4", "[7, 0, 8]", "342 + 465 = 807, represented reversed as [7, 0, 8]."),
                        TestCaseDef.publicCase(2, "0\n0", "[0]", "0 + 0 = 0."),
                        TestCaseDef.hiddenCase(3, "9,9,9,9,9,9,9\n9,9,9,9", "[8, 9, 9, 9, 0, 0, 0, 1]", "Carry cascades throughout multiple places.")
                )
        ));

        // 9. Palindrome Linked List
        map.put("palindrome-linked-list", new ChallengeProblemDef(
                "palindrome-linked-list",
                """
import java.util.*;

public class Solution {
    public static boolean isPalindrome(int[] nums) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        String[] parts = line.split(",");
        int[] nums = new int[parts.length];
        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());
        System.out.println(isPalindrome(nums));
    }
}
""",
                """
import sys

def is_palindrome(nums: list[int]) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        nums = [int(x.strip()) for x in line.split(",") if x.strip()]
        print(str(is_palindrome(nums)).lower())
""",
                """
const fs = require('fs');

function isPalindrome(nums) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const nums = line.split(',').map(s => parseInt(s.trim(), 10));
    console.log(isPalindrome(nums));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "1,2,2,1", "true", "List reads same forwards and backwards."),
                        TestCaseDef.publicCase(2, "1,2", "false", "Not a palindrome."),
                        TestCaseDef.hiddenCase(3, "1", "true", "Single node palindrome."),
                        TestCaseDef.hiddenCase(4, "1,2,3,2,1", "true", "Odd length palindrome.")
                )
        ));

        // 10. Intersection of Two Linked Lists
        map.put("intersection-of-two-linked-lists", new ChallengeProblemDef(
                "intersection-of-two-linked-lists",
                """
import java.util.*;

public class Solution {
    public static String getIntersectionNode(String val) {
        // Write your solution here
        return "";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line1 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String line2 = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String intersectVal = sc.nextLine().trim();
        System.out.println(getIntersectionNode(intersectVal));
    }
}
""",
                """
import sys

def get_intersection(intersect_val: str) -> str:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 3:
        print(get_intersection(lines[2].strip()))
""",
                """
const fs = require('fs');

function getIntersection(intersectVal) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 3) {
    console.log(getIntersection(lines[2].trim()));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "4,1,8,4,5\n5,6,1,8,4,5\n8", "8", "Intersected node value is 8."),
                        TestCaseDef.publicCase(2, "1,9,1,2,4\n3,2,4\n2", "2", "Intersected node value is 2."),
                        TestCaseDef.hiddenCase(3, "2,6,4\n1,5\n0", "null", "No intersection exists.")
                )
        ));

        return map;
    }
}
