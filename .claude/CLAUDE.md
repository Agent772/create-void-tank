# CLAUDE.md

This is the default CLAUDE.md used when a repository does not ship its own
`.claude/CLAUDE.md`. It contains only project-agnostic conventions that apply
broadly. When a repository has its own CLAUDE.md, it fully replaces this one
and these defaults do not apply.

## Commits

- Commit message format: `type(scope): description`
  - Types: `feat`, `fix`, `chore`, `refactor`, `docs`, `style`, `test`, `perf`
  - Scope is **optional**. Use it when the repository has clear scopes (e.g.
    `server`, `client`, `api`) and the change is contained to one. Omit the
    scope if the change spans multiple scopes, or if the repository has no
    established scope convention.
  - Description: lowercase, imperative mood, no period at the end
  - Examples:
    - `fix: handle null user in session start` (no scope)
    - `feat(server): add health check endpoint` (single-scope change)
    - `chore: bump dependencies` (no scope)
- Never add `Co-authored-by`, `Signed-off-by`, or any trailer that credits
  Claude as a contributor. The deployment enforces this at the settings level
  but your commits should also never include such trailers in the body.
- Never modify the git config (`user.name`, `user.email`, etc.)

## Branching

- Branch naming: `<type>/<short-slug>` where `<type>` matches the commit type
  (`feat/add-health-check`, `fix/null-session-start`, `chore/bump-deps`)
- Never push directly to a protected default branch (`main`, `master`, `dev`).
  Always create a feature branch and open a pull request.
- Never force-push to a shared branch.
- Never push to remote unless your task explicitly requires it. Session
  prompts that instruct you to push and open a PR are the exception — follow
  those instructions.

## Before committing

Detect the repository's own verification commands and run them if they exist:

- **Node / TypeScript**: if `package.json` is present, check its `scripts`
  section for `typecheck`, `lint`, `test`, and `format:check` and run whichever
  exist. Use the repository's package manager — detect by lockfile
  (`pnpm-lock.yaml` → pnpm, `yarn.lock` → yarn, `package-lock.json` → npm).
- **Gradle**: if `build.gradle` or `build.gradle.kts` is present, run
  `./gradlew check` (or a narrower target like `./gradlew compileJava test`
  if `check` is too slow for the task).
- **Rust**: if `Cargo.toml` is present, run `cargo check` and `cargo clippy`.
- **Go**: if `go.mod` is present, run `go build ./...` and `go vet ./...`.

If no verification command can be discovered, do NOT fabricate one. Committing
without running a non-existent command is better than failing a command that
does not exist.

## Exploration

- Prefer reading existing code over assuming — the repository's language,
  framework, package manager, and conventions should be derivable from what's
  already there.
- Do not assume any specific framework, language, or tooling without first
  confirming it exists in the repository.
- Do not invent file paths, scripts, or commands. If something is missing that
  you need, say so — do not fabricate it.
