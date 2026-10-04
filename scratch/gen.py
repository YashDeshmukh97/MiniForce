import os

code_dir = r"backend/src/main/java/com/miniforce/judge/pilot"

generators = {
    "TwoSumPythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class TwoSumPythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "import json\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    tokens = sys.stdin.read().split()\\n" +
               "    if not tokens:\\n" +
               "        return\\n" +
               "    n = int(tokens[0])\\n" +
               "    nums = [int(x) for x in tokens[1:n+1]]\\n" +
               "    target = int(tokens[n+1])\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.twoSum(nums, target)\\n" +
               "    print(json.dumps(result))\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
""",
    "ValidParenthesesPythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class ValidParenthesesPythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    s = sys.stdin.read().strip()\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.isValid(s)\\n" +
               "    print(str(result).lower())\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
""",
    "LongestSubstringPythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class LongestSubstringPythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    s = sys.stdin.read().strip()\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.lengthOfLongestSubstring(s)\\n" +
               "    print(result)\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
""",
    "MaxProfitPythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class MaxProfitPythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    tokens = sys.stdin.read().split()\\n" +
               "    if not tokens:\\n" +
               "        return\\n" +
               "    n = int(tokens[0])\\n" +
               "    prices = [int(x) for x in tokens[1:n+1]]\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.maxProfit(prices)\\n" +
               "    print(result)\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
""",
    "ClimbStairsPythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class ClimbStairsPythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    tokens = sys.stdin.read().split()\\n" +
               "    if not tokens:\\n" +
               "        return\\n" +
               "    n = int(tokens[0])\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.climbStairs(n)\\n" +
               "    print(result)\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
""",
    "ProductExceptSelfPythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class ProductExceptSelfPythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "import json\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    tokens = sys.stdin.read().split()\\n" +
               "    if not tokens:\\n" +
               "        return\\n" +
               "    n = int(tokens[0])\\n" +
               "    nums = [int(x) for x in tokens[1:n+1]]\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.productExceptSelf(nums)\\n" +
               "    print(json.dumps(result))\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
""",
    "MaxSubArrayPythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class MaxSubArrayPythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    tokens = sys.stdin.read().split()\\n" +
               "    if not tokens:\\n" +
               "        return\\n" +
               "    n = int(tokens[0])\\n" +
               "    nums = [int(x) for x in tokens[1:n+1]]\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.maxSubArray(nums)\\n" +
               "    print(result)\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
""",
    "LongestConsecutivePythonHarnessGenerator": """package com.miniforce.judge.pilot;
public class LongestConsecutivePythonHarnessGenerator {
    public static String generate() {
        return "import sys\\n" +
               "from solution import Solution\\n" +
               "\\n" +
               "def main():\\n" +
               "    tokens = sys.stdin.read().split()\\n" +
               "    if not tokens:\\n" +
               "        return\\n" +
               "    n = int(tokens[0])\\n" +
               "    nums = [int(x) for x in tokens[1:n+1]]\\n" +
               "    sol = Solution()\\n" +
               "    result = sol.longestConsecutive(nums)\\n" +
               "    print(result)\\n" +
               "\\n" +
               "if __name__ == '__main__':\\n" +
               "    main()\\n";
    }
}
"""
}

for name, content in generators.items():
    with open(os.path.join(code_dir, name + ".java"), "w") as f:
        f.write(content)
