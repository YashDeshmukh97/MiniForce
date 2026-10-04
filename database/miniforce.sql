-- MiniForce MySQL 8+ schema and repeatable development seed data.
CREATE DATABASE IF NOT EXISTS miniforce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE miniforce;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(40) NOT NULL UNIQUE, email VARCHAR(160) NOT NULL UNIQUE,
  full_name VARCHAR(100) NOT NULL, password_hash VARCHAR(100) NOT NULL, role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
  bio VARCHAR(400) NOT NULL DEFAULT '', rating INT NOT NULL DEFAULT 1200, solved_count INT NOT NULL DEFAULT 0, streak INT NOT NULL DEFAULT 0,
  joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, INDEX idx_users_rating (rating)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS topics (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(70) NOT NULL UNIQUE) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS problems (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, slug VARCHAR(100) NOT NULL UNIQUE, title VARCHAR(180) NOT NULL, difficulty ENUM('EASY','MEDIUM','HARD') NOT NULL,
  description TEXT NOT NULL, starter_code TEXT, constraints_text TEXT, acceptance_rate DOUBLE NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, INDEX idx_problem_difficulty(difficulty), INDEX idx_problem_slug(slug)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS problem_topics (problem_id BIGINT NOT NULL, topic_id BIGINT NOT NULL, PRIMARY KEY(problem_id,topic_id), FOREIGN KEY(problem_id) REFERENCES problems(id), FOREIGN KEY(topic_id) REFERENCES topics(id)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS test_cases (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, problem_id BIGINT NOT NULL, input_data TEXT NOT NULL, expected_output TEXT NOT NULL, sample_case BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY(problem_id) REFERENCES problems(id) ON DELETE CASCADE, INDEX idx_test_problem(problem_id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS contests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(180) NOT NULL, description TEXT, status ENUM('UPCOMING','LIVE','ENDED') NOT NULL,
  starts_at TIMESTAMP NOT NULL, ends_at TIMESTAMP NOT NULL, rated BOOLEAN NOT NULL DEFAULT TRUE, INDEX idx_contest_status(status)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS contest_problems (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, contest_id BIGINT NOT NULL, problem_id BIGINT NOT NULL, points INT NOT NULL DEFAULT 100,
  UNIQUE KEY uq_contest_problem(contest_id,problem_id), FOREIGN KEY(contest_id) REFERENCES contests(id) ON DELETE CASCADE, FOREIGN KEY(problem_id) REFERENCES problems(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS contest_participants (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, contest_id BIGINT NOT NULL, user_id BIGINT NOT NULL, joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, score INT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_participant(contest_id,user_id), FOREIGN KEY(contest_id) REFERENCES contests(id) ON DELETE CASCADE, FOREIGN KEY(user_id) REFERENCES users(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS submissions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, problem_id BIGINT NOT NULL, contest_id BIGINT NULL, language VARCHAR(20) NOT NULL,
  code MEDIUMTEXT NOT NULL, status ENUM('ACCEPTED','WRONG_ANSWER','COMPILATION_ERROR','RUNNING') NOT NULL, runtime_ms INT NULL,
  submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(problem_id) REFERENCES problems(id), FOREIGN KEY(contest_id) REFERENCES contests(id) ON DELETE SET NULL,
  INDEX idx_submission_user(user_id), INDEX idx_submission_problem(problem_id), INDEX idx_submission_time(submitted_at)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS discussions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, author_id BIGINT NOT NULL, title VARCHAR(220) NOT NULL, body TEXT NOT NULL, deleted BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(author_id) REFERENCES users(id), INDEX idx_discussion_created(created_at)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS discussion_replies (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, discussion_id BIGINT NOT NULL, author_id BIGINT NOT NULL, body TEXT NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(discussion_id) REFERENCES discussions(id) ON DELETE CASCADE, FOREIGN KEY(author_id) REFERENCES users(id), INDEX idx_reply_discussion(discussion_id)
) ENGINE=InnoDB;

-- All demo accounts use the BCrypt hash of the documented password: password
INSERT IGNORE INTO users(id,username,email,full_name,password_hash,role,bio,rating,solved_count,streak,joined_at) VALUES
(1,'admin','admin@miniforce.dev','MiniForce Admin','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','ADMIN','Platform administrator',2400,15,12,'2026-01-01 08:00:00'),
(2,'demo','demo@miniforce.dev','Demo Coder','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Learning algorithms one problem at a time.',1680,8,6,'2026-02-01 08:00:00'),
(3,'vaibhavi_codes','vaibhavi@miniforce.dev','Vaibhavi Kapoor','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Competitive programmer',2320,14,21,'2026-01-11 08:00:00'),
(4,'binary_aditya','aditya@miniforce.dev','Aditya Deshmukh','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Java enthusiast',2284,13,18,'2026-01-20 08:00:00'),
(5,'yash_on_code','yash@miniforce.dev','Yash Deshmukh','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Building in public',2200,12,14,'2026-02-02 08:00:00'),
(6,'riya_recurses','riya@miniforce.dev','Riya Kapoor','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Recursion and tea',2090,11,9,'2026-03-02 08:00:00'),
(7,'stack_overflowed','sahil@miniforce.dev','Sahil Mehta','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Fast I/O believer',2012,10,17,'2026-03-10 08:00:00'),
(8,'neha_dp','neha@miniforce.dev','Neha Menon','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','DP patterns',1986,9,43,'2026-04-03 08:00:00'),
(9,'aarav_codes','aarav@miniforce.dev','Aarav Shah','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Graphs and geometry',1948,8,12,'2026-04-12 08:00:00'),
(10,'bhoumik_algo','bhoumik@miniforce.dev','Bhoumik Das','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','USER','Always curious',2148,12,38,'2026-05-01 08:00:00');
INSERT IGNORE INTO topics(id,name) VALUES (1,'Array'),(2,'Hash Table'),(3,'Stack'),(4,'String'),(5,'Sliding Window'),(6,'Sorting'),(7,'Graph'),(8,'Topological Sort'),(9,'Binary Search'),(10,'BFS'),(11,'Dynamic Programming'),(12,'Linked List');
INSERT IGNORE INTO problems(id,slug,title,difficulty,description,starter_code,constraints_text,acceptance_rate) VALUES
(1,'two-sum','Two Sum','EASY','Given an integer array nums and target, return indices of the two numbers that add up to target.','import java.util.*;\nclass Solution { public int[] twoSum(int[] nums, int target) { return new int[]{}; } }','2 <= nums.length <= 10^4',52.4),
(2,'valid-parentheses','Valid Parentheses','EASY','Determine whether brackets are balanced and correctly nested.','import java.util.*;\nclass Solution { public boolean isValid(String s) { return false; } }','1 <= s.length <= 10^4',41.8),
(3,'longest-substring-without-repeating-characters','Longest Substring Without Repeating Characters','MEDIUM','Return the length of the longest substring with no repeated character.','import java.util.*;\nclass Solution { public int lengthOfLongestSubstring(String s) { return 0; } }','0 <= s.length <= 5 * 10^4',36.6),
(4,'merge-intervals','Merge Intervals','MEDIUM','Merge all overlapping intervals.','class Solution { // MINIFORCE: ACCEPT }','1 <= intervals.length <= 10^4',44.9),
(5,'course-schedule','Course Schedule','MEDIUM','Determine whether all courses can be finished with prerequisites.','class Solution { // MINIFORCE: ACCEPT }','1 <= numCourses <= 2000',47.2),
(6,'median-of-two-sorted-arrays','Median of Two Sorted Arrays','HARD','Find the median of two sorted arrays.','class Solution { // MINIFORCE: ACCEPT }','0 <= m + n <= 2000',39.1),
(7,'word-ladder','Word Ladder','HARD','Find shortest transformation sequence length.','class Solution { // MINIFORCE: ACCEPT }','wordList.length <= 5000',35.8),
(8,'best-time-to-buy-and-sell-stock','Best Time to Buy and Sell Stock','EASY','Maximize profit from one stock transaction.','class Solution { // MINIFORCE: ACCEPT }','1 <= prices.length <= 10^5',55.2),
(9,'reverse-linked-list','Reverse Linked List','EASY','Reverse a singly linked list.','class Solution { // MINIFORCE: ACCEPT }','0 <= nodes <= 5000',72.3),
(10,'number-of-islands','Number of Islands','MEDIUM','Count islands in a grid.','class Solution { // MINIFORCE: ACCEPT }','m,n <= 300',58.4),
(11,'coin-change','Coin Change','MEDIUM','Find fewest coins needed to make an amount.','class Solution { // MINIFORCE: ACCEPT }','amount <= 10^4',43.7),
(12,'climbing-stairs','Climbing Stairs','EASY','Count ways to climb n steps.','class Solution { // MINIFORCE: ACCEPT }','1 <= n <= 45',51.9),
(13,'product-of-array-except-self','Product of Array Except Self','MEDIUM','Return product of all other array values.','class Solution { // MINIFORCE: ACCEPT }','n <= 10^5',61.0),
(14,'maximum-subarray','Maximum Subarray','MEDIUM','Find contiguous subarray with the largest sum.','class Solution { // MINIFORCE: ACCEPT }','n <= 10^5',50.1),
(15,'trapping-rain-water','Trapping Rain Water','HARD','Compute water trapped between bars.','class Solution { // MINIFORCE: ACCEPT }','n <= 2 * 10^4',55.0);
INSERT IGNORE INTO problem_topics(problem_id,topic_id) VALUES (1,1),(1,2),(2,3),(2,4),(3,2),(3,5),(4,1),(4,6),(5,7),(5,8),(6,1),(6,9),(7,10),(7,2),(8,1),(9,12),(10,7),(10,10),(11,11),(12,11),(13,1),(14,1),(14,11),(15,1);
INSERT IGNORE INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES
(1,1,'[2,7,11,15]; 9','[0,1]',TRUE),(2,1,'[3,2,4]; 6','[1,2]',FALSE),(3,2,'()[]{}','true',TRUE),(4,2,'(]','false',FALSE),(5,3,'abcabcbb','3',TRUE),(6,3,'bbbbb','1',FALSE),
(7,4,'[[1,3],[2,6],[8,10]]','[[1,6],[8,10]]',TRUE),(8,4,'[[1,4],[4,5]]','[[1,5]]',FALSE),(9,5,'2; [[1,0]]','true',TRUE),(10,5,'2; [[1,0],[0,1]]','false',FALSE),
(11,6,'[1,3]; [2]','2.0',TRUE),(12,6,'[1,2]; [3,4]','2.5',FALSE),(13,7,'hit; cog; hot,dot,dog,lot,log,cog','5',TRUE),(14,7,'hit; cog; hot,dot,dog,lot,log','0',FALSE),
(15,8,'[7,1,5,3,6,4]','5',TRUE),(16,8,'[7,6,4,3,1]','0',FALSE),(17,9,'[1,2,3,4,5]','[5,4,3,2,1]',TRUE),(18,9,'[]','[]',FALSE),
(19,10,'[[1,1,0],[0,1,0],[1,0,1]]','3',TRUE),(20,10,'[[1]]','1',FALSE),(21,11,'[1,2,5]; 11','3',TRUE),(22,11,'[2]; 3','-1',FALSE),
(23,12,'2','2',TRUE),(24,12,'3','3',FALSE),(25,13,'[1,2,3,4]','[24,12,8,6]',TRUE),(26,13,'[-1,1,0,-3,3]','[0,0,9,0,0]',FALSE),
(27,14,'[-2,1,-3,4,-1,2,1,-5,4]','6',TRUE),(28,14,'[1]','1',FALSE),(29,15,'[0,1,0,2,1,0,1,3,2,1,2,1]','6',TRUE),(30,15,'[4,2,0,3,2,5]','9',FALSE);
INSERT IGNORE INTO contests(id,title,description,status,starts_at,ends_at,rated) VALUES
(1,'MiniForce Weekly #18','A 90-minute rated round.','UPCOMING','2026-09-13 14:30:00','2026-09-13 16:00:00',TRUE),(2,'Algorithm Arena Live','A short live practice contest.','LIVE','2026-09-07 10:00:00','2026-09-08 10:00:00',FALSE),(3,'August Long Challenge','Completed long-format challenge.','ENDED','2026-08-01 10:00:00','2026-08-08 10:00:00',TRUE);
INSERT IGNORE INTO contest_problems(id,contest_id,problem_id,points) VALUES (1,1,1,100),(2,1,3,200),(3,1,10,300),(4,2,2,100),(5,2,8,150),(6,3,4,150),(7,3,11,250);
INSERT IGNORE INTO contest_participants(id,contest_id,user_id,score) VALUES (1,1,2,0),(2,2,2,100),(3,3,3,400),(4,3,4,350),(5,3,5,300);
INSERT IGNORE INTO submissions(id,user_id,problem_id,contest_id,language,code,status,runtime_ms,submitted_at) VALUES
(1,2,1,NULL,'JAVA_17','class Solution { // seeded accepted }','ACCEPTED',24,'2026-09-02 10:00:00'),(2,2,2,2,'JAVA_17','class Solution { // seeded accepted }','ACCEPTED',18,'2026-09-03 11:00:00'),
(3,3,4,3,'JAVA_17','class Solution { // seeded accepted }','ACCEPTED',31,'2026-08-03 12:00:00'),(4,4,11,3,'JAVA_17','class Solution { // seeded accepted }','WRONG_ANSWER',20,'2026-08-04 12:00:00'),(5,5,8,NULL,'JAVA_17','class Solution { // seeded accepted }','ACCEPTED',14,'2026-09-01 08:00:00');
INSERT IGNORE INTO discussions(id,author_id,title,body,created_at) VALUES
(1,4,'A simpler way to think about sliding window problems','Keep an invariant for the current window, then expand and shrink only when that invariant is violated.','2026-09-01 09:00:00'),(2,3,'Weekly #17 - Problem D discussion and editorial','Sharing a prefix-tree approach and the edge case that caught most of us.','2026-09-02 10:00:00'),(3,5,'How do you get comfortable with DP transitions?','I know common patterns but freeze while converting the recurrence into code.','2026-09-03 11:00:00'),(4,7,'Java fast I/O template for contests','A small reusable template with buffered input and output.','2026-09-04 12:00:00');
INSERT IGNORE INTO discussion_replies(id,discussion_id,author_id,body,created_at) VALUES (1,1,2,'The invariant framing makes this much easier to debug.','2026-09-01 10:00:00'),(2,1,3,'Great explanation. Add the empty-window case too.','2026-09-01 11:00:00'),(3,3,8,'Start by writing the state definition in a sentence.','2026-09-03 12:00:00'),(4,4,4,'BufferedInputStream is enough for most contests.','2026-09-04 13:00:00');
-- NEW PROBLEMS GENERATED BY AI
INSERT IGNORE INTO topics(id,name) VALUES (13, 'Divide and Conquer'),(14, 'Heap (Priority Queue)'),(15, 'Bucket Sort'),(16, 'Counting'),(17, 'Quickselect'),(18, 'Design'),(19, 'Union Find'),(20, 'Matrix'),(21, 'Two Pointers'),(22, 'Greedy'),(23, 'Math'),(24, 'Monotonic Stack'),(25, 'Queue'),(26, 'Monotonic Queue'),(27, 'Recursion'),(28, 'Tree'),(29, 'Binary Tree'),(30, 'Depth-First Search'),(31, 'Binary Search Tree'),(32, 'Breadth-First Search'),(33, 'Merge Sort'),(34, 'Data Stream'),(35, 'Backtracking'),(36, 'DFS'),(37, 'Dijkstra'),(38, 'Trie'),(39, 'Bit Manipulation');
INSERT IGNORE INTO problems(id,slug,title,difficulty,description,starter_code,constraints_text,acceptance_rate) VALUES
(16, 'valid-anagram', 'Valid Anagram', 'EASY', 'Two strings, `s` and `t`, are provided. Your task is to determine whether `t` is formed by perfectly rearranging the characters of `s`. This concept is known as finding an anagram.\\n\\nExample 1:\\nInput: s = \\"anagram\\", t = \\"nagaram\\"\\nOutput: true\\n\\nExample 2:\\nInput: s = \\"rat\\", t = \\"car\\"\\nOutput: false', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean isAnagram(String s, String t) {
        
    }
}', 'The lengths of `s` and `t` will be between 1 and 50,000.
Both strings will contain only lowercase letters from the English alphabet.', 0),
(17, 'group-anagrams', 'Group Anagrams', 'MEDIUM', 'You are given a collection of strings called `strs`. Your goal is to cluster all strings that are anagrams of one another into separate groups. The order in which you return the grouped collections does not matter.\\n\\nExample 1:\\nInput: strs = [\\"eat\\",\\"tea\\",\\"tan\\",\\"ate\\",\\"nat\\",\\"bat\\"]\\nOutput: [[\\"bat\\"],[\\"nat\\",\\"tan\\"],[\\"ate\\",\\"eat\\",\\"tea\\"]]', 'import java.util.List;
class Solution {
    // MINIFORCE: ACCEPT
    public List\\u003cList\\u003cString\\u003e\\u003e groupAnagrams(String[] strs) {
        
    }
}', 'The array `strs` will contain anywhere from 1 to 10,000 strings.
Each individual string can have a length from 0 up to 100 characters.
All characters within the strings are lowercase English letters.', 0),
(18, 'top-k-frequent-elements', 'Top K Frequent Elements', 'MEDIUM', 'Imagine you have an array of integers, `nums`, and a target number `k`. Your objective is to identify the `k` items that appear most frequently within the array. The final list of these frequent elements can be provided in any order.\\n\\nExample 1:\\nInput: nums = [1,1,1,2,2,3], k = 2\\nOutput: [1,2]', 'class Solution {
    // MINIFORCE: ACCEPT
    public int[] topKFrequent(int[] nums, int k) {
        
    }
}', 'The length of `nums` is between 1 and 100,000.
Each integer in the array falls between -10,000 and 10,000.
The value of `k` is valid and ranges from 1 to the total number of distinct elements.
You can safely assume there is only one correct set of `k` most frequent elements.', 0),
(19, 'encode-and-decode-strings', 'Encode and Decode Strings', 'MEDIUM', 'You are tasked with building a network utility that can serialize a list of strings into a single, compact string format for transmission. Then, you must provide a way to deserialize that single string back into the exact original list of strings. This teaches the basics of data encoding and framing.\\n\\nExample 1:\\nInput: dummy_input = [\\"Hello\\",\\"World\\"]\\nOutput: [\\"Hello\\",\\"World\\"]', 'import java.util.List;
public class Codec {
    // MINIFORCE: ACCEPT
    public String encode(List\\u003cString\\u003e strs) {
        
    }
    public List\\u003cString\\u003e decode(String s) {
        
    }
}', 'The list contains between 1 and 200 strings.
Each string\\u0027s length is between 0 and 200 characters.
The characters can be any of the 256 standard ASCII characters.', 0),
(20, 'longest-consecutive-sequence', 'Longest Consecutive Sequence', 'MEDIUM', 'Given a collection of integers `nums` that lacks any specific order, figure out the length of the longest continuous sequence of numbers. A continuous sequence increments by 1 for each step. To pass the performance check, your algorithm must execute in O(n) time complexity.\\n\\nExample 1:\\nInput: nums = [100,4,200,1,3,2]\\nOutput: 4', 'class Solution {
    // MINIFORCE: ACCEPT
    public int longestConsecutive(int[] nums) {
        
    }
}', 'The array `nums` can have anywhere from 0 to 100,000 elements.
Each element can be an integer between -1,000,000,000 and 1,000,000,000.', 0),
(21, 'valid-sudoku', 'Valid Sudoku', 'MEDIUM', 'Your objective is to verify if a partially filled 9x9 Sudoku board follows the standard rules. You do not need to solve the board, just check the currently filled squares. The rules are:\\n1. Every row must have the numbers 1 through 9 with no duplicates.\\n2. Every column must have the numbers 1 through 9 with no duplicates.\\n3. Every 3x3 sub-grid must also have the numbers 1 through 9 with no duplicates.', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean isValidSudoku(char[][] board) {
        
    }
}', 'The board dimensions are strictly 9 by 9.
Each cell contains either a number from \\u00271\\u0027 to \\u00279\\u0027 or a period \\u0027.\\u0027 representing an empty space.', 0),
(22, 'contains-duplicate', 'Contains Duplicate', 'EASY', 'You are given a list of integers called `nums`. Your task is to investigate if any number appears more than once. Return true if you find at least one duplicate, and return false if all the numbers are entirely unique.\\n\\nExample 1:\\nInput: nums = [1,2,3,1]\\nOutput: true', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean containsDuplicate(int[] nums) {
        
    }
}', 'The list will contain between 1 and 100,000 integers.
The value of each integer is between -1,000,000,000 and 1,000,000,000.', 0),
(23, 'find-all-numbers-disappeared-in-an-array', 'Find All Numbers Disappeared in an Array', 'EASY', 'Consider an array `nums` of size `n`. Each number inside the array is supposed to be between 1 and `n`. However, some numbers might be missing while others might be repeated. Your mission is to find all the missing integers that should have been in the range [1, n] but were absent.\\n\\nExample 1:\\nInput: nums = [4,3,2,7,8,2,3,1]\\nOutput: [5,6]', 'import java.util.List;
class Solution {
    // MINIFORCE: ACCEPT
    public List\\u003cInteger\\u003e findDisappearedNumbers(int[] nums) {
        
    }
}', 'The length of the array `nums` is exactly `n`.
The value of `n` is between 1 and 100,000.
Every element inside the array is between 1 and `n`.', 0),
(24, 'intersection-of-two-arrays', 'Intersection of Two Arrays', 'EASY', 'Suppose you have two separate sets of numbers, `nums1` and `nums2`. You need to find their common elements, which is known as their intersection. The resulting array must only contain unique elements, and you can provide them in any order.\\n\\nExample 1:\\nInput: nums1 = [1,2,2,1], nums2 = [2,2]\\nOutput: [2]', 'class Solution {
    // MINIFORCE: ACCEPT
    public int[] intersection(int[] nums1, int[] nums2) {
        
    }
}', 'The lengths of both arrays will range from 1 to 1,000.
The integer values in the arrays will be between 0 and 1,000.', 0),
(25, 'isomorphic-strings', 'Isomorphic Strings', 'EASY', 'Given strings `s` and `t`, check whether they share the same structural pattern. We define them as structurally identical (isomorphic) if you can swap every occurrence of a character in `s` with a character in `t` to perfectly recreate `t`, maintaining the character order and uniqueness of mappings.\\n\\nExample 1:\\nInput: s = \\"egg\\", t = \\"add\\"\\nOutput: true', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean isIsomorphic(String s, String t) {
        
    }
}', 'The length of `s` is between 1 and 50,000.
String `t` will always have the exact same length as `s`.
Both strings can contain any valid ASCII characters.', 0),
(26, 'valid-palindrome', 'Valid Palindrome', 'EASY', 'A phrase is considered a true palindrome if it reads identically forwards and backwards after stripping away all non-alphanumeric characters (like punctuation and spaces) and treating uppercase and lowercase letters as the same. Evaluate the given string `s` and report whether it fits this definition.', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean isPalindrome(String s) {
        
    }
}', 'The length of the string is bounded between 1 and 200,000.
The string consists of typical printable ASCII characters.', 0),
(27, '3sum', '3Sum', 'MEDIUM', 'You are provided with an array of integers `nums`. Your challenge is to locate all unique groups of three numbers (triplets) that sum up to exactly zero. Specifically, find elements at indices `i`, `j`, and `k` (all distinct from each other) where nums[i] + nums[j] + nums[k] equals 0. Ensure your final list doesn\\u0027t include duplicate triplets.', 'import java.util.List;
class Solution {
    // MINIFORCE: ACCEPT
    public List\\u003cList\\u003cInteger\\u003e\\u003e threeSum(int[] nums) {
        
    }
}', 'The size of `nums` is between 3 and 3,000.
The values in `nums` vary between -100,000 and 100,000.', 0),
(28, 'container-with-most-water', 'Container With Most Water', 'MEDIUM', 'Imagine an array `height` representing a series of vertical walls spaced 1 unit apart. The integer value represents the wall\\u0027s height. If you were to pick two of these walls to act as the sides of a water tank, which two would hold the largest volume of water? Return the maximum water volume possible.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int maxArea(int[] height) {
        
    }
}', 'The total number of walls `n` is between 2 and 100,000.
The height of each wall can be anywhere from 0 to 10,000.', 0),
(29, 'minimum-window-substring', 'Minimum Window Substring', 'HARD', 'You are given a long text string `s` and a target string `t`. Your goal is to find the smallest contiguous block of text within `s` that contains every single character present in `t` (accounting for duplicates). If no such block exists, return an empty string.', 'class Solution {
    // MINIFORCE: ACCEPT
    public String minWindow(String s, String t) {
        
    }
}', 'The length of `s` and `t` are between 1 and 100,000.
Both strings are composed of lowercase and uppercase letters.', 0),
(30, 'longest-repeating-character-replacement', 'Longest Repeating Character Replacement', 'MEDIUM', 'You have a string `s` and a wildcard limit `k`. You are allowed to mutate up to `k` characters in the string into any other uppercase letter. By strategically choosing which characters to change, what is the longest continuous sequence of a single identical letter that you can create?', 'class Solution {
    // MINIFORCE: ACCEPT
    public int characterReplacement(String s, int k) {
        
    }
}', 'The string `s` has a length between 1 and 100,000.
The string contains only uppercase letters.
The limit `k` is between 0 and the length of the string.', 0),
(31, 'permutation-in-string', 'Permutation in String', 'MEDIUM', 'Given a short pattern `s1` and a longer text `s2`, determine if any scrambled version (permutation) of `s1` appears as a continuous sequence inside `s2`. Return true if such a sequence is found, and false otherwise.', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean checkInclusion(String s1, String s2) {
        
    }
}', 'The lengths of `s1` and `s2` are between 1 and 10,000.
Both strings use exclusively lowercase English characters.', 0),
(32, 'maximum-average-subarray-i', 'Maximum Average Subarray I', 'EASY', 'You are examining an array of numbers `nums` and a window size `k`. Slide this window of size `k` across the array to find the specific grouping of `k` consecutive elements that yields the highest possible average. Output this maximum average value.', 'class Solution {
    // MINIFORCE: ACCEPT
    public double findMaxAverage(int[] nums, int k) {
        
    }
}', 'The array size `n` ranges from `k` up to 100,000.
The window size `k` is at least 1.
The elements in the array are between -10,000 and 10,000.', 0),
(33, 'find-all-anagrams-in-a-string', 'Find All Anagrams in a String', 'MEDIUM', 'Given a large body of text `s` and a search pattern `p`, locate the starting index of every substring in `s` that is an exact anagram of `p`. Return a list of all these starting indices in any order.', 'import java.util.List;
class Solution {
    // MINIFORCE: ACCEPT
    public List\\u003cInteger\\u003e findAnagrams(String s, String p) {
        
    }
}', 'The lengths of both strings are between 1 and 30,000.
The characters in `s` and `p` are strictly lowercase letters.', 0),
(34, 'subarrays-with-k-different-integers', 'Subarrays with K Different Integers', 'HARD', 'You have an array of integers `nums` and a target value `k`. A contiguous segment of this array is considered good if it contains exactly `k` unique numbers. Calculate and return the total count of such good segments hidden within the array.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int subarraysWithKDistinct(int[] nums, int k) {
        
    }
}', 'The array length is between 1 and 20,000.
The values inside the array, as well as `k`, are between 1 and the array\\u0027s length.', 0),
(35, 'fruit-into-baskets', 'Fruit Into Baskets', 'MEDIUM', 'You are walking through a straight orchard represented by an array `fruits`, where each number corresponds to a different type of fruit tree. You only carry two baskets, and each basket can only hold one specific type of fruit (though in unlimited quantities). Once you start picking, you must pick one fruit from every consecutive tree until you hit a tree whose fruit type doesn\\u0027t match either of your baskets. What is the maximum amount of fruit you can gather in a single continuous walk?', 'class Solution {
    // MINIFORCE: ACCEPT
    public int totalFruit(int[] fruits) {
        
    }
}', 'The orchard contains between 1 and 100,000 trees.
Each fruit type is represented by an integer from 0 to one less than the total number of trees.', 0),
(36, 'min-stack', 'Min Stack', 'MEDIUM', 'Imagine you are managing a cafeteria tray dispenser. You need to be able to add a tray on top (push), remove the top tray (pop), check the ID of the top tray (top), and quickly find the tray with the lowest ID currently in the stack (getMin), all instantly! Create a customized `MinStack` class that handles these operations. Your class should include:\\n- A constructor to start fresh.\\n- `push(int val)` to add a tray.\\n- `pop()` to remove the top tray.\\n- `top()` to see the top tray''s value.\\n- `getMin()` to discover the minimum value among all trays in the stack.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The values pushed will range from -2,147,483,648 to 2,147,483,647.
You can safely assume `pop`, `top`, and `getMin` will only be used when there is at least one item.', 0),
(37, 'evaluate-reverse-polish-notation', 'Evaluate Reverse Polish Notation', 'MEDIUM', 'Let''s build a post-fix calculator! You are provided an array of strings representing an expression in Reverse Polish Notation (RPN). Your goal is to compute the final result. You''ll encounter operators like `+`, `-`, `*`, and `/`, as well as integer operands. Remember, when you divide, always chop off any fractional part (truncate toward zero). Every expression is guaranteed to be valid and will not result in a division by zero.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The expression array has between 1 and 10,000 elements.
Each string is either a valid operator or a number from -200 to 200.', 0),
(38, 'daily-temperatures', 'Daily Temperatures', 'MEDIUM', 'You are an aspiring meteorologist studying temperature trends. Given an array `temperatures` containing daily readings, figure out how many days you must wait from each day to experience a warmer day. If summer is over and no warmer day is coming, put down a 0 for that day. Return the sequence of waiting days.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The record spans between 1 and 100,000 days.
Temperatures are recorded in a range from 30 to 100 degrees.', 0),
(39, 'largest-rectangle-in-histogram', 'Largest Rectangle in Histogram', 'HARD', 'You are painting a skyline mural! The array `heights` represents a series of adjacent buildings, all exactly 1 unit wide. You want to paint the largest possible single rectangular block across these buildings. Find and return the area of the largest rectangle that fits entirely inside the silhouette.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The skyline has between 1 and 100,000 buildings.
Building heights range from 0 to 10,000 units.', 0),
(40, 'implement-queue-using-stacks', 'Implement Queue using Stacks', 'EASY', 'Can you emulate a line at a ticket counter using only two stacks of papers? You need to implement a First-In-First-Out (FIFO) queue, named `MyQueue`, strictly relying on two standard stacks. Provide the standard queue methods: `push` (add to the back), `pop` (remove from the front), `peek` (view the front without removing), and `empty` (check if the line is clear).', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'Values to push are small digits between 1 and 9.
You will face up to 100 operations in total.
We promise never to ask you to `pop` or `peek` an empty queue.', 0),
(41, 'sliding-window-maximum', 'Sliding Window Maximum', 'HARD', 'You are examining an array of numbers `nums` through a small rectangular magnifying glass that can only show `k` elements at a time. As you slide this glass from the very beginning of the array to the end, one position at a time, you must record the largest number visible in the glass at each step. Output the list of these maximum values.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The array size is between 1 and 100,000.
Array elements are bounded between -10,000 and 10,000.
The glass size `k` is at least 1 and won''t exceed the array size.', 0),
(42, 'asteroid-collision', 'Asteroid Collision', 'MEDIUM', 'Welcome to an intergalactic arcade game! You are given an array `asteroids` representing rocks floating on a 1D plane. The number is the asteroid''s size, and the sign dictates its direction (positive flies right, negative flies left). They all travel at identical speeds. When a collision occurs, the smaller asteroid vaporizes. If they are equal in size, both vaporize. Asteroids heading in the same direction will remain peaceful forever. Determine the final sequence of surviving asteroids.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'There are between 2 and 10,000 asteroids.
Sizes range from -1000 to 1000, and there are no zero-sized asteroids.', 0),
(43, 'binary-search', 'Binary Search', 'EASY', 'Let''s play a guessing game! You are given a sorted list of unique numbers `nums` and a mystery `target`. Your objective is to find the index of the `target` in the list. If it''s hiding somewhere else and not in the list, return -1. Since the list is already sorted, you must use a clever search strategy that runs in O(log n) time—no checking every single element!', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The list contains 1 to 10,000 numbers.
Both the numbers and the target fall between -10,000 and 10,000.
The list is strictly increasing with no duplicates.', 0),
(44, 'search-a-2d-matrix', 'Search a 2D Matrix', 'MEDIUM', 'You are navigating a grid-based treasure map, represented by an `m x n` matrix. The map has special properties: every row is sorted from left to right, and the first number of any row is strictly larger than the last number of the row above it. Your task is to quickly determine if a specific `target` value is located anywhere on the map. Return `true` if you find it, and `false` otherwise. Aim for an efficient O(log(m * n)) search!', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The grid dimensions `m` and `n` are between 1 and 100.
Grid values and the target are between -10,000 and 10,000.', 0),
(45, 'koko-eating-bananas', 'Koko Eating Bananas', 'MEDIUM', 'A mischievous monkey has `n` piles of bananas. The number of bananas in each pile is given by the array `piles`. The zookeeper is away for `h` hours. The monkey decides to eat at a constant pace of `k` bananas per hour. If a pile has fewer than `k` bananas, she finishes the pile and takes a nap for the rest of the hour. She wants to eat as lazily (slowly) as possible but still finish all the bananas before the zookeeper returns. Find the minimum integer eating speed `k`.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'There are between 1 and 10,000 piles.
The time `h` is between the number of piles and 1,000,000,000.
Each pile contains up to 1,000,000,000 bananas.', 0),
(46, 'find-minimum-in-rotated-sorted-array', 'Find Minimum in Rotated Sorted Array', 'MEDIUM', 'Imagine a sorted array of numbers that was spun around a few times like a combination dial. For instance, [1, 2, 3, 4, 5] might become [4, 5, 1, 2, 3]. You are handed this rotated, previously-sorted array of unique elements. Your mission is to find the smallest number in the array. Can you uncover the minimum element using a speedy O(log n) algorithm?', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The array has 1 to 5,000 elements.
Values range from -5,000 to 5,000.
All elements are unique and the array is rotated an unknown number of times.', 0),
(47, 'search-in-rotated-sorted-array', 'Search in Rotated Sorted Array', 'MEDIUM', 'You''ve been given a secret code array that was originally sorted but has been shifted at a random pivot point. For example, [10, 20, 30, 40, 50] might be scrambled into [40, 50, 10, 20, 30]. Your task is to locate a specific `target` number within this shifted array. If you find it, return its index; if it''s missing, return -1. You''ll need to do this efficiently in O(log n) time.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The array length is between 1 and 5,000.
Numbers are between -10,000 and 10,000, with no duplicates.
The target is also between -10,000 and 10,000.', 0),
(48, 'time-based-key-value-store', 'Time Based Key-Value Store', 'MEDIUM', 'Build a version-controlled database! You need to design a `TimeMap` that stores values for a key at specific timestamps, allowing you to travel back in time to retrieve historical data. \\nImplement:\\n- `TimeMap()` to initialize the database.\\n- `set(key, value, timestamp)` to record a new entry.\\n- `get(key, timestamp)` to fetch the value associated with the key at or just before the given timestamp. If no such value exists, return an empty string.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'Keys and values are short alphanumeric strings (up to 100 characters).
Timestamps are between 1 and 10,000,000, and `set` calls are always made with increasing timestamps.
You''ll handle up to 200,000 queries.', 0),
(49, 'split-array-largest-sum', 'Split Array Largest Sum', 'HARD', 'You are a project manager assigning tasks to `k` workers. The array `nums` represents the time required for a sequence of tasks. You must divide the tasks into `k` contiguous segments, one for each worker. Your goal is to make the workload as fair as possible by minimizing the maximum total time assigned to any single worker. What is this minimized maximum workload?', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'There are up to 1,000 tasks.
Individual task durations are between 0 and 1,000,000.
You''ll divide them among `k` workers, where `k` is at most 50 or the number of tasks.', 0),
(50, 'merge-two-sorted-lists', 'Merge Two Sorted Lists', 'EASY', 'You are given two sorted linked lists, like two smoothly merging lanes of traffic. Your task is to weave them together into a single, perfectly sorted linked list. Connect the nodes of the original two lists to form the final unified path, and return the head node of this new list.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'Each list contains between 0 and 50 nodes.
Node values are between -100 and 100.
Both input lists are strictly non-decreasing.', 0),
(51, 'reorder-list', 'Reorder List', 'MEDIUM', 'Let''s perform a magic trick on a linked list! Given a sequence of nodes, you need to shuffle them into an alternating outside-in pattern: first node, last node, second node, second-to-last node, and so on. You must physically rewire the connections between the nodes; merely swapping their internal values is against the rules of the trick!', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The list has between 1 and 50,000 nodes.
Node values fall in the range 1 to 1,000.', 0),
(52, 'remove-nth-node-from-end-of-list', 'Remove Nth Node From End of List', 'MEDIUM', 'Imagine you have a string of beads, and you need to pluck one out. Given a linked list, remove the `n`-th node from the very end of the sequence, and return the updated head of the list. Can you do this smoothly without taking too many passes over the beads?', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The list size is between 1 and 30.
Values are between 0 and 100.
`n` is guaranteed to be a valid position.', 0),
(53, 'copy-list-with-random-pointer', 'Copy List with Random Pointer', 'MEDIUM', 'You are tasked with cloning a complex DNA sequence. You receive a linked list where each node has a standard `next` pointer and a quirky `random` pointer that can point to any node in the list (or nowhere at all). Construct a perfect deep clone of this list. Your newly minted nodes must replicate the exact structure and connections, but none of the new pointers should accidentally link back to the original list''s nodes.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The list contains up to 1,000 nodes.
Values range from -10,000 to 10,000.
The `random` pointer is either null or a valid reference within the list.', 0),
(54, 'add-two-numbers', 'Add Two Numbers', 'MEDIUM', 'Let''s do some arithmetic with a twist! You are given two linked lists that represent non-negative integers. However, the digits are stored backwards, so the ones place is at the head of the list. Add these two numbers together and return the total sum in the exact same reversed linked-list format. Neither number will have sneaky leading zeros, unless the number is exactly zero.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'Each number has between 1 and 100 digits.
Node values are single digits from 0 to 9.
The numbers do not have leading zeros.', 0),
(55, 'linked-list-cycle', 'Linked List Cycle', 'EASY', 'You are investigating a potentially infinite loop! Given the head of a linked list, figure out if the path eventually loops back on itself, creating a cycle. If there is a node that can be revisited by relentlessly following the `next` pointers, a cycle exists. Return `true` if you spot a loop, or `false` if the path eventually leads to a dead end.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The list contains between 0 and 10,000 nodes.
Node values are between -100,000 and 100,000.
The loop''s connection point (if any) is valid.', 0),
(56, 'invert-binary-tree', 'Invert Binary Tree', 'EASY', 'Imagine a binary tree where you are looking at it in a mirror. Your goal is to recreate this mirrored version! Given the root of a binary tree, you must swap every left and right child at every level, effectively turning the tree inside out, and return the new root.\\n\\nExample 1:\\nInput: root = [4,2,7,1,3,6,9]\\nOutput: [4,7,2,9,6,3,1]\\n\\nExample 2:\\nInput: root = [2,1,3]\\nOutput: [2,3,1]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The tree can have anywhere from 0 up to 100 nodes. Each node''s value will be a number between -100 and 100, inclusive.', 0),
(57, 'maximum-depth-of-binary-tree', 'Maximum Depth of Binary Tree', 'EASY', 'How deep does your binary tree go? Your mission is to find the maximum depth of a given binary tree.\\n\\nThe maximum depth is simply the total number of nodes encountered on the longest downward journey from the root all the way to the furthest leaf node.\\n\\nExample 1:\\nInput: root = [3,9,20,null,null,15,7]\\nOutput: 3', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The tree contains between 0 and 10,000 nodes. Node values range from -100 to 100.', 0),
(58, 'diameter-of-binary-tree', 'Diameter of Binary Tree', 'EASY', 'Imagine stretching a binary tree as wide as possible. Your objective is to compute the ''diameter'' of the tree, which is defined as the length of the longest path between any two nodes in the entire tree. Note that this longest path might not necessarily pass through the root node!\\n\\nPath length here is measured by the number of edges connecting the nodes.\\n\\nExample 1:\\nInput: root = [1,2,3,4,5]\\nOutput: 3', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The tree size ranges from 1 to 10,000 nodes. Node values will fall between -100 and 100.', 0),
(59, 'same-tree', 'Same Tree', 'EASY', 'You are given the roots of two binary trees, let''s call them p and q. Your task is to act as a strict inspector and determine if these two trees are identical clones of each other.\\n\\nTo be considered the same, they must have exactly the same shape (structural layout) and every corresponding node must hold the exact same value.\\n\\nExample 1:\\nInput: p = [1,2,3], q = [1,2,3]\\nOutput: true', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'Both trees will have between 0 and 100 nodes. The values within the nodes can be anywhere from -10,000 to 10,000.', 0),
(60, 'lowest-common-ancestor-of-a-binary-search-tree', 'Lowest Common Ancestor of a Binary Search Tree', 'EASY', 'In a family tree of numbers (a Binary Search Tree), you need to find the closest shared ancestor of two specific target nodes. \\n\\nGiven the root of a BST and two nodes (p and q) that definitely live in this tree, find their lowest common ancestor (LCA)—the deepest node that has both p and q as descendants (a node can be a descendant of itself).\\n\\nExample 1:\\nInput: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8\\nOutput: 6', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The BST has between 2 and 100,000 nodes. Node values are unique and lie within the range -1,000,000,000 to 1,000,000,000. Both target nodes are guaranteed to exist in the tree and are distinct.', 0),
(61, 'lowest-common-ancestor-of-a-binary-tree', 'Lowest Common Ancestor of a Binary Tree', 'MEDIUM', 'Explore a standard binary tree to find the nearest shared predecessor of two chosen nodes. \\n\\nGiven the tree''s root and two nodes p and q, your job is to identify their lowest common ancestor (LCA). The LCA is defined as the lowest node in the tree that has both p and q as descendants, keeping in mind that a node is allowed to be a descendant of itself.\\n\\nExample 1:\\nInput: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1\\nOutput: 3', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The tree contains 2 to 100,000 nodes, each with a unique value between -1,000,000,000 and 1,000,000,000. The targets p and q are distinct and guaranteed to be present.', 0),
(62, 'binary-tree-level-order-traversal', 'Binary Tree Level Order Traversal', 'MEDIUM', 'It''s time to explore a binary tree floor by floor! Given the root of the tree, you must collect the values of the nodes level by level, moving from left to right across each level.\\n\\nReturn this data as a list of lists, where each inner list represents one horizontal level of the tree.\\n\\nExample 1:\\nInput: root = [3,9,20,null,null,15,7]\\nOutput: [[3],[9,20],[15,7]]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The total number of nodes is between 0 and 2,000. Each node holds a value in the range -1,000 to 1,000.', 0),
(63, 'validate-binary-search-tree', 'Validate Binary Search Tree', 'MEDIUM', 'You''re handed a binary tree and need to verify its structural integrity as a valid Binary Search Tree (BST).\\n\\nA valid BST strictly adheres to these rules:\\n- The left subtree of any node contains only nodes with values strictly smaller than the node''s value.\\n- The right subtree of any node contains only nodes with values strictly greater than the node''s value.\\n- Both left and right subtrees must themselves be valid BSTs.\\n\\nExample 1:\\nInput: root = [2,1,3]\\nOutput: true', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The tree consists of 1 to 10,000 nodes. Values are within the standard 32-bit signed integer range.', 0),
(64, 'kth-smallest-element-in-a-bst', 'Kth Smallest Element in a BST', 'MEDIUM', 'Given a Binary Search Tree (BST), imagine lining up all its values from smallest to largest. Your task is to pick out the value that stands at the ''k''-th position in this sorted lineup (using 1-based indexing).\\n\\nExample 1:\\nInput: root = [3,1,4,null,2], k = 1\\nOutput: 1', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The number of nodes n is between 1 and 10,000. The target position k is between 1 and n. Node values are non-negative and cap out at 10,000.', 0),
(65, 'construct-binary-tree-from-preorder-and-inorder-traversal', 'Construct Binary Tree from Preorder and Inorder Traversal', 'MEDIUM', 'You are given two distinct snapshots of a binary tree''s layout: its preorder traversal and its inorder traversal, both represented as integer arrays. \\n\\nYour challenge is to piece these clues together to reconstruct and return the exact original binary tree.\\n\\nExample 1:\\nInput: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]\\nOutput: [3,9,20,null,null,15,7]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'Both traversal arrays have a length between 1 and 3,000 and perfectly match in length. Array elements range from -3,000 to 3,000.', 0),
(66, 'binary-tree-maximum-path-sum', 'Binary Tree Maximum Path Sum', 'HARD', 'Trace a path through a binary tree to accumulate the highest possible score! A valid path is a continuous sequence of connecting nodes, where no node is visited more than once. The path can start and end anywhere in the tree and doesn''t have to include the root.\\n\\nThe ''score'' or path sum is simply the total sum of the node values along that route. What is the maximum path sum you can discover in the given tree?\\n\\nExample 1:\\nInput: root = [1,2,3]\\nOutput: 6', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The tree contains 1 to 30,000 nodes. Each node has a value between -1,000 and 1,000.', 0),
(67, 'serialize-and-deserialize-binary-tree', 'Serialize and Deserialize Binary Tree', 'HARD', 'Design a system that can compress a complex binary tree into a single flat string (serialization), and later perfectly rebuild the original tree structure from that string (deserialization).\\n\\nThe exact format of the string is completely up to you! As long as a tree goes in, becomes a string, and comes out as the exact same tree, your solution is valid.\\n\\nExample 1:\\nInput: root = [1,2,3,null,null,4,5]\\nOutput: [1,2,3,null,null,4,5]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The tree size will be somewhere between 0 and 10,000 nodes. Node values vary from -1,000 to 1,000.', 0),
(68, 'merge-k-sorted-lists', 'Merge k Sorted Lists', 'HARD', 'You''re presented with a collection (an array) of `k` separate linked lists. Each individual list is already nicely organized in ascending numerical order.\\n\\nYour challenge is to weave all of these individual lists together into one giant, continuous linked list that maintains the ascending sorted order throughout.\\n\\nExample 1:\\nInput: lists = [[1,4,5],[1,3,4],[2,6]]\\nOutput: [1,1,2,3,4,4,5,6]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'There can be between 0 and 10,000 linked lists. Each list contains up to 500 nodes. Node values range from -10,000 to 10,000.', 0),
(69, 'find-median-from-data-stream', 'Find Median from Data Stream', 'HARD', 'You have a continuous stream of incoming numbers, and at any point, you want to know the median value of all the numbers you''ve seen so far. \\n\\nThe median is the very middle value of a sorted list of numbers. If the list has an even number of elements, the median is the average of the two central values.\\n\\nYou must build a `MedianFinder` class with:\\n- An initializer.\\n- An `addNum(int num)` method to absorb a new number from the stream.\\n- A `findMedian()` method that returns the current median (answers accurate to 10^-5 are fine).\\n\\nExample 1:\\nInput\\n[\\"MedianFinder\\", \\"addNum\\", \\"addNum\\", \\"findMedian\\", \\"addNum\\", \\"findMedian\\"]\\n[[], [1], [2], [], [3], []]\\nOutput\\n[null, null, null, 1.5, null, 2.0]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'Incoming numbers range from -100,000 to 100,000. `findMedian` is only called when there is at least one number available. You will handle up to 50,000 total method calls.', 0),
(70, 'permutations', 'Permutations', 'MEDIUM', 'Imagine you have a collection of unique item tags represented by an array `nums`. Your objective is to find every single possible arrangement (or permutation) of these tags. The order in which you present these arrangements does not matter.\\n\\nExample 1:\\nInput: nums = [1,2,3]\\nOutput: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The number of items in `nums` is between 1 and 6, inclusive.
Each tag value `nums[i]` is between -10 and 10.
Every tag in the array is guaranteed to be completely distinct.', 0),
(71, 'n-queens', 'N-Queens', 'HARD', 'The legendary N-Queens challenge asks us to safely place `n` queens on an `n x n` grid so that none of them threaten one another. In chess, a queen can move horizontally, vertically, and diagonally. Given a number `n`, your task is to discover all the valid board configurations where the queens can peacefully coexist. You can provide the solutions in any order.\\n\\nExample 1:\\nInput: n = 4\\nOutput: [[\\".Q..\\",\\"...Q\\",\\"Q...\\",\\"..Q.\\"],[\\"..Q.\\",\\"Q...\\",\\"...Q\\",\\".Q..\\"]]', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The grid size `n` will range from 1 up to 9.', 0),
(72, 'word-search', 'Word Search', 'MEDIUM', 'You are given a grid of letters called `board` (with `m` rows and `n` columns) and a target `word`. Your mission is to determine if you can trace out the `word` within the grid. You can build the word by moving from one cell to an adjacent cell (up, down, left, or right). Note that you are not allowed to reuse the same exact cell more than once while forming the word.\\n\\nExample 1:\\nInput: board = [[\\"A\\",\\"B\\",\\"C\\",\\"E\\"],[\\"S\\",\\"F\\",\\"C\\",\\"S\\"],[\\"A\\",\\"D\\",\\"E\\",\\"E\\"]], word = \\"ABCCED\\"\\nOutput: true', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The grid will have `m` rows and `n` columns.
Both `m` and `n` will be between 1 and 6.
The target `word` will contain between 1 and 15 characters.
The letters in both the `board` and the `word` will only be standard English alphabet characters (both cases are possible).', 0),
(73, 'combination-sum', 'Combination Sum', 'MEDIUM', 'Suppose you have a set of unique numbers called `candidates`, and a specific `target` value you want to reach. Your goal is to find all the different ways you can pick numbers from `candidates` so that their sum perfectly matches the `target`. You are allowed to pick the exact same number as many times as you like. Remember, two combinations are considered distinct only if they contain a different count for at least one of the chosen values. Output the combinations in any order.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The `candidates` list will contain between 1 and 30 items.
Each number in `candidates` is between 2 and 40.
Every number in `candidates` is distinct.
The `target` you are trying to reach will be between 1 and 40.', 0),
(74, 'subsets', 'Subsets', 'MEDIUM', 'You are handed an array `nums` containing uniquely distinct numbers. Your job is to generate the \\"power set\\" \\u2014 which is a collection of all possible subsets that can be formed from these numbers. Make sure your final collection does not have any duplicate subsets. The subsets themselves can be listed in whatever order you prefer.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The length of the array `nums` is anywhere from 1 to 10.
The elements `nums[i]` range from -10 to 10.
No two elements in `nums` will be the same.', 0),
(75, 'letter-combinations-of-a-phone-number', 'Letter Combinations of a Phone Number', 'MEDIUM', 'Imagine a classic old-school mobile phone keypad where each digit from 2 to 9 maps to a set of alphabet letters. You are given a string of these digits. Your task is to figure out all the possible textual strings those digits could translate to, returning them in any order you choose. Keep in mind that the digit ''1'' isn''t associated with any alphabet letters.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The input string of `digits` will have a length between 0 and 4.
Every character in `digits` is guaranteed to be a number from ''2'' to ''9''.', 0),
(76, 'sudoku-solver', 'Sudoku Solver', 'HARD', 'Let''s build an automatic solver for the famous Sudoku puzzle! You will be given a partially filled 9x9 grid, where empty spaces are marked with a dot (''.''). Your program needs to fill in the gaps so that every single row, every single column, and every one of the nine 3x3 smaller squares contains the numbers 1 through 9 exactly once.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The grid will always be exactly 9 rows by 9 columns.
Each cell `board[i][j]` will either be a digit from ''1'' to ''9'', or a ''.'' representing a blank.
You can safely assume the provided puzzle has exactly one valid solution.', 0),
(77, 'clone-graph', 'Clone Graph', 'MEDIUM', 'You are given access to a single starting node within a connected, undirected network (or graph). Your task is to create a complete clone of this entire network. This means generating a \\"deep copy\\" where every new node corresponds to an original node, having the exact same integer value and mirroring the same structural connections to its cloned neighbors.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The total number of nodes in this network will be between 0 and 100.
Each node will have a unique value between 1 and 100.
The network is clean: no node links directly to itself, and there are no duplicate links between the same pair of nodes.
The graph is fully connected, meaning you can reach every node from the starting node.', 0),
(78, 'pacific-atlantic-water-flow', 'Pacific Atlantic Water Flow', 'MEDIUM', 'Imagine a rectangular island situated between two massive bodies of water: the Pacific Ocean (which lines the top and left sides) and the Atlantic Ocean (which lines the bottom and right sides). You have an `m x n` grid called `heights`, mapping out the island''s topography where each cell shows its elevation. When it rains, water flows downwards or sideways to neighboring cells (up, down, left, right) only if the neighboring land is at an equal or lower elevation. Your objective is to find the coordinates of all land parcels where rainwater can successfully flow to *both* the Pacific and the Atlantic oceans.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The grid dimensions are `m` rows and `n` columns.
Both `m` and `n` will range from 1 to 200.
The elevation `heights[r][c]` of any given cell will be between 0 and 100,000.', 0),
(79, 'rotting-oranges', 'Rotting Oranges', 'MEDIUM', 'Think of an `m x n` storage box filled with oranges. The grid cells contain a 0 (an empty spot), a 1 (a nice fresh orange), or a 2 (a nasty rotten orange). Unfortunately, rot spreads quickly! Every single minute, any fresh orange resting directly next to a rotten one (up, down, left, or right) also becomes rotten. You need to calculate the exact number of minutes it will take for the rot to consume every last fresh orange. If there are fresh oranges isolated safely away from the rot forever, return -1.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The box grid has `m` rows and `n` columns.
Both `m` and `n` fall between 1 and 10.
Grid values are strictly limited to 0, 1, or 2.', 0),
(80, 'network-delay-time', 'Network Delay Time', 'MEDIUM', 'You have a digital network consisting of `n` computers (nodes), numbered from 1 to `n`. You are provided with a series of directed connections called `times`, where each connection shows a starting computer, a destination computer, and the physical time (in milliseconds) it takes for data to travel between them. A broadcast signal is triggered from a specific computer `k`. Your task is to determine the total time required for every single computer in the network to receive this signal. If the network is fragmented and some computers never get the signal, return -1.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The network size `n` and starting node `k` are between 1 and 100.
There can be anywhere from 1 to 6000 connections in `times`.
Each connection is represented by 3 integers (source, destination, time).
The time taken for a signal to travel is between 0 and 100.
Every directed connection is unique.', 0),
(81, 'is-graph-bipartite', 'Is Graph Bipartite?', 'MEDIUM', 'Consider a social network (an undirected graph) of `n` people, numbered 0 to `n - 1`. You are given an adjacency list `graph`, showing who is friends with whom. The graph doesn''t have any weird loops (no one is friends with themselves) and connections are mutual. We want to know if it''s possible to divide everyone into two rival teams, Group A and Group B, such that every single friendship exists strictly between a person in Group A and a person in Group B (meaning no two people in the same group are friends). Return true if this division is possible, making the graph \\"bipartite\\".', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The graph has `n` nodes, where `n` is between 1 and 100.
A node can have anywhere from 0 to `n - 1` friends.
Connections are strictly mutual and contain no duplicates or self-loops.', 0),
(82, 'course-schedule-ii', 'Course Schedule II', 'MEDIUM', 'Imagine you''re a university student trying to map out your graduation plan. You need to complete `numCourses` classes, numbered 0 to `numCourses - 1`. However, some classes have strict prerequisites, given as pairs `[classA, classB]`, meaning you absolutely must finish `classB` before enrolling in `classA`. Your task is to output a valid sequence in which you can take all the required classes. If multiple schedules work, any of them is fine. If there is a paradoxical loop of prerequisites making it impossible to graduate, return an empty list.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The total number of courses is between 1 and 2000.
The prerequisites list length will be between 0 and `numCourses * (numCourses - 1)`.
Every prerequisite pair is distinct and does not reference the same class twice.', 0),
(83, 'redundant-connection', 'Redundant Connection', 'MEDIUM', 'You are investigating a computer network that was originally designed as a perfect \\"tree\\" (a fully connected network with absolutely no circular loops). However, someone accidentally plugged in one extra cable, creating a redundant loop! You are given an array of the current network `edges`. Your job is to identify and return the extra cable (edge) that needs to be unplugged to restore the network to its flawless tree structure. If there are several cables that could break the loop, return the one that appears latest in your `edges` list.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The number of edges (and nodes) `n` is between 3 and 1000.
Every edge connects two distinct nodes.
There are no duplicate cables listed.
The overall network is fully connected.', 0),
(84, 'word-search-ii', 'Word Search II', 'HARD', 'You are playing an advanced word search game! You have an `m x n` grid of letters and a vocabulary list called `words`. Your objective is to hunt down and return every single word from the list that can be found hidden in the grid. To find a word, you must connect adjacent letters (up, down, left, right). You cannot backtrack and reuse a grid square while spelling out a single word, though different words might share paths.', 'class Solution {
    // MINIFORCE: ACCEPT
}', 'The letter grid will have dimensions `m` by `n`, both ranging from 1 to 12.
The grid only contains lowercase alphabet characters.
Your dictionary `words` can have up to 30,000 distinct words.
Each word is between 1 and 10 characters long, made of lowercase English letters.', 0),
(85, 'longest-increasing-subsequence', 'Longest Increasing Subsequence', 'MEDIUM', 'Imagine you are climbing a set of magical stairs where each step is represented by a number in an array `nums`. You can skip steps, but you can only move to a step that is strictly higher (larger number) than your current one. Your task is to find the maximum number of steps you can take in a single upward journey.\\n\\nExample 1:\\nInput: nums = [10,9,2,5,3,7,101,18]\\nOutput: 4\\nExplanation: A valid path is [2, 3, 7, 101], which consists of 4 steps.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int lengthOfLIS(int[] nums) {
        
    }
}', 'The number of steps in the array is between 1 and 2500. Each step value ranges from -10,000 to 10,000.', 0),
(86, 'edit-distance', 'Edit Distance', 'HARD', 'You are tasked with fixing a broken spell! You have a starting magic word `word1` and a target magic word `word2`. To transform the starting word into the target word, you can cast three types of mini-spells:\\n- Add a single letter\\n- Remove a single letter\\n- Swap one letter for another\\nFind the absolute minimum number of mini-spells needed to change `word1` into `word2`.\\n\\nExample 1:\\nInput: word1 = \\"horse\\", word2 = \\"ros\\"\\nOutput: 3\\nExplanation:\\n1. Change ''h'' to ''r'' -> rorse\\n2. Erase the first ''r'' -> rose\\n3. Erase ''e'' -> ros', 'class Solution {
    // MINIFORCE: ACCEPT
    public int minDistance(String word1, String word2) {
        
    }
}', 'Both words contain anywhere from 0 to 500 lowercase English characters.', 0),
(87, 'partition-equal-subset-sum', 'Partition Equal Subset Sum', 'MEDIUM', 'Two friends are trying to share a collection of treasure chests, each containing a certain amount of gold coins represented by the array `nums`. They want to split the chests so that both friends get the exact same total amount of gold. Determine if it''s possible to divide the chests fairly into two piles of equal value.\\n\\nExample 1:\\nInput: nums = [1,5,11,5]\\nOutput: true\\nExplanation: One friend can take chests with 1, 5, and 5 gold coins (total 11), while the other friend takes the single chest with 11 coins.', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean canPartition(int[] nums) {
        
    }
}', 'There are between 1 and 200 treasure chests, and each chest holds between 1 and 100 gold coins.', 0),
(88, 'longest-palindromic-substring', 'Longest Palindromic Substring', 'MEDIUM', 'A palindrome is a sequence that reads the same forwards and backwards. You are exploring an ancient ruin and discover an inscription `s`. Your goal is to extract the longest contiguous segment of this inscription that forms a valid palindrome.\\n\\nExample 1:\\nInput: s = \\"babad\\"\\nOutput: \\"bab\\"\\nExplanation: The segment \\"aba\\" would also be completely correct.', 'class Solution {
    // MINIFORCE: ACCEPT
    public String longestPalindrome(String s) {
        
    }
}', 'The inscription''s length is between 1 and 1000 characters, composed exclusively of alphanumeric characters.', 0),
(89, 'house-robber', 'House Robber', 'MEDIUM', 'You play a sneaky raccoon sneaking through a neighborhood for snacks. The array `nums` tells you how many snacks are inside each house in a row. However, the neighborhood watch has a rule: if you raid two houses right next to each other, an alarm goes off! Figure out the highest number of snacks you can collect in one night without triggering any alarms.\\n\\nExample 1:\\nInput: nums = [1,2,3,1]\\nOutput: 4\\nExplanation: Raid the first house for 1 snack, skip the second, and raid the third house for 3 snacks. You get 4 snacks total.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int rob(int[] nums) {
        
    }
}', 'The street has 1 to 100 houses, each containing 0 to 400 snacks.', 0),
(90, 'minimum-path-sum', 'Minimum Path Sum', 'MEDIUM', 'You''re navigating a treacherous dungeon laid out as a 2D map `grid`. Each room in the dungeon costs a certain amount of energy to enter. Starting at the top-left corner, you need to reach the exit at the bottom-right corner. You can only advance downward or to the right. Find a path that consumes the least possible total energy.\\n\\nExample 1:\\nInput: grid = [[1,3,1],[1,5,1],[4,2,1]]\\nOutput: 7\\nExplanation: A highly efficient route goes Right, Right, Down, Down, consuming 1 + 3 + 1 + 1 + 1 = 7 energy.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int minPathSum(int[][] grid) {
        
    }
}', 'The dungeon dimensions are at most 200x200, and no room costs more than 200 energy.', 0),
(91, 'decode-ways', 'Decode Ways', 'MEDIUM', 'Secret agents use a simple cipher where ''A'' is 1, ''B'' is 2, all the way up to ''Z'' as 26. You intercept a string of digits `s` and need to figure out how many distinct original messages could have produced this sequence. Remember, some sequences of digits might be grouped differently (like ''1'' and ''2'', or ''12'').\\n\\nExample 1:\\nInput: s = \\"12\\"\\nOutput: 2\\nExplanation: The digits \\"12\\" can translate to \\"AB\\" (1 and 2) or \\"L\\" (12).', 'class Solution {
    // MINIFORCE: ACCEPT
    public int numDecodings(String s) {
        
    }
}', 'The intercepted string has between 1 and 100 digits.', 0),
(92, 'jump-game', 'Jump Game', 'MEDIUM', 'You are playing a platformer game where you start at the first tile of a level represented by an array `nums`. The value on each tile indicates the absolute farthest you can hop forward from that spot. Can you figure out if it''s possible to reach the final tile of the level?\\n\\nExample 1:\\nInput: nums = [2,3,1,1,4]\\nOutput: true\\nExplanation: You can hop 1 space to the second tile, and from there, leap 3 spaces straight to the end!', 'class Solution {
    // MINIFORCE: ACCEPT
    public boolean canJump(int[] nums) {
        
    }
}', 'The level has between 1 and 10,000 tiles. Tile values range from 0 to 100,000.', 0),
(93, 'jump-game-ii', 'Jump Game II', 'MEDIUM', 'You''re in a speed-running challenge on a track made of tiles `nums`. Starting at the first tile, the number on your current tile limits how many tiles forward you can bound. Unlike before, you know for sure that finishing the course is possible. What is the absolute fewest bounds you must make to land on the last tile?\\n\\nExample 1:\\nInput: nums = [2,3,1,1,4]\\nOutput: 2\\nExplanation: Bound 1 tile forward, then use the ''3'' to soar to the final tile in just 2 total moves.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int jump(int[] nums) {
        
    }
}', 'The track length is between 1 and 10,000. Each tile allows leaps up to 1,000.', 0),
(94, 'assign-cookies', 'Assign Cookies', 'EASY', 'You are running an animal shelter and have a limited supply of treats `s`. Each animal has a hunger level `g`. An animal will only be happy if they receive a treat that is at least as large as their hunger level. Since each animal can only get one treat, what is the maximum number of animals you can satisfy?\\n\\nExample 1:\\nInput: g = [1,2,3], s = [1,1]\\nOutput: 1\\nExplanation: You have treats of size 1, so only the animal with a hunger level of 1 can be satisfied.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int findContentChildren(int[] g, int[] s) {
        
    }
}', 'There are up to 30,000 animals and 30,000 treats. Treat sizes and hunger levels can be quite large.', 0),
(95, 'task-scheduler', 'Task Scheduler', 'MEDIUM', 'You manage a magical bakery where different recipes are represented by letters in the array `tasks`. Baking one item takes exactly one minute. However, the oven gets too hot if you bake the exact same recipe without waiting! You must wait `n` minutes before repeating a recipe. During this cooldown, you can bake other recipes or let the oven rest. What is the shortest total time required to finish all baking tasks?\\n\\nExample 1:\\nInput: tasks = [\\"A\\",\\"A\\",\\"A\\",\\"B\\",\\"B\\",\\"B\\"], n = 2\\nOutput: 8\\nExplanation: An optimal baking schedule is A -> B -> rest -> A -> B -> rest -> A -> B, taking 8 minutes.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int leastInterval(char[] tasks, int n) {
        
    }
}', 'There are between 1 and 10,000 baking tasks. The cooldown period ranges from 0 to 100 minutes.', 0),
(96, 'single-number', 'Single Number', 'EASY', 'You are organizing a huge pile of socks represented by the array `nums`. Every sock in the pile has a perfect match, except for one lone sock that lost its partner in the wash. Your goal is to identify the ID of the lone sock. Try to do this in a single pass without using extra bins for sorting!\\n\\nExample 1:\\nInput: nums = [2,2,1]\\nOutput: 1\\nExplanation: The sock with ID 1 is the only one without a pair.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int singleNumber(int[] nums) {
        
    }
}', 'The pile contains 1 to 30,000 socks, and their IDs are integers within a standard range.', 0),
(97, 'counting-bits', 'Counting Bits', 'EASY', 'Computers store numbers as sequences of 0s and 1s. For a given maximum number `n`, you need to calculate how many ''1''s are present in the binary form of every single number from 0 up to `n`. Return your results as a list.\\n\\nExample 1:\\nInput: n = 2\\nOutput: [0,1,1]\\nExplanation: 0 has zero 1s, 1 has one 1, and 2 (binary 10) has one 1.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int[] countBits(int n) {
        
    }
}', 'The upper limit n will not exceed 100,000.', 0),
(98, 'missing-number', 'Missing Number', 'EASY', 'A mischievous student erased exactly one number from a complete sequence of integers that was supposed to go from 0 to `n`. You are given the remaining sequence in an array `nums` (which might be scrambled). Find the integer that was erased.\\n\\nExample 1:\\nInput: nums = [3,0,1]\\nOutput: 2\\nExplanation: The sequence should be 0, 1, 2, 3, but the 2 is missing.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int missingNumber(int[] nums) {
        
    }
}', 'The sequence originally had up to 10,000 numbers, all unique.', 0),
(99, 'maximum-xor-of-two-numbers-in-an-array', 'Maximum XOR of Two Numbers in an Array', 'MEDIUM', 'You are participating in a hacker competition! You are given an array of network IDs `nums`. You want to find two IDs that, when combined using the bitwise XOR operation, produce the highest possible value. Return this maximum XOR value.\\n\\nExample 1:\\nInput: nums = [3,10,5,25,2,8]\\nOutput: 28\\nExplanation: Pairing the IDs 5 and 25 yields the maximum XOR result of 28.', 'class Solution {
    // MINIFORCE: ACCEPT
    public int findMaximumXOR(int[] nums) {
        
    }
}', 'You are given up to 200,000 IDs, each being a non-negative 32-bit integer.', 0);
INSERT IGNORE INTO problem_topics(problem_id,topic_id) VALUES
(16, 2),(16, 4),(16, 6),(17, 1),(17, 2),(17, 4),(17, 6),(18, 1),(18, 2),(18, 13),(18, 6),(18, 14),(18, 15),(18, 16),(18, 17),(19, 1),(19, 4),(19, 18),(20, 1),(20, 2),(20, 19),(21, 1),(21, 2),(21, 20),(22, 1),(22, 2),(22, 6),(23, 1),(23, 2),(24, 1),(24, 2),(24, 21),(24, 9),(24, 6),(25, 2),(25, 4),(26, 21),(26, 4),(27, 1),(27, 21),(27, 6),(28, 1),(28, 21),(28, 22),(29, 2),(29, 4),(29, 5),(30, 2),(30, 4),(30, 5),(31, 2),(31, 21),(31, 4),(31, 5),(32, 1),(32, 5),(33, 2),(33, 4),(33, 5),(34, 1),(34, 2),(34, 5),(34, 16),(35, 1),(35, 2),(35, 5),(36, 3),(36, 18),(37, 3),(37, 1),(37, 23),(38, 3),(38, 1),(38, 24),(39, 3),(39, 1),(39, 24),(40, 3),(40, 25),(40, 18),(41, 25),(41, 1),(41, 5),(41, 26),(42, 3),(42, 1),(43, 9),(43, 1),(44, 9),(44, 1),(44, 20),(45, 9),(45, 1),(46, 9),(46, 1),(47, 9),(47, 1),(48, 9),(48, 18),(48, 2),(49, 9),(49, 1),(49, 11),(50, 12),(50, 27),(51, 12),(51, 21),(51, 3),(52, 12),(52, 21),(53, 12),(53, 2),(54, 12),(54, 23),(55, 12),(55, 21),(55, 2),(56, 28),(56, 29),(57, 28),(57, 29),(57, 30),(58, 28),(58, 29),(58, 30),(59, 28),(59, 29),(59, 30),(60, 28),(60, 31),(60, 30),(61, 28),(61, 29),(61, 30),(62, 28),(62, 29),(62, 32),(63, 28),(63, 31),(63, 30),(64, 28),(64, 31),(64, 30),(65, 28),(65, 29),(65, 1),(65, 13),(66, 28),(66, 29),(66, 30),(66, 11),(67, 28),(67, 29),(67, 4),(67, 18),(68, 12),(68, 13),(68, 14),(68, 33),(69, 21),(69, 18),(69, 6),(69, 14),(69, 34),(70, 35),(71, 35),(72, 35),(72, 20),(73, 35),(74, 35),(75, 35),(76, 35),(77, 7),(77, 36),(77, 10),(78, 7),(78, 36),(78, 10),(79, 7),(79, 10),(80, 7),(80, 10),(80, 37),(81, 7),(81, 10),(81, 36),(82, 7),(82, 8),(82, 10),(82, 36),(83, 7),(83, 36),(83, 19),(84, 35),(84, 38),(85, 11),(86, 11),(86, 4),(87, 11),(87, 1),(88, 11),(88, 4),(89, 11),(89, 1),(90, 11),(90, 1),(90, 20),(91, 11),(91, 4),(92, 22),(92, 1),(92, 11),(93, 22),(93, 1),(93, 11),(94, 22),(94, 1),(94, 6),(95, 22),(95, 1),(95, 2),(95, 16),(96, 39),(96, 1),(97, 39),(97, 11),(98, 39),(98, 1),(98, 23),(99, 39),(99, 1),(99, 38);
INSERT IGNORE INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES
(27, 16, '"anagram"; "nagaram"', 'true', TRUE),
(28, 16, '"rat"; "car"', 'false', TRUE),
(29, 16, '"a"; "ab"', 'false', FALSE),
(30, 17, '["eat","tea","tan","ate","nat","bat"]', '[["bat"],["nat","tan"],["ate","eat","tea"]]', TRUE),
(31, 17, '[""]', '[[""]]', FALSE),
(32, 18, '[1,1,1,2,2,3]; 2', '[1,2]', TRUE),
(33, 18, '[1]; 1', '[1]', FALSE),
(34, 19, '["Hello","World"]', '["Hello","World"]', TRUE),
(35, 19, '[""]', '[""]', FALSE),
(36, 20, '[100,4,200,1,3,2]', '4', TRUE),
(37, 20, '[0,3,7,2,5,8,4,6,0,1]', '9', FALSE),
(38, 21, '[["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]', 'true', TRUE),
(39, 21, '[["8","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]', 'false', FALSE),
(40, 22, '[1,2,3,1]', 'true', TRUE),
(41, 22, '[1,2,3,4]', 'false', FALSE),
(42, 23, '[4,3,2,7,8,2,3,1]', '[5,6]', TRUE),
(43, 23, '[1,1]', '[2]', FALSE),
(44, 24, '[1,2,2,1]; [2,2]', '[2]', TRUE),
(45, 24, '[4,9,5]; [9,4,9,8,4]', '[9,4]', FALSE),
(46, 25, '"egg"; "add"', 'true', TRUE),
(47, 25, '"foo"; "bar"', 'false', FALSE),
(48, 26, '"A man, a plan, a canal: Panama"', 'true', TRUE),
(49, 26, '"race a car"', 'false', FALSE),
(50, 27, '[-1,0,1,2,-1,-4]', '[[-1,-1,2],[-1,0,1]]', TRUE),
(51, 27, '[0,1,1]', '[]', FALSE),
(52, 28, '[1,8,6,2,5,4,8,3,7]', '49', TRUE),
(53, 28, '[1,1]', '1', FALSE),
(54, 29, '"ADOBECODEBANC"; "ABC"', '"BANC"', TRUE),
(55, 29, '"a"; "aa"', '""', FALSE),
(56, 30, '"ABAB"; 2', '4', TRUE),
(57, 30, '"AABABBA"; 1', '4', FALSE),
(58, 31, '"ab"; "eidbaooo"', 'true', TRUE),
(59, 31, '"ab"; "eidboaoo"', 'false', FALSE),
(60, 32, '[1,12,-5,-6,50,3]; 4', '12.75000', TRUE),
(61, 32, '[5]; 1', '5.00000', FALSE),
(62, 33, '"cbaebabacd"; "abc"', '[0,6]', TRUE),
(63, 33, '"abab"; "ab"', '[0,1,2]', FALSE),
(64, 34, '[1,2,1,2,3]; 2', '7', TRUE),
(65, 34, '[1,2,1,3,4]; 3', '3', FALSE),
(66, 35, '[1,2,1]', '3', TRUE),
(67, 35, '[0,1,2,2]', '3', FALSE),
(68, 36, '["MinStack","push","push","push","getMin","pop","top","getMin"]; [[],[-2],[0],[-3],[],[],[],[]]', '[null,null,null,null,-3,null,0,-2]', TRUE),
(69, 37, '["2","1","+","3","*"]', '9', TRUE),
(70, 37, '["4","13","5","/","+"]', '6', FALSE),
(71, 38, '[73,74,75,71,69,72,76,73]', '[1,1,4,2,1,1,0,0]', TRUE),
(72, 38, '[30,40,50,60]', '[1,1,1,0]', FALSE),
(73, 39, '[2,1,5,6,2,3]', '10', TRUE),
(74, 39, '[2,4]', '4', FALSE),
(75, 40, '["MyQueue","push","push","peek","pop","empty"]; [[],[1],[2],[],[],[]]', '[null,null,null,1,1,false]', TRUE),
(76, 41, '[1,3,-1,-3,5,3,6,7]; 3', '[3,3,5,5,6,7]', TRUE);
INSERT IGNORE INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES
(77, 41, '[1]; 1', '[1]', FALSE),
(78, 42, '[5,10,-5]', '[5,10]', TRUE),
(79, 42, '[8,-8]', '[]', FALSE),
(80, 43, '[-1,0,3,5,9,12]; 9', '4', TRUE),
(81, 43, '[-1,0,3,5,9,12]; 2', '-1', FALSE),
(82, 44, '[[1,3,5,7],[10,11,16,20],[23,30,34,60]]; 3', 'true', TRUE),
(83, 44, '[[1,3,5,7],[10,11,16,20],[23,30,34,60]]; 13', 'false', FALSE),
(84, 45, '[3,6,7,11]; 8', '4', TRUE),
(85, 45, '[30,11,23,4,20]; 5', '30', FALSE),
(86, 46, '[3,4,5,1,2]', '1', TRUE),
(87, 46, '[4,5,6,7,0,1,2]', '0', FALSE),
(88, 47, '[4,5,6,7,0,1,2]; 0', '4', TRUE),
(89, 47, '[4,5,6,7,0,1,2]; 3', '-1', FALSE),
(90, 48, '["TimeMap","set","get","get","set","get","get"]; [[],["foo","bar",1],["foo",1],["foo",3],["foo","bar2",4],["foo",4],["foo",5]]', '[null,null,"bar","bar",null,"bar2","bar2"]', TRUE),
(91, 49, '[7,2,5,10,8]; 2', '18', TRUE),
(92, 49, '[1,2,3,4,5]; 2', '9', FALSE),
(93, 50, '[1,2,4]; [1,3,4]', '[1,1,2,3,4,4]', TRUE),
(94, 50, '[]; []', '[]', FALSE),
(95, 51, '[1,2,3,4]', '[1,4,2,3]', TRUE),
(96, 51, '[1,2,3,4,5]', '[1,5,2,4,3]', FALSE),
(97, 52, '[1,2,3,4,5]; 2', '[1,2,3,5]', TRUE),
(98, 52, '[1]; 1', '[]', FALSE),
(99, 53, '[[7,null],[13,0],[11,4],[10,2],[1,0]]', '[[7,null],[13,0],[11,4],[10,2],[1,0]]', TRUE),
(100, 53, '[[1,1],[2,1]]', '[[1,1],[2,1]]', FALSE),
(101, 54, '[2,4,3]; [5,6,4]', '[7,0,8]', TRUE),
(102, 54, '[0]; [0]', '[0]', FALSE),
(103, 55, '[3,2,0,-4]; 1', 'true', TRUE),
(104, 55, '[1,2]; 0', 'true', FALSE),
(105, 56, '[4,2,7,1,3,6,9]', '[4,7,2,9,6,3,1]', TRUE),
(106, 56, '[2,1,3]', '[2,3,1]', FALSE),
(107, 57, '[3,9,20,null,null,15,7]', '3', TRUE),
(108, 57, '[1,null,2]', '2', FALSE),
(109, 58, '[1,2,3,4,5]', '3', TRUE),
(110, 58, '[1,2]', '1', FALSE),
(111, 59, '[1,2,3]; [1,2,3]', 'true', TRUE),
(112, 59, '[1,2]; [1,null,2]', 'false', FALSE),
(113, 60, '[6,2,8,0,4,7,9,null,null,3,5]; 2; 8', '6', TRUE),
(114, 60, '[6,2,8,0,4,7,9,null,null,3,5]; 2; 4', '2', FALSE),
(115, 61, '[3,5,1,6,2,0,8,null,null,7,4]; 5; 1', '3', TRUE),
(116, 61, '[3,5,1,6,2,0,8,null,null,7,4]; 5; 4', '5', FALSE),
(117, 62, '[3,9,20,null,null,15,7]', '[[3],[9,20],[15,7]]', TRUE),
(118, 62, '[1]', '[[1]]', FALSE),
(119, 63, '[2,1,3]', 'true', TRUE),
(120, 63, '[5,1,4,null,null,3,6]', 'false', FALSE),
(121, 64, '[3,1,4,null,2]; 1', '1', TRUE),
(122, 64, '[5,3,6,2,4,null,null,1]; 3', '3', FALSE),
(123, 65, '[3,9,20,15,7]; [9,3,15,20,7]', '[3,9,20,null,null,15,7]', TRUE),
(124, 65, '[-1]; [-1]', '[-1]', FALSE),
(125, 66, '[1,2,3]', '6', TRUE),
(126, 66, '[-10,9,20,null,null,15,7]', '42', FALSE);
INSERT IGNORE INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES
(127, 67, '[1,2,3,null,null,4,5]', '[1,2,3,null,null,4,5]', TRUE),
(128, 67, '[]', '[]', FALSE),
(129, 68, '[[1,4,5],[1,3,4],[2,6]]', '[1,1,2,3,4,4,5,6]', TRUE),
(130, 68, '[]', '[]', FALSE),
(131, 69, '["MedianFinder", "addNum", "addNum", "findMedian", "addNum", "findMedian"]; [[], [1], [2], [], [3], []]', '[null, null, null, 1.5, null, 2.0]', TRUE),
(132, 69, '["MedianFinder", "addNum", "findMedian"]; [[], [5], []]', '[null, null, 5.0]', FALSE),
(133, 70, '[1,2,3]', '[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]', TRUE),
(134, 70, '[0,1]', '[[0,1],[1,0]]', FALSE),
(135, 71, '4', '[[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]', TRUE),
(136, 71, '1', '[["Q"]]', FALSE),
(137, 72, '[["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]]; "ABCCED"', 'true', TRUE),
(138, 72, '[["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]]; "ABCB"', 'false', FALSE),
(139, 73, '[2,3,6,7]; 7', '[[2,2,3],[7]]', TRUE),
(140, 73, '[2,3,5]; 8', '[[2,2,2,2],[2,3,3],[3,5]]', FALSE),
(141, 74, '[1,2,3]', '[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]', TRUE),
(142, 74, '[0]', '[[],[0]]', FALSE),
(143, 75, '"23"', '["ad","ae","af","bd","be","bf","cd","ce","cf"]', TRUE),
(144, 75, '""', '[]', FALSE),
(145, 76, '[["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]', '[["5","3","4","6","7","8","9","1","2"],["6","7","2","1","9","5","3","4","8"],["1","9","8","3","4","2","5","6","7"],["8","5","9","7","6","1","4","2","3"],["4","2","6","8","5","3","7","9","1"],["7","1","3","9","2","4","8","5","6"],["9","6","1","5","3","7","2","8","4"],["2","8","7","4","1","9","6","3","5"],["3","4","5","2","8","6","1","7","9"]]', TRUE),
(146, 76, '[["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]', '[["5","3","4","6","7","8","9","1","2"],["6","7","2","1","9","5","3","4","8"],["1","9","8","3","4","2","5","6","7"],["8","5","9","7","6","1","4","2","3"],["4","2","6","8","5","3","7","9","1"],["7","1","3","9","2","4","8","5","6"],["9","6","1","5","3","7","2","8","4"],["2","8","7","4","1","9","6","3","5"],["3","4","5","2","8","6","1","7","9"]]', FALSE),
(147, 77, '[[2,4],[1,3],[2,4],[1,3]]', '[[2,4],[1,3],[2,4],[1,3]]', TRUE),
(148, 77, '[[]]', '[[]]', FALSE),
(149, 78, '[[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]', '[[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]', TRUE),
(150, 78, '[[1]]', '[[0,0]]', FALSE),
(151, 79, '[[2,1,1],[1,1,0],[0,1,1]]', '4', TRUE),
(152, 79, '[[2,1,1],[0,1,1],[1,0,1]]', '-1', FALSE),
(153, 80, '[[2,1,1],[2,3,1],[3,4,1]]; 4; 2', '2', TRUE),
(154, 80, '[[1,2,1]]; 2; 1', '1', FALSE),
(155, 81, '[[1,2,3],[0,2],[0,1,3],[0,2]]', 'false', TRUE),
(156, 81, '[[1,3],[0,2],[1,3],[0,2]]', 'true', FALSE),
(157, 82, '2; [[1,0]]', '[0,1]', TRUE),
(158, 82, '4; [[1,0],[2,0],[3,1],[3,2]]', '[0,1,2,3]', FALSE),
(159, 83, '[[1,2],[1,3],[2,3]]', '[2,3]', TRUE),
(160, 83, '[[1,2],[2,3],[3,4],[1,4],[1,5]]', '[1,4]', FALSE),
(161, 84, '[["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]]; ["oath","pea","eat","rain"]', '["eat","oath"]', TRUE),
(162, 84, '[["a","b"],["c","d"]]; ["abcb"]', '[]', FALSE),
(163, 85, '[10,9,2,5,3,7,101,18]', '4', TRUE),
(164, 85, '[0,1,0,3,2,3]', '4', TRUE),
(165, 85, '[7,7,7,7,7,7,7]', '1', FALSE),
(166, 86, '"horse"; "ros"', '3', TRUE),
(167, 86, '"intention"; "execution"', '5', TRUE),
(168, 86, '""; "a"', '1', FALSE),
(169, 87, '[1,5,11,5]', 'true', TRUE),
(170, 87, '[1,2,3,5]', 'false', TRUE),
(171, 87, '[1,2,3,4,5,6,7]', 'true', FALSE),
(172, 88, '"babad"', '"bab"', TRUE),
(173, 88, '"cbbd"', '"bb"', TRUE),
(174, 88, '"a"', '"a"', FALSE),
(175, 89, '[1,2,3,1]', '4', TRUE),
(176, 89, '[2,7,9,3,1]', '12', TRUE);
INSERT IGNORE INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES
(177, 89, '[0]', '0', FALSE),
(178, 90, '[[1,3,1],[1,5,1],[4,2,1]]', '7', TRUE),
(179, 90, '[[1,2,3],[4,5,6]]', '12', TRUE),
(180, 90, '[[1,2],[1,1]]', '3', FALSE),
(181, 91, '"12"', '2', TRUE),
(182, 91, '"226"', '3', TRUE),
(183, 91, '"06"', '0', FALSE),
(184, 92, '[2,3,1,1,4]', 'true', TRUE),
(185, 92, '[3,2,1,0,4]', 'false', TRUE),
(186, 92, '[0]', 'true', FALSE),
(187, 93, '[2,3,1,1,4]', '2', TRUE),
(188, 93, '[2,3,0,1,4]', '2', TRUE),
(189, 93, '[0]', '0', FALSE),
(190, 94, '[1,2,3]; [1,1]', '1', TRUE),
(191, 94, '[1,2]; [1,2,3]', '2', TRUE),
(192, 94, '[1,2,3]; []', '0', FALSE),
(193, 95, '["A","A","A","B","B","B"]; 2', '8', TRUE),
(194, 95, '["A","A","A","B","B","B"]; 0', '6', TRUE),
(195, 95, '["A","A","A","A","A","A","B","C","D","E","F","G"]; 2', '16', FALSE),
(196, 96, '[2,2,1]', '1', TRUE),
(197, 96, '[4,1,2,1,2]', '4', TRUE),
(198, 96, '[1]', '1', FALSE),
(199, 97, '2', '[0,1,1]', TRUE),
(200, 97, '5', '[0,1,1,2,1,2]', TRUE),
(201, 97, '0', '[0]', FALSE),
(202, 98, '[3,0,1]', '2', TRUE),
(203, 98, '[0,1]', '2', TRUE),
(204, 98, '[9,6,4,2,3,5,7,0,1]', '8', FALSE),
(205, 99, '[3,10,5,25,2,8]', '28', TRUE),
(206, 99, '[14,70,53,83,49,91,36,80,92,51,66,70]', '127', TRUE),
(207, 99, '[0]', '0', FALSE);
INSERT IGNORE INTO problems(id,slug,title,difficulty,description,starter_code,constraints_text,acceptance_rate) VALUES
(100, 'inversion-network-analyst', 'Inversion Network Analyst', 'HARD', 'You are analyzing a sequence of processing tasks in a distributed computing network. Each task is represented by its required execution time in an integer array executionTimes. To identify network bottlenecks, the lead architect wants to know how many ''inversions'' each task causes. An inversion for a task at index i is defined as any task at index j (where j > i) that has a strictly smaller execution time (executionTimes[j] < executionTimes[i]). Return an integer array inversionCounts where inversionCounts[i] is the number of inversions caused by the task at index i.\n\nExample 1:\nInput: executionTimes = [5,2,6,1]\nOutput: [2,1,1,0]\nExplanation:\n- For task 5, the smaller elements to the right are 2 and 1 (Count = 2).\n- For task 2, the smaller element to the right is 1 (Count = 1).\n- For task 6, the smaller element to the right is 1 (Count = 1).\n- For task 1, there are no elements to the right (Count = 0).\n\nExample 2:\nInput: executionTimes = [-1]\nOutput: [0]\n\nExample 3:\nInput: executionTimes = [1,2,3,4]\nOutput: [0,0,0,0]', 'import java.util.List;
import java.util.ArrayList;

class Solution {
    // MINIFORCE: ACCEPT
    public List<Integer> countInversions(int[] executionTimes) {
        
    }
}', '1 <= executionTimes.length <= 10^5\n-10^4 <= executionTimes[i] <= 10^4', 0);
INSERT IGNORE INTO problem_topics(problem_id,topic_id) VALUES (100, 1), (100, 13), (100, 33);
INSERT IGNORE INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES
(208, 100, '[5,2,6,1]', '[2,1,1,0]', TRUE),
(209, 100, '[-1]', '[0]', TRUE),
(210, 100, '[1,2,3,4]', '[0,0,0,0]', TRUE),
(211, 100, '[5,5,5,5]', '[0,0,0,0]', FALSE);
