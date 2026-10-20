package com.miniforce.judge.pilot;

public final class PythonHarnessRegistry {

    private PythonHarnessRegistry() {}

    public static String getHarness(long problemId) {
        if (problemId < 1L || problemId > 50L) {
            return null;
        }
        return switch ((int) problemId) {
            case 1 -> hTwoSum();
            case 2 -> hValidParentheses();
            case 3 -> hLongestSubstring();
            case 4 -> hMergeIntervals();
            case 5 -> hCourseSchedule();
            case 6 -> hMedianSortedArrays();
            case 7 -> hWordLadder();
            case 8 -> hMaxProfit();
            case 9 -> hReverseLinkedList();
            case 10 -> hNumIslands();
            case 11 -> hCoinChange();
            case 12 -> hClimbStairs();
            case 13 -> hProductExceptSelf();
            case 14 -> hMaxSubArray();
            case 15 -> hTrapRainWater();
            case 16 -> hValidAnagram();
            case 17 -> hGroupAnagrams();
            case 18 -> hTopKFrequent();
            case 19 -> hEncodeDecode();
            case 20 -> hLongestConsecutive();
            case 21 -> hValidSudoku();
            case 22 -> hContainsDuplicate();
            case 23 -> hFindDisappearedNumbers();
            case 24 -> hIntersection();
            case 25 -> hIsomorphicStrings();
            case 26 -> hValidPalindrome();
            case 27 -> hThreeSum();
            case 28 -> hContainerWithMostWater();
            case 29 -> hMinimumWindowSubstring();
            case 30 -> hLongestRepeatingCharacterReplacement();
            case 31 -> hPermutationInString();
            case 32 -> hMaximumAverageSubarrayI();
            case 33 -> hFindAllAnagramsInAString();
            case 34 -> hSubarraysWithKDifferentIntegers();
            case 35 -> hFruitIntoBaskets();
            case 36 -> hMinStack();
            case 37 -> hEvaluateReversePolishNotation();
            case 38 -> hDailyTemperatures();
            case 39 -> hLargestRectangleInHistogram();
            case 40 -> hImplementQueueUsingStacks();
            case 41 -> hSlidingWindowMaximum();
            case 42 -> hAsteroidCollision();
            case 43 -> hBinarySearch();
            case 44 -> hSearch2DMatrix();
            case 45 -> hKokoEatingBananas();
            case 46 -> hFindMinInRotated();
            case 47 -> hSearchRotated();
            case 48 -> hTimeBasedKeyValueStore();
            case 49 -> hSplitArrayLargestSum();
            case 50 -> hMergeTwoSortedLists();
            default -> null;
        };
    }

