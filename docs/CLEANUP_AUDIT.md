# MiniForce Project Cleanup & File Classification Audit

## 1. Executive Summary

This audit establishes a rigorous, dependency-aware inventory of all files and directories across the `miniforce4` project root, categorizing them into:
- **Category A**: Essential platform source, configuration, assets, test suites, and operational launchers.
- **Category B**: Confirmed temporary scratch files, execution logs, and one-off generator scripts safe for removal.
- **Category C**: Historical milestones, legacy reference prototypes, and archival verification artifacts.

---

## 2. File Classification Matrix

### Category A — Essential Platform Files (Retained & Maintained)
| Path | Component | Rationale |
| :--- | :--- | :--- |
| `backend/src/main/java/**` | Backend | Core Spring Boot application, REST controllers, services, repositories, models, and DTOs. |
| `backend/src/main/resources/**` | Backend | Application configuration (`application.properties`, `application-test.properties`). |
| `backend/src/test/**` | Backend | Test suites verifying judge reliability, Python/Java execution, security, and avatar persistence. |
| `backend/pom.xml` | Backend | Maven project configuration and dependency tree. |
| `backend/Dockerfile` | Judge Sandbox | Container specification for isolated Java 17 and Python 3 judge runner. |
| `frontend/react/**` | Frontend | React 19, TypeScript, Vite, Tailwind CSS application source, components, routes, and assets. |
| `database/` | Database | Baseline schemas and Versioned SQL migrations (`V1` through `V7_2_4`). |
| `START-MINIFORCE.bat` | Automation | Production-grade Windows startup orchestrator with self-healing, port detection, and preflight checks. |
| `STOP-MINIFORCE.bat` | Automation | Clean graceful process teardown utility. |
| `docker-compose.yml` | Infrastructure | Local multi-container Docker compose definitions. |
| `Dockerfile` | Infrastructure | Root container build file. |
| `.gitignore`, `.env` | Environment | Git ignore configuration and local environment variable declarations. |
| `README.md` | Docs | Primary project documentation and repository overview. |
| `docs/` | Docs | Architecture documents, threat models, sandbox specs, and Phase 10 operational guides. |

### Category B — Obsolete Temporary & Scratch Files (Quarantined / Cleaned)
| File / Directory | Location | Creation Context | Action |
| :--- | :--- | :--- | :--- |
| `inserts.txt` | Root | 0-byte scratch buffer from Phase 4 SQL inspection | **Removed** |
| `lines.txt` | Root | 0-byte scratch buffer from Phase 4 SQL inspection | **Removed** |
| `ScratchExtract.java` | Root | One-off helper script from Phase 4 problem extraction | **Removed** |
| `scratch_extract.py` | Root | One-off helper script from Phase 4 problem extraction | **Removed** |
| `backend/P4_Audit.txt` | Backend | Temporary audit log from Phase 4 test runs | **Removed** |
| `backend/out.log` | Backend | Build log from Maven test execution | **Removed** |
| `backend/out2.log` | Backend | Build log from Maven test execution | **Removed** |
| `backend/out3.log` | Backend | Build log from Maven test execution | **Removed** |
| `backend/real_judge.log`| Backend | Temporary test judge stdout capture | **Removed** |
| `scratch/` | Root | Collection of one-off scrapers, test generators, and temporary text files | **Removed** |

### Category C — Historical Reference & Archival Artifacts (Preserved in Place)
| File | Rationale |
| :--- | :--- |
| `about.html`, `admin.html`, `contest.html`, `contests.html`, `discuss.html`, `index.html`, `leaderboard.html`, `login.html`, `problems.html`, `profile.html`, `signup.html`, `solve.html`, `miniforce-app.js`, `miniforce.css` | Original vanilla prototype UI; preserved for historical comparison without affecting Vite build. |
| `test_dump.sql` | Historical snapshot database dump; preserved for reference. |
| `D13_Avatar_Report.md`, `FINAL_REPORT_*.md`, `P4_Real_Judge_Problems_21_to_30_Report.md`, `PRODUCTION.md` | Phase completion milestones and verification reports. |

---

## 3. Cleanup Execution Log

The following Category B temporary artifacts were audited, confirmed to have zero runtime dependencies or code imports, and safely purged:
1. `inserts.txt` (Purged)
2. `lines.txt` (Purged)
3. `ScratchExtract.java` (Purged)
4. `scratch_extract.py` (Purged)
5. `backend/P4_Audit.txt` (Purged)
6. `backend/out.log` (Purged)
7. `backend/out2.log` (Purged)
8. `backend/out3.log` (Purged)
9. `backend/real_judge.log` (Purged)
10. `scratch/` directory and contents (Purged)

Following this cleanup, both `mvn test` and `npm run build` were re-executed to verify absolute platform integrity.
