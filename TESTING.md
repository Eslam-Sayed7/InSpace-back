# Testing Documentation

This document describes the testing strategy and how to run tests for the InSpace-back project.

## Overview

The project contains comprehensive unit and integration tests for both backend components:
- **Java Backend (Api)**: Spring Boot application with services and controllers
- **Python Backend (Mock_API)**: FastAPI application for UI automation

## Test Structure

### Java Backend Tests

Located in `Api/src/test/java/com/InSpace/Api/`

```
test/
├── adapters/controller/     # Controller unit tests
│   ├── TestSuiteControllerTest.java
│   └── TestScenarioControllerTest.java
├── services/                # Service unit tests
│   ├── TestSuiteServiceTest.java
│   └── TestScenarioServiceTest.java
├── integration/             # Integration tests
│   ├── TestSuiteIntegrationTest.java
│   └── TestScenarioIntegrationTest.java
└── Auth/                    # Authentication tests
    ├── AuthControllerLoginTest.java
    ├── AuthControllerRegisterTest.java
    └── UserServiceTests.java
```

### Python Backend Tests

Located in `Mock_API/tests/`

```
tests/
├── unit/                    # Unit tests with mocked dependencies
│   ├── test_controller.py   # Controller function tests
│   └── test_routes.py       # FastAPI route tests
└── integration/             # Integration tests
    └── test_api_integration.py
```

## Running Tests

### Java Tests

#### Run all unit tests (excluding integration tests):
```bash
cd Api
./mvnw test -Dtest="*ServiceTest,*ControllerTest,AuthControllerLoginTest,AuthControllerRegisterTest,UserControllerTest,UserServiceTests"
```

#### Run all tests:
```bash
cd Api
./mvnw test
```

#### Run specific test class:
```bash
cd Api
./mvnw test -Dtest=TestSuiteServiceTest
```

#### Run with verbose output:
```bash
cd Api
./mvnw test -X
```

### Python Tests

#### Run all tests:
```bash
cd Mock_API
python -m pytest tests/ -v
```

#### Run only unit tests:
```bash
cd Mock_API
python -m pytest tests/unit/ -v
```

#### Run only integration tests:
```bash
cd Mock_API
python -m pytest tests/integration/ -v
```

#### Run with coverage report:
```bash
cd Mock_API
python -m pytest tests/ --cov=app --cov-report=html
```

#### Run specific test file:
```bash
cd Mock_API
python -m pytest tests/unit/test_controller.py -v
```

#### Run specific test:
```bash
cd Mock_API
python -m pytest tests/unit/test_controller.py::TestClickOperations::test_perform_left_click -v
```

## Test Statistics

### Java Backend
- **Total Unit Tests**: 58 tests
  - TestSuiteServiceTest: 13 tests
  - TestScenarioServiceTest: 15 tests
  - TestSuiteControllerTest: 14 tests
  - TestScenarioControllerTest: 6 tests
  - Auth tests: 10 tests

### Python Backend
- **Total Tests**: 45 tests
  - Unit tests: 30 tests
  - Integration tests: 15 tests

## Testing Best Practices Applied

### 1. Test Organization
- ✅ Separate unit tests from integration tests
- ✅ One test class per production class
- ✅ Clear test directory structure

### 2. Test Naming
- ✅ Descriptive test names using `@DisplayName` (Java)
- ✅ Docstrings explaining test purpose (Python)
- ✅ Test method names follow pattern: `test_<methodName>_<scenario>_<expectedResult>`

### 3. Test Structure (AAA Pattern)
All tests follow the Arrange-Act-Assert pattern:
```java
@Test
void testCreateTestSuite_Success() {
    // Arrange - Set up test data and mocks
    String name = "Test Suite";
    when(repository.save(any())).thenReturn(testSuite);
    
    // Act - Execute the method under test
    TestSuite result = service.createTestSuite(name);
    
    // Assert - Verify the results
    assertNotNull(result);
    verify(repository, times(1)).save(any());
}
```

### 4. Mocking Strategy
- ✅ **Unit Tests**: Mock all dependencies
  - Java: Mockito (`@Mock`, `@InjectMocks`)
  - Python: pytest-mock and unittest.mock
- ✅ **Integration Tests**: Use real components with in-memory database (H2 for Java)

### 5. Test Coverage
- ✅ Success scenarios
- ✅ Failure scenarios
- ✅ Edge cases (empty strings, null values, boundary conditions)
- ✅ Validation testing
- ✅ Error handling

### 6. Dependencies
- ✅ Test dependencies are isolated from production dependencies
- ✅ H2 in-memory database for Java integration tests
- ✅ TestClient for Python FastAPI testing

## Test Configuration

### Java Test Configuration

**In-memory Database**: Tests use H2 database instead of PostgreSQL
```java
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
```

**Dependencies** (in `pom.xml`):
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Python Test Configuration

**Mock Configuration**: PyAutoGUI is mocked at import time (in `tests/conftest.py`):
```python
import sys
from unittest.mock import MagicMock

sys.modules['pyautogui'] = MagicMock()
```

**Dependencies** (in `requirements.txt`):
```
pytest
pytest-mock
httpx
```

## Continuous Integration

Tests should be run in CI/CD pipeline:

```yaml
# Example GitHub Actions workflow
- name: Run Java tests
  run: cd Api && ./mvnw test

- name: Run Python tests
  run: cd Mock_API && python -m pytest tests/ -v
```

## Adding New Tests

### Java
1. Create test class in appropriate package
2. Use `@DisplayName` for readable test names
3. Mock dependencies with `@Mock` and `@InjectMocks`
4. Follow AAA pattern
5. Test both success and failure scenarios

### Python
1. Create test file with `test_` prefix
2. Organize tests in classes
3. Use descriptive docstrings
4. Mock external dependencies
5. Use pytest fixtures for reusable setup

## Troubleshooting

### Java Tests

**Issue**: Tests fail with database connection error
- **Solution**: Ensure H2 database dependency is added and test properties are set correctly

**Issue**: Mockito injection fails
- **Solution**: Check that `MockitoAnnotations.openMocks(this)` is called in `@BeforeEach`

### Python Tests

**Issue**: PyAutoGUI import error
- **Solution**: Ensure `conftest.py` is present in tests directory to mock PyAutoGUI

**Issue**: FastAPI route tests fail
- **Solution**: Check that test client is properly initialized: `TestClient(app)`

## References

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)
- [Pytest Documentation](https://docs.pytest.org/)
- [FastAPI Testing](https://fastapi.tiangolo.com/tutorial/testing/)