    private static String hTwoSum() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    target = int(tokens[n+1])
    sol = Solution()
    result = sol.twoSum(nums, target)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hValidParentheses() {
        return """
import sys
from solution import Solution

def main():
    s = sys.stdin.read().strip()
    sol = Solution()
    result = sol.isValid(s)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hLongestSubstring() {
        return """
import sys
from solution import Solution

def main():
    s = sys.stdin.read().strip()
    sol = Solution()
    result = sol.lengthOfLongestSubstring(s)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hMergeIntervals() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n = int(tokens[idx])
    idx += 1
    intervals = []
    for _ in range(n):
        m = int(tokens[idx])
        idx += 1
        row = [int(tokens[idx + j]) for j in range(m)]
        idx += m
        intervals.append(row)
    sol = Solution()
    result = sol.merge(intervals)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hCourseSchedule() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    numCourses = int(tokens[idx])
    idx += 1
    n = int(tokens[idx])
    idx += 1
    prereqs = []
    for _ in range(n):
        m = int(tokens[idx])
        idx += 1
        row = [int(tokens[idx + j]) for j in range(m)]
        idx += m
        prereqs.append(row)
    sol = Solution()
    result = sol.canFinish(numCourses, prereqs)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hMedianSortedArrays() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n1 = int(tokens[idx])
    idx += 1
    nums1 = [int(tokens[idx + i]) for i in range(n1)]
    idx += n1
    n2 = int(tokens[idx])
    idx += 1
    nums2 = [int(tokens[idx + i]) for i in range(n2)]
    idx += n2
    sol = Solution()
    result = sol.findMedianSortedArrays(nums1, nums2)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hWordLadder() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    beginWord = tokens[0]
    endWord = tokens[1]
    n = int(tokens[2])
    wordList = tokens[3:3+n]
    sol = Solution()
    result = sol.ladderLength(beginWord, endWord, wordList)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hMaxProfit() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    prices = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.maxProfit(prices)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hReverseLinkedList() {
        return """
import sys
import json
import builtins

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

builtins.ListNode = ListNode
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    vals = [int(x) for x in tokens[1:n+1]]
    dummy = ListNode(0)
    curr = dummy
    for v in vals:
        curr.next = ListNode(v)
        curr = curr.next
    sol = Solution()
    res = sol.reverseList(dummy.next)
    out = []
    while res:
        out.append(res.val)
        res = res.next
    print(json.dumps(out))

if __name__ == '__main__':
    main()
""";
    }

    private static String hNumIslands() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n = int(tokens[idx])
    idx += 1
    grid = []
    for _ in range(n):
        m = int(tokens[idx])
        idx += 1
        row = [tokens[idx + j] for j in range(m)]
        idx += m
        grid.append(row)
    sol = Solution()
    result = sol.numIslands(grid)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hCoinChange() {
        return """
import sys
import json
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    coins = json.loads(lines[0])
    amount = int(lines[1])
    sol = Solution()
    result = sol.coinChange(coins, amount)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hClimbStairs() {
        return """
import sys
from solution import Solution

def main():
    n = int(sys.stdin.read().strip())
    sol = Solution()
    result = sol.climbStairs(n)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hProductExceptSelf() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.productExceptSelf(nums)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hMaxSubArray() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.maxSubArray(nums)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hTrapRainWater() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    height = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.trap(height)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hValidAnagram() {
        return """
import sys
import json
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    s = json.loads(lines[0]) if lines[0].startswith('"') else lines[0]
    t = json.loads(lines[1]) if lines[1].startswith('"') else lines[1]
    sol = Solution()
    result = sol.isAnagram(s, t)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hGroupAnagrams() {
        return """
import sys
import json
from solution import Solution

def main():
    raw = sys.stdin.read().strip()
    if not raw:
        return
    strs = json.loads(raw)
    sol = Solution()
    result = sol.groupAnagrams(strs)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hTopKFrequent() {
        return """
import sys
import json
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    nums = json.loads(lines[0])
    k = int(lines[1])
    sol = Solution()
    result = sol.topKFrequent(nums, k)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hEncodeDecode() {
        return """
import sys
import json
from solution import Codec

def main():
    raw = sys.stdin.read().strip()
    if not raw:
        return
    strs = json.loads(raw)
    codec = Codec()
    encoded = codec.encode(strs)
    decoded = codec.decode(encoded)
    print(json.dumps(decoded))

if __name__ == '__main__':
    main()
""";
    }

    private static String hLongestConsecutive() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.longestConsecutive(nums)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hValidSudoku() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n = int(tokens[idx])
    idx += 1
    board = []
    for _ in range(n):
        m = int(tokens[idx])
        idx += 1
        row = [tokens[idx + j] for j in range(m)]
        idx += m
        board.append(row)
    sol = Solution()
    result = sol.isValidSudoku(board)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hContainsDuplicate() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.containsDuplicate(nums)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hFindDisappearedNumbers() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.findDisappearedNumbers(nums)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hIntersection() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n1 = int(tokens[idx])
    idx += 1
    nums1 = [int(tokens[idx + i]) for i in range(n1)]
    idx += n1
    n2 = int(tokens[idx])
    idx += 1
    nums2 = [int(tokens[idx + i]) for i in range(n2)]
    idx += n2
    sol = Solution()
    result = sol.intersection(nums1, nums2)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hIsomorphicStrings() {
        return """
import sys
import json
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    s = json.loads(lines[0]) if lines[0].startswith('"') else lines[0]
    t = json.loads(lines[1]) if lines[1].startswith('"') else lines[1]
    sol = Solution()
    result = sol.isIsomorphic(s, t)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hValidPalindrome() {
        return """
import sys
from solution import Solution

def main():
    s = sys.stdin.read().rstrip('\\r\\n')
    sol = Solution()
    result = sol.isPalindrome(s)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hThreeSum() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.threeSum(nums)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hContainerWithMostWater() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    height = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.maxArea(height)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hMinimumWindowSubstring() {
        return """
import sys
import json
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    s = json.loads(lines[0]) if lines[0].startswith('"') else lines[0]
    t = json.loads(lines[1]) if lines[1].startswith('"') else lines[1]
    sol = Solution()
    result = sol.minWindow(s, t)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hLongestRepeatingCharacterReplacement() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    s = lines[0]
    k = int(lines[1])
    sol = Solution()
    result = sol.characterReplacement(s, k)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hPermutationInString() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    s1 = lines[0]
    s2 = lines[1]
    sol = Solution()
    result = sol.checkInclusion(s1, s2)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hMaximumAverageSubarrayI() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    nums = [int(x) for x in lines[0].split(',')] if lines[0] else []
    k = int(lines[1])
    sol = Solution()
    result = sol.findMaxAverage(nums, k)
    print(f"{result:.5f}")

if __name__ == '__main__':
    main()
""";
    }

    private static String hFindAllAnagramsInAString() {
        return """
import sys
import json
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    s = lines[0]
    p = lines[1]
    sol = Solution()
    result = sol.findAnagrams(s, p)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hSubarraysWithKDifferentIntegers() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    nums = [int(x) for x in lines[0].split(',')] if lines[0] else []
    k = int(lines[1])
    sol = Solution()
    result = sol.subarraysWithKDistinct(nums, k)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hFruitIntoBaskets() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    fruits = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.totalFruit(fruits)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hMinStack() {
        return """
import sys
import json
from solution import MinStack

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n = int(tokens[idx])
    idx += 1
    stack = None
    res = []
    for _ in range(n):
        cmd = tokens[idx]
        idx += 1
        numArgs = int(tokens[idx])
        idx += 1
        if cmd == "MinStack":
            stack = MinStack()
            res.append(None)
        elif cmd == "push":
            val = int(tokens[idx])
            idx += 1
            stack.push(val)
            res.append(None)
        elif cmd == "pop":
            stack.pop()
            res.append(None)
        elif cmd == "top":
            res.append(stack.top())
        elif cmd == "getMin":
            res.append(stack.getMin())
    print(json.dumps(res).replace(" ", ""))

if __name__ == '__main__':
    main()
""";
    }

    private static String hEvaluateReversePolishNotation() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    n = int(lines[0])
    tokens = lines[1:n+1]
    sol = Solution()
    result = sol.evalRPN(tokens)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hDailyTemperatures() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    temperatures = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.dailyTemperatures(temperatures)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hLargestRectangleInHistogram() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    heights = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.largestRectangleArea(heights)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hImplementQueueUsingStacks() {
        return """
import sys
import json
from solution import MyQueue

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n = int(tokens[idx])
    idx += 1
    queue = None
    res = []
    for _ in range(n):
        cmd = tokens[idx]
        idx += 1
        numArgs = int(tokens[idx])
        idx += 1
        if cmd == "MyQueue":
            queue = MyQueue()
            res.append(None)
        elif cmd == "push":
            val = int(tokens[idx])
            idx += 1
            queue.push(val)
            res.append(None)
        elif cmd == "pop":
            res.append(queue.pop())
        elif cmd == "peek":
            res.append(queue.peek())
        elif cmd == "empty":
            res.append(bool(queue.empty()))
    print(json.dumps(res).replace(" ", ""))

if __name__ == '__main__':
    main()
""";
    }

    private static String hSlidingWindowMaximum() {
        return """
import sys
import json
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    nums = [int(x) for x in lines[0].split(',')] if lines[0] else []
    k = int(lines[1])
    sol = Solution()
    result = sol.maxSlidingWindow(nums, k)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hAsteroidCollision() {
        return """
import sys
import json
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    asteroids = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.asteroidCollision(asteroids)
    print(json.dumps(result))

if __name__ == '__main__':
    main()
""";
    }

    private static String hBinarySearch() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    nums = [int(x) for x in lines[0].split(',')] if lines[0] else []
    target = int(lines[1])
    sol = Solution()
    result = sol.search(nums, target)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hSearch2DMatrix() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    rows = int(tokens[idx])
    idx += 1
    cols = int(tokens[idx])
    idx += 1
    matrix = []
    for _ in range(rows):
        row = [int(tokens[idx + j]) for j in range(cols)]
        idx += cols
        matrix.append(row)
    target = int(tokens[idx])
    sol = Solution()
    result = sol.searchMatrix(matrix, target)
    print(str(result).lower())

if __name__ == '__main__':
    main()
""";
    }

    private static String hKokoEatingBananas() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    piles = [int(x) for x in lines[0].split(',')] if lines[0] else []
    h = int(lines[1])
    sol = Solution()
    result = sol.minEatingSpeed(piles, h)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hFindMinInRotated() {
        return """
import sys
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    n = int(tokens[0])
    nums = [int(x) for x in tokens[1:n+1]]
    sol = Solution()
    result = sol.findMin(nums)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hSearchRotated() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    nums = [int(x) for x in lines[0].split(',')] if lines[0] else []
    target = int(lines[1])
    sol = Solution()
    result = sol.search(nums, target)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hTimeBasedKeyValueStore() {
        return """
import sys
import json
from solution import TimeMap

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n = int(tokens[idx])
    idx += 1
    ops = [tokens[idx + i] for i in range(n)]
    idx += n
    argCount = int(tokens[idx])
    idx += 1
    args = [tokens[idx + i] for i in range(argCount)]
    idx += argCount
    argIdx = 0
    obj = None
    res = []
    for op in ops:
        if op == "TimeMap":
            obj = TimeMap()
            res.append(None)
        elif op == "set":
            key = args[argIdx]
            argIdx += 1
            val = args[argIdx]
            argIdx += 1
            time = int(args[argIdx])
            argIdx += 1
            obj.set(key, val, time)
            res.append(None)
        elif op == "get":
            key = args[argIdx]
            argIdx += 1
            time = int(args[argIdx])
            argIdx += 1
            val = obj.get(key, time)
            res.append(val if val is not None else "")
    print(json.dumps(res))

if __name__ == '__main__':
    main()
""";
    }

    private static String hSplitArrayLargestSum() {
        return """
import sys
from solution import Solution

def main():
    lines = [l.strip() for l in sys.stdin.read().splitlines() if l.strip()]
    if not lines:
        return
    nums = [int(x) for x in lines[0].split(',')] if lines[0] else []
    k = int(lines[1])
    sol = Solution()
    result = sol.splitArray(nums, k)
    print(result)

if __name__ == '__main__':
    main()
""";
    }

    private static String hMergeTwoSortedLists() {
        return """
import sys
import json
import builtins

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

builtins.ListNode = ListNode
from solution import Solution

def main():
    tokens = sys.stdin.read().split()
    if not tokens:
        return
    idx = 0
    n1 = int(tokens[idx])
    idx += 1
    nums1 = [int(tokens[idx + i]) for i in range(n1)]
    idx += n1
    n2 = int(tokens[idx])
    idx += 1
    nums2 = [int(tokens[idx + i]) for i in range(n2)]
    idx += n2

    def to_linked(arr):
        dummy = ListNode(0)
        curr = dummy
        for x in arr:
            curr.next = ListNode(x)
            curr = curr.next
        return dummy.next

    l1 = to_linked(nums1)
    l2 = to_linked(nums2)
    sol = Solution()
    res = sol.mergeTwoLists(l1, l2)
    out = []
    while res:
        out.append(res.val)
        res = res.next
    print(json.dumps(out))

if __name__ == '__main__':
    main()
""";
    }
}
