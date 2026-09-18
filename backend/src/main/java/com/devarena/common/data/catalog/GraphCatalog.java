package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Number of Islands
        map.put("number-of-islands", new ChallengeProblemDef(
                "number-of-islands",
                """
import java.util.*;

public class Solution {
    public static int numIslands(char[][] grid) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) lines.add(line);
        }
        if (lines.isEmpty()) {
            System.out.println(0);
            return;
        }
        char[][] grid = new char[lines.size()][lines.get(0).length()];
        for (int i = 0; i < lines.size(); i++) grid[i] = lines.get(i).toCharArray();
        System.out.println(numIslands(grid));
    }
}
""",
                """
import sys

def num_islands(grid: list[list[str]]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = [list(line.strip()) for line in sys.stdin.read().strip().splitlines() if line.strip()]
    print(num_islands(lines))
""",
                """
const fs = require('fs');

function numIslands(grid) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n').filter(s => s.trim()).map(s => s.trim().split(''));
console.log(numIslands(lines));
""",
                List.of(
                        TestCaseDef.publicCase(1, "11110\n11010\n11000\n00000", "1", "All connected 1s form 1 single island."),
                        TestCaseDef.publicCase(2, "11000\n11000\n00100\n00011", "3", "Three disconnected clusters of 1s form 3 islands."),
                        TestCaseDef.hiddenCase(3, "000\n000", "0", "All water matrix has 0 islands.")
                )
        ));

        // 2. Course Schedule
        map.put("course-schedule", new ChallengeProblemDef(
                "course-schedule",
                """
import java.util.*;

public class Solution {
    public static boolean canFinish(int numCourses, int[][] prerequisites) {
        // Write your solution here
        return false;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        int numCourses = Integer.parseInt(sc.nextLine().trim());
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        if (line.equals("[]")) {
            System.out.println("true");
            return;
        }
        line = line.substring(1, line.length() - 1);
        String[] parts = line.split("(?<=\\]),\\s*(?=\\[)");
        int[][] prereqs = new int[parts.length][2];
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = p.split(",");
            prereqs[i][0] = Integer.parseInt(nums[0].trim());
            prereqs[i][1] = Integer.parseInt(nums[1].trim());
        }
        System.out.println(canFinish(numCourses, prereqs));
    }
}
""",
                """
import sys
import json
from collections import deque, defaultdict

def can_finish(num_courses: int, prerequisites: list[list[int]]) -> bool:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        n = int(lines[0].strip())
        prereqs = json.loads(lines[1].strip())
        print(str(can_finish(n, prereqs)).lower())
""",
                """
const fs = require('fs');

function canFinish(numCourses, prerequisites) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const n = parseInt(lines[0].trim(), 10);
    const prereqs = JSON.parse(lines[1].trim());
    console.log(canFinish(n, prereqs));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2\n[[1,0]]", "true", "Course 1 requires Course 0, finishable in topological order."),
                        TestCaseDef.publicCase(2, "2\n[[1,0],[0,1]]", "false", "Cyclic dependency between courses 0 and 1."),
                        TestCaseDef.hiddenCase(3, "4\n[[1,0],[2,0],[3,1],[3,2]]", "true", "DAG with 4 courses is finishable.")
                )
        ));

        // 3. Course Schedule II
        map.put("course-schedule-ii", new ChallengeProblemDef(
                "course-schedule-ii",
                """
import java.util.*;

public class Solution {
    public static int[] findOrder(int numCourses, int[][] prerequisites) {
        // Write your solution here
        return new int[]{};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        int numCourses = Integer.parseInt(sc.nextLine().trim());
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        int[][] prereqs;
        if (line.equals("[]")) {
            prereqs = new int[0][2];
        } else {
            line = line.substring(1, line.length() - 1);
            String[] parts = line.split("(?<=\\]),\\s*(?=\\[)");
            prereqs = new int[parts.length][2];
            for (int i = 0; i < parts.length; i++) {
                String p = parts[i].replaceAll("[\\[\\]]", "").trim();
                String[] nums = p.split(",");
                prereqs[i][0] = Integer.parseInt(nums[0].trim());
                prereqs[i][1] = Integer.parseInt(nums[1].trim());
            }
        }
        System.out.println(Arrays.toString(findOrder(numCourses, prereqs)));
    }
}
""",
                """
import sys
import json
from collections import deque, defaultdict

def find_order(num_courses: int, prerequisites: list[list[int]]) -> list[int]:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 2:
        n = int(lines[0].strip())
        prereqs = json.loads(lines[1].strip())
        print(f"[{', '.join(map(str, find_order(n, prereqs)))}]")
""",
                """
const fs = require('fs');

function findOrder(numCourses, prerequisites) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 2) {
    const n = parseInt(lines[0].trim(), 10);
    const prereqs = JSON.parse(lines[1].trim());
    console.log(`[${findOrder(n, prereqs).join(', ')}]`);
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "2\n[[1,0]]", "[0, 1]", "Take course 0 then course 1."),
                        TestCaseDef.publicCase(2, "4\n[[1,0],[2,0],[3,1],[3,2]]", "[0, 1, 2, 3]", "Valid topological sequence."),
                        TestCaseDef.hiddenCase(3, "1\n[]", "[0]", "Single course with no prerequisites.")
                )
        ));

        // 4. Rotting Oranges
        map.put("rotting-oranges", new ChallengeProblemDef(
                "rotting-oranges",
                """
import java.util.*;

public class Solution {
    public static int orangesRotting(int[][] grid) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        line = line.substring(1, line.length() - 1);
        String[] parts = line.split("(?<=\\]),\\s*(?=\\[)");
        int m = parts.length;
        String[] firstCols = parts[0].replaceAll("[\\[\\]]", "").split(",");
        int n = firstCols.length;
        int[][] grid = new int[m][n];
        for (int i = 0; i < m; i++) {
            String p = parts[i].replaceAll("[\\[\\]]", "").trim();
            String[] nums = p.split(",");
            for (int j = 0; j < n; j++) grid[i][j] = Integer.parseInt(nums[j].trim());
        }
        System.out.println(orangesRotting(grid));
    }
}
""",
                """
import sys
import json
from collections import deque

def oranges_rotting(grid: list[list[int]]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        grid = json.loads(line)
        print(oranges_rotting(grid))
""",
                """
const fs = require('fs');

function orangesRotting(grid) {
    // Write your solution here
    return null;
}

const line = fs.readFileSync(0, 'utf-8').trim();
if (line) {
    const grid = JSON.parse(line);
    console.log(orangesRotting(grid));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "[[2,1,1],[1,1,0],[0,1,1]]", "4", "4 minutes until all fresh oranges rot."),
                        TestCaseDef.publicCase(2, "[[2,1,1],[0,1,1],[1,0,1]]", "-1", "Fresh orange at bottom left can never be reached."),
                        TestCaseDef.hiddenCase(3, "[[0,2]]", "0", "Zero fresh oranges initially.")
                )
        ));

        // 5. Word Ladder
        map.put("word-ladder", new ChallengeProblemDef(
                "word-ladder",
                """
import java.util.*;

public class Solution {
    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        // Write your solution here
        return 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) return;
        String beginWord = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String endWord = sc.nextLine().trim();
        if (!sc.hasNextLine()) return;
        String line = sc.nextLine().trim();
        List<String> wordList = Arrays.asList(line.split(","));
        System.out.println(ladderLength(beginWord, endWord, wordList));
    }
}
""",
                """
import sys
from collections import deque

def ladder_length(begin_word: str, end_word: str, word_list: list[str]) -> int:
    # Write your solution here
    pass

if __name__ == "__main__":
    lines = sys.stdin.read().strip().splitlines()
    if len(lines) >= 3:
        b = lines[0].strip()
        e = lines[1].strip()
        w = [x.strip() for x in lines[2].split(",")]
        print(ladder_length(b, e, w))
""",
                """
const fs = require('fs');

function ladderLength(beginWord, endWord, wordList) {
    // Write your solution here
    return null;
}

const lines = fs.readFileSync(0, 'utf-8').trim().split('\\n');
if (lines.length >= 3) {
    console.log(ladderLength(lines[0].trim(), lines[1].trim(), lines[2].split(',').map(s => s.trim())));
}
""",
                List.of(
                        TestCaseDef.publicCase(1, "hit\ncog\nhot,dot,dog,lot,log,cog", "5", "hit -> hot -> dot -> dog -> cog is 5 words."),
                        TestCaseDef.publicCase(2, "hit\ncog\nhot,dot,dog,lot,log", "0", "endWord 'cog' is not in wordList.")
                )
        ));

        return map;
    }
}
