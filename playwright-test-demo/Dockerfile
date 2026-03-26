FROM mcr.microsoft.com/playwright/python:v1.49.1-noble

WORKDIR /app

# Copy dependency files first for layer caching
COPY requirements.txt pyproject.toml ./

RUN pip install --no-cache-dir -r requirements.txt

# Copy source code
COPY pages/ pages/
COPY tests/ tests/

# Default: run all tests in headless mode
ENTRYPOINT ["pytest", "tests/", "-v", "--tb=short"]
