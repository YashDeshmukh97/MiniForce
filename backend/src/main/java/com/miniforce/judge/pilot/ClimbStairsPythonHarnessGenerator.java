package com.miniforce.judge.pilot;
public class ClimbStairsPythonHarnessGenerator {
    public static String generate() {
        return "import sys\n" +
               "from solution import Solution\n" +
               "\n" +
               "def main():\n" +
               "    tokens = sys.stdin.read().split()\n" +
               "    if not tokens:\n" +
               "        return\n" +
               "    n = int(tokens[0])\n" +
               "    sol = Solution()\n" +
               "    result = sol.climbStairs(n)\n" +
               "    print(result)\n" +
               "\n" +
               "if __name__ == '__main__':\n" +
               "    main()\n";
    }
}
