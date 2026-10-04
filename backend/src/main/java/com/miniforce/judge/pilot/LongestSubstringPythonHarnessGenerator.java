package com.miniforce.judge.pilot;
public class LongestSubstringPythonHarnessGenerator {
    public static String generate() {
        return "import sys\n" +
               "from solution import Solution\n" +
               "\n" +
               "def main():\n" +
               "    s = sys.stdin.read().strip()\n" +
               "    sol = Solution()\n" +
               "    result = sol.lengthOfLongestSubstring(s)\n" +
               "    print(result)\n" +
               "\n" +
               "if __name__ == '__main__':\n" +
               "    main()\n";
    }
}
