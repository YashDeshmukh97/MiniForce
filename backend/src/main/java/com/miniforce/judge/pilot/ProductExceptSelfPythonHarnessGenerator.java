package com.miniforce.judge.pilot;
public class ProductExceptSelfPythonHarnessGenerator {
    public static String generate() {
        return "import sys\n" +
               "import json\n" +
               "from solution import Solution\n" +
               "\n" +
               "def main():\n" +
               "    tokens = sys.stdin.read().split()\n" +
               "    if not tokens:\n" +
               "        return\n" +
               "    n = int(tokens[0])\n" +
               "    nums = [int(x) for x in tokens[1:n+1]]\n" +
               "    sol = Solution()\n" +
               "    result = sol.productExceptSelf(nums)\n" +
               "    print(json.dumps(result))\n" +
               "\n" +
               "if __name__ == '__main__':\n" +
               "    main()\n";
    }
}
