# MiniForce Admin Panel & Platform Operations Guide

## Overview

MiniForce includes a dedicated, secure **Admin Management Console** accessible at `/admin` for users with the `ADMIN` role. The admin console enables real-time monitoring of platform statistics, user privilege management, discussion board moderation, and programmatic problem management.

---

## 1. Role-Based Access Control (RBAC) Architecture

MiniForce enforces strict defense-in-depth Role-Based Access Control (RBAC) across both frontend routing and backend API endpoints:

### Frontend Route Protection
- Implemented in `frontend/react/src/routes/guards.tsx` via `<RequireAdmin>`.
- Any unauthenticated visitor or user with role `USER` attempting to navigate to `/admin` is immediately redirected to the home page `/` with the route rejected before any UI renders.
- The Admin link in the primary navigation header (`AppShell.tsx`) is rendered conditionally only when `user?.role === 'ADMIN'`.

### Backend API Security (`SecurityConfig.java`)
All administrative REST endpoints are protected at the Spring Security filter chain level:
- `/api/admin/**`: Enforces `.hasRole("ADMIN")` (requires HTTP 403 Forbidden rejection for non-admin tokens).
- Problem mutation endpoints:
  - `POST /api/problems/**`: `.hasRole("ADMIN")`
  - `PUT /api/problems/**`: `.hasRole("ADMIN")`
  - `DELETE /api/problems/**`: `.hasRole("ADMIN")`
- Contest mutation endpoints:
  - `POST /api/contests`: `.hasRole("ADMIN")`
  - `PUT /api/contests/**`: `.hasRole("ADMIN")`
  - `DELETE /api/contests/**`: `.hasRole("ADMIN")`

Authentication is validated via cryptographically signed JWT tokens passed in the `Authorization: Bearer <token>` header, verified by `JwtAuthenticationFilter`.

---

## 2. Admin API Specification

| Endpoint | HTTP Method | Security | Description | Request Body | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/admin/dashboard` | `GET` | `ROLE_ADMIN` | Platform metrics summary | None | `{ users: int, submissions: int, problems: int, discussions: int }` |
| `/api/admin/users` | `GET` | `ROLE_ADMIN` | Search & list platform users | Query param: `?search=` (optional) | `List<AdminUserResponse>` |
| `/api/admin/users/{id}/role` | `PUT` | `ROLE_ADMIN` | Change a user's role | `{ role: "USER" \| "ADMIN" }` | `AdminUserResponse` |
| `/api/admin/discussions/{id}`| `DELETE` | `ROLE_ADMIN` | Moderate/remove discussion thread | None | `204 No Content` |
| `/api/problems` | `POST` | `ROLE_ADMIN` | Create new problem | Full problem JSON specification | `ProblemDetail` |
| `/api/problems/{id}` | `PUT` | `ROLE_ADMIN` | Update problem definition | Partial problem JSON | `ProblemDetail` |
| `/api/problems/{id}` | `DELETE` | `ROLE_ADMIN` | Remove problem from catalog | None | `204 No Content` |

---

## 3. Administrative Workflows

### 3.1 Monitoring Platform Overview
The top section of the Admin Console provides live counters for:
- **Total Users**: Registered accounts in the MySQL database.
- **Submissions**: Total judge evaluations (Java & Python) submitted across the platform.
- **Problems**: Current problem count in the catalog.
- **Discussions**: Community threads created.

### 3.2 User Management & Role Promotion
Administrators can search users by username or email:
1. Enter search query in the **User Management** filter.
2. Search results update with debounce.
3. To promote or demote a user, select the role dropdown (`USER` or `ADMIN`).
4. The change is saved immediately to the database via `PUT /api/admin/users/{id}/role`.

### 3.3 Community Discussion Moderation
1. The **Moderation** card displays all discussion posts with author attribution.
2. Clicking **Delete** prompts for confirmation and dispatches `DELETE /api/admin/discussions/{id}`.
3. All replies and discussion records are purged in accordance with community guidelines.

### 3.4 Problem Authoring
Administrators can seed additional custom problems:
1. Provide a unique URL slug (e.g., `word-break`).
2. Provide Problem Title, Difficulty (`EASY`, `MEDIUM`, `HARD`), and comma-separated Topics.
3. Enter the detailed Problem Statement with input/output format and constraints.
4. Click **Create Problem** to insert into the platform catalog.

---

## 4. Bootstrapping & Rotating Administrator Credentials

### 4.1 Production Credential Security Policy
MiniForce enforces strict credential rotation in production (`application-prod.properties`):
- All default development seed accounts (`admin`, `demo`, etc.) have their initial development passwords disabled automatically when running under the production profile.
- Attempts to log in with unrotated default seed credentials will be rejected with `401 Unauthorized`.
- Existing database records are never deleted or arbitrarily rewritten on startup.

### 4.2 Production Admin Credential Rotation Options

#### Option A: Automated Bootstrap via Environment Variable (Recommended)
1. Define the environment variable `MINIFORCE_ADMIN_PASSWORD` in your production environment settings with a strong, secure passphrase.
2. Start or restart the MiniForce backend service.
3. Log in through `/login` using username `admin` and your configured `MINIFORCE_ADMIN_PASSWORD`.
4. Upon successful authentication, MiniForce securely re-hashes the password using BCrypt with a unique salt, updates the `users` table record, and retires the development seed hash.

#### Option B: Manual SQL Hash Update
1. Generate a BCrypt hash (cost factor 10 or higher) for your chosen administrative password using an offline tool or CLI utility.
2. In the production MySQL database CLI, execute:
   ```sql
   UPDATE users SET password_hash = '<YOUR_NEW_BCRYPT_HASH>' WHERE username = 'admin';
   ```

#### Option C: Bootstrap from a New User Account
1. Register a standard account via the signup page (`/register`).
2. In the MySQL database CLI, promote the account to administrative privileges:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
   ```
3. Log out and log back in. The **Admin** management portal will be active in the navigation bar.
