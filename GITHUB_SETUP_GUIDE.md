# GitHub Setup Guide for MiniForce

Welcome! This guide will help you upload your MiniForce project to GitHub. Since MiniForce is a full-stack platform with a backend, frontend, and database, it's important to learn how to store your code safely without accidentally exposing passwords.

## 1. What is Git?
Git is a "version control system". It acts like a time machine for your code, tracking every change you make so you can always go back to an earlier version if something breaks. It works entirely on your local computer.

## 2. What is GitHub?
GitHub is a website that hosts Git repositories online. It allows you to back up your code, showcase your projects to potential employers or classmates, and collaborate with others.

## 3. What is a Repository?
A repository (or "repo") is essentially a folder where Git tracks your project files and their history. 

## 4. How to Create a GitHub Repository Manually
1. Go to [github.com](https://github.com) and log in.
2. Click the **"+"** icon in the top right corner and select **"New repository"**.
3. Name your repository `MiniForce`.
4. Add a description: *"Full-stack competitive programming platform with real Java and Python Docker sandbox judging."*
5. Choose **Public** (to show it off on your resume) or **Private** (if it's for a graded class assignment where sharing code is prohibited).
6. **Important:** Do NOT check "Initialize this repository with a README" or add a `.gitignore`. We already have those locally!
7. Click **"Create repository"**.

## 5. How to Connect the Local MiniForce Repository
Once you install Git (if you haven't already from [git-scm.com](https://git-scm.com)), open your terminal in the `miniforce4` folder and run these commands:

```bash
# 1. Initialize Git in your folder (creates a hidden .git folder)
git init

# 2. Tell your local Git where the GitHub repository is
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/MiniForce.git
```

## 6. How to Check Git Status
To see which files have changed or are waiting to be saved:
```bash
git status
```
*Always check this before saving to ensure you aren't uploading secrets or large generated files!*

## 7. How to Stage Files
"Staging" tells Git which files you want to include in your next save.
```bash
git add .
```
*(The `.` means "stage everything in this folder that isn't ignored")*

## 8. How to Create a Commit
A "commit" is the actual save point.
```bash
git commit -m "Initial MiniForce commit with React frontend and Docker judge"
```

## 9. How to Push to GitHub
"Pushing" uploads your saved commits from your computer to GitHub.
```bash
git branch -M main
git push -u origin main
```
*(You may be asked to log in to GitHub in a popup window.)*

## 10. How to Verify Files on GitHub
Simply refresh the repository page you created on GitHub. You should see your folders (`backend`, `frontend`, `database`, etc.) listed there!

## 11. How to Clone MiniForce on Another Computer
If you get a new laptop and want your code back:
```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/MiniForce.git
```

## 12. How to Pull Future Updates
If you made changes directly on GitHub or another computer, download them with:
```bash
git pull origin main
```

## 13. What NOT to Upload
Never upload:
- `.env` files (these contain your real passwords and secrets!)
- `node_modules/` (huge folders of frontend dependencies)
- `target/` or `dist/` (compiled code that can be generated again)
- Database dumps containing real user data.

## 14. How .gitignore Protects Secrets
You'll notice files named `.gitignore` in this project. Git reads these files and automatically ignores anything listed inside them. This is how we prevent your `MINIFORCE_DB_PASSWORD` or `MINIFORCE_JWT_SECRET` from ending up on the public internet!

## 15. Basic Git Commands Cheat Sheet
- `git status` -> Check what's going on
- `git add .` -> Stage all changes
- `git commit -m "Message"` -> Save changes locally
- `git push` -> Upload to GitHub
- `git pull` -> Download from GitHub
- `git log` -> See your history
