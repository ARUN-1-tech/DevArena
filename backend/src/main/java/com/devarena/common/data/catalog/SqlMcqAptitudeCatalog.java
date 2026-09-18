package com.devarena.common.data.catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlMcqAptitudeCatalog {

    public static Map<String, ChallengeProblemDef> getDefinitions() {
        Map<String, ChallengeProblemDef> map = new HashMap<>();

        // 1. Department Highest Salary
        map.put("department-highest-salary", new ChallengeProblemDef(
                "department-highest-salary",
                """
-- SQL Solution for Department Highest Salary
SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary
FROM Employee e
JOIN Department d ON e.departmentId = d.id
WHERE (e.departmentId, e.salary) IN (
    SELECT departmentId, MAX(salary)
    FROM Employee
    GROUP BY departmentId
);
""",
                """
# SQL Query
query = \"\"\"
SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary
FROM Employee e
JOIN Department d ON e.departmentId = d.id
WHERE (e.departmentId, e.salary) IN (
    SELECT departmentId, MAX(salary)
    FROM Employee
    GROUP BY departmentId
)
\"\"\"
""",
                """
// SQL Query
const query = `
SELECT d.name AS Department, e.name AS Employee, e.salary AS Salary
FROM Employee e
JOIN Department d ON e.departmentId = d.id
WHERE (e.departmentId, e.salary) IN (
    SELECT departmentId, MAX(salary)
    FROM Employee
    GROUP BY departmentId
);
`;
""",
                List.of(
                        TestCaseDef.publicCase(1, "Employee: [[1, \"Joe\", 70000, 1], [2, \"Jim\", 90000, 1], [3, \"Henry\", 80000, 2], [4, \"Sam\", 60000, 2], [5, \"Max\", 90000, 1]]\nDepartment: [[1, \"IT\"], [2, \"Sales\"]]", "[[\"IT\", \"Jim\", 90000], [\"IT\", \"Max\", 90000], [\"Sales\", \"Henry\", 80000]]", "IT department max salary is 90000 (Jim, Max), Sales max is 80000 (Henry).")
                )
        ));

        // 2. Second Highest Salary
        map.put("second-highest-salary", new ChallengeProblemDef(
                "second-highest-salary",
                """
-- SQL Solution for Second Highest Salary
SELECT (
    SELECT DISTINCT salary
    FROM Employee
    ORDER BY salary DESC
    LIMIT 1 OFFSET 1
) AS SecondHighestSalary;
""",
                """
# SQL Query
query = \"\"\"
SELECT (
    SELECT DISTINCT salary
    FROM Employee
    ORDER BY salary DESC
    LIMIT 1 OFFSET 1
) AS SecondHighestSalary;
\"\"\"
""",
                """
// SQL Query
const query = `
SELECT (
    SELECT DISTINCT salary
    FROM Employee
    ORDER BY salary DESC
    LIMIT 1 OFFSET 1
) AS SecondHighestSalary;
`;
""",
                List.of(
                        TestCaseDef.publicCase(1, "Employee: [[1, 100], [2, 200], [3, 300]]", "200", "Second highest salary is 200."),
                        TestCaseDef.publicCase(2, "Employee: [[1, 100]]", "null", "No second highest salary exists, returns null.")
                )
        ));

        return map;
    }
}
