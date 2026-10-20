package com.miniforce.service;

import com.miniforce.model.ProblemHint;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HintCatalog {

    private static final Map<Long, List<ProblemHint>> HINTS_BY_PROBLEM;

    static {
        Map<Long, List<ProblemHint>> m = new LinkedHashMap<>();

        // 1. Two Sum
        put(m, 1L,
            "Instead of checking every pair (i, j) in O(n^2) time, ask what complement value target - nums[i] you need to find for the current element.",
            "Use a HashMap that maps each number seen so far to its array index so you can look up whether the complement already exists in O(1) time.",
            "Iterate i from 0 to nums.length - 1: compute complement = target - nums[i], check if map.containsKey(complement) BEFORE inserting nums[i] into the map (so you never reuse the same index twice), and return the pair of indices immediately when found."
        );

        // 2. Best Time to Buy and Sell Stock
        put(m, 2L,
            "To maximize profit from a single buy followed by a single sell, any sell on day i is best paired with the lowest stock price seen on or before day i.",
            "Use a single-pass greedy scan that tracks two values as you move left to right: the minimum buy price seen so far and the maximum profit achieved so far.",
            "Initialize minPrice = Integer.MAX_VALUE and maxProfit = 0. For each price in prices, update minPrice = Math.min(minPrice, price) and then update maxProfit = Math.max(maxProfit, price - minPrice)."
        );

        // 3. Maximum Subarray
        put(m, 3L,
            "When extending a contiguous subarray to index i, a negative running sum from earlier elements only reduces the sum you can achieve starting at or including nums[i].",
            "Apply Kadane's algorithm (dynamic programming): at each position i, decide whether to extend the best subarray ending at i - 1 or start a fresh subarray at nums[i].",
            "Initialize currentSum = nums[0] and maxSum = nums[0]. Loop i from 1 to nums.length - 1: set currentSum = Math.max(nums[i], currentSum + nums[i]) and maxSum = Math.max(maxSum, currentSum). Starting from nums[0] handles arrays where all numbers are negative."
        );

        // 4. Product of Array Except Self
        put(m, 4L,
            "The product of all elements except nums[i] can be factored into two parts: the product of all elements to the left of i multiplied by the product of all elements to the right of i.",
            "Use a two-pass prefix-and-suffix product accumulation without division: first populate each output slot with the prefix product of elements before i, then multiply in the suffix product of elements after i.",
            "Create ans[n] and set ans[0] = 1. In a left-to-right pass (i = 1..n-1), set ans[i] = ans[i - 1] * nums[i - 1]. Then maintain a running int suffix = 1 in a right-to-left pass (i = n-1..0), multiplying ans[i] *= suffix and updating suffix *= nums[i]."
        );

        // 5. Move Zeroes
        put(m, 5L,
            "Moving all zeroes to the end while keeping the relative order of non-zero elements is equivalent to compacting all non-zero elements to the front of the array in order.",
            "Use a two-pointer approach where a slow pointer writeIdx marks the next position to place a non-zero element and a fast pointer readIdx scans the array.",
            "Initialize writeIdx = 0. Loop readIdx from 0 to nums.length - 1: whenever nums[readIdx] != 0, assign nums[writeIdx++] = nums[readIdx]. After the loop finishes, fill the remaining indices from writeIdx to nums.length - 1 with 0."
        );

        // 6. Rotate Array
        put(m, 6L,
            "Rotating an array of length n to the right by k steps moves the last k % n elements to the front and shifts the first n - (k % n) elements to the back.",
            "Use the three-reversal in-place technique: reversing the entire array places the trailing k elements in the front block and the leading elements in the back block, though each block is internally reversed.",
            "First normalize k = k % nums.length. Then call a helper reverse(nums, l, r) three times: reverse(nums, 0, n - 1), reverse(nums, 0, k - 1), and reverse(nums, k, n - 1)."
        );

        // 7. Subarray Sum Equals K
        put(m, 7L,
            "Because nums can contain negative numbers and zeroes, a sliding window will not work monotonically; instead, express the sum of subarray nums[l..r] as prefixSum[r] - prefixSum[l - 1].",
            "Use a running prefix sum alongside a HashMap<Integer, Integer> that records how many times each prefix sum value has occurred so far.",
            "Initialize prefixCounts with map.put(0, 1) to account for subarrays starting at index 0, plus runningSum = 0 and count = 0. For each num in nums: add num to runningSum, add prefixCounts.getOrDefault(runningSum - k, 0) to count, and increment the frequency of runningSum in prefixCounts."
        );

        // 8. Merge Intervals
        put(m, 8L,
            "Overlapping intervals are much easier to detect when intervals are ordered by their start coordinate, because mergeable intervals become adjacent in the sorted order.",
            "Sort the intervals array by start time ascending (a[0] vs b[0]), then iterate through the sorted intervals while maintaining a list of merged intervals.",
            "Push the first interval into a List<int[]> merged. For each subsequent interval curr, compare curr[0] with the end of the last interval in merged (last[1]): if curr[0] <= last[1], merge them via last[1] = Math.max(last[1], curr[1]); otherwise append curr to merged."
        );

        // 9. Find the Duplicate Number
        put(m, 9L,
            "Since the array has n + 1 integers with every value in the range [1, n], you can treat each index i as a node pointing to next node nums[i], forming a directed functional graph.",
            "Apply Floyd's Tortoise and Hare cycle detection algorithm: the duplicate value has an in-degree of at least 2, which makes it the entrance to a cycle reachable from index 0.",
            "Phase 1: start slow = nums[0] and fast = nums[nums[0]], advancing slow = nums[slow] and fast = nums[nums[fast]] until slow == fast. Phase 2: reset slow = 0 and advance both slow = nums[slow] and fast = nums[fast] one step at a time until they meet at the duplicate number."
        );

        // 10. Trapping Rain Water
        put(m, 10L,
            "The amount of water trapped above bar i is determined by the shorter of the tallest bar to its left and the tallest bar to its right: max(0, min(leftMax[i], rightMax[i]) - height[i]).",
            "Use two pointers (left = 0, right = n - 1) moving inward while tracking leftMax and rightMax; whichever side currently has the smaller boundary height can safely resolve its trapped water immediately.",
            "While left < right: if height[left] <= height[right], update leftMax = Math.max(leftMax, height[left]), add leftMax - height[left] to totalWater, and increment left; otherwise update rightMax = Math.max(rightMax, height[right]), add rightMax - height[right] to totalWater, and decrement right."
        );

        // 11. Longest Substring Without Repeating Characters
        put(m, 11L,
            "As you scan the string from left to right, a repeating character at index right invalidates any substring that starts at or before that character's previous occurrence.",
            "Use a variable-size sliding window [left, right] backed by a HashMap<Character, Integer> (or 128-element int array) storing the most recent index of each character.",
            "Loop right from 0 to s.length() - 1: if char c = s.charAt(right) was seen at index prev >= left, jump left = prev + 1 (never move left backward). Update the last seen index of c to right and update maxLen = Math.max(maxLen, right - left + 1)."
        );

        // 12. Valid Palindrome II
        put(m, 12L,
            "When checking a palindrome from the outer ends inward, everything matches until you hit the first mismatch at indices (left, right).",
            "Use two pointers from both ends (left = 0, right = s.length() - 1); at the first mismatch s.charAt(left) != s.charAt(right), you have at most one deletion budget: either skip s.charAt(left) or skip s.charAt(right).",
            "Write a helper isPalindromeRange(s, i, j) that checks if s[i..j] is a strict palindrome. In validPalindrome, advance left++ and right-- while characters match; on the first mismatch, return isPalindromeRange(s, left + 1, right) || isPalindromeRange(s, left, right - 1)."
        );

        // 13. Longest Palindromic Substring
        put(m, 13L,
            "Every palindrome mirrors around its center, and a string of length n has 2n - 1 possible centers: n single-character centers (odd length) and n - 1 between-character centers (even length).",
            "Use the expand-around-center technique: for each index i, expand outward for both odd-length palindromes centered at (i, i) and even-length palindromes centered at (i, i + 1).",
            "Write a helper expand(s, left, right) that decrements left and increments right while left >= 0, right < s.length(), and s.charAt(left) == s.charAt(right), returning right - left - 1. Track bestStart and bestLen across all i, then return s.substring(bestStart, bestStart + bestLen)."
        );

        // 14. Palindromic Substrings
        put(m, 14L,
            "Each valid expansion step around a palindrome center discovers one distinct palindromic substring.",
            "Use the expand-around-center approach across all 2n - 1 centers: for each index i in 0..n-1, count palindromes centered at (i, i) for odd length and (i, i + 1) for even length.",
            "Create a helper countFromCenter(s, left, right) that loops while left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right), incrementing count, decrementing left, and incrementing right. Sum countFromCenter(s, i, i) + countFromCenter(s, i, i + 1) over all i."
        );

        // 15. String Compression
        put(m, 15L,
            "You need to group consecutive identical characters and overwrite the input array chars in-place, knowing that the compressed representation of a group is never longer than the group itself.",
            "Use two pointers: readIdx to scan contiguous runs of identical characters and writeIdx to write each character followed by the decimal digits of its run length when the count exceeds 1.",
            "While readIdx < chars.length: record char current = chars[readIdx] and advance readIdx while counting runLen. Write chars[writeIdx++] = current. If runLen > 1, convert Integer.toString(runLen).toCharArray() and copy each digit into chars[writeIdx++]. Return writeIdx."
        );

        // 16. Valid Anagram
        put(m, 16L,
            "Two strings are anagrams if and only if they have the exact same length and identical character frequencies.",
            "Since s and t consist of lowercase English letters, use a fixed-size frequency array int[26] to count occurrences in O(n) time and O(1) space.",
            "First check if s.length() != t.length() and return false immediately. Loop i from 0 to s.length() - 1, incrementing freq[s.charAt(i) - 'a'] and decrementing freq[t.charAt(i) - 'a']. Finally verify that all 26 entries in freq equal 0."
        );

        // 17. Group Anagrams
        put(m, 17L,
            "Strings that are anagrams of one another reduce to the exact same canonical representation when their characters are sorted or counted.",
            "Use a HashMap<String, List<String>> where the key is the canonical form of each word (such as its sorted character string) and the value is the list of words sharing that key.",
            "For each word in strs, convert it to char[] arr = word.toCharArray(), call Arrays.sort(arr), and build String key = new String(arr). Use map.computeIfAbsent(key, k -> new ArrayList<>()).add(word), then return new ArrayList<>(map.values())."
        );

        // 18. Longest Common Prefix
        put(m, 18L,
            "The longest common prefix of all strings in the array cannot be longer than the first string strs[0], and must match at every character position across all strings.",
            "Use vertical scanning (or prefix shrinking): compare characters column by column starting at index 0 of strs[0] against the same index in every other string.",
            "Loop i from 0 to strs[0].length() - 1: let c = strs[0].charAt(i). For each string s in strs, if i == s.length() || s.charAt(i) != c, immediately return strs[0].substring(0, i). If the outer loop completes, return strs[0]."
        );

        // 19. Decode String
        put(m, 19L,
            "Nested brackets like 3[a2[c]] require decoding the innermost bracketed substring first and then concatenating it into the enclosing string.",
            "Use two stacks—a countStack for repeat multipliers k and a stringStack for the StringBuilder accumulated before each '['.",
            "Maintain currentCount = 0 and currentStr = new StringBuilder(). On a digit, update currentCount = currentCount * 10 + (ch - '0'). On '[', push currentCount and currentStr, then reset both. On ']', pop repeatTimes and prevStr, append currentStr repeated repeatTimes onto prevStr, and set currentStr = prevStr. On a letter, append to currentStr."
        );

        // 20. Multiply Strings
        put(m, 20L,
            "In grade-school multiplication, multiplying digit num1[i] (from the left) with digit num2[j] contributes to positions i + j and i + j + 1 in a result array of length m + n.",
            "Allocate an int[m + n] array pos to accumulate digit-by-digit products from right to left without converting the full strings to integers.",
            "Loop i from m - 1 down to 0 and j from n - 1 down to 0: compute mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0') + pos[i + j + 1]. Set pos[i + j + 1] = mul % 10 and add mul / 10 to pos[i + j]. Finally build the result string skipping leading zeroes (returning \"0\" if empty)."
        );

        // 21. Valid Sudoku
        put(m, 21L,
            "Validating the filled cells of a 9x9 Sudoku board requires checking that no digit '1'..'9' repeats within any row r, any column c, or any of the nine 3x3 sub-boxes.",
            "Map each cell (r, c) to its 3x3 sub-box index using boxIdx = (r / 3) * 3 + (c / 3), and track seen digits using boolean[9][9] arrays (or HashSets) for rows, cols, and boxes.",
            "Iterate r from 0 to 8 and c from 0 to 8: skip if board[r][c] == '.'. Otherwise compute d = board[r][c] - '1' and box = (r / 3) * 3 + (c / 3). If rows[r][d], cols[c][d], or boxes[box][d] is already true, return false; otherwise mark all three true."
        );

        // 22. Contains Duplicate
        put(m, 22L,
            "To detect whether any value appears at least twice, you need fast membership testing as you inspect each number in the array.",
            "Use a HashSet<Integer> to record elements seen so far (or sort the array first and check adjacent elements nums[i] == nums[i - 1]).",
            "Create Set<Integer> seen = new HashSet<>(). For each n in nums, call seen.add(n)—if add(n) returns false, the element is already in the set so return true immediately. If the loop finishes without duplicates, return false."
        );

        // 23. Find All Numbers Disappeared in an Array
        put(m, 23L,
            "Because every value in nums is in the range [1, n], each number x naturally corresponds to the 0-based array index x - 1.",
            "Use the input array itself as a presence marker by negating the value stored at index Math.abs(nums[i]) - 1 whenever you see nums[i].",
            "Pass 1: for each i in 0..n-1, let idx = Math.abs(nums[i]) - 1; if nums[idx] > 0, set nums[idx] = -nums[idx]. Pass 2: collect every (i + 1) into your result list where nums[i] > 0, since a positive value at index i means (i + 1) never appeared."
        );

        // 24. Intersection of Two Arrays
        put(m, 24L,
            "The intersection requires unique elements present in both arrays, so duplicates within nums1 or nums2 must be deduplicated.",
            "Insert all elements of nums1 into a HashSet<Integer> set1, then scan nums2 and collect matching elements into a second HashSet<Integer> resultSet.",
            "After populating set1 from nums1, loop x in nums2: if set1.contains(x), add x to resultSet. Finally copy the elements of resultSet into an int[] array of size resultSet.size() and return it."
        );

        // 25. Isomorphic Strings
        put(m, 25L,
            "Isomorphism requires a one-to-one bijection between characters of s and characters of t: each character in s must map to a single character in t, and no two characters in s may map to the same character in t.",
            "Maintain two lookup tables (such as two int[256] arrays or two HashMaps) to enforce both the forward mapping s -> t and the reverse mapping t -> s simultaneously.",
            "Initialize int[] mapST = new int[256] and int[] mapTS = new int[256]. Loop i from 0 to s.length() - 1: let c1 = s.charAt(i) and c2 = t.charAt(i). If mapST[c1] != mapTS[c2], return false; otherwise set both mapST[c1] = i + 1 and mapTS[c2] = i + 1."
        );

        // 26. Valid Palindrome
        put(m, 26L,
            "Only letters and digits matter for this palindrome check, and uppercase letters should be treated as equivalent to their lowercase counterparts.",
            "Use two pointers (left = 0, right = s.length() - 1) moving inward, skipping any non-alphanumeric characters before comparing.",
            "While left < right: advance left++ while left < right && !Character.isLetterOrDigit(s.charAt(left)); advance right-- while left < right && !Character.isLetterOrDigit(s.charAt(right)). Compare Character.toLowerCase(s.charAt(left)) and Character.toLowerCase(s.charAt(right)); if unequal return false, else left++ and right--."
        );

        // 27. 3Sum
        put(m, 27L,
            "Sorting the array first allows you to fix the first element nums[i] and reduce finding the other two elements to a sorted Two Sum problem with target -nums[i], while also making duplicate triplets easy to skip.",
            "Sort nums ascending, iterate i from 0 to n - 3, and for each i run a converging two-pointer scan with left = i + 1 and right = n - 1.",
            "Skip duplicate anchors when i > 0 && nums[i] == nums[i - 1]. While left < right: compute sum = nums[i] + nums[left] + nums[right]. If sum == 0, record the triplet, increment left and decrement right, and advance both past any duplicate values (nums[left] == nums[left - 1], nums[right] == nums[right + 1]); if sum < 0 increment left; else decrement right."
        );

        // 28. Container With Most Water
        put(m, 28L,
            "The water held between lines left and right is (right - left) * Math.min(height[left], height[right]), so the width shrinks as you move inward and only a taller shorter-side line can compensate.",
            "Use a two-pointer greedy approach starting at the widest container (left = 0, right = height.length - 1) and always move the pointer pointing to the shorter line inward.",
            "Initialize maxArea = 0, left = 0, right = height.length - 1. While left < right: update maxArea = Math.max(maxArea, (right - left) * Math.min(height[left], height[right])). If height[left] < height[right], increment left; otherwise decrement right."
        );

        // 29. Minimum Window Substring
        put(m, 29L,
            "You want the shortest contiguous substring of s that covers the multiset of characters in t; expand the right end of a window until all required characters are satisfied, then contract the left end as much as possible.",
            "Use a sliding window [left, right] with an int[128] frequency table of needed characters from t and a counter tracking how many characters of t are currently satisfied in the window.",
            "Populate need[c] for each char in t and set missing = t.length(). Expand right over s: if need[s.charAt(right)] > 0, decrement missing; then decrement need[s.charAt(right)]. While missing == 0, update bestStart/bestLen if right - left + 1 is smaller, increment need[s.charAt(left)], and if it becomes > 0 increment missing, then increment left."
        );

        // 30. Sliding Window Maximum
        put(m, 30L,
            "When a new element nums[i] enters the sliding window, any older element already in the window that is <= nums[i] can never become the maximum of the current or any future window.",
            "Use a monotonic decreasing Deque<Integer> that stores array indices whose corresponding values in nums are strictly decreasing from front to back.",
            "For each i in 0..nums.length-1: 1) if !deque.isEmpty() && deque.peekFirst() <= i - k, removeFirst(); 2) while !deque.isEmpty() && nums[deque.peekLast()] <= nums[i], removeLast(); 3) offerLast(i); 4) once i >= k - 1, record ans[i - k + 1] = nums[deque.peekFirst()]."
        );

        // 31. Permutation in String
        put(m, 31L,
            "s2 contains a permutation of s1 if and only if some contiguous substring of s2 of length s1.length() has the exact same character frequency counts as s1.",
            "Use a fixed-size sliding window of length k = s1.length() across s2, maintaining two int[26] frequency arrays for s1 and the current window in s2.",
            "If s1.length() > s2.length() return false. Count frequencies for the first k characters of s1 and s2. Check Arrays.equals(freq1, freq2). Then slide i from k to s2.length() - 1: increment freq2[s2.charAt(i) - 'a'], decrement freq2[s2.charAt(i - k) - 'a'], and return true as soon as Arrays.equals(freq1, freq2)."
        );

        // 32. Maximum Average Subarray I
        put(m, 32L,
            "Since the window length k is fixed, maximizing the average of a contiguous subarray of length k is identical to maximizing its sum.",
            "Use a fixed-size sliding window of length k: compute the sum of the first k elements, then slide the window one step at a time by adding the entering element and subtracting the leaving element.",
            "Compute long windowSum for indices 0..k-1 and set maxSum = windowSum. Loop i from k to nums.length - 1: update windowSum += nums[i] - nums[i - k] and maxSum = Math.max(maxSum, windowSum). Finally return (double) maxSum / k."
        );

        // 33. Find All Anagrams in a String
        put(m, 33L,
            "An anagram of p starting at index l in s is simply a substring of length p.length() whose 26-letter frequency profile matches that of p.",
            "Maintain a fixed-size sliding window of length k = p.length() over s using two int[26] arrays (pCount and sCount) and compare them at each window position.",
            "If s.length() < p.length() return an empty list. Populate pCount and sCount for the first k characters; if Arrays.equals(pCount, sCount), add 0 to the result list. For i from k to s.length() - 1, add s.charAt(i) to sCount, remove s.charAt(i - k) from sCount, and if Arrays.equals(pCount, sCount) add (i - k + 1) to the result."
        );

        // 34. Binary Search
        put(m, 34L,
            "Because nums is sorted in ascending order, comparing target with the middle element eliminates half of the remaining search range on every step.",
            "Use iterative binary search with two boundary pointers low = 0 and high = nums.length - 1.",
            "While low <= high: compute mid = low + (high - low) / 2 to avoid integer overflow. If nums[mid] == target return mid; if nums[mid] < target set low = mid + 1; otherwise set high = mid - 1. Return -1 if the loop terminates."
        );

        // 35. Search in Rotated Sorted Array
        put(m, 35L,
            "When you split a rotated sorted array at mid, at least one of the two halves—either [low..mid] or [mid..high]—is guaranteed to be strictly sorted.",
            "Run a modified binary search: at each mid, first identify which half is normally sorted by comparing nums[low] <= nums[mid], then check whether target lies within the bounds of that sorted half.",
            "While low <= high: let mid = low + (high - low) / 2; if nums[mid] == target return mid. If nums[low] <= nums[mid] (left half sorted), check if nums[low] <= target && target < nums[mid] to set high = mid - 1, else low = mid + 1. Otherwise (right half sorted), check if nums[mid] < target && target <= nums[high] to set low = mid + 1, else high = mid - 1."
        );

        // 36. Find First and Last Position of Element in Sorted Array
        put(m, 36L,
            "A single binary search that stops on the first match cannot tell how far the duplicates of target extend to the left and right in O(log n) time.",
            "Run two separate binary searches: one biased toward the left to find the first index where nums[idx] == target, and one biased toward the right to find the last index.",
            "Write a helper findBound(nums, target, isFirst): when nums[mid] == target, record bound = mid and continue searching left (high = mid - 1) if isFirst is true, or continue searching right (low = mid + 1) if isFirst is false. Return new int[]{findBound(nums, target, true), findBound(nums, target, false)}."
        );

        // 37. Search Insert Position
        put(m, 37L,
            "The insertion index for target in a sorted array is the first index i such that nums[i] >= target (or nums.length if all elements are smaller than target).",
            "Use standard lower-bound binary search over [0, nums.length - 1].",
            "Initialize low = 0 and high = nums.length - 1. While low <= high: let mid = low + (high - low) / 2. If nums[mid] == target return mid; if nums[mid] < target set low = mid + 1; else set high = mid - 1. When the loop ends, low is the exact insertion index."
        );

        // 38. Koko Eating Bananas
        put(m, 38L,
            "If Koko can finish all piles within h hours at eating speed k, she can also finish them at any speed > k; this monotonic property allows binary searching over the speed k.",
            "Binary search the answer space for speed k in the range [1, max(piles)], evaluating total hours needed for a candidate speed mid as the sum of ceil(pile / mid).",
            "Set low = 1 and high = maxElement(piles). While low < high: let mid = low + (high - low) / 2, and accumulate long hours = 0 using hours += (pile + mid - 1) / mid for each pile. If hours <= h, mid is feasible so set high = mid; otherwise set low = mid + 1. Return low."
        );

        // 39. Find Minimum in Rotated Sorted Array
        put(m, 39L,
            "In a rotated sorted array with unique elements, all elements before the inflection point are greater than nums[n - 1], and all elements from the minimum onward are <= nums[n - 1].",
            "Use binary search comparing the middle element nums[mid] against the right boundary element nums[high] to determine which half contains the minimum.",
            "Initialize low = 0, high = nums.length - 1. While low < high: compute mid = low + (high - low) / 2. If nums[mid] > nums[high], the minimum must lie strictly to the right of mid, so set low = mid + 1; otherwise mid could be the minimum, so set high = mid. Return nums[low]."
        );

        // 40. Median of Two Sorted Arrays
        put(m, 40L,
            "Finding the median of two sorted arrays of sizes m and n is equivalent to partitioning both arrays into a combined left half of size (m + n + 1) / 2 and a right half such that every element on the left is <= every element on the right.",
            "Binary search the partition cut i in the smaller array nums1 (0 <= i <= m), which automatically fixes the partition cut j = (m + n + 1) / 2 - i in nums2.",
            "Ensure nums1.length <= nums2.length. Binary search i in [0, m]: define left1 = (i == 0 ? Integer.MIN_VALUE : nums1[i - 1]), right1 = (i == m ? Integer.MAX_VALUE : nums1[i]), and similarly left2, right2 around j. When left1 <= right2 && left2 <= right1, return max(left1, left2) if m + n is odd, or (max(left1, left2) + min(right1, right2)) / 2.0 if even."
        );

        // 41. Subsets
        put(m, 41L,
            "Every element in nums is either included or excluded in a subset, yielding 2^n distinct subsets in total.",
            "Use backtracking (DFS) over the array index start: at every recursive call, add a copy of the current path to the result list, then iterate i from start to nums.length - 1 to extend the subset.",
            "In backtrack(nums, start, currentList, result): first call result.add(new ArrayList<>(currentList)). Then loop i from start to nums.length - 1: add nums[i] to currentList, recurse with start = i + 1, and remove the last element of currentList on return."
        );

        // 42. Permutations
        put(m, 42L,
            "Unlike subsets, permutations care about element order and must use every element in nums exactly once per permutation.",
            "Use backtracking with a boolean[] used array of length n to track which indices are already placed in the current partial permutation.",
            "When currentList.size() == nums.length, add new ArrayList<>(currentList) to results and return. Otherwise loop i from 0 to nums.length - 1: skip if used[i] is true; mark used[i] = true, add nums[i] to currentList, recurse, and then backtrack by removing the last element and setting used[i] = false."
        );

        // 43. Combination Sum
        put(m, 43L,
            "Since the same candidate number may be chosen an unlimited number of times, after picking candidates[i] your next recursive choice can still start at index i (but never earlier than i, to avoid duplicate combinations in different orders).",
            "Use recursive backtracking tracking the remaining target sum and the starting candidate index start.",
            "In backtrack(candidates, remain, start, current, result): if remain == 0, add new ArrayList<>(current) to result and return; if remain < 0, return. Loop i from start to candidates.length - 1: add candidates[i] to current, call backtrack(candidates, remain - candidates[i], i, current, result), and remove the last element."
        );

        // 44. Generate Parentheses
        put(m, 44L,
            "A string of 2n parentheses is well-formed if and only if we use at most n '(' characters and at every prefix the number of ')' placed so far never exceeds the number of '(' placed so far.",
            "Use backtracking with counts (open, close) representing how many '(' and ')' have been appended to the current StringBuilder.",
            "Base case: when current.length() == 2 * n, add current.toString() to the list. Recursive choices: if open < n, append '(', recurse with (open + 1, close), and delete the last char; if close < open, append ')', recurse with (open, close + 1), and delete the last char."
        );

        // 45. Word Search
        put(m, 45L,
            "The word can start at any cell (r, c) whose letter matches word.charAt(0) and move in the 4 cardinal directions without reusing a cell along the same path.",
            "Run a depth-first search (DFS) with backtracking from every cell (r, c), temporarily marking visited board cells in-place so the current path cannot revisit them.",
            "In dfs(board, word, r, c, idx): if idx == word.length() return true; if r or c is out of bounds or board[r][c] != word.charAt(idx) return false. Save char temp = board[r][c], set board[r][c] = '#', check dfs in all 4 neighbors (r+1, r-1, c+1, c-1) for idx + 1, restore board[r][c] = temp, and return whether any neighbor succeeded."
        );

        // 46. N-Queens
        put(m, 46L,
            "Exactly one queen must be placed in each row from 0 to n - 1, and two queens attack each other if they share the same column c, main diagonal (r - c), or anti-diagonal (r + c).",
            "Place queens row by row using backtracking while maintaining three boolean arrays: cols[n], diag1[2 * n] (indexed by r - c + n), and diag2[2 * n] (indexed by r + c).",
            "In backtrack(row): when row == n, convert the char[][] board into a List<String> and add to results. For col in 0..n-1: if cols[col], diag1[row - col + n], or diag2[row + col] is true, skip; otherwise mark all three true, set board[row][col] = 'Q', recurse on row + 1, and undo the markings and reset board[row][col] = '.'."
        );

        // 47. Valid Parentheses
        put(m, 47L,
            "The most recently opened bracket must be the first one closed, which follows a Last-In-First-Out (LIFO) discipline.",
            "Use a stack (ArrayDeque<Character>): push every opening bracket ('(', '{', '[') or its expected closing counterpart onto the stack, and verify matches when encountering a closing bracket.",
            "For each char c in s.toCharArray(): if c == '(' push ')'; else if c == '{' push '}'; else if c == '[' push ']'; else if stack.isEmpty() || stack.pop() != c return false. After processing all characters, return stack.isEmpty()."
        );

        // 48. Min Stack
        put(m, 48L,
            "To retrieve the minimum element in O(1) time even after arbitrary pop operations, each stack state needs to remember what the minimum value was at the moment that element was pushed.",
            "Maintain two synchronized stacks (or a stack of int[]{val, currentMin} pairs): one for the pushed values and one that tracks the running minimum at each depth.",
            "Iterate i from 0 to ops.length - 1: on \"push\", push vals[i] onto valStack and push Math.min(vals[i], minStack.isEmpty() ? vals[i] : minStack.peek()) onto minStack, setting ans[i] = -1; on \"pop\", pop both stacks and set ans[i] = -1; on \"top\", set ans[i] = valStack.peek(); on \"getMin\", set ans[i] = minStack.peek()."
        );

        // 49. Evaluate Reverse Polish Notation
        put(m, 49L,
            "In Reverse Polish (postfix) notation, every operator applies to the two most recently evaluated operands immediately preceding it.",
            "Use an integer stack (ArrayDeque<Integer>): push numbers onto the stack, and whenever you see an operator (\"+\", \"-\", \"*\", \"/\"), pop the top two operands, apply the operator, and push the result back.",
            "Pay careful attention to operand order for non-commutative operations '-' and '/': when an operator token arrives, the first pop is the right operand (int b = stack.pop()) and the second pop is the left operand (int a = stack.pop()), so compute a - b or a / b before pushing the result."
        );

        // 50. Daily Temperatures
        put(m, 50L,
            "As you scan temperatures from left to right, a warmer temperature on day i resolves the waiting time for all earlier unresolved days whose temperatures are strictly less than temperatures[i].",
            "Use a monotonic decreasing stack of day indices (ArrayDeque<Integer>) where the temperatures at the stored indices are non-increasing from bottom to top.",
            "Create int[] ans = new int[n] and Deque<Integer> stack = new ArrayDeque<>(). For i from 0 to n - 1: while !stack.isEmpty() && temperatures[i] > temperatures[stack.peek()], pop prevDay = stack.pop() and set ans[prevDay] = i - prevDay. Then push i onto stack. Any indices left on the stack default to 0 in ans."
        );

        HINTS_BY_PROBLEM = Collections.unmodifiableMap(m);
    }

    private HintCatalog() {}

    private static void put(Map<Long, List<ProblemHint>> m, Long problemId, String h1, String h2, String h3) {
        m.put(problemId, List.of(
            new ProblemHint(problemId, 1, h1),
            new ProblemHint(problemId, 2, h2),
            new ProblemHint(problemId, 3, h3)
        ));
    }

    public static List<ProblemHint> getHintsForProblem(Long problemId) {
        return HINTS_BY_PROBLEM.getOrDefault(problemId, Collections.emptyList());
    }

    public static Map<Long, List<ProblemHint>> getAllHints() {
        return HINTS_BY_PROBLEM;
    }
}
