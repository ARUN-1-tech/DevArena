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
        if (grid == null || grid.length == 0) return 0;
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '1') {
                    dfs(grid, i, j);
                    count++;
                }
            }
        }
        return count;
    }

    private static void dfs(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
        grid[r][c] = '0';
        dfs(grid, r + 1, c);
        dfs(grid, r - 1, c);
        dfs(grid, r, c + 1);
        dfs(grid, r, c - 1);
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
    if not grid:
        return 0
    m, n = len(grid), len(grid[0])
    count = 0
    def dfs(r, c):
        if r < 0 or r >= m or c < 0 or c >= n or grid[r][c] != '1':
            return
        grid[r][c] = '0'
        dfs(r + 1, c)
        dfs(r - 1, c)
        dfs(r, c + 1)
        dfs(r, c - 1)

    for i in range(m):
        for j in range(n):
            if grid[i][j] == '1':
                dfs(i, j)
                count += 1
    return count

if __name__ == "__main__":
    lines = [list(line.strip()) for line in sys.stdin.read().strip().splitlines() if line.strip()]
    print(num_islands(lines))
""",
                """
const fs = require('fs');

function numIslands(grid) {
    if (!grid.length) return 0;
    const m = grid.length, n = grid[0].length;
    let count = 0;
    function dfs(r, c) {
        if (r < 0 || r >= m || c < 0 || c >= n || grid[r][c] !== '1') return;
        grid[r][c] = '0';
        dfs(r + 1, c);
        dfs(r - 1, c);
        dfs(r, c + 1);
        dfs(r, c - 1);
    }
    for (let i = 0; i < m; i++) {
        for (let j = 0; j < n; j++) {
            if (grid[i][j] === '1') {
                dfs(i, j);
                count++;
            }
        }
    }
    return count;
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
        int[] inDegree = new int[numCourses];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            inDegree[p[0]]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) q.offer(i);
        }
        int count = 0;
        while (!q.isEmpty()) {
            int curr = q.poll();
            count++;
            for (int neighbor : adj.get(curr)) {
                if (--inDegree[neighbor] == 0) q.offer(neighbor);
            }
        }
        return count == numCourses;
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
    in_degree = [0] * num_courses
    adj = defaultdict(list)
    for dest, src in prerequisites:
        adj[src].append(dest)
        in_degree[dest] += 1
    q = deque([i for i in range(num_courses) if in_degree[i] == 0])
    count = 0
    while q:
        curr = q.popleft()
        count += 1
        for nxt in adj[curr]:
            in_degree[nxt] -= 1
            if in_degree[nxt] == 0:
                q.append(nxt)
    return count == num_courses

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
    const inDegree = new Array(numCourses).fill(0);
    const adj = Array.from({ length: numCourses }, () => []);
    for (const [dest, src] of prerequisites) {
        adj[src].push(dest);
        inDegree[dest]++;
    }
    const q = [];
    for (let i = 0; i < numCourses; i++) if (inDegree[i] === 0) q.push(i);
    let count = 0;
    while (q.length) {
        const curr = q.shift();
        count++;
        for (const next of adj[curr]) {
            if (--inDegree[next] === 0) q.push(next);
        }
    }
    return count === numCourses;
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
        int[] inDegree = new int[numCourses];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            inDegree[p[0]]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) q.offer(i);
        }
        int[] order = new int[numCourses];
        int idx = 0;
        while (!q.isEmpty()) {
            int curr = q.poll();
            order[idx++] = curr;
            for (int neighbor : adj.get(curr)) {
                if (--inDegree[neighbor] == 0) q.offer(neighbor);
            }
        }
        return idx == numCourses ? order : new int[0];
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
    in_degree = [0] * num_courses
    adj = defaultdict(list)
    for dest, src in prerequisites:
        adj[src].append(dest)
        in_degree[dest] += 1
    q = deque([i for i in range(num_courses) if in_degree[i] == 0])
    order = []
    while q:
        curr = q.popleft()
        order.append(curr)
        for nxt in adj[curr]:
            in_degree[nxt] -= 1
            if in_degree[nxt] == 0:
                q.append(nxt)
    return order if len(order) == num_courses else []

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
    const inDegree = new Array(numCourses).fill(0);
    const adj = Array.from({ length: numCourses }, () => []);
    for (const [dest, src] of prerequisites) {
        adj[src].push(dest);
        inDegree[dest]++;
    }
    const q = [];
    for (let i = 0; i < numCourses; i++) if (inDegree[i] === 0) q.push(i);
    const order = [];
    while (q.length) {
        const curr = q.shift();
        order.push(curr);
        for (const next of adj[curr]) {
            if (--inDegree[next] === 0) q.push(next);
        }
    }
    return order.length === numCourses ? order : [];
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
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int freshCount = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) queue.offer(new int[]{r, c});
                else if (grid[r][c] == 1) freshCount++;
            }
        }
        if (freshCount == 0) return 0;
        int minutes = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty() && freshCount > 0) {
            int size = queue.size();
            minutes++;
            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                for (int[] d : dirs) {
                    int nr = curr[0] + d[0], nc = curr[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;
                        freshCount--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
        }
        return freshCount == 0 ? minutes : -1;
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
    m, n = len(grid), len(grid[0])
    q = deque()
    fresh = 0
    for r in range(m):
        for c in range(n):
            if grid[r][c] == 2:
                q.append((r, c))
            elif grid[r][c] == 1:
                fresh += 1
    if fresh == 0:
        return 0
    minutes = 0
    while q and fresh > 0:
        minutes += 1
        for _ in range(len(q)):
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1:
                    grid[nr][nc] = 2
                    fresh -= 1
                    q.append((nr, nc))
    return minutes if fresh == 0 else -1

if __name__ == "__main__":
    line = sys.stdin.read().strip()
    if line:
        grid = json.loads(line)
        print(oranges_rotting(grid))
""",
                """
const fs = require('fs');

function orangesRotting(grid) {
    const m = grid.length, n = grid[0].length;
    const q = [];
    let fresh = 0;
    for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
            if (grid[r][c] === 2) q.push([r, c]);
            else if (grid[r][c] === 1) fresh++;
        }
    }
    if (fresh === 0) return 0;
    let minutes = 0;
    const dirs = [[1,0],[-1,0],[0,1],[0,-1]];
    while (q.length && fresh > 0) {
        minutes++;
        const size = q.length;
        for (let i = 0; i < size; i++) {
            const [r, c] = q.shift();
            for (const [dr, dc] of dirs) {
                const nr = r + dr, nc = c + dc;
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] === 1) {
                    grid[nr][nc] = 2;
                    fresh--;
                    q.push([nr, nc]);
                }
            }
        }
    }
    return fresh === 0 ? minutes : -1;
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
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        int level = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String curr = queue.poll();
                char[] chars = curr.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char orig = chars[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        chars[j] = c;
                        String next = new String(chars);
                        if (next.equals(endWord)) return level + 1;
                        if (wordSet.remove(next)) {
                            queue.offer(next);
                        }
                    }
                    chars[j] = orig;
                }
            }
            level++;
        }
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
    word_set = set(word_list)
    if end_word not in word_set:
        return 0
    q = deque([(begin_word, 1)])
    while q:
        word, level = q.popleft()
        if word == end_word:
            return level
        for i in range(len(word)):
            for c in 'abcdefghijklmnopqrstuvwxyz':
                nxt = word[:i] + c + word[i+1:]
                if nxt in word_set:
                    word_set.remove(nxt)
                    q.append((nxt, level + 1))
    return 0

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
    const wordSet = new Set(wordList);
    if (!wordSet.has(endWord)) return 0;
    const q = [[beginWord, 1]];
    while (q.length) {
        const [word, level] = q.shift();
        if (word === endWord) return level;
        for (let i = 0; i < word.length; i++) {
            for (let c = 97; c <= 122; c++) {
                const nxt = word.slice(0, i) + String.fromCharCode(c) + word.slice(i + 1);
                if (wordSet.has(nxt)) {
                    wordSet.delete(nxt);
                    q.push([nxt, level + 1]);
                }
            }
        }
    }
    return 0;
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
