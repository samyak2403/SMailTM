# Contributing to SMailTM

First off, thank you for considering contributing to SMailTM! 🎉

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Commit Messages](#commit-messages)

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce**
- **Expected behavior**
- **Actual behavior**
- **Screenshots** (if applicable)
- **Environment details** (Android version, device, etc.)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Clear title and description**
- **Use case** - Why is this enhancement useful?
- **Possible implementation** (optional)

### Pull Requests

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Development Setup

### Prerequisites

- JDK 17 or higher
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24+
- Git

### Setup Steps

```bash
# Clone your fork
git clone https://github.com/samyak2403/SMailTM.git
cd SMailTM

# Add upstream remote
git remote add upstream https://github.com/originalowner/SMailTM.git

# Create a branch
git checkout -b feature/my-feature

# Make your changes
# ...

# Build and test
./gradlew build
./gradlew test
```

## Pull Request Process

1. **Update documentation** - Update README.md if needed
2. **Add tests** - Add tests for new features
3. **Follow coding standards** - See below
4. **Update CHANGELOG** - Add entry to CHANGELOG.md
5. **One feature per PR** - Keep PRs focused
6. **Describe changes** - Provide clear PR description

### PR Checklist

- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests added/updated
- [ ] All tests pass
- [ ] CHANGELOG.md updated

## Coding Standards

### Kotlin Style Guide

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

### Key Points

- **Naming:**
  - Classes: `PascalCase`
  - Functions: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Variables: `camelCase`

- **Formatting:**
  - Indentation: 4 spaces
  - Line length: 120 characters max
  - No trailing whitespace

- **Documentation:**
  - Add KDoc for public APIs
  - Comment complex logic
  - Keep comments up-to-date

### Example

```kotlin
/**
 * Creates a temporary email account with the specified password.
 *
 * @param password The password for the account
 * @return SMailTM instance
 * @throws LoginException if account creation fails
 */
fun createAccount(password: String): SMailTM {
    require(password.isNotEmpty()) { "Password cannot be empty" }
    
    return SMaliBuilder.createDefault(password).apply {
        init()
    }
}
```

## Commit Messages

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat:** New feature
- **fix:** Bug fix
- **docs:** Documentation changes
- **style:** Code style changes (formatting)
- **refactor:** Code refactoring
- **test:** Adding/updating tests
- **chore:** Maintenance tasks

### Examples

```
feat(auth): add token-based authentication

Implement JWT token authentication for persistent sessions.
Users can now login with saved tokens.

Closes #123
```

```
fix(messages): resolve null pointer in message fetch

Fixed crash when fetching messages with null sender.
Added null safety checks.

Fixes #456
```

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew :SMailTM:test --tests "MessageTest"

# Run with coverage
./gradlew jacocoTestReport
```

### Writing Tests

- Write unit tests for new features
- Maintain test coverage above 80%
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)

```kotlin
@Test
fun `createAccount should return valid SMailTM instance`() {
    // Arrange
    val password = "testPassword123"
    
    // Act
    val result = SMaliBuilder.createDefault(password)
    
    // Assert
    assertNotNull(result)
    assertTrue(result.getSelf().email.isNotEmpty())
}
```

## Questions?

Feel free to:
- Open an issue for questions
- Start a discussion on GitHub Discussions
- Contact maintainers

## Recognition

Contributors will be recognized in:
- README.md Contributors section
- Release notes
- GitHub contributors page

Thank you for contributing! 🙏

