---
description: How to use Spotless code formatter in this project
---

# Spotless Code Formatter Workflow

This project uses Spotless with ktlint to enforce consistent code formatting.

## Available Commands

// turbo
1. **Check formatting** (dry-run, fails on violations):
   ```bash
   ./gradlew spotlessCheck
   ```

// turbo
2. **Apply formatting** (automatically fix violations):
   ```bash
   ./gradlew spotlessApply
   ```

3. **Install Git pre-push hook** (optional, runs spotlessCheck before push):
   ```bash
   ./gradlew spotlessInstallGitPrePushHook
   ```

## Configuration

- **Kotlin files**: Uses ktlint with IntelliJ IDEA style
- **Gradle files**: Uses ktlint for `.gradle.kts` files
- **Misc files**: Trims trailing whitespace and ensures newline at EOF

## EditorConfig

The project includes an `.editorconfig` file that ktlint respects. Key settings:
- `indent_size = 4` (matches Best_Practice.md)
- `max_line_length = 120`
- `ktlint_code_style = intellij_idea`

## Troubleshooting

If you see formatting errors:
1. Run `./gradlew spotlessApply` to auto-fix
2. Review any remaining lint errors in the output
3. Fix manually or suppress using spotless configuration

## Integration with CI

The project includes a GitHub Actions workflow at `.github/workflows/spotless.yml` that runs `spotlessCheck` automatically on every push and PR.
