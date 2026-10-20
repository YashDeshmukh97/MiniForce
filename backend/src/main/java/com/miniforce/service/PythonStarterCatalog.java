package com.miniforce.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PythonStarterCatalog {

    private static final Map<Long, String> STARTERS;

    static {
        Map<Long, String> m = new HashMap<>();
        m.put(1L, """
class Solution:
    def twoSum(self, nums, target):
        # Write your code here
        pass
""");
        m.put(2L, """
class Solution:
    def isValid(self, s):
        # Write your code here
        pass
""");
        m.put(3L, """
class Solution:
    def lengthOfLongestSubstring(self, s):
        # Write your code here
        pass
""");
        m.put(4L, """
class Solution:
    def merge(self, intervals):
        # Write your code here
        pass
""");
        m.put(5L, """
class Solution:
    def canFinish(self, numCourses, prerequisites):
        # Write your code here
        pass
""");
        m.put(6L, """
class Solution:
    def findMedianSortedArrays(self, nums1, nums2):
        # Write your code here
        pass
""");
        m.put(7L, """
class Solution:
    def ladderLength(self, beginWord, endWord, wordList):
        # Write your code here
        pass
""");
        m.put(8L, """
class Solution:
    def maxProfit(self, prices):
        # Write your code here
        pass
""");
        m.put(9L, """
# Definition for singly-linked list.
# class ListNode:
#     def __init__(self, val=0, next=None):
#         self.val = val
#         self.next = next
class Solution:
    def reverseList(self, head):
        # Write your code here
        pass
""");
        m.put(10L, """
class Solution:
    def numIslands(self, grid):
        # Write your code here
        pass
""");
        m.put(11L, """
class Solution:
    def coinChange(self, coins, amount):
        # Write your code here
        pass
""");
        m.put(12L, """
class Solution:
    def climbStairs(self, n):
        # Write your code here
        pass
""");
        m.put(13L, """
class Solution:
    def productExceptSelf(self, nums):
        # Write your code here
        pass
""");
        m.put(14L, """
class Solution:
    def maxSubArray(self, nums):
        # Write your code here
        pass
""");
        m.put(15L, """
class Solution:
    def trap(self, height):
        # Write your code here
        pass
""");
        m.put(16L, """
class Solution:
    def isAnagram(self, s, t):
        # Write your code here
        pass
""");
        m.put(17L, """
class Solution:
    def groupAnagrams(self, strs):
        # Write your code here
        pass
""");
        m.put(18L, """
class Solution:
    def topKFrequent(self, nums, k):
        # Write your code here
        pass
""");
        m.put(19L, """
class Codec:
    def encode(self, strs):
        # Write your code here
        pass

    def decode(self, s):
        # Write your code here
        pass
""");
        m.put(20L, """
class Solution:
    def longestConsecutive(self, nums):
        # Write your code here
        pass
""");
        m.put(21L, """
class Solution:
    def isValidSudoku(self, board):
        # Write your code here
        pass
""");
        m.put(22L, """
class Solution:
    def containsDuplicate(self, nums):
        # Write your code here
        pass
""");
        m.put(23L, """
class Solution:
    def findDisappearedNumbers(self, nums):
        # Write your code here
        pass
""");
        m.put(24L, """
class Solution:
    def intersection(self, nums1, nums2):
        # Write your code here
        pass
""");
        m.put(25L, """
class Solution:
    def isIsomorphic(self, s, t):
        # Write your code here
        pass
""");
        m.put(26L, """
class Solution:
    def isPalindrome(self, s):
        # Write your code here
        pass
""");
        m.put(27L, """
class Solution:
    def threeSum(self, nums):
        # Write your code here
        pass
""");
        m.put(28L, """
class Solution:
    def maxArea(self, height):
        # Write your code here
        pass
""");
        m.put(29L, """
class Solution:
    def minWindow(self, s, t):
        # Write your code here
        pass
""");
        m.put(30L, """
class Solution:
    def characterReplacement(self, s, k):
        # Write your code here
        pass
""");
        m.put(31L, """
class Solution:
    def checkInclusion(self, s1, s2):
        # Write your code here
        pass
""");
        m.put(32L, """
class Solution:
    def findMaxAverage(self, nums, k):
        # Write your code here
        pass
""");
        m.put(33L, """
class Solution:
    def findAnagrams(self, s, p):
        # Write your code here
        pass
""");
        m.put(34L, """
class Solution:
    def subarraysWithKDistinct(self, nums, k):
        # Write your code here
        pass
""");
        m.put(35L, """
class Solution:
    def totalFruit(self, fruits):
        # Write your code here
        pass
""");
        m.put(36L, """
class MinStack:
    def __init__(self):
        # Write your code here
        pass

    def push(self, val: int) -> None:
        pass

    def pop(self) -> None:
        pass

    def top(self) -> int:
        pass

    def getMin(self) -> int:
        pass
""");
        m.put(37L, """
class Solution:
    def evalRPN(self, tokens):
        # Write your code here
        pass
""");
        m.put(38L, """
class Solution:
    def dailyTemperatures(self, temperatures):
        # Write your code here
        pass
""");
        m.put(39L, """
class Solution:
    def largestRectangleArea(self, heights):
        # Write your code here
        pass
""");
        m.put(40L, """
class MyQueue:
    def __init__(self):
        # Write your code here
        pass

    def push(self, x: int) -> None:
        pass

    def pop(self) -> int:
        pass

    def peek(self) -> int:
        pass

    def empty(self) -> bool:
        pass
""");
        m.put(41L, """
class Solution:
    def maxSlidingWindow(self, nums, k):
        # Write your code here
        pass
""");
        m.put(42L, """
class Solution:
    def asteroidCollision(self, asteroids):
        # Write your code here
        pass
""");
        m.put(43L, """
class Solution:
    def search(self, nums, target):
        # Write your code here
        pass
""");
        m.put(44L, """
class Solution:
    def searchMatrix(self, matrix, target):
        # Write your code here
        pass
""");
        m.put(45L, """
class Solution:
    def minEatingSpeed(self, piles, h):
        # Write your code here
        pass
""");
        m.put(46L, """
class Solution:
    def findMin(self, nums):
        # Write your code here
        pass
""");
        m.put(47L, """
class Solution:
    def search(self, nums, target):
        # Write your code here
        pass
""");
        m.put(48L, """
class TimeMap:
    def __init__(self):
        # Write your code here
        pass

    def set(self, key: str, value: str, timestamp: int) -> None:
        pass

    def get(self, key: str, timestamp: int) -> str:
        pass
""");
        m.put(49L, """
class Solution:
    def splitArray(self, nums, k):
        # Write your code here
        pass
""");
        m.put(50L, """
# Definition for singly-linked list.
# class ListNode:
#     def __init__(self, val=0, next=None):
#         self.val = val
#         self.next = next
class Solution:
    def mergeTwoLists(self, list1, list2):
        # Write your code here
        pass
""");
        STARTERS = Collections.unmodifiableMap(m);
    }

    private PythonStarterCatalog() {}

    public static String getStarterCode(Long problemId) {
        if (problemId == null) return null;
        return STARTERS.get(problemId);
    }

    public static Map<Long, String> getAllStarters() {
        return STARTERS;
    }
}
